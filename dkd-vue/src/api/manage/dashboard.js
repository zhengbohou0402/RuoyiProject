import request from '@/utils/request'

// 工单统计
export function getTaskStats() {
  return request({
    url: '/home/dashboard/taskStats',
    method: 'get'
  })
}

// 销售统计
export function getSaleStats() {
  return request({
    url: '/home/dashboard/saleStats',
    method: 'get'
  })
}

// 商品热榜
export function getSkuRank() {
  return request({
    url: '/home/dashboard/skuRank',
    method: 'get'
  })
}

// 销售数据，包含趋势和分布。
export function getSaleCollect(params) {
  return request({
    url: '/home/dashboard/saleCollect',
    method: 'get',
    params
  })
}

// 合作商点位数 Top5
export function getPartnerNode() {
  return request({
    url: '/home/dashboard/partnerNode',
    method: 'get'
  })
}

// 异常设备监控
export function getAbnormalEquipment() {
  return request({
    url: '/home/dashboard/abnormalEquipment',
    method: 'get'
  })
}
