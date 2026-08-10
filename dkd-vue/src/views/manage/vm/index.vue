<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="设备编号" prop="innerCode">
        <el-input
          v-model="queryParams.innerCode"
          placeholder="请输入设备编号"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备状态" prop="vmStatus">
        <el-select v-model="queryParams.vmStatus" placeholder="设备状态" clearable style="width: 200px">
          <el-option label="未投放" :value="0" />
          <el-option label="运营" :value="1" />
          <el-option label="撤机" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="合作商" prop="partnerId">
        <el-input
          v-model="queryParams.partnerId"
          placeholder="请输入合作商ID"
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
          v-hasPermi="['manage:vm:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['manage:vm:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['manage:vm:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['manage:vm:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="vmList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="设备编号" align="center" prop="innerCode" />
      <el-table-column label="设备型号" align="center" prop="vmTypeId" />
      <el-table-column label="详细地址" align="center" prop="addr" show-overflow-tooltip />
      <el-table-column label="设备状态" align="center" prop="vmStatus">
        <template #default="scope">
          <el-tag v-if="scope.row.vmStatus === 0" type="info">未投放</el-tag>
          <el-tag v-else-if="scope.row.vmStatus === 1" type="success">运营</el-tag>
          <el-tag v-else-if="scope.row.vmStatus === 3" type="danger">撤机</el-tag>
          <el-tag v-else type="warning">{{ scope.row.vmStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="运行状态" align="center" prop="runningStatus" />
      <el-table-column label="合作商ID" align="center" prop="partnerId" />
      <el-table-column label="区域ID" align="center" prop="regionId" />
      <el-table-column label="上次补货时间" align="center" prop="lastSupplyTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.lastSupplyTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['manage:vm:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['manage:vm:remove']">删除</el-button>
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

    <!-- 添加或修改设备对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="vmRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备编号" prop="innerCode">
          <el-input v-model="form.innerCode" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="设备型号" prop="vmTypeId">
          <el-input v-model="form.vmTypeId" placeholder="请输入设备型号ID" />
        </el-form-item>
        <el-form-item label="详细地址" prop="addr">
          <el-input v-model="form.addr" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="设备状态" prop="vmStatus">
          <el-select v-model="form.vmStatus" placeholder="请选择设备状态">
            <el-option label="未投放" :value="0" />
            <el-option label="运营" :value="1" />
            <el-option label="撤机" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="合作商ID" prop="partnerId">
          <el-input v-model="form.partnerId" placeholder="请输入合作商ID" />
        </el-form-item>
        <el-form-item label="区域ID" prop="regionId">
          <el-input v-model="form.regionId" placeholder="请输入区域ID" />
        </el-form-item>
        <el-form-item label="点位ID" prop="nodeId">
          <el-input v-model="form.nodeId" placeholder="请输入点位ID" />
        </el-form-item>
        <el-form-item label="设备容量" prop="channelMaxCapacity">
          <el-input v-model="form.channelMaxCapacity" placeholder="请输入设备容量" />
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

<script setup name="Vm">
import { listVm, getVm, addVm, updateVm, delVm } from "@/api/manage/vm";

const { proxy } = getCurrentInstance();

const vmList = ref([]);
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
    innerCode: undefined,
    vmStatus: undefined,
    partnerId: undefined
  },
  rules: {
    innerCode: [{ required: true, message: "设备编号不能为空", trigger: "blur" }],
    vmTypeId: [{ required: true, message: "设备型号不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询设备列表 */
function getList() {
  loading.value = true;
  listVm(queryParams.value).then(response => {
    vmList.value = response.rows;
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
    innerCode: undefined,
    channelMaxCapacity: undefined,
    nodeId: undefined,
    addr: undefined,
    lastSupplyTime: undefined,
    businessType: undefined,
    regionId: undefined,
    partnerId: undefined,
    vmTypeId: undefined,
    vmStatus: undefined,
    runningStatus: undefined,
    longitudes: undefined,
    latitude: undefined,
    clientId: undefined,
    policyId: undefined,
    remark: undefined
  };
  proxy.resetForm("vmRef");
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
  title.value = "添加设备";
}
/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getVm(id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改设备";
  });
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["vmRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateVm(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addVm(form.value).then(response => {
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
  const vmIds = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除设备编号为"' + vmIds + '"的数据项？').then(function() {
    return delVm(vmIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}
/** 导出按钮操作 */
function handleExport() {
  proxy.download("manage/vm/export", {
    ...queryParams.value
  }, `vm_${new Date().getTime()}.xlsx`);
}

getList();
</script>
