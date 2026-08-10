<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="货道编号" prop="channelCode">
        <el-input
          v-model="queryParams.channelCode"
          placeholder="请输入货道编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="售货机编号" prop="innerCode">
        <el-input
          v-model="queryParams.innerCode"
          placeholder="请输入售货机编号"
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
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['manage:channel:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['manage:channel:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['manage:channel:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['manage:channel:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="channelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="货道ID" align="center" prop="id" />
      <el-table-column label="货道编号" align="center" prop="channelCode" />
      <el-table-column label="售货机编号" align="center" prop="innerCode" />
      <el-table-column label="商品ID" align="center" prop="skuId" />
      <el-table-column label="最大容量" align="center" prop="maxCapacity" />
      <el-table-column label="当前容量" align="center" prop="currentCapacity" />
      <el-table-column label="上次补货时间" align="center" prop="lastSupplyTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.lastSupplyTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['manage:channel:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['manage:channel:remove']">删除</el-button>
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

    <!-- 添加或修改货道对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="channelRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="货道编号" prop="channelCode">
          <el-input v-model="form.channelCode" placeholder="请输入货道编号" />
        </el-form-item>
        <el-form-item label="售货机编号" prop="innerCode">
          <el-input v-model="form.innerCode" placeholder="请输入售货机编号" />
        </el-form-item>
        <el-form-item label="商品ID" prop="skuId">
          <el-input v-model="form.skuId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="最大容量" prop="maxCapacity">
          <el-input-number v-model="form.maxCapacity" :min="1" controls-position="right" />
        </el-form-item>
        <el-form-item label="当前容量" prop="currentCapacity">
          <el-input-number v-model="form.currentCapacity" :min="0" controls-position="right" />
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

<script setup name="Channel">
import { listChannel, getChannel, addChannel, updateChannel, delChannel } from "@/api/manage/channel";

const { proxy } = getCurrentInstance();

const channelList = ref([]);
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
    channelCode: undefined,
    innerCode: undefined
  },
  rules: {
    channelCode: [{ required: true, message: "货道编号不能为空", trigger: "blur" }],
    innerCode: [{ required: true, message: "售货机编号不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询货道列表 */
function getList() {
  loading.value = true;
  listChannel(queryParams.value).then(response => {
    channelList.value = response.rows;
    total.value = response.total;
  }).finally(() => {
    loading.value = false
  });
}
/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}
/** 表单重置 */
function reset() {
  form.value = {
    id: undefined,
    channelCode: undefined,
    skuId: undefined,
    vmId: undefined,
    innerCode: undefined,
    maxCapacity: undefined,
    currentCapacity: undefined,
    lastSupplyTime: undefined,
    remark: undefined
  };
  proxy.resetForm("channelRef");
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}
/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加货道";
}
/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getChannel(id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改货道";
  });
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["channelRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateChannel(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addChannel(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}
/** 删除按钮操作 */
function handleDelete(row) {
  const channelIds = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除货道编号为"' + channelIds + '"的数据项？').then(function() {
    return delChannel(channelIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}
/** 导出按钮操作 */
function handleExport() {
  proxy.download("manage/channel/export", {
    ...queryParams.value
  }, `channel_${new Date().getTime()}.xlsx`);
}

getList();
</script>
