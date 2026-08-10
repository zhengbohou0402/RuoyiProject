<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="用户手机号" prop="userMobile">
        <el-input v-model="queryParams.userMobile" placeholder="请输入手机号" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="请求类型" prop="eventKey">
        <el-input v-model="queryParams.eventKey" placeholder="请输入请求类型" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="数据类型" prop="eventType">
        <el-input v-model="queryParams.eventType" placeholder="请输入数据类型" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item label="设备ID" prop="deviceId">
        <el-input v-model="queryParams.deviceId" placeholder="请输入设备ID" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['manage:behaviorLog:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['manage:behaviorLog:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['manage:behaviorLog:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport" v-hasPermi="['manage:behaviorLog:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button :type="isPolling ? 'danger' : 'success'" plain @click="togglePolling">
          {{ isPolling ? '停止轮询' : '增量轮询' }}
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-tag :type="isPolling ? 'success' : 'info'">{{ isPolling ? '轮询中...' : '已停止' }}</el-tag>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="logList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="用户手机号" align="center" prop="userMobile" width="130" />
      <el-table-column label="请求类型" align="center" prop="eventKey" width="120" show-overflow-tooltip />
      <el-table-column label="数据类型" align="center" prop="eventType" width="100" show-overflow-tooltip />
      <el-table-column label="设备ID" align="center" prop="deviceId" width="120" show-overflow-tooltip />
      <el-table-column label="平台" align="center" prop="platform" width="100" />
      <el-table-column label="系统信息" align="center" prop="browser" width="120" show-overflow-tooltip />
      <el-table-column label="客户端版本" align="center" prop="clientVersion" width="110" />
      <el-table-column label="域名" align="center" prop="domain" width="150" show-overflow-tooltip />
      <el-table-column label="IP地址" align="center" prop="ip" width="130" />
      <el-table-column label="客户端时间" align="center" prop="clientTime" width="180" />
      <el-table-column label="服务端时间" align="center" prop="eventTime" width="180" />
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" @click="handleDetail(scope.row)">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['manage:behaviorLog:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['manage:behaviorLog:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="logRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户手机号" prop="userMobile">
          <el-input v-model="form.userMobile" placeholder="请输入用户手机号" />
        </el-form-item>
        <el-form-item label="请求类型" prop="eventKey">
          <el-input v-model="form.eventKey" placeholder="请输入请求类型" />
        </el-form-item>
        <el-form-item label="数据类型" prop="eventType">
          <el-input v-model="form.eventType" placeholder="请输入数据类型" />
        </el-form-item>
        <el-form-item label="设备ID" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备ID" />
        </el-form-item>
        <el-form-item label="会话ID" prop="sessionId">
          <el-input v-model="form.sessionId" placeholder="请输入会话ID" />
        </el-form-item>
        <el-form-item label="平台" prop="platform">
          <el-input v-model="form.platform" placeholder="请输入平台" />
        </el-form-item>
        <el-form-item label="系统信息" prop="browser">
          <el-input v-model="form.browser" placeholder="请输入系统信息" />
        </el-form-item>
        <el-form-item label="客户端版本" prop="clientVersion">
          <el-input v-model="form.clientVersion" placeholder="请输入客户端版本" />
        </el-form-item>
        <el-form-item label="域名" prop="domain">
          <el-input v-model="form.domain" placeholder="请输入域名" />
        </el-form-item>
        <el-form-item label="IP地址" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入IP地址" />
        </el-form-item>
        <el-form-item label="SDK版本" prop="sdkVersion">
          <el-input v-model="form.sdkVersion" placeholder="请输入SDK版本" />
        </el-form-item>
        <el-form-item label="数据源ID" prop="dataSourceId">
          <el-input v-model="form.dataSourceId" placeholder="请输入数据源ID" />
        </el-form-item>
        <el-form-item label="属性" prop="attributes">
          <el-input v-model="form.attributes" type="textarea" :rows="3" placeholder="请输入属性(JSON格式)" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="日志详情" v-model="detailVisible" width="750px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="用户手机号">{{ detailData.userMobile }}</el-descriptions-item>
        <el-descriptions-item label="请求类型">{{ detailData.eventKey }}</el-descriptions-item>
        <el-descriptions-item label="数据类型">{{ detailData.eventType }}</el-descriptions-item>
        <el-descriptions-item label="设备ID">{{ detailData.deviceId }}</el-descriptions-item>
        <el-descriptions-item label="会话ID">{{ detailData.sessionId }}</el-descriptions-item>
        <el-descriptions-item label="平台">{{ detailData.platform }}</el-descriptions-item>
        <el-descriptions-item label="系统信息">{{ detailData.browser }}</el-descriptions-item>
        <el-descriptions-item label="客户端版本">{{ detailData.clientVersion }}</el-descriptions-item>
        <el-descriptions-item label="域名">{{ detailData.domain }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ip }}</el-descriptions-item>
        <el-descriptions-item label="SDK版本">{{ detailData.sdkVersion }}</el-descriptions-item>
        <el-descriptions-item label="数据源ID">{{ detailData.dataSourceId }}</el-descriptions-item>
        <el-descriptions-item label="客户端时间">{{ detailData.clientTime }}</el-descriptions-item>
        <el-descriptions-item label="服务端时间" :span="2">{{ detailData.eventTime }}</el-descriptions-item>
        <el-descriptions-item label="浏览器信息" :span="2">{{ detailData.userAgent }}</el-descriptions-item>
        <el-descriptions-item label="属性" :span="2">
          <pre style="margin:0;white-space:pre-wrap;">{{ detailData.attributes || '无' }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="BehaviorLog">
import { listBehaviorLog, getBehaviorLog, addBehaviorLog, updateBehaviorLog, delBehaviorLog, getIncrementalBehaviorLog } from '@/api/manage/behaviorLog'

const { proxy } = getCurrentInstance()

const logList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const isPolling = ref(false)
const detailVisible = ref(false)
const detailData = ref({})
let pollingTimer = null

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userMobile: undefined,
    eventKey: undefined,
    eventType: undefined,
    deviceId: undefined
  },
  rules: {
    userMobile: [{ required: true, message: '用户手机号不能为空', trigger: 'blur' }]
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listBehaviorLog(queryParams.value).then(response => {
    logList.value = response.rows
    total.value = response.total
  }).finally(() => {
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: undefined,
    userMobile: undefined,
    eventKey: undefined,
    eventType: undefined,
    deviceId: undefined,
    sessionId: undefined,
    platform: undefined,
    browser: undefined,
    clientVersion: undefined,
    domain: undefined,
    ip: undefined,
    sdkVersion: undefined,
    dataSourceId: undefined,
    attributes: undefined
  }
  proxy.resetForm('logRef')
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = '添加用户行为日志'
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value
  getBehaviorLog(id).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改用户行为日志'
  })
}

