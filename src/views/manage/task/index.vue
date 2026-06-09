<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="工单编号" prop="taskCode">
        <el-input
          v-model="queryParams.taskCode"
          placeholder="请输入工单编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备编号" prop="innerCode">
        <el-input
          v-model="queryParams.innerCode"
          placeholder="请输入设备编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工单状态" prop="taskStatus">
        <el-select v-model="queryParams.taskStatus" placeholder="工单状态" clearable style="width: 200px">
          <el-option label="待办" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['manage:task:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['manage:task:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport" v-hasPermi="['manage:task:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="taskList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="工单编号" align="center" prop="taskCode" />
      <el-table-column label="设备编号" align="center" prop="innerCode" />
      <el-table-column label="工单状态" align="center" prop="taskStatus">
        <template #default="scope">
          <el-tag v-if="scope.row.taskStatus === 0" type="warning">待办</el-tag>
          <el-tag v-else-if="scope.row.taskStatus === 1" type="primary">进行中</el-tag>
          <el-tag v-else-if="scope.row.taskStatus === 2" type="success">已完成</el-tag>
          <el-tag v-else-if="scope.row.taskStatus === 3" type="info">已取消</el-tag>
          <el-tag v-else type="info">{{ scope.row.taskStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建类型" align="center" prop="createType">
        <template #default="scope">
          <el-tag v-if="scope.row.createType === 0" type="info">自动</el-tag>
          <el-tag v-else-if="scope.row.createType === 1" type="primary">手动</el-tag>
          <span v-else>{{ scope.row.createType }}</span>
        </template>
      </el-table-column>
      <el-table-column label="工单类型" align="center" prop="taskType">
        <template #default="scope">
          <span>{{ scope.row.taskType ? scope.row.taskType.typeName : '' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="执行人" align="center" prop="userName" />
      <el-table-column label="地址" align="center" prop="addr" show-overflow-tooltip />
      <el-table-column label="备注" align="center" prop="desc" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['manage:task:edit']" v-if="scope.row.taskStatus !== 2 && scope.row.taskStatus !== 3">修改</el-button>
          <el-button link type="primary" icon="CircleClose" @click="handleCancel(scope.row)" v-hasPermi="['manage:task:edit']" v-if="scope.row.taskStatus === 0 || scope.row.taskStatus === 1">取消</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['manage:task:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="taskRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备编号" prop="innerCode">
          <el-input v-model="form.innerCode" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="工单类型" prop="productTypeId">
          <el-input v-model="form.productTypeId" placeholder="请输入工单类型ID" />
        </el-form-item>
        <el-form-item label="创建类型" prop="createType">
          <el-radio-group v-model="form.createType">
            <el-radio :value="0">自动</el-radio>
            <el-radio :value="1">手动</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="desc">
          <el-input v-model="form.desc" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Task">
import { listTask, getTask, addTask, updateTask, delTask, cancelTask } from "@/api/manage/task";

const { proxy } = getCurrentInstance();

const taskList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    taskCode: undefined,
    innerCode: undefined,
    taskStatus: undefined
  },
  rules: {
    innerCode: [{ required: true, message: "设备编号不能为空", trigger: "blur" }],
    productTypeId: [{ required: true, message: "工单类型不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listTask(queryParams.value).then(response => {
    taskList.value = response.rows;
    total.value = response.total;
  }).finally(() => {
    loading.value = false
  });
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    taskId: undefined,
    taskCode: undefined,
    taskStatus: undefined,
    createType: 1,
    innerCode: undefined,
    userId: undefined,
    regionId: undefined,
    desc: undefined,
    productTypeId: undefined,
    addr: undefined
  };
  proxy.resetForm("taskRef");
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.taskId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加工单";
}

function handleUpdate(row) {
  reset();
  const taskId = row.taskId || ids.value;
  getTask(taskId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改工单";
  });
}

function submitForm() {
  proxy.$refs["taskRef"].validate(valid => {
    if (valid) {
      if (form.value.taskId != undefined) {
        updateTask(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addTask(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row) {
  const taskIds = row.taskId || ids.value;
  proxy.$modal.confirm('是否确认删除工单编号为"' + taskIds + '"的数据项？').then(function() {
    return delTask(taskIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

function handleCancel(row) {
  proxy.$modal.confirm('是否确认取消工单"' + row.taskCode + '"？').then(function() {
    return cancelTask({ taskId: row.taskId });
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("取消成功");
  }).catch(() => {});
}

function handleExport() {
  proxy.download("manage/task/export", {
    ...queryParams.value
  }, `task_${new Date().getTime()}.xlsx`);
}

getList();
</script>
