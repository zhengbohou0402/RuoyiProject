import request from '@/utils/request'

// 查询数据源下拉选项
export function getDatasourceOptions() {
  return request({
    url: '/quality/config/datasourceOptions',
    method: 'get'
  })
}

// 查询字段列表（含所有规则配置）
export function getFieldList(query) {
  return request({
    url: '/quality/config/fields',
    method: 'get',
    params: query
  })
}

// 批量保存规则配置
export function saveRules(data) {
  return request({
    url: '/quality/config',
    method: 'post',
    data: data
  })
}

// 查询规则配置列表
export function listRules(query) {
  return request({
    url: '/quality/config/rules',
    method: 'get',
    params: query
  })
}

// 查询规则配置详情
export function getRule(id) {
  return request({
    url: '/quality/config/rules/' + id,
    method: 'get'
  })
}

// 修改规则配置
export function updateRule(data) {
  return request({
    url: '/quality/config',
    method: 'put',
    data: data
  })
}

// 删除规则配置
export function delRules(ids) {
  return request({
    url: '/quality/config/rules/' + ids,
    method: 'delete'
  })
}
