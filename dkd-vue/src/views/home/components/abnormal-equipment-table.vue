<template>
  <div class="box abnormal-equipment">
    <div class="header">
      <div class="title">异常设备监控</div>
      <el-icon @click="handleMore"><MoreFilled /></el-icon>
    </div>
    <el-scrollbar v-if="listData.length" class="scrollbar body">
      <el-table
        :data="listData"
        fit
        highlight-current-row
        style="width: 100%"
        :header-cell-style="{
          padding: '7px 0 6px',
          background: '#EFF0F2',
          'font-weight': '400',
          'text-align': 'left',
          color: '#333333',
        }"
        :cell-style="{
          padding: '15px 0',
          'text-align': 'left',
          color: '#999999',
        }"
      >
        <el-table-column label="故障时间" width="160px">
          <template #default="scope">
            <span>{{ scope.row.updateTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="设备地址" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.addr }}</span>
          </template>
        </el-table-column>
        <el-table-column label="设备编号" width="120px">
          <template #default="scope">
            <span>{{ scope.row.innerCode }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-scrollbar>
    <!-- TODO：一开始显示加载中 -->
    <div v-else class="empty">
      <img src="@/assets/images/empty.png" />
      <div class="tips">暂无数据</div>
    </div>
  </div>
</template>
<script setup>
import { onMounted } from 'vue';
import { getAbnormalEquipment } from '@/api/manage/dashboard'

const router = useRouter();
const listData = ref([]);

function loadAbnormalEquipment() {
  getAbnormalEquipment().then(response => {
    listData.value = response.data?.list || [];
  }).catch(() => {})
}

onMounted(() => {
  loadAbnormalEquipment();
});

const handleMore = () => {
  router.push({ path: '/manage/index' });
};
</script>
<style lang="scss" scoped>
@import '@/assets/styles/variables.module.scss';

.abnormal-equipment {
  display: flex;
  flex-direction: column;
  height: calc((100vh - 120px) * 0.4);
  min-height: 353px;
  background: #ffffff;
  border-radius: 20px;

  .more {
    color: $--color-primary;
    cursor: pointer;
  }

  .empty {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    .tips {
      margin-top: 25px;
      color: #20232a;
      font-size: 14px;
    }
  }

  .body {
    flex: 1;
    margin-top: 20px;
  }
}
</style>