import request from '@/utils/request'

// 查询货道列表
export function listChannel(query) {
  return request({ url: '/manage/channel/list', method: 'get', params: query })
}

// 查询货道详细
export function getChannel(id) {
  return request({ url: '/manage/channel/' + id, method: 'get' })
}

// 新增货道
export function addChannel(data) {
  return request({ url: '/manage/channel', method: 'post', data: data })
}

// 修改货道
export function updateChannel(data) {
  return request({ url: '/manage/channel', method: 'put', data: data })
}

// 删除货道
export function delChannel(ids) {
  return request({ url: '/manage/channel/' + ids, method: 'delete' })
}

// 导出货道
export function exportChannel(data) {
  return request({ url: '/manage/channel/export', method: 'post', data: data, responseType: 'blob' })
}

// 按设备编号查询货道列表
export function listChannelByInnerCode(innerCode) {
  return request({ url: '/manage/channel/list/' + innerCode, method: 'get' })
}

// 货道关联商品
export function configChannel(data) {
  return request({ url: '/manage/channel/config', method: 'put', data: data })
}
