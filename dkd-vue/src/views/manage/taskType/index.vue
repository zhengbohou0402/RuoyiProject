<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="类型名称" prop="typeName">
        <el-input
          v-model="queryParams.typeName"
          placeholder="请输入类型名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['manage:taskType:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['manage:taskType:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['manage:taskType:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport" v-hasPermi="['manage:taskType:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="taskTypeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="类型ID" align="center" prop="typeId" />
      <el-table-column label="类型名称" align="center" prop="typeName" />
      <el-table-column label="工单类型" align="center" prop="type">
        <template #default="scope">
          <el-tag v-if="scope.row.type === 1" type="danger">维修工单</el-tag>
          <el-tag v-else-if="scope.row.type === 2" type="primary">运营工单</el-tag>
          <span v-else>{{ scope.row.type }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['manage:taskType:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['manage:taskType:remove']">删除</el-button>
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
      <el-form ref="taskTypeRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="工单类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择工单类型">
            <el-option label="维修工单" :value="1" />
            <el-option label="运营工单" :value="2" />
          </el-select>
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

<script setup name="TaskType">
import { listTaskType, getTaskType, addTaskType, updateTaskType, delTaskType } from "@/api/manage/taskType";

const { proxy } = getCurrentInstance();

const taskTypeList = ref([]);
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
    typeName: undefined
  },
  rules: {
    typeName: [{ required: true, message: "类型名称不能为空", trigger: "blur" }],
    type: [{ required: true, message: "工单类型不能为空", trigger: "change" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listTaskType(queryParams.value).then(response => {
    taskTypeList.value = response.rows;
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
    typeId: undefined,
    typeName: undefined,
    type: undefined
  };
  proxy.resetForm("taskTypeRef");
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
  ids.value = selection.map(item => item.typeId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加工单类型";
}

function handleUpdate(row) {
  reset();
  const typeId = row.typeId || ids.value;
  getTaskType(typeId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改工单类型";
  });
}

function submitForm() {
  proxy.$refs["taskTypeRef"].validate(valid => {
    if (valid) {
      if (form.value.typeId != undefined) {
        updateTaskType(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addTaskType(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row) {
  const typeIds = row.typeId || ids.value;
  proxy.$modal.confirm('是否确认删除工单类型编号为"' + typeIds + '"的数据项？').then(function() {
    return delTaskType(typeIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

function handleExport() {
  proxy.download("manage/taskType/export", {
    ...queryParams.value
  }, `taskType_${new Date().getTime()}.xlsx`);
}

getList();
</script>
