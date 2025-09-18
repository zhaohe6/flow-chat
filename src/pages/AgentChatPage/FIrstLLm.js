import { ChatOpenAI } from "@langchain/openai";
import { HumanMessage } from "@langchain/core/messages";

// 创建 DeepSeek 实例
const createDeepSeekInstance = () => {
  // 获取API密钥
  const apiKey = process.env.REACT_APP_DEEPSEEK_API_KEY;
  console.log("DeepSeek apikey", apiKey);
  
  // 检查API密钥是否存在
  if (!apiKey || apiKey === 'your_deepseek_api_key_here') {
    throw new Error('请在 .env 文件中设置正确的 REACT_APP_DEEPSEEK_API_KEY');
  }
  
  const model = new ChatOpenAI({
    apiKey: apiKey,
    temperature: 0.9,
    modelName: "deepseek-chat", // DeepSeek 的模型名称
    configuration: {
      baseURL: "https://api.deepseek.com/v1", // DeepSeek API 地址
    },
  });
  
  return model;
};

// 使用示例
export const generateText = async (prompt) => {
  try {
    // 检查环境变量
    console.log('Environment check:', {
      hasApiKey: !!process.env.REACT_APP_DEEPSEEK_API_KEY,
      apiKeyPrefix: process.env.REACT_APP_DEEPSEEK_API_KEY?.substring(0, 7) + '...'
    });
    
    const model = createDeepSeekInstance();
    
    // 使用 ChatOpenAI 的正确调用方式
    const message = new HumanMessage(prompt);
    const response = await model.invoke([message]);
    
    return {
      success: true,
      data: response.content // ChatOpenAI 返回的是 message 对象，需要取 content
    };
  } catch (error) {
    console.error('LangChain DeepSeek 调用失败:', error);
    
    // 具体的错误处理
    if (error.message.includes('API key')) {
      return {
        success: false,
        error: 'API密钥配置错误',
        solution: '请检查 .env 文件中的 REACT_APP_DEEPSEEK_API_KEY 是否正确设置'
      };
    } else if (error.message.includes('429')) {
      return {
        success: false,
        error: 'API配额已用完',
        solution: '请检查 DeepSeek 账户余额和计费设置'
      };
    } else {
      return {
        success: false,
        error: error.message,
        solution: '请检查网络连接和API配置'
      };
    }
  }
};

export default createDeepSeekInstance; 