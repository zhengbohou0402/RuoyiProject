import request from '@/utils/request'

// 查询元数据列表
export function listMetaData(query) {
  return request({ url: '/metaData/listAll', method: 'get', params: query })
}

// 查询元数据详细
export function getMetaData(id) {
  return request({ url: '/metaData/attribute_info', method: 'get', params: { id: id } })
}

// 新增元数据
export function addMetaData(data) {
  return request({ url: '/metaData', method: 'post', data: data })
}
