<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="渠道号" prop="channelId">
        <el-select v-model="queryParams.channelId" placeholder="请选择渠道号" clearable filterable style="width: 200px">
          <el-option label="空白（全部）" value="" />
          <el-option v-for="item in channelOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="事件编码" prop="eventCode">
        <el-select v-model="queryParams.eventCode" placeholder="请选择事件编码" clearable filterable style="width: 200px">
          <el-option v-for="item in eventOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="数据等级" prop="dataLevel">
        <el-select v-model="queryParams.dataLevel" placeholder="请选择数据等级" clearable style="width: 200px">
          <el-option label="核心" value="1" />
          <el-option label="重要" value="2" />
          <el-option label="CORE" value="CORE" />
          <el-option label="IMPORTANT" value="IMPORTANT" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button class="add-button" icon="Plus" @click="handleAdd" v-hasPermi="['system:level:add']">新增</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="handleQuery" />
    </el-row>

    <el-alert
      v-if="!hasSearched && dataList.length === 0"
      class="mb8"
      title="请选择筛选条件后点击搜索，页面进入时不会自动加载数据。"
      type="info"
      :closable="false"
      show-icon
    />

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="渠道号" align="center" prop="channelId" min-width="140" show-overflow-tooltip />
      <el-table-column label="事件编码" align="center" prop="eventCode" min-width="180" show-overflow-tooltip />
      <el-table-column label="数据等级" align="center" prop="dataLevel" width="120">
        <template #default="scope">
          <LevelTag :level="scope.row.dataLevel" />
        </template>
      </el-table-column>
      <el-table-column label="创建者" align="center" prop="createBy" width="120" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新者" align="center" prop="updateBy" width="120" show-overflow-tooltip />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <div class="table-actions">
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:level:edit']">修改</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:level:remove']">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="dataLevelLabelRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="渠道号" prop="channelIds">
          <el-select v-model="form.channelIds" multiple filterable placeholder="请选择渠道号" style="width: 100%">
            <el-option v-for="item in channelOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="事件编码" prop="eventCodes">
          <el-select v-model="form.eventCodes" multiple filterable placeholder="请选择事件编码" style="width: 100%">
            <el-option v-for="item in eventOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据等级" prop="dataLevel">
          <el-select v-model="form.dataLevel" placeholder="请选择数据等级" style="width: 100%">
            <el-option label="核心" value="1" />
            <el-option label="重要" value="2" />
            <el-option label="CORE" value="CORE" />
            <el-option label="IMPORTANT" value="IMPORTANT" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="重复数据确认" v-model="confirmOpen" width="600px" append-to-body>
      <el-alert :title="confirmMsg" type="warning" :closable="false" show-icon style="margin-bottom: 15px" />
      <el-table :data="confirmList" border size="small">
        <el-table-column label="渠道号" align="center" prop="channelId" />
        <el-table-column label="事件编码" align="center" prop="eventCode" />
        <el-table-column label="数据等级" align="center" prop="dataLevel">
          <template #default="scope">
            <LevelTag :level="scope.row.dataLevel" />
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="warning" @click="submitForceUpdate">强制覆盖</el-button>
          <el-button @click="confirmOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="DataLevelLabel">
import { h } from "vue";
import { ElTag } from "element-plus";
import { listDataLevelLabel, addDataLevelLabel, updateDataLevelLabel, delDataLevelLabel, getDataLevelOptions } from "@/api/manage/dataLevelLabel";

const { proxy } = getCurrentInstance();

const LevelTag = {
  props: {
    level: {
      type: [String, Number],
      default: ""
    }
  },
  setup(props) {
    return () => {
      const normalized = String(props.level || "");
      const levelMap = {
        "1": { text: "核心", type: "danger" },
        CORE: { text: "核心", type: "danger" },
        "2": { text: "重要", type: "warning" },
        IMPORTANT: { text: "重要", type: "warning" }
      };
      const option = levelMap[normalized] || { text: normalized || "-", type: "info" };
      return h(ElTag, { class: "level-tag", type: option.type, effect: "light" }, () => option.text);
    };
  }
};

