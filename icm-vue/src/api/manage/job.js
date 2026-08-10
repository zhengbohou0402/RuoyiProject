import request from '@/utils/request'

// 查询自动补货任务列表
export function listJob(query) {
  return request({ url: '/manage/job/list', method: 'get', params: query })
}

// 查询自动补货任务详细
export function getJob(id) {
  return request({ url: '/manage/job/' + id, method: 'get' })
}

// 新增自动补货任务
export function addJob(data) {
  return request({ url: '/manage/job', method: 'post', data: data })
}

// 修改自动补货任务
export function updateJob(data) {
  return request({ url: '/manage/job', method: 'put', data: data })
}

// 删除自动补货任务
export function delJob(ids) {
  return request({ url: '/manage/job/' + ids, method: 'delete' })
}

// 导出自动补货任务
export function exportJob(data) {
  return request({ url: '/manage/job/export', method: 'post', data: data, responseType: 'blob' })
}
