import request from '@/utils/request'

const previewHeaders = {
  silent: import.meta.env.DEV
}

export function getTaskStats() {
  return request({
    url: '/home/dashboard/taskStats',
    method: 'get',
    headers: previewHeaders
  })
}

export function getSaleStats() {
  return request({
    url: '/home/dashboard/saleStats',
    method: 'get',
    headers: previewHeaders
  })
}

export function getSkuRank() {
  return request({
    url: '/home/dashboard/skuRank',
    method: 'get',
    headers: previewHeaders
  })
}

export function getSaleCollect(params) {
  return request({
    url: '/home/dashboard/saleCollect',
    method: 'get',
    params,
    headers: previewHeaders
  })
}

export function getPartnerNode() {
  return request({
    url: '/home/dashboard/partnerNode',
    method: 'get',
    headers: previewHeaders
  })
}

export function getAbnormalEquipment() {
  return request({
    url: '/home/dashboard/abnormalEquipment',
    method: 'get',
    headers: previewHeaders
  })
}
