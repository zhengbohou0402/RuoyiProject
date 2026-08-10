<template>
  <div class="app-container">
    <el-tabs v-model="activeTab">
      <!-- 规则列表 Tab -->
      <el-tab-pane label="规则列表" name="rules">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
          <el-form-item label="数据源标识" prop="datasourceId">
            <el-input v-model="queryParams.datasourceId" placeholder="请输入数据源标识" clearable style="width: 200px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="字段标识" prop="fieldIdentification">
            <el-input v-model="queryParams.fieldIdentification" placeholder="请输入字段标识" clearable style="width: 200px" />
          </el-form-item>
          <el-form-item label="字段名称" prop="fieldName">
            <el-input v-model="queryParams.fieldName" placeholder="请输入字段名称" clearable style="width: 200px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Setting" @click="openFieldConfig">字段批量配置</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['manage:qualityConfig:remove']">删除</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
        </el-row>

        <el-table v-loading="loading" :data="rulesList" @selection-change="handleSelectionChange" border>
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="ID" align="center" prop="id" width="80" />
          <el-table-column label="数据源标识" align="center" prop="datasourceId" width="150" show-overflow-tooltip />
          <el-table-column label="表名" align="center" prop="tableName" width="120" show-overflow-tooltip />
          <el-table-column label="字段标识" align="center" prop="fieldIdentification" width="150" show-overflow-tooltip />
          <el-table-column label="字段名称" align="center" prop="fieldName" width="120" />
          <el-table-column label="抽样条数" align="center" prop="sampleCount" width="100" />
          <el-table-column label="非空校验" align="center" width="90">
            <template #default="scope">
              <el-tag v-if="isYes(scope.row.isNotNull)" type="success">是</el-tag>
              <el-tag v-else type="info">否</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="是否加密" align="center" width="90">
            <template #default="scope">
              <el-tag v-if="isYes(scope.row.isEncrypted)" type="warning">是</el-tag>
              <el-tag v-else type="info">否</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="格式校验" align="center" prop="formatValidationType" width="100" />
          <el-table-column label="自定义正则" align="center" prop="customRegex" width="150" show-overflow-tooltip />
          <el-table-column label="长度范围" align="center" width="100">
            <template #default="scope">
              <span v-if="scope.row.lengthMin || scope.row.lengthMax">{{ scope.row.lengthMin || 0 }}-{{ scope.row.lengthMax || '∞' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="启用" align="center" width="80">
            <template #default="scope">
              <el-tag v-if="isYes(scope.row.isEnabled)" type="success">是</el-tag>
              <el-tag v-else type="info">否</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['manage:qualityConfig:edit']">修改</el-button>
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['manage:qualityConfig:remove']">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-tab-pane>

      <!-- 字段配置 Tab -->
      <el-tab-pane label="字段批量配置" name="fields">
        <el-form :inline="true" class="mb8">
          <el-form-item label="数据源">
            <el-select v-model="fieldConfig.datasourceIds" multiple placeholder="请选择数据源" style="width: 350px" @change="loadFieldList">
              <el-option v-for="item in datasourceOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="表名">
            <el-input v-model="fieldConfig.tableName" placeholder="请输入表名" clearable style="width: 200px" />
          </el-form-item>
          <el-form-item label="抽样条数">
            <el-input-number v-model="fieldConfig.sampleCount" :min="1" :max="100000" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Refresh" @click="loadFieldList">刷新字段</el-button>
            <el-button type="success" icon="Check" @click="handleSaveFieldConfig" :disabled="!fieldConfig.datasourceIds.length">保存配置</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="fieldLoading" :data="fieldList" border max-height="500">
          <el-table-column label="字段标识" align="center" prop="fieldIdentification" width="180" show-overflow-tooltip />
          <el-table-column label="字段名称" align="center" prop="fieldName" width="120" />
          <el-table-column label="数据类型" align="center" prop="fieldType" width="100" />
          <el-table-column label="非空" align="center" width="80">
            <template #default="scope">
              <el-checkbox v-model="scope.row.isNotNull" true-label="1" false-label="0" />
            </template>
          </el-table-column>
          <el-table-column label="加密" align="center" width="80">
            <template #default="scope">
              <el-checkbox v-model="scope.row.isEncrypted" true-label="1" false-label="0" />
            </template>
          </el-table-column>
          <el-table-column label="格式校验" align="center" width="130">
            <template #default="scope">
              <el-select v-model="scope.row.formatValidationType" placeholder="无" clearable size="small">
                <el-option label="日期" value="date" />
                <el-option label="URL" value="url" />
                <el-option label="无" value="none" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="包含数字" align="center" width="90">
            <template #default="scope">
              <el-checkbox v-model="scope.row.isContentNumber" true-label="1" false-label="0" />
            </template>
          </el-table-column>
          <el-table-column label="包含汉字" align="center" width="90">
            <template #default="scope">
              <el-checkbox v-model="scope.row.isContentChinese" true-label="1" false-label="0" />
            </template>
          </el-table-column>
          <el-table-column label="自定义正则" align="center" width="200">
            <template #default="scope">
              <el-input v-model="scope.row.customRegex" size="small" placeholder="正则表达式" />
            </template>
          </el-table-column>
          <el-table-column label="最小长度" align="center" width="100">
            <template #default="scope">
              <el-input-number v-model="scope.row.lengthMin" size="small" :min="0" controls-position="right" style="width:80px" />
            </template>
          </el-table-column>
          <el-table-column label="最大长度" align="center" width="100">
            <template #default="scope">
              <el-input-number v-model="scope.row.lengthMax" size="small" :min="0" controls-position="right" style="width:80px" />
            </template>
          </el-table-column>
          <el-table-column label="枚举值" align="center" width="180">
            <template #default="scope">
              <el-input v-model="scope.row.enumValues" size="small" placeholder="逗号分隔" />
            </template>
          </el-table-column>
          <el-table-column label="启用" align="center" width="80">
            <template #default="scope">
              <el-checkbox v-model="scope.row.isEnabled" true-label="1" false-label="0" />
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 修改规则对话框 -->
    <el-dialog title="修改规则配置" v-model="editOpen" width="700px" append-to-body>
      <el-form ref="ruleRef" :model="editForm" :rules="editRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据源标识" prop="datasourceId">
              <el-input v-model="editForm.datasourceId" placeholder="请输入数据源标识" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="表名" prop="tableName">
              <el-input v-model="editForm.tableName" placeholder="请输入表名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="字段标识" prop="fieldIdentification">
              <el-input v-model="editForm.fieldIdentification" placeholder="请输入字段标识" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="字段名称" prop="fieldName">
              <el-input v-model="editForm.fieldName" placeholder="请输入字段名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="抽样条数" prop="sampleCount">
              <el-input-number v-model="editForm.sampleCount" :min="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="非空校验" prop="isNotNull">
              <el-radio-group v-model="editForm.isNotNull">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否加密" prop="isEncrypted">
              <el-radio-group v-model="editForm.isEncrypted">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="格式校验类型">
              <el-select v-model="editForm.formatValidationType" placeholder="请选择" clearable>
                <el-option label="日期" value="date" />
                <el-option label="URL" value="url" />
                <el-option label="无" value="none" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日期格式">
              <el-input v-model="editForm.dateFormat" placeholder="如 yyyy-MM-dd" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="URL协议">
              <el-select v-model="editForm.urlProtocol" placeholder="请选择" clearable>
                <el-option label="HTTP" value="http" />
                <el-option label="HTTPS" value="https" />
                <el-option label="两者" value="both" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="包含数字">
              <el-checkbox v-model="editForm.isContentNumber" true-label="1" false-label="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="包含汉字">
              <el-checkbox v-model="editForm.isContentChinese" true-label="1" false-label="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="包含小写">
              <el-checkbox v-model="editForm.isContentLowercase" true-label="1" false-label="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="包含大写">
              <el-checkbox v-model="editForm.isContentUppercase" true-label="1" false-label="0" />
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="自定义正则">
              <el-input v-model="editForm.customRegex" placeholder="请输入正则表达式" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最小长度">
              <el-input-number v-model="editForm.lengthMin" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大长度">
              <el-input-number v-model="editForm.lengthMax" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="枚举值">
              <el-input v-model="editForm.enumValues" placeholder="逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否启用">
              <el-radio-group v-model="editForm.isEnabled">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitEditForm">确 定</el-button>
          <el-button @click="editOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="DataQualityConfig">
import { listRules, getRule, updateRule, delRules, getDatasourceOptions, getFieldList, saveRules } from '@/api/manage/dataQualityConfig'

const { proxy } = getCurrentInstance()

const activeTab = ref('rules')
const rulesList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const editOpen = ref(false)
const editForm = ref({})

const datasourceOptions = ref([])
const fieldList = ref([])
const fieldLoading = ref(false)
const fieldConfig = reactive({
  datasourceIds: [],
  tableName: '',
  sampleCount: 1000
})

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    datasourceId: undefined,
    fieldIdentification: undefined,
    fieldName: undefined
  },
  editRules: {
    datasourceId: [{ required: true, message: '数据源标识不能为空', trigger: 'blur' }],
    fieldIdentification: [{ required: true, message: '字段标识不能为空', trigger: 'blur' }],
    sampleCount: [{ required: true, message: '抽样条数不能为空', trigger: 'blur' }],
    isNotNull: [{ required: true, message: '请选择非空校验', trigger: 'change' }]
  }
})

