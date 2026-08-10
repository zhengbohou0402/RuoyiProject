import request from '@/utils/request'

// 获取路由
export const getRouters = (silent = false) => {
  return request({
    url: '/getRouters',
    method: 'get',
    headers: { silent }
  })
}