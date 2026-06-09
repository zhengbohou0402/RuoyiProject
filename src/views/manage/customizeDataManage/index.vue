<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="queryParams.ruleName" placeholder="请输入规则名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="数据源" prop="dataSourceId">
        <el-select v-model="queryParams.dataSourceId" placeholder="请选择数据源" clearable style="width: 200px">
          <el-option v-for="item in channelOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option label="默认" value="0" />
          <el-option label="运行中" value="1" />
          <el-option label="成功" value="2" />
          <el-option label="失败" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['manage:customizeData:add']">新增</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" border>
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="规则名称" align="center" prop="ruleName" min-width="150" show-overflow-tooltip />
      <el-table-column label="规则描述" align="center" prop="ruleDesc" min-width="150" show-overflow-tooltip />
      <el-table-column label="校验字段" align="center" prop="checkField" width="120" show-overflow-tooltip />
      <el-table-column label="数据源" align="center" prop="dataSourceId" width="120" />
      <el-table-column label="事件类型" align="center" prop="eventType" width="100" />
      <el-table-column label="统计开始" align="center" prop="statisticsTimeStart" width="170" />
      <el-table-column label="统计结束" align="center" prop="statisticsTimeEnd" width="170" />
      <el-table-column label="任务状态" align="center" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status === '1'" type="warning">运行中</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="success">成功</el-tag>
          <el-tag v-else-if="scope.row.status === '3'" type="danger">失败</el-tag>
          <el-tag v-else type="info">默认</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="耗时(ms)" align="center" prop="consumeTime" width="100" />
      <el-table-column label="创建人" align="center" prop="creator" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="170" />
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" @click="handleDetail(scope.row)">详情</el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['manage:customizeData:edit']">编辑</el-button>
          <el-button link type="success" @click="handleStart(scope.row)" v-if="scope.row.status !== '1'" v-hasPermi="['manage:customizeData:start']">启动</el-button>
          <el-button link type="warning" @click="handleStop(scope.row)" v-if="scope.row.status === '1'" v-hasPermi="['manage:customizeData:stop']">停止</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['manage:customizeData:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增对话框 -->
    <el-dialog title="新增自定义数据规则" v-model="addOpen" width="650px" append-to-body>
      <el-form ref="addRef" :model="addForm" :rules="addRules" label-width="120px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="addForm.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="规则描述" prop="ruleDesc">
          <el-input v-model="addForm.ruleDesc" type="textarea" :rows="2" placeholder="请输入规则描述" />
        </el-form-item>
        <el-form-item label="校验字段" prop="checkField">
          <el-input v-model="addForm.checkField" placeholder="请输入校验字段" />
        </el-form-item>
        <el-form-item label="数据源" prop="dataSourceId">
          <el-select v-model="addForm.dataSourceId" placeholder="请选择数据源">
            <el-option v-for="item in channelOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="事件类型">
          <el-input v-model="addForm.eventType" placeholder="请输入事件类型" />
        </el-form-item>
        <el-form-item label="营销活动码">
          <el-input v-model="addForm.marketCode" placeholder="请输入营销活动码" />
        </el-form-item>
        <el-form-item label="页面链接">
          <el-input v-model="addForm.pageUrl" placeholder="请输入页面链接" />
        </el-form-item>
        <el-form-item label="统计开始时间" prop="statisticsTimeStart">
          <el-date-picker v-model="addForm.statisticsTimeStart" type="datetime" placeholder="选择统计开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="统计结束时间" prop="statisticsTimeEnd">
          <el-date-picker v-model="addForm.statisticsTimeEnd" type="datetime" placeholder="选择统计结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="对比开始时间">
          <el-date-picker v-model="addForm.compareTimeStart" type="datetime" placeholder="选择对比开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="对比结束时间">
          <el-date-picker v-model="addForm.compareTimeEnd" type="datetime" placeholder="选择对比结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="分页大小" prop="pageSize">
          <el-input-number v-model="addForm.pageSize" :min="1" :max="10000" />
        </el-form-item>
        <el-form-item label="页码" prop="pageNum">
          <el-input-number v-model="addForm.pageNum" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAddForm">确 定</el-button>
          <el-button @click="addOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="数据详情" v-model="detailOpen" width="750px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="规则名称">{{ detailData.ruleName }}</el-descriptions-item>
        <el-descriptions-item label="规则描述" :span="2">{{ detailData.ruleDesc }}</el-descriptions-item>
        <el-descriptions-item label="校验字段">{{ detailData.checkField }}</el-descriptions-item>
        <el-descriptions-item label="数据源">{{ detailData.dataSourceId }}</el-descriptions-item>
        <el-descriptions-item label="事件类型">{{ detailData.eventType }}</el-descriptions-item>
        <el-descriptions-item label="营销活动码">{{ detailData.marketCode }}</el-descriptions-item>
        <el-descriptions-item label="页面链接" :span="2">{{ detailData.pageUrl }}</el-descriptions-item>
        <el-descriptions-item label="统计开始">{{ detailData.statisticsTimeStart }}</el-descriptions-item>
        <el-descriptions-item label="统计结束">{{ detailData.statisticsTimeEnd }}</el-descriptions-item>
        <el-descriptions-item label="对比开始">{{ detailData.compareTimeStart }}</el-descriptions-item>
        <el-descriptions-item label="对比结束">{{ detailData.compareTimeEnd }}</el-descriptions-item>
        <el-descriptions-item label="任务状态">
          <el-tag v-if="detailData.status === '1'" type="warning">运行中</el-tag>
          <el-tag v-else-if="detailData.status === '2'" type="success">成功</el-tag>
          <el-tag v-else-if="detailData.status === '3'" type="danger">失败</el-tag>
          <el-tag v-else type="info">默认</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ detailData.consumeTime }}</el-descriptions-item>
        <el-descriptions-item label="任务ID">{{ detailData.taskId }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detailData.creator }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="CH任务开始">{{ detailData.queryStartTime }}</el-descriptions-item>
        <el-descriptions-item label="CH任务结束">{{ detailData.queryEndTime }}</el-descriptions-item>
        <el-descriptions-item label="异常信息" :span="2">
          <span style="color:#f56c6c">{{ detailData.errorMsg || '无' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="数据内容" :span="2">
          <pre style="margin:0;white-space:pre-wrap;max-height:300px;overflow:auto;">{{ detailData.statisticsContent || '无' }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="CustomizeDataManage">
import { listCustomizeDataManage, getCustomizeDataManage, addCustomizeDataManage, updateCustomizeDataManage, delCustomizeDataManage, startTask, stopTask, queryChannelList } from '@/api/manage/customizeDataManage'

const { proxy } = getCurrentInstance()

const dataList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const total = ref(0)
const addOpen = ref(false)
const detailOpen = ref(false)
const detailData = ref({})
const channelOptions = ref([])

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    ruleName: undefined,
    dataSourceId: undefined,
    status: undefined
  },
  addForm: {},
  addRules: {
    ruleName: [{ required: true, message: '规则名称不能为空', trigger: 'blur' }],
    ruleDesc: [{ required: true, message: '规则描述不能为空', trigger: 'blur' }],
    checkField: [{ required: true, message: '校验字段不能为空', trigger: 'blur' }],
    dataSourceId: [{ required: true, message: '请选择数据源', trigger: 'change' }],
    statisticsTimeStart: [{ required: true, message: '请选择统计开始时间', trigger: 'change' }],
    statisticsTimeEnd: [{ required: true, message: '请选择统计结束时间', trigger: 'change' }],
    pageSize: [{ required: true, message: '请输入分页大小', trigger: 'blur' }],
    pageNum: [{ required: true, message: '请输入页码', trigger: 'blur' }]
  }
})