const { queryParams, editRules } = toRefs(data)

function isYes(value) {
  return String(value) === '1'
}

function getList() {
  loading.value = true
  listRules(queryParams.value).then(response => {
    rulesList.value = response.rows
    total.value = response.total
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

function openFieldConfig() {
  activeTab.value = 'fields'
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  multiple.value = !selection.length
}

function handleUpdate(row) {
  getRule(row.id).then(response => {
    editForm.value = response.data
    editOpen.value = true
  })
}

function submitEditForm() {
  proxy.$refs['ruleRef'].validate(valid => {
    if (valid) {
      updateRule(editForm.value).then(() => {
        proxy.$modal.msgSuccess('修改成功')
        editOpen.value = false
        getList()
      })
    }
  })
}

function handleDelete(row) {
  const ruleIds = row.id || ids.value
  proxy.$modal.confirm('是否确认删除规则配置编号为"' + ruleIds + '"的数据项？').then(function () {
    return delRules(ruleIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

function loadDatasourceOptions() {
  getDatasourceOptions().then(response => {
    datasourceOptions.value = response.data || []
  })
}

function loadFieldList() {
  if (!fieldConfig.datasourceIds.length) return
  fieldLoading.value = true
  getFieldList({
    datasourceId: fieldConfig.datasourceIds[0],
    tableName: fieldConfig.tableName
  }).then(response => {
    fieldList.value = (response.data || []).map(item => ({
      ...item,
      id: item.id || undefined,
      isNotNull: item.isNotNull || '0',
      isEncrypted: item.isEncrypted || '0',
      formatValidationType: item.formatValidationType || 'none',
      isContentNumber: item.isContentNumber || '0',
      isContentChinese: item.isContentChinese || '0',
      isContentLowercase: item.isContentLowercase || '0',
      isContentUppercase: item.isContentUppercase || '0',
      customRegex: item.customRegex || '',
      lengthMin: item.lengthMin || undefined,
      lengthMax: item.lengthMax || undefined,
      enumValues: item.enumValues || '',
      isEnabled: item.isEnabled != null ? item.isEnabled : '1'
    }))
  }).finally(() => {
    fieldLoading.value = false
  })
}

function handleSaveFieldConfig() {
  if (!fieldConfig.datasourceIds.length) {
    proxy.$modal.msgWarning('请先选择数据源')
    return
  }
  const payload = {
    datasourceIds: fieldConfig.datasourceIds,
    tableName: fieldConfig.tableName,
    sampleCount: fieldConfig.sampleCount,
    configs: fieldList.value.map(item => ({
      id: item.id,
      datasourceId: item.datasourceId,
      tableName: item.tableName,
      fieldIdentification: item.fieldIdentification,
      fieldName: item.fieldName,
      fieldType: item.fieldType,
      isNotNull: item.isNotNull || '0',
      isEncrypted: item.isEncrypted || '0',
      formatValidationType: item.formatValidationType,
      dateFormat: item.dateFormat,
      urlProtocol: item.urlProtocol,
      isContentNumber: item.isContentNumber || '0',
      isContentChinese: item.isContentChinese || '0',
      isContentLowercase: item.isContentLowercase || '0',
      isContentUppercase: item.isContentUppercase || '0',
      customRegex: item.customRegex,
      lengthMin: item.lengthMin,
      lengthMax: item.lengthMax,
      enumValues: item.enumValues,
      isEnabled: item.isEnabled != null ? item.isEnabled : '1'
    }))
  }
  saveRules(payload).then(() => {
    proxy.$modal.msgSuccess('保存成功')
    getList()
  })
}

loadDatasourceOptions()
getList()
</script>
