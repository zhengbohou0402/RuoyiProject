import request from '@/utils/request'

// 查询实时数据核查列表
export function listRealTimeData(query) {
  return request({
    url: '/manage/realTimeData/list',
    method: 'get',
    params: query
  })
}

// 增量轮询接口（长链接）
export function getIncrementalData(query) {
  return request({
    url: '/manage/realTimeData/incremental',
    method: 'get',
    params: query
  })
}

// 获取实时轮询游标
export function getRealTimeCursor(query) {
  return request({
    url: '/manage/realTimeData/cursor',
    method: 'get',
    params: query
  })
}
