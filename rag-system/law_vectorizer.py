#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
RAG 向量化處理器
將結構化的法規資料轉換為向量，準備用於檢索
"""

import os
import json
import numpy as np
from pathlib import Path
from typing import List, Dict, Any, Tuple
import logging
from sentence_transformers import SentenceTransformer
import faiss
import pickle

# 設置日誌
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class LawVectorizer:
    def __init__(self, 
                 parsed_laws_dir: str = "./parsed_laws_new",
                 model_name: str = "paraphrase-multilingual-MiniLM-L12-v2",
                 vector_db_path: str = "./vector_db"):
        """
        初始化法規向量化器
        
        Args:
            parsed_laws_dir: 解析後的法規 JSON 檔案目錄
            model_name: Sentence Transformer 模型名稱
            vector_db_path: 向量資料庫儲存路徑
        """
        self.parsed_laws_dir = Path(parsed_laws_dir)
        self.vector_db_path = Path(vector_db_path)
        self.vector_db_path.mkdir(exist_ok=True)
        
        # 載入模型
        logger.info(f"載入模型: {model_name}")
        self.model = SentenceTransformer(model_name)
        self.embedding_dim = self.model.get_sentence_embedding_dimension()
        
        # 初始化向量索引
        self.index = faiss.IndexFlatIP(self.embedding_dim)  # 使用內積相似度
        self.law_chunks = []  # 儲存法規片段資訊
        
    def load_laws(self) -> List[Dict[str, Any]]:
        """載入所有解析後的法規"""
        laws = []
        json_files = list(self.parsed_laws_dir.glob("*.json"))
        
        for json_file in json_files:
            if json_file.name == "statistics.json" or json_file.name == "all_laws_summary.json":
                continue
                
            try:
                with open(json_file, 'r', encoding='utf-8') as f:
                    law_data = json.load(f)
                    if law_data.get('articles'):  # 只處理有條文的法規
                        laws.append(law_data)
            except Exception as e:
                logger.error(f"載入法規檔案 {json_file} 時發生錯誤: {str(e)}")
        
        logger.info(f"成功載入 {len(laws)} 部法規")
        return laws
    
    def create_law_chunks(self, laws: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        將法規分割為可檢索的片段
        
        每一條法規作為一個片段，包含：
        - 法規名稱
        - 條號
        - 條文內容
        - 法規類型
        """
        chunks = []
        
        for law in laws:
            law_title = law['title']
            # 嘗試從metadata獲取法規類型，如果沒有則根據標題判斷
            law_type = "其他法規"  # 預設值
            if 'metadata' in law and 'law_type' in law['metadata']:
                law_type = law['metadata']['law_type']
            else:
                # 根據標題判斷法規類型
                if '刑法' in law_title:
                    law_type = "刑法"
                elif '民法' in law_title:
                    law_type = "民法"
                elif '警察' in law_title:
                    law_type = "警察法規"
                elif '交通' in law_title or '道路' in law_title:
                    law_type = "交通法規"
                elif '毒品' in law_title:
                    law_type = "毒品防制法規"
                elif '槍砲' in law_title or '槍械' in law_title:
                    law_type = "槍械管制法規"
                elif '社會秩序' in law_title:
                    law_type = "行政法規"
            
            for article in law['articles']:
                if not article['content'].strip():
                    continue
                
                # 創建片段文本（用於向量化）
                chunk_text = f"{law_title} 第{article['article_number']}條 {article['content']}"
                
                chunk = {
                    'law_title': law_title,
                    'law_type': law_type,
                    'article_number': article['article_number'],
                    'article_content': article['content'],
                    'full_text': article['full_text'],
                    'chunk_text': chunk_text,
                    'chunk_id': f"{law_title}_{article['article_number']}"
                }
                chunks.append(chunk)
        
        logger.info(f"創建 {len(chunks)} 個法規片段")
        return chunks
    
    def vectorize_chunks(self, chunks: List[Dict[str, Any]]) -> np.ndarray:
        """將法規片段向量化"""
        logger.info("開始向量化法規片段...")
        
        # 提取用於向量化的文本
        texts = [chunk['chunk_text'] for chunk in chunks]
        
        # 批次向量化
        batch_size = 32
        embeddings = []
        
        for i in range(0, len(texts), batch_size):
            batch_texts = texts[i:i + batch_size]
            batch_embeddings = self.model.encode(batch_texts, 
                                                convert_to_numpy=True,
                                                normalize_embeddings=True)
            embeddings.append(batch_embeddings)
            
            if i % (batch_size * 10) == 0:
                logger.info(f"已處理 {i + len(batch_texts)} / {len(texts)} 個片段")
        
        embeddings = np.vstack(embeddings)
        logger.info(f"向量化完成，形狀: {embeddings.shape}")
        return embeddings
    
    def build_vector_database(self):
        """建立向量資料庫"""
        logger.info("開始建立向量資料庫...")
        
        # 載入法規
        laws = self.load_laws()
        if not laws:
            logger.error("沒有可用的法規資料")
            return
        
        # 創建片段
        self.law_chunks = self.create_law_chunks(laws)
        if not self.law_chunks:
            logger.error("沒有可用的法規片段")
            return
        
        # 向量化
        embeddings = self.vectorize_chunks(self.law_chunks)
        
        # 建立 FAISS 索引
        self.index.add(embeddings)
        
        # 儲存向量資料庫
        self.save_vector_database()
        
        logger.info("向量資料庫建立完成")
    
    def save_vector_database(self):
        """儲存向量資料庫"""
        # 儲存 FAISS 索引
        faiss_index_path = self.vector_db_path / "law_index.faiss"
        faiss.write_index(self.index, str(faiss_index_path))
        
        # 儲存法規片段資訊
        chunks_path = self.vector_db_path / "law_chunks.pkl"
        with open(chunks_path, 'wb') as f:
            pickle.dump(self.law_chunks, f)
        
        # 儲存元資料
        metadata = {
            'total_chunks': len(self.law_chunks),
            'embedding_dim': self.embedding_dim,
            'model_name': self.model.model_name if hasattr(self.model, 'model_name') else 'unknown'
        }
        metadata_path = self.vector_db_path / "metadata.json"
        with open(metadata_path, 'w', encoding='utf-8') as f:
            json.dump(metadata, f, ensure_ascii=False, indent=2)
        
        logger.info(f"向量資料庫已儲存至: {self.vector_db_path}")
    
    def load_vector_database(self):
        """載入向量資料庫"""
        try:
            # 載入 FAISS 索引
            faiss_index_path = self.vector_db_path / "law_index.faiss"
            self.index = faiss.read_index(str(faiss_index_path))
            
            # 載入法規片段資訊
            chunks_path = self.vector_db_path / "law_chunks.pkl"
            with open(chunks_path, 'rb') as f:
                self.law_chunks = pickle.load(f)
            
            logger.info(f"成功載入向量資料庫，包含 {len(self.law_chunks)} 個片段")
            return True
            
        except Exception as e:
            logger.error(f"載入向量資料庫失敗: {str(e)}")
            return False
    
    def search(self, query: str, top_k: int = 5) -> List[Dict[str, Any]]:
        """
        檢索相關法規
        
        Args:
            query: 查詢文本
            top_k: 返回最相關的 k 個結果
            
        Returns:
            相關法規片段列表
        """
        if not self.law_chunks:
            logger.error("向量資料庫未載入")
            return []
        
        # 將查詢向量化
        query_embedding = self.model.encode([query], 
                                          convert_to_numpy=True,
                                          normalize_embeddings=True)
        
        # 搜尋相似片段
        scores, indices = self.index.search(query_embedding, top_k)
        
        results = []
        for i, (score, idx) in enumerate(zip(scores[0], indices[0])):
            if idx >= len(self.law_chunks):
                continue
                
            chunk = self.law_chunks[idx].copy()
            chunk['similarity_score'] = float(score)
            chunk['rank'] = i + 1
            results.append(chunk)
        
        return results
    
    def get_statistics(self) -> Dict[str, Any]:
        """獲取向量資料庫統計資訊"""
        if not self.law_chunks:
            return {}
        
        # 統計各類法規的片段數量
        law_type_stats = {}
        law_title_stats = {}
        
        for chunk in self.law_chunks:
            law_type = chunk['law_type']
            law_title = chunk['law_title']
            
            law_type_stats[law_type] = law_type_stats.get(law_type, 0) + 1
            law_title_stats[law_title] = law_title_stats.get(law_title, 0) + 1
        
        return {
            'total_chunks': len(self.law_chunks),
            'embedding_dimension': self.embedding_dim,
            'law_types': law_type_stats,
            'law_titles': law_title_stats
        }

def main():
    """主函數"""
    vectorizer = LawVectorizer()
    
    # 檢查是否已存在向量資料庫
    if vectorizer.load_vector_database():
        logger.info("使用現有的向量資料庫")
    else:
        logger.info("建立新的向量資料庫")
        vectorizer.build_vector_database()
    
    # 顯示統計資訊
    stats = vectorizer.get_statistics()
    print(f"\n向量資料庫統計:")
    print(f"總片段數: {stats.get('total_chunks', 0)}")
    print(f"向量維度: {stats.get('embedding_dimension', 0)}")
    print(f"法規類型: {list(stats.get('law_types', {}).keys())}")
    
    # 測試檢索
    print(f"\n測試檢索:")
    test_queries = [
        "殺人罪的刑責",
        "警察執法權限",
        "毒品持有的處罰",
        "交通違規罰款"
    ]
    
    for query in test_queries:
        print(f"\n查詢: {query}")
        results = vectorizer.search(query, top_k=3)
        for result in results:
            print(f"  - {result['law_title']} 第{result['article_number']}條 (相似度: {result['similarity_score']:.3f})")
            print(f"    {result['article_content'][:100]}...")

if __name__ == "__main__":
    main()
