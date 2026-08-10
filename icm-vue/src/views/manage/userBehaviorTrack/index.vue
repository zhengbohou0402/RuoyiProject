<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="开始时间" prop="beginTime">
        <el-date-picker v-model="queryParams.beginTime" type="datetime" placeholder="选择开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 200px" />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker v-model="queryParams.endTime" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport" v-hasPermi="['manage:userBehaviorTrack:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="trackList" border>
      <el-table-column label="时间" align="center" prop="client_time" width="180" />
      <el-table-column label="手机号" align="center" prop="userId" width="140" />
      <el-table-column label="事件类型" align="center" prop="WT_et" width="120" />
      <el-table-column label="页面名称" align="center" prop="WT_ti" min-width="150" show-overflow-tooltip />
      <el-table-column label="页面链接" align="center" prop="WT_es" min-width="200" show-overflow-tooltip />
      <el-table-column label="点位内容" align="center" prop="WT_envName" min-width="150" show-overflow-tooltip />
      <el-table-column label="事件编码" align="center" prop="WT_event" width="150" />
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="UserBehaviorTrack">
import { listUserBehaviorTrack } from '@/api/manage/userBehaviorTrack'
import { ElMessage } from 'element-plus'

const { proxy } = getCurrentInstance()

const trackList = ref([])
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    phone: undefined,
    beginTime: undefined,
    endTime: undefined
  }
})

const { queryParams } = toRefs(data)

/** 查询列表 */
function getList() {
  if (!queryParams.value.phone || !queryParams.value.beginTime || !queryParams.value.endTime) {
    ElMessage.warning('请先填写手机号、开始时间和结束时间')
    return
  }
  loading.value = true
  listUserBehaviorTrack(queryParams.value).then(response => {
    trackList.value = response.rows
    total.value = response.total
    if (response.total === 0) {
      ElMessage.info('暂无符合条件的数据')
    }
  }).catch((error) => {
    console.error('用户行为轨迹查询失败:', error)
  }).finally(() => {
    loading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

/** 导出按钮操作 */
function handleExport() {
  if (!queryParams.value.phone || !queryParams.value.beginTime || !queryParams.value.endTime) {
    ElMessage.warning('请先填写手机号、开始时间和结束时间')
    return
  }
  proxy.download('manage/userBehaviorTrack/export', {
    ...queryParams.value
  }, `userBehaviorTrack_${new Date().getTime()}.xlsx`)
}
</script>
