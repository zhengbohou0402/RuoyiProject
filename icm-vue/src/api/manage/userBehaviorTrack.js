import request from '@/utils/request'

// 查询用户行为轨迹列表
export function listUserBehaviorTrack(query) {
  return request({
    url: '/manage/userBehaviorTrack/list',
    method: 'get',
    params: query
  })
}

// 导出用户行为轨迹
export function exportUserBehaviorTrack(data) {
  return request({
    url: '/manage/userBehaviorTrack/export',
    method: 'post',
    params: data,
    responseType: 'blob'
  })
}
