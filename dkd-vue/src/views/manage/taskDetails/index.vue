<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="工单ID" prop="taskId">
        <el-input
          v-model="queryParams.taskId"
          placeholder="请输入工单ID"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="货道编号" prop="channelCode">
        <el-input
          v-model="queryParams.channelCode"
          placeholder="请输入货道编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商品名称" prop="skuName">
        <el-input
          v-model="queryParams.skuName"
          placeholder="请输入商品名称"
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['manage:taskDetails:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['manage:taskDetails:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['manage:taskDetails:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport" v-hasPermi="['manage:taskDetails:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="taskDetailsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="详情ID" align="center" prop="detailsId" width="80" />
      <el-table-column label="工单ID" align="center" prop="taskId" width="80" />
      <el-table-column label="货道编号" align="center" prop="channelCode" />
      <el-table-column label="商品名称" align="center" prop="skuName" />
      <el-table-column label="商品图片" align="center" prop="skuImage" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.skuImage" :width="50" :height="50" />
        </template>
      </el-table-column>
      <el-table-column label="期望容量" align="center" prop="expectCapacity" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['manage:taskDetails:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['manage:taskDetails:remove']">删除</el-button>
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
      <el-form ref="taskDetailsRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="工单ID" prop="taskId">
          <el-input v-model="form.taskId" placeholder="请输入工单ID" />
        </el-form-item>
        <el-form-item label="货道编号" prop="channelCode">
          <el-input v-model="form.channelCode" placeholder="请输入货道编号" />
        </el-form-item>
        <el-form-item label="商品ID" prop="skuId">
          <el-input v-model="form.skuId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="商品名称" prop="skuName">
          <el-input v-model="form.skuName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="期望容量" prop="expectCapacity">
          <el-input v-model="form.expectCapacity" placeholder="请输入期望容量" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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

<script setup name="TaskDetails">
import { listTaskDetails, getTaskDetails, addTaskDetails, updateTaskDetails, delTaskDetails } from "@/api/manage/taskDetails";

const { proxy } = getCurrentInstance();

const taskDetailsList = ref([]);
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
    taskId: undefined,
    channelCode: undefined,
    skuName: undefined
  },
  rules: {
    taskId: [{ required: true, message: "工单ID不能为空", trigger: "blur" }],
    channelCode: [{ required: true, message: "货道编号不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listTaskDetails(queryParams.value).then(response => {
    taskDetailsList.value = response.rows;
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
    detailsId: undefined,
    taskId: undefined,
    channelCode: undefined,
    skuId: undefined,
    skuName: undefined,
    expectCapacity: undefined,
    remark: undefined
  };
  proxy.resetForm("taskDetailsRef");
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
  ids.value = selection.map(item => item.detailsId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加工单详情";
}

function handleUpdate(row) {
  reset();
  const detailsId = row.detailsId || ids.value;
  getTaskDetails(detailsId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改工单详情";
  });
}

function submitForm() {
  proxy.$refs["taskDetailsRef"].validate(valid => {
    if (valid) {
      if (form.value.detailsId != undefined) {
        updateTaskDetails(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addTaskDetails(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row) {
  const detailsIds = row.detailsId || ids.value;
  proxy.$modal.confirm('是否确认删除工单详情编号为"' + detailsIds + '"的数据项？').then(function() {
    return delTaskDetails(detailsIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

function handleExport() {
  proxy.download("manage/taskDetails/export", {
    ...queryParams.value
  }, `taskDetails_${new Date().getTime()}.xlsx`);
}

getList();
</script>
