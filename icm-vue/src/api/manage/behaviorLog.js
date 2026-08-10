import request from '@/utils/request'

// 查询用户行为日志列表
export function listBehaviorLog(query) {
  return request({
    url: '/manage/behaviorLog/list',
    method: 'get',
    params: query
  })
}

// 查询用户行为日志详细
export function getBehaviorLog(id) {
  return request({
    url: '/manage/behaviorLog/' + id,
    method: 'get'
  })
}

// 新增用户行为日志
export function addBehaviorLog(data) {
  return request({
    url: '/manage/behaviorLog',
    method: 'post',
    data: data
  })
}

// 修改用户行为日志
export function updateBehaviorLog(data) {
  return request({
    url: '/manage/behaviorLog',
    method: 'put',
    data: data
  })
}

// 删除用户行为日志
export function delBehaviorLog(ids) {
  return request({
    url: '/manage/behaviorLog/' + ids,
    method: 'delete'
  })
}

// 查询增量数据
export function getIncrementalBehaviorLog(userMobile, queryTime) {
  return request({
    url: '/manage/behaviorLog/incremental',
    method: 'get',
    params: { userMobile, queryTime }
  })
}
