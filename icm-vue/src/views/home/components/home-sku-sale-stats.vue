<template>
  <div class="box home-sku-sale-stats">
    <div class="header">
      <div class="title">
        Sales<span class="sub-title">{{ start }} ~ {{ end }}</span>
      </div>
    </div>
    <div class="body">
      <div class="stats">
        <div class="item">
          <div class="num">{{ orderCountNum }}</div>
          <div class="text">Orders</div>
        </div>
      </div>
      <div class="stats">
        <div class="item">
          <div class="num">
            {{ orderAmountNum > 10000 ? (orderAmountNum / 10000).toFixed(2) : orderAmountNum }}
          </div>
          <div class="text">Revenue ({{ orderAmountNum > 10000 ? '10k CNY' : 'CNY' }})</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import dayjs from 'dayjs'
import { getSaleStats } from '@/api/manage/dashboard'

const orderCountNum = ref(0)
const orderAmountNum = ref(0)
const start = dayjs().startOf('month').format('YYYY.MM.DD')
const end = dayjs().endOf('day').format('YYYY.MM.DD')

function loadSaleStats() {
  getSaleStats().then(response => {
    const data = response.data || {}
    orderCountNum.value = Number(data.orderCountNum) || 0
    orderAmountNum.value = Number(data.orderAmountNum) || 0
  }).catch(() => {})
}

loadSaleStats()
</script>

<style lang="scss" scoped>
.home-sku-sale-stats {
  display: flex;
  flex-direction: column;
  height: calc((100vh - 120px) * 0.2);
  min-height: 166px;
  background: #ffffff;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  overflow: hidden;

  .body {
    flex: 1;
    display: flex;
  }

  .stats {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    border-right: 1px solid #f1f5f9;

    &:last-child {
      border-right: none;
    }
  }

  .item {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
  }

  .num {
    height: 44px;
    font-size: 30px;
    font-weight: 700;
    line-height: 44px;
    color: #0f172a;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  }

  .text {
    min-height: 17px;
    margin-top: 6px;
    font-size: 12px;
    font-weight: 500;
    color: #64748b;
    line-height: 17px;
  }
}
</style>
