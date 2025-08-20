from langchain_google_genai import ChatGoogleGenerativeAI
from langchain.prompts import PromptTemplate
import os

# 設定 Google Gemini API Key
# 方法1: 在這裡直接設定 (不建議用於生產環境)
# os.environ["GOOGLE_API_KEY"] = "your-gemini-api-key-here"

# 方法2: 或者直接在 ChatGoogleGenerativeAI 中傳入
# llm = ChatGoogleGenerativeAI(model="gemini-1.5-flash", google_api_key="your-gemini-api-key-here")

# 1. 建立 LLM (假設你已經設定了 GOOGLE_API_KEY 環境變數)
llm = ChatGoogleGenerativeAI(model="gemini-1.5-flash")

# 2. 設定 Prompt 模板
prompt = PromptTemplate.from_template("請用三句話解釋：{topic}")

# 3. 建立 Chain (使用新的語法)
chain = prompt | llm

# 4. 執行
result = chain.invoke({"topic": "LangChain 是什麼"})
print(result.content)