const dataList = ref([]);
const open = ref(false);
const confirmOpen = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const hasSearched = ref(false);
const ids = ref([]);
const total = ref(0);
const title = ref("");
const isUpdate = ref(false);
const confirmMsg = ref("");
const confirmList = ref([]);
const pendingForm = ref({});

const channelOptions = ref([]);
const eventOptions = ref([]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    channelId: undefined,
    eventCode: undefined,
    dataLevel: undefined
  },
  rules: {
    channelIds: [{ required: true, message: "请选择渠道号", trigger: "change" }],
    eventCodes: [{ required: true, message: "请选择事件编码", trigger: "change" }],
    dataLevel: [{ required: true, message: "请选择数据等级", trigger: "change" }]
  }
});

const { queryParams, form, rules } = toRefs(data);

function getOptions() {
  getDataLevelOptions().then(response => {
    const options = response.data || response;
    channelOptions.value = options?.channelIds || [];
    eventOptions.value = options?.eventCodes || [];
  });
}

function getList() {
  hasSearched.value = true;
  loading.value = true;
  listDataLevelLabel(queryParams.value).then(response => {
    dataList.value = response.rows || [];
    total.value = response.total || 0;
  }).finally(() => {
    loading.value = false;
  });
}

function refreshAfterMutation() {
  if (hasSearched.value) {
    getList();
  }
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    id: undefined,
    channelIds: [],
    eventCodes: [],
    dataLevel: undefined,
    forceUpdate: false
  };
  proxy.resetForm("dataLevelLabelRef");
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm("queryRef");
  queryParams.value.pageNum = 1;
  dataList.value = [];
  total.value = 0;
  hasSearched.value = false;
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
}

function handleAdd() {
  reset();
  isUpdate.value = false;
  open.value = true;
  title.value = "新增数据等级标注";
}

function handleUpdate(row) {
  reset();
  isUpdate.value = true;
  form.value = {
    id: row.id,
    channelId: row.channelId,
    eventCode: row.eventCode,
    channelIds: row.channelId ? [row.channelId] : [],
    eventCodes: row.eventCode ? [row.eventCode] : [],
    dataLevel: row.dataLevel,
    forceUpdate: false
  };
  open.value = true;
  title.value = "修改数据等级标注";
}

function submitForm() {
  proxy.$refs["dataLevelLabelRef"].validate(valid => {
    if (!valid) {
      return;
    }
    const submitData = { ...form.value };
    if (isUpdate.value) {
      updateDataLevelLabel(submitData).then(() => {
        proxy.$modal.msgSuccess("修改成功");
        open.value = false;
        refreshAfterMutation();
      });
      return;
    }

    addDataLevelLabel(submitData).then(response => {
      const res = response.data || response;
      if (res && res.needConfirm) {
        pendingForm.value = submitData;
        confirmMsg.value = res.msg || "存在重复数据，请确认是否覆盖";
        confirmList.value = res.existingList || [];
        open.value = false;
        confirmOpen.value = true;
      } else {
        proxy.$modal.msgSuccess("新增成功");
        open.value = false;
        refreshAfterMutation();
      }
    });
  });
}

function submitForceUpdate() {
  const submitData = { ...pendingForm.value, forceUpdate: true };
  addDataLevelLabel(submitData).then(() => {
    proxy.$modal.msgSuccess("新增成功，已覆盖重复数据");
    confirmOpen.value = false;
    refreshAfterMutation();
  });
}

function handleDelete(row) {
  proxy.$modal.confirm(`是否确认删除数据等级标注编号为 "${row.id}" 的数据项？`).then(function() {
    return delDataLevelLabel(row.id);
  }).then(() => {
    refreshAfterMutation();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

getOptions();
</script>

<style scoped>
.level-tag {
  min-width: 56px;
  justify-content: center;
  font-weight: 600;
}

.table-actions {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  white-space: nowrap;
}

.add-button {
  color: #303133;
  background: #ffffff;
  border-color: #dcdfe6;
}

.add-button:hover,
.add-button:focus {
  color: #409eff;
  background: #ecf5ff;
  border-color: #a0cfff;
}
</style>