function submitForm() {
  proxy.$refs['logRef'].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateBehaviorLog(form.value).then(() => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        })
      } else {
        addBehaviorLog(form.value).then(() => {
          proxy.$modal.msgSuccess('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const logIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除用户行为日志编号为"' + logIds + '"的数据项？').then(function () {
    return delBehaviorLog(logIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function handleExport() {
  proxy.download('manage/behaviorLog/export', {
    ...queryParams.value
  }, `behaviorLog_${new Date().getTime()}.xlsx`)
}

function handleDetail(row) {
  detailData.value = row
  detailVisible.value = true
}

function togglePolling() {
  if (isPolling.value) {
    stopPolling()
  } else {
    startPolling()
  }
}

function startPolling() {
  if (!queryParams.value.userMobile) {
    proxy.$modal.msgWarning('请先输入手机号再开启轮询')
    return
  }
  isPolling.value = true
  pollingTimer = setInterval(() => {
    fetchIncrementalData()
  }, 3000)
}

function stopPolling() {
  isPolling.value = false
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

let lastQueryTime = null

function fetchIncrementalData() {
  const queryTime = lastQueryTime || new Date().toISOString().replace('T', ' ').substring(0, 23)
  getIncrementalBehaviorLog(queryParams.value.userMobile, queryTime).then(response => {
    if (response.data && response.data.dataList && response.data.dataList.length > 0) {
      logList.value = [...response.data.dataList, ...logList.value]
      total.value += response.data.dataList.length
    }
    if (response.data && response.data.queryEndTime) {
      lastQueryTime = response.data.queryEndTime
    }
  }).catch(() => {})
}

onBeforeUnmount(() => {
  stopPolling()
})

getList()
</script>
