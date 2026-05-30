<template>
  <div class="box home-user-task-stats">
    <div class="header">
      <div class="title">
        Work orders<span class="sub-title">{{ start }} ~ {{ end }}</span>
      </div>
    </div>
    <div class="body">
      <div v-for="item in stats" :key="item.label" class="stats">
        <div class="item">
          <div class="num">{{ item.value }}</div>
          <div class="text">{{ item.label }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import dayjs from 'dayjs'
import { getTaskStats } from '@/api/manage/dashboard'

const start = dayjs().startOf('month').format('YYYY.MM.DD')
const end = dayjs().endOf('day').format('YYYY.MM.DD')
const userTaskStats = ref([])

const taskSummary = computed(() => userTaskStats.value.reduce((summary, item) => {
  summary.total += item.total
  summary.completedTotal += item.completedTotal
  summary.cancelTotal += item.cancelTotal
  summary.progressTotal += item.progressTotal
  return summary
}, {
  total: 0,
  completedTotal: 0,
  cancelTotal: 0,
  progressTotal: 0
}))

const stats = computed(() => [
  { label: 'Total', value: taskSummary.value.total },
  { label: 'Completed', value: taskSummary.value.completedTotal },
  { label: 'In progress', value: taskSummary.value.progressTotal },
  { label: 'Cancelled', value: taskSummary.value.cancelTotal }
])

function loadTaskStats() {
  getTaskStats().then(response => {
    const data = response.data?.stats || []
    userTaskStats.value = data.map(s => ({
      total: Number(s.total) || 0,
      completedTotal: Number(s.completedTotal) || 0,
      cancelTotal: Number(s.cancelTotal) || 0,
      progressTotal: Number(s.progressTotal) || 0,
      workerCount: Number(s.workerCount) || 0,
      repair: s.repair === 'true',
      date: null
    }))
  }).catch(() => {})
}

loadTaskStats()
</script>

<style lang="scss" scoped>
.home-user-task-stats {
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
