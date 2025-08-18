"""
直接測試法規載入情況
"""
import json
import os
import glob

def check_traffic_law_loading():
    """檢查道路交通管理處罰條例的載入情況"""
    
    # 檢查parsed_laws_new目錄
    parsed_laws_dir = os.path.join(os.path.dirname(__file__), 'law-target')
    print(f"檢查目錄: {parsed_laws_dir}")
    
    # 獲取所有json文件
    json_files = glob.glob(os.path.join(parsed_laws_dir, '*.json'))
    print(f"找到 {len(json_files)} 個JSON文件")
    
    traffic_laws_found = []
    total_articles = 0
    all_laws = {}
    
    for json_file in json_files:
        # 跳過統計文件
        if 'statistics.json' in json_file or 'summary.json' in json_file:
            continue
        
        filename = os.path.basename(json_file)
        print(f"\n檢查文件: {filename}")
        
        try:
            with open(json_file, 'r', encoding='utf-8') as f:
                law_data = json.load(f)
            
            law_title = law_data.get('title', '未知法規')
            articles_count = len(law_data.get('articles', []))
            
            print(f"  法規標題: {law_title}")
            print(f"  條文數量: {articles_count}")
            
            all_laws[law_title] = articles_count
            total_articles += articles_count
            
            # 檢查是否為交通相關法規
            if '道路交通管理處罰條例' in law_title:
                traffic_laws_found.append({
                    'title': law_title,
                    'file': filename,
                    'articles': articles_count,
                    'sample_articles': law_data.get('articles', [])[:3]  # 前3條樣本
                })
                
        except Exception as e:
            print(f"  錯誤: {e}")
    
    print(f"\n=== 總結 ===")
    print(f"總法規數: {len(all_laws)}")
    print(f"總條文數: {total_articles}")
    
    print(f"\n=== 所有法規列表 ===")
    for law_title, count in sorted(all_laws.items()):
        print(f"  {law_title}: {count} 條")
    
    print(f"\n=== 道路交通管理處罰條例檢查結果 ===")
    if traffic_laws_found:
        for traffic_law in traffic_laws_found:
            print(f"✓ 找到: {traffic_law['title']}")
            print(f"  文件: {traffic_law['file']}")
            print(f"  條文數: {traffic_law['articles']}")
            print(f"  樣本條文:")
            for i, article in enumerate(traffic_law['sample_articles']):
                print(f"    第{article.get('article_number', '?')}條: {article.get('content', '')[:100]}...")
    else:
        print("✗ 未找到道路交通管理處罰條例")
    
    # 模擬Mock API的載入過程
    print(f"\n=== 模擬Mock API載入過程 ===")
    laws_data = []
    
    for json_file in json_files:
        if 'statistics.json' in json_file or 'summary.json' in json_file:
            continue
            
        try:
            with open(json_file, 'r', encoding='utf-8') as f:
                law_data = json.load(f)
                
            if 'articles' in law_data and law_data['articles']:
                law_name = law_data.get('title', '未知法規')
                
                for article in law_data['articles']:
                    content = article.get('content', '').strip()
                    
                    # 跳過刪除的條文
                    if '（刪除）' in content or '(刪除)' in content:
                        continue
                    
                    # 跳過空內容
                    if not content or content == '':
                        continue
                    
                    laws_data.append({
                        "law_name": law_name,
                        "article_number": f"第{article.get('article_number', '')}條",
                        "content": content
                    })
                    
        except Exception as e:
            print(f"載入 {json_file} 時發生錯誤: {e}")
    
    print(f"Mock API會載入 {len(laws_data)} 個條文")
    
    # 檢查道路交通管理處罰條例在Mock API中的情況
    traffic_articles = [item for item in laws_data if '道路交通管理處罰條例' in item['law_name']]
    print(f"其中道路交通管理處罰條例: {len(traffic_articles)} 條")
    
    if traffic_articles:
        print("前5條示例:")
        for i, article in enumerate(traffic_articles[:5]):
            print(f"  {article['article_number']}: {article['content'][:80]}...")

if __name__ == "__main__":
    check_traffic_law_loading()
