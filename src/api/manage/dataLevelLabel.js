import request from '@/utils/request'

// 查询数据等级标注列表
export function listDataLevelLabel(query) {
  return request({ url: '/data_level/list', method: 'get', params: query })
}

// 新增数据等级标注
export function addDataLevelLabel(data) {
  return request({ url: '/data_level', method: 'post', data: data })
}

// 修改数据等级标注
export function updateDataLevelLabel(data) {
  return request({ url: '/data_level', method: 'put', data: data })
}

// 删除数据等级标注
export function delDataLevelLabel(id) {
  return request({ url: '/data_level/' + id, method: 'delete' })
}

// 获取事件类型和渠道ID下拉选项
export function getDataLevelOptions() {
  return request({ url: '/data_level/options', method: 'get' })
}
