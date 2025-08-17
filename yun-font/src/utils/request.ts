import axios from 'axios'
import {
  AxiosError
} from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'

// 扩展请求配置类型
interface RequestConfig extends InternalAxiosRequestConfig {
  isToken?: boolean
}

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8101/api/v2/api-docs',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const typedConfig = config as RequestConfig

    // 转换page参数格式
    if (typedConfig.params?.page) {
      typedConfig.params = {
        ...typedConfig.params,
        pageNum: typedConfig.params.page.pageNum,
        pageSize: typedConfig.params.page.pageSize
      }
      delete typedConfig.params.page
    }

    return typedConfig
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data } = response

    if (data.code !== 0) {
      message.error(data.msg)
      return Promise.reject(data)
    }
    return data
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      const errorMsg = data?.msg || '请求错误'
      message.error(errorMsg)
    } else {
      message.error('网络连接异常')
    }
    return Promise.reject(error)
  }
)
export default request