const { queryParams, addForm, addRules } = toRefs(data)

function getList() {
  loading.value = true
  listCustomizeDataManage(queryParams.value).then(response => {
    // 该接口返回 Map 格式，兼容处理
    dataList.value = response.rows || response.data?.rows || []
    total.value = response.total || response.data?.total || 0
  }).catch(() => {
  }).finally(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleAdd() {
  addForm.value = {
    ruleName: undefined,
    ruleDesc: undefined,
    checkField: undefined,
    dataSourceId: undefined,
    eventType: undefined,
    marketCode: undefined,
    pageUrl: undefined,
    statisticsTimeStart: undefined,
    statisticsTimeEnd: undefined,
    compareTimeStart: undefined,
    compareTimeEnd: undefined,
    pageSize: 100,
    pageNum: 1
  }
  addOpen.value = true
}

function submitAddForm() {
  proxy.$refs['addRef'].validate(valid => {
    if (valid) {
      const request = addForm.value.id ? updateCustomizeDataManage : addCustomizeDataManage
      request(addForm.value).then(() => {
        proxy.$modal.msgSuccess(addForm.value.id ? '修改成功' : '新增成功')
        addOpen.value = false
        getList()
      })
    }
  })
}

function handleDetail(row) {
  getCustomizeDataManage(row.id).then(response => {
    detailData.value = response.data || response
    detailOpen.value = true
  })
}

function handleUpdate(row) {
  getCustomizeDataManage(row.id).then(response => {
    addForm.value = response.data || response
    addOpen.value = true
  })
}

function handleStart(row) {
  proxy.$modal.confirm('是否确认启动规则"' + row.ruleName + '"的查询任务？').then(function () {
    return startTask(row.id)
  }).then(() => {
    proxy.$modal.msgSuccess('任务已启动')
    getList()
  }).catch(() => {})
}

function handleStop(row) {
  proxy.$modal.confirm('是否确认停止规则"' + row.ruleName + '"的查询任务？').then(function () {
    return stopTask(row.id)
  }).then(() => {
    proxy.$modal.msgSuccess('任务已停止')
    getList()
  }).catch(() => {})
}

function handleDelete(row) {
  proxy.$modal.confirm('是否确认删除规则"' + row.ruleName + '"？').then(function () {
    return delCustomizeDataManage(row.id)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function loadChannelOptions() {
  queryChannelList().then(response => {
    channelOptions.value = response.data || []
  }).catch(() => {})
}

loadChannelOptions()
getList()
</script>
