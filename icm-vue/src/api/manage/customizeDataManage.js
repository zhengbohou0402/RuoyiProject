import request from '@/utils/request'

export function listCustomizeDataManage(data) {
  return request({
    url: '/customizeDataManage/list',
    method: 'post',
    data: data
  })
}

export function getCustomizeDataManage(id) {
  return request({
    url: '/customizeDataManage/detail/' + id,
    method: 'get'
  })
}

export function addCustomizeDataManage(data) {
  return request({
    url: '/customizeDataManage/add',
    method: 'post',
    data: data
  })
}

export function updateCustomizeDataManage(data) {
  return request({
    url: '/customizeDataManage/edit',
    method: 'put',
    data: data
  })
}

export function delCustomizeDataManage(id) {
  return request({
    url: '/customizeDataManage/delete/' + id,
    method: 'delete'
  })
}

export function startTask(id) {
  return request({
    url: '/customizeDataManage/start/' + id,
    method: 'get'
  })
}

export function stopTask(id) {
  return request({
    url: '/customizeDataManage/stop/' + id,
    method: 'get'
  })
}

export function queryChannelList() {
  return request({
    url: '/customizeDataManage/queryChannelList',
    method: 'get'
  })
}
