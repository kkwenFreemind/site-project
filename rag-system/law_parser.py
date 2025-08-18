#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
法規 HTML 解析器
將法規目錄中的 HTML 檔案解析為結構化的 JSON 格式
"""

import os
import json
import re
from pathlib import Path
from bs4 import BeautifulSoup
from typing import List, Dict, Any
import logging

# 設置日誌
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class LawParser:
    def __init__(self, law_directory: str = "./law-source", output_directory: str = "./law-target"):
        """
        初始化法規解析器
        
        Args:
            law_directory: 法規 HTML 檔案目錄
            output_directory: 輸出 JSON 檔案目錄
        """
        self.law_directory = Path(law_directory)
        self.output_directory = Path(output_directory)
        self.output_directory.mkdir(exist_ok=True)
        
    def parse_html_file(self, file_path: Path) -> Dict[str, Any]:
        """
        解析單個 HTML 法規檔案
        
        Args:
            file_path: HTML 檔案路徑
            
        Returns:
            解析後的法規資料字典
        """
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                
            soup = BeautifulSoup(content, 'html.parser')
            
            # 提取法規標題
            title = self._extract_title(soup, file_path.name)
            
            # 提取法規內容
            articles = self._extract_articles(soup)
            
            # 提取章節結構
            chapters = self._extract_chapters(soup)
            
            law_data = {
                "title": title,
                "filename": file_path.name,
                "total_articles": len(articles),
                "chapters": chapters,
                "articles": articles,
                "metadata": {
                    "source_file": str(file_path),
                    "parsing_date": "",
                    "law_type": self._classify_law_type(title)
                }
            }
            
            logger.info(f"成功解析: {title} ({len(articles)} 條)")
            return law_data
            
        except Exception as e:
            logger.error(f"解析檔案 {file_path} 時發生錯誤: {str(e)}")
            return {}
    
    def _extract_title(self, soup: BeautifulSoup, filename: str) -> str:
        """提取法規標題"""
        # 嘗試從 title 標籤提取
        title_tag = soup.find('title')
        if title_tag:
            title = title_tag.get_text().strip()
            # 移除"-全國法規資料庫"等後綴
            title = re.sub(r'-.*資料庫.*$', '', title)
            if title:
                return title
        
        # 從檔名提取
        title = filename.replace('.html', '').replace('.htm', '')
        title = re.sub(r'-.*資料庫.*$', '', title)
        return title
    
    def _extract_articles(self, soup: BeautifulSoup) -> List[Dict[str, Any]]:
        """提取法條內容"""
        articles = []
        
        # 方法1: 查找包含條號的 div
        article_rows = soup.find_all('div', class_='row')
        
        for row in article_rows:
            article_num_div = row.find('div', class_='col-no')
            article_content_div = row.find('div', class_='col-data')
            
            if article_num_div and article_content_div:
                # 提取條號
                article_num = self._extract_article_number(article_num_div)
                if not article_num:
                    continue
                
                # 提取條文內容
                content = self._extract_article_content(article_content_div)
                
                if content:
                    articles.append({
                        "article_number": article_num,
                        "content": content,
                        "full_text": f"第 {article_num} 條 {content}"
                    })
        
        return articles
    
    def _extract_article_number(self, div) -> str:
        """提取條號"""
        text = div.get_text().strip()
        
        # 匹配各種條號格式
        patterns = [
            r'第\s*(\d+(?:-\d+)?)\s*條',  # 第 X 條, 第 X-Y 條
            r'(\d+(?:-\d+)?)\s*條',      # X 條
            r'第\s*(\d+(?:-\d+)?)',      # 第 X
        ]
        
        for pattern in patterns:
            match = re.search(pattern, text)
            if match:
                return match.group(1)
        
        return ""
    
    def _extract_article_content(self, div) -> str:
        """提取條文內容"""
        # 移除條號部分，只保留內容
        content_parts = []
        
        # 查找所有文本行
        lines = div.find_all('div', class_=re.compile(r'line-\d+'))
        if not lines:
            # 如果沒有找到特定格式，就提取所有文本
            content = div.get_text().strip()
            # 清理內容
            content = re.sub(r'第\s*\d+(?:-\d+)?\s*條\s*', '', content)
            return content
        
        for line in lines:
            line_text = line.get_text().strip()
            if line_text and not re.match(r'^第\s*\d+(?:-\d+)?\s*條\s*$', line_text):
                content_parts.append(line_text)
        
        return '\n'.join(content_parts)
    
    def _extract_chapters(self, soup: BeautifulSoup) -> List[Dict[str, str]]:
        """提取章節結構"""
        chapters = []
        
        # 查找章節標題
        chapter_headers = soup.find_all(['h1', 'h2', 'h3', 'h4'], class_=re.compile(r'char-\d+'))
        
        for header in chapter_headers:
            chapter_text = header.get_text().strip()
            if chapter_text:
                chapters.append({
                    "title": chapter_text,
                    "level": header.name
                })
        
        return chapters
    
    def _classify_law_type(self, title: str) -> str:
        """根據標題分類法規類型"""
        if '民法' in title:
            return '民法'
        elif '刑法' in title:
            return '刑法'
        elif '刑事訴訟' in title:
            return '刑事訴訟法規'
        elif '警察' in title or '警械' in title:
            return '警察法規'
        elif '社會秩序維護法' in title or '行政' in title:
            return '行政法規'
        elif '道路交通' in title:
            return '交通法規'
        elif '毒品' in title:
            return '毒品防制法規'
        elif '槍砲' in title or '彈藥' in title:
            return '槍械管制法規'
        elif '集會遊行' in title:
            return '集會遊行法規'
        else:
            return '其他法規'
    
    def parse_all_laws(self):
        """解析所有法規檔案"""
        html_files = list(self.law_directory.glob('*.html')) + list(self.law_directory.glob('*.htm'))
        
        all_laws = []
        
        for file_path in html_files:
            if file_path.is_file():
                law_data = self.parse_html_file(file_path)
                if law_data:
                    all_laws.append(law_data)
                    
                    # 儲存個別法規檔案
                    output_filename = f"{law_data['title'].replace('/', '_')}.json"
                    output_path = self.output_directory / output_filename
                    with open(output_path, 'w', encoding='utf-8') as f:
                        json.dump(law_data, f, ensure_ascii=False, indent=2)
        
        # 儲存彙總檔案
        summary_path = self.output_directory / "all_laws_summary.json"
        with open(summary_path, 'w', encoding='utf-8') as f:
            json.dump(all_laws, f, ensure_ascii=False, indent=2)
        
        # 產生統計報告
        self._generate_statistics(all_laws)
        
        logger.info(f"完成解析 {len(all_laws)} 部法規")
        return all_laws
    
    def _generate_statistics(self, all_laws: List[Dict[str, Any]]):
        """產生統計報告"""
        stats = {
            "total_laws": len(all_laws),
            "total_articles": sum(law['total_articles'] for law in all_laws),
            "law_types": {},
            "laws_by_type": {}
        }
        
        for law in all_laws:
            law_type = law['metadata']['law_type']
            if law_type not in stats['law_types']:
                stats['law_types'][law_type] = 0
                stats['laws_by_type'][law_type] = []
            
            stats['law_types'][law_type] += 1
            stats['laws_by_type'][law_type].append({
                "title": law['title'],
                "articles": law['total_articles']
            })
        
        # 儲存統計報告
        stats_path = self.output_directory / "statistics.json"
        with open(stats_path, 'w', encoding='utf-8') as f:
            json.dump(stats, f, ensure_ascii=False, indent=2)
        
        logger.info(f"統計報告已儲存至: {stats_path}")

def main():
    """主函數"""
    parser = LawParser()
    laws = parser.parse_all_laws()
    
    print(f"\n解析完成!")
    print(f"共解析 {len(laws)} 部法規")
    print(f"輸出目錄: {parser.output_directory}")

if __name__ == "__main__":
    main()
