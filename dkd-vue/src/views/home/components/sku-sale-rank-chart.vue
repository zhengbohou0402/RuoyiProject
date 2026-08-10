<template>
  <div class="box sku-sale-rank">
    <div class="header">
      <div class="title">
        Product ranking<span class="sub-title">{{ start }} ~ {{ end }}</span>
      </div>
    </div>
    <div class="body">
      <el-row v-for="(item, index) in skuSaleRank" :key="index" class="rank-row">
        <el-col :span="5">
          <div :class="'top top' + (index + 1)">{{ index + 1 }}</div>
        </el-col>
        <el-col :span="13">
          <div class="sku-name" :title="item.skuName">{{ item.skuName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="count">{{ item.count }} sold</div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import dayjs from 'dayjs'
import { getSkuRank } from '@/api/manage/dashboard'

const skuSaleRank = ref([])
const start = dayjs().startOf('month').format('YYYY.MM.DD')
const end = dayjs().endOf('day').format('YYYY.MM.DD')

function loadSkuRank() {
  getSkuRank().then(response => {
    skuSaleRank.value = (response.data?.list || []).map(item => ({
      skuId: String(item.skuId || ''),
      skuName: item.skuName || '',
      count: Number(item.count) || 0,
      amount: Number(item.amount) || 0
    }))
  }).catch(() => {})
}

onMounted(() => {
  loadSkuRank()
})
</script>

<style lang="scss" scoped>
.sku-sale-rank {
  display: flex;
  flex-direction: column;
  height: calc((100vh - 120px) * 0.6);
  min-height: 520px;
  background: #ffffff;
  border-radius: 10px;
  border: 1px solid #e2e8f0;

  .body {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    margin-top: 18px;
    gap: 13px;
  }

  .rank-row {
    align-items: center;
  }

  .top {
    display: inline-block;
    width: 24px;
    height: 24px;
    margin-left: 10px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 7px;
    text-align: center;
    font-size: 12px;
    font-weight: 650;
    color: #64748b;
    line-height: 22px;
  }

  .top1 {
    background: #111827;
    border-color: #111827;
    color: #ffffff;
  }

  .top2 {
    background: #334155;
    border-color: #334155;
    color: #ffffff;
  }

  .top3 {
    background: #64748b;
    border-color: #64748b;
    color: #ffffff;
  }

  .sku-name {
    height: 20px;
    font-size: 14px;
    font-weight: 600;
    color: #0f172a;
    line-height: 20px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .count {
    height: 20px;
    font-size: 13px;
    font-weight: 500;
    color: #64748b;
    line-height: 20px;
    text-align: right;
  }
}
</style>
