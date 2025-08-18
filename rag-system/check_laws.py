import pickle
import json
from collections import Counter

# 載入向量化的法條資料
with open('law_documents.pkl', 'rb') as f:
    docs = pickle.load(f)

print(f"總共載入 {len(docs)} 條法條")

# 統計各法規的條文數量
law_titles = [doc.get('law_title', '未知法規') for doc in docs]
law_counter = Counter(law_titles)

print("\n各法規條文數量:")
for law, count in sorted(law_counter.items()):
    print(f"  {law}: {count} 條")

# 檢查道路交通管理處罰條例
traffic_docs = [doc for doc in docs if '道路交通管理處罰條例' in doc.get('law_title', '')]
print(f"\n道路交通管理處罰條例條文數量: {len(traffic_docs)}")

if traffic_docs:
    print("前5條範例:")
    for i, doc in enumerate(traffic_docs[:5]):
        article_num = doc.get('article_number', '?')
        content = doc.get('content', '')[:80] + '...' if len(doc.get('content', '')) > 80 else doc.get('content', '')
        print(f"  第{article_num}條: {content}")
else:
    print("找不到道路交通管理處罰條例的條文!")

# 檢查是否有其他交通相關法規
traffic_related = [doc for doc in docs if any(keyword in doc.get('law_title', '') for keyword in ['交通', '道路', '車輛'])]
if traffic_related:
    traffic_titles = set([doc.get('law_title', '') for doc in traffic_related])
    print(f"\n交通相關法規:")
    for title in sorted(traffic_titles):
        count = len([doc for doc in traffic_related if doc.get('law_title', '') == title])
        print(f"  {title}: {count} 條")

# 檢查資料結構
if docs:
    print(f"\n資料結構範例 (第一條法條):")
    first_doc = docs[0]
    for key, value in first_doc.items():
        if isinstance(value, str) and len(value) > 100:
            print(f"  {key}: {value[:100]}...")
        else:
            print(f"  {key}: {value}")
