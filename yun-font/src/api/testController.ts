// @ts-ignore
/* eslint-disable */
import request from '@/utils/request'

/** 测试1 GET /api/test */
export async function test1UsingGet(options?: { [key: string]: any }) {
  return request<API.ResultTUtilString_>('/api/test', {
    method: 'GET',
    ...(options || {}),
  })
}
