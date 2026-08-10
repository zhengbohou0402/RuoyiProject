<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="点位名称" prop="nodeName">
        <el-input
          v-model="queryParams.nodeName"
          placeholder="请输入点位名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="区域" prop="regionId">
        <el-input
          v-model="queryParams.regionId"
          placeholder="请输入区域ID"
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['manage:node:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['manage:node:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['manage:node:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport" v-hasPermi="['manage:node:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="nodeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="点位名称" align="center" prop="nodeName" />
      <el-table-column label="详细地址" align="center" prop="address" show-overflow-tooltip />
      <el-table-column label="商圈类型" align="center" prop="businessType" />
      <el-table-column label="所属区域" align="center" prop="region">
        <template #default="scope">
          <span>{{ scope.row.region ? scope.row.region.regionName : '' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="合作商" align="center" prop="partner">
        <template #default="scope">
          <span>{{ scope.row.partner ? scope.row.partner.partnerName : '' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备数量" align="center" prop="vmCount" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['manage:node:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['manage:node:remove']">删除</el-button>
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
      <el-form ref="nodeRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="点位名称" prop="nodeName">
          <el-input v-model="form.nodeName" placeholder="请输入点位名称" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="商圈类型" prop="businessType">
          <el-input v-model="form.businessType" placeholder="请输入商圈类型" />
        </el-form-item>
        <el-form-item label="区域ID" prop="regionId">
          <el-input v-model="form.regionId" placeholder="请输入区域ID" />
        </el-form-item>
        <el-form-item label="合作商ID" prop="partnerId">
          <el-input v-model="form.partnerId" placeholder="请输入合作商ID" />
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

<script setup name="Node">
import { listNode, getNode, addNode, updateNode, delNode } from "@/api/manage/node";

const { proxy } = getCurrentInstance();

const nodeList = ref([]);
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
    nodeName: undefined,
    regionId: undefined
  },
  rules: {
    nodeName: [{ required: true, message: "点位名称不能为空", trigger: "blur" }],
    address: [{ required: true, message: "详细地址不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listNode(queryParams.value).then(response => {
    nodeList.value = response.rows;
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
    id: undefined,
    nodeName: undefined,
    address: undefined,
    businessType: undefined,
    regionId: undefined,
    partnerId: undefined,
    remark: undefined
  };
  proxy.resetForm("nodeRef");
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
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加点位";
}

function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getNode(id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改点位";
  });
}

function submitForm() {
  proxy.$refs["nodeRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateNode(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addNode(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row) {
  const nodeIds = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除点位编号为"' + nodeIds + '"的数据项？').then(function() {
    return delNode(nodeIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

function handleExport() {
  proxy.download("manage/node/export", {
    ...queryParams.value
  }, `node_${new Date().getTime()}.xlsx`);
}

getList();
</script>
