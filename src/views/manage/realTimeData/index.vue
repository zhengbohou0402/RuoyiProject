<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="queryParams.mobile" placeholder="请输入手机号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="事件类型" prop="wtEt">
        <el-input v-model="queryParams.wtEt" placeholder="请输入事件类型" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="事件编码" prop="wtEvent">
        <el-input v-model="queryParams.wtEvent" placeholder="请输入事件编码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">查询并开始</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button :type="isPolling ? 'danger' : 'success'" plain @click="togglePolling">
          {{ isPolling ? '停止轮询' : '开始轮询' }}
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-tag :type="isPolling ? 'success' : 'info'">
          {{ isPolling ? '监听新数据中' : '未监听' }}
        </el-tag>
      </el-col>
      <el-col :span="1.5">
        <span class="polling-cursor">当前游标ID: {{ cursorText || '-' }}</span>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="handleQuery" />
    </el-row>

    <el-alert
      v-if="!isPolling && dataList.length === 0"
      class="mb8"
      title="实时数据核查不会在进入页面时加载历史数据，请点击查询并开始或开始轮询后等待新增数据。"
      type="info"
      :closable="false"
      show-icon
    />

    <el-table v-loading="loading" :data="dataList" border max-height="500">
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="手机号" align="center" prop="mobile" width="130" />
      <el-table-column label="事件类型" align="center" prop="wt_et" width="100" />
      <el-table-column label="事件编码" align="center" prop="wt_event" width="150" show-overflow-tooltip />
      <el-table-column label="点位内容" align="center" prop="wt_envName" min-width="150" show-overflow-tooltip />
      <el-table-column label="页面名称" align="center" prop="wt_ti" min-width="150" show-overflow-tooltip />
      <el-table-column label="页面链接" align="center" prop="wt_es" min-width="180" show-overflow-tooltip />
      <el-table-column label="省/市" align="center" width="150">
        <template #default="scope">
          {{ scope.row.wt_loginProvince }}/{{ scope.row.wt_loginCity }}
        </template>
      </el-table-column>
      <el-table-column label="设备ID" align="center" prop="deviceId" width="120" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="100">
        <template #default="scope">
          <el-button link type="primary" @click="handleDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="数据详情" v-model="detailVisible" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.mobile }}</el-descriptions-item>
        <el-descriptions-item label="事件类型">{{ detailData.wt_et }}</el-descriptions-item>
        <el-descriptions-item label="事件编码">{{ detailData.wt_event }}</el-descriptions-item>
        <el-descriptions-item label="页面名称">{{ detailData.wt_ti }}</el-descriptions-item>
        <el-descriptions-item label="页面链接">{{ detailData.wt_es }}</el-descriptions-item>
        <el-descriptions-item label="点位内容" :span="2">{{ detailData.wt_envName }}</el-descriptions-item>
        <el-descriptions-item label="省">{{ detailData.wt_loginProvince }}</el-descriptions-item>
        <el-descriptions-item label="市">{{ detailData.wt_loginCity }}</el-descriptions-item>
        <el-descriptions-item label="设备ID">{{ detailData.deviceId }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ detailData.clientIp }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="扩展字段(XY_)" :span="2">
          <pre class="detail-json">{{ detailData.xy || '无' }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="RealTimeData">
import { getIncrementalData, getRealTimeCursor } from '@/api/manage/realTimeData'

const { proxy } = getCurrentInstance()

const dataList = ref([])
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const isPolling = ref(false)
const lastId = ref(0)
const detailVisible = ref(false)
const detailData = ref({})
let pollingTimer = null
const cursorText = computed(() => lastId.value < 0 ? 0 : lastId.value)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 50,
    mobile: undefined,
    wtEt: undefined,
    wtEvent: undefined
  }
})

const { queryParams } = toRefs(data)

function buildPollingParams() {
  return {
    mobile: queryParams.value.mobile,
    wtEt: queryParams.value.wtEt,
    wtEvent: queryParams.value.wtEvent,
    pageSize: queryParams.value.pageSize
  }
}

function handleQuery() {
  startPolling(true)
}

function resetQuery() {
  proxy.resetForm('queryRef')
  stopPolling()
  dataList.value = []
  total.value = 0
  lastId.value = 0
}

function togglePolling() {
  if (isPolling.value) {
    stopPolling()
  } else {
    startPolling(true)
  }
}

function startPolling(resetCursor = false) {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }

  if (resetCursor) {
    dataList.value = []
    total.value = 0
    loading.value = true
    getRealTimeCursor(buildPollingParams()).then(response => {
      const cursor = Number(response.total || 0)
      lastId.value = cursor > 0 ? cursor : -1
      isPolling.value = true
      pollingTimer = setInterval(fetchIncrementalData, 3000)
      proxy.$modal.msgSuccess('已开始监听新增实时数据')
    }).finally(() => {
      loading.value = false
    })
    return
  }

  isPolling.value = true
  pollingTimer = setInterval(fetchIncrementalData, 3000)
}

function stopPolling() {
  isPolling.value = false
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

function fetchIncrementalData() {
  getIncrementalData({
    ...buildPollingParams(),
    lastId: lastId.value
  }).then(response => {
    const rows = response.rows || []
    if (rows.length > 0) {
      dataList.value = [...rows.slice().reverse(), ...dataList.value].slice(0, 200)
      lastId.value = Math.max(...rows.map(item => item.id))
      total.value += rows.length
    }
  }).catch(() => {})
}

function handleDetail(row) {
  detailData.value = row
  detailVisible.value = true
}

onBeforeUnmount(() => {
  stopPolling()
})
</script>

<style scoped>
.polling-cursor {
  color: #909399;
  font-size: 12px;
  line-height: 32px;
}

.detail-json {
  margin: 0;
  white-space: pre-wrap;
}
</style>
