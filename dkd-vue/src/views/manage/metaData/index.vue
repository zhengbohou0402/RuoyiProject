<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="字段名称" prop="fieldName">
        <el-input
          v-model="queryParams.fieldName"
          placeholder="请输入字段名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="字段标识" prop="fieldIdentification">
        <el-input
          v-model="queryParams.fieldIdentification"
          placeholder="请输入字段标识"
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['metaData:add']">新增</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="metaDataList">
      <el-table-column label="字段名称" align="center" prop="fieldName" show-overflow-tooltip />
      <el-table-column label="字段标识" align="center" prop="fieldIdentification" show-overflow-tooltip />
      <el-table-column label="适用范围" align="center" prop="applyScope" show-overflow-tooltip />
      <el-table-column label="是否必填" align="center" prop="isRequired" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.isRequired" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="维护时间" align="center" prop="maintenanceTime" width="180" />
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleDetail(scope.row)" v-hasPermi="['metaData:query']">详情</el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog title="元数据详情" v-model="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="字段名称">{{ detailForm.fieldName }}</el-descriptions-item>
        <el-descriptions-item label="字段标识">{{ detailForm.fieldIdentification }}</el-descriptions-item>
        <el-descriptions-item label="维护时间">{{ detailForm.maintenanceTime }}</el-descriptions-item>
        <el-descriptions-item label="适用范围">{{ detailForm.applyScope }}</el-descriptions-item>
        <el-descriptions-item label="适用H5">
          <el-tag v-if="detailForm.isScopeH5" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="适用原生">
          <el-tag v-if="detailForm.isScopeNative" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="适用小程序">
          <el-tag v-if="detailForm.isScopeMini" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="是否必传">
          <el-tag v-if="detailForm.isRequired" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最大长度">{{ detailForm.maxLength }}</el-descriptions-item>
        <el-descriptions-item label="包含数字">
          <el-tag v-if="detailForm.isContentDigit" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="包含字母">
          <el-tag v-if="detailForm.isContentLetter" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="包含汉字">
          <el-tag v-if="detailForm.isContentChinese" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="包含下划线">
          <el-tag v-if="detailForm.isContentUnderscore" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="业务口径说明" :span="2">{{ detailForm.businessDescription }}</el-descriptions-item>
        <el-descriptions-item label="技术口径说明" :span="2">{{ detailForm.technicalDescription }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 新增弹窗 -->
    <el-dialog title="新增元数据" v-model="open" width="700px" append-to-body>
      <el-form ref="metaDataRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="字段名称" prop="fieldName">
              <el-input v-model="form.fieldName" placeholder="请输入字段名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="字段标识" prop="fieldIdentification">
              <el-input v-model="form.fieldIdentification" placeholder="请输入字段标识" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="适用H5" prop="isScopeH5">
              <el-switch v-model="form.isScopeH5" :active-value="true" :inactive-value="false" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适用原生" prop="isScopeNative">
              <el-switch v-model="form.isScopeNative" :active-value="true" :inactive-value="false" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="适用小程序" prop="isScopeMini">
              <el-switch v-model="form.isScopeMini" :active-value="true" :inactive-value="false" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否必传" prop="isRequired">
              <el-switch v-model="form.isRequired" :active-value="true" :inactive-value="false" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最大长度" prop="maxLength">
              <el-input-number v-model="form.maxLength" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维护时间" prop="maintenanceTime">
              <el-input v-model="form.maintenanceTime" placeholder="请输入维护时间" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">内容校验规则</el-divider>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="包含数字">
              <el-switch v-model="form.isContentDigit" :active-value="true" :inactive-value="false" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="包含字母">
              <el-switch v-model="form.isContentLetter" :active-value="true" :inactive-value="false" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="包含汉字">
              <el-switch v-model="form.isContentChinese" :active-value="true" :inactive-value="false" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="包含下划线">
              <el-switch v-model="form.isContentUnderscore" :active-value="true" :inactive-value="false" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="业务口径说明" prop="businessDescription">
          <el-input v-model="form.businessDescription" type="textarea" :rows="3" placeholder="请输入业务口径说明" />
        </el-form-item>
        <el-form-item label="技术口径说明" prop="technicalDescription">
          <el-input v-model="form.technicalDescription" type="textarea" :rows="3" placeholder="请输入技术口径说明" />
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

<script setup name="MetaData">
import { listMetaData, getMetaData, addMetaData } from "@/api/manage/metaData";

const { proxy } = getCurrentInstance();

const metaDataList = ref([]);
const open = ref(false);
const detailOpen = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

const data = reactive({
  form: {},
  detailForm: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    fieldName: undefined,
    fieldIdentification: undefined
  },
  rules: {
    fieldName: [{ required: true, message: "字段名称不能为空", trigger: "blur" }],
    fieldIdentification: [{ required: true, message: "字段标识不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, detailForm, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listMetaData(queryParams.value).then(response => {
    metaDataList.value = response.rows;
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
    fieldName: undefined,
    fieldIdentification: undefined,
    maintenanceTime: undefined,
    applyScope: undefined,
    isScopeH5: false,
    isScopeNative: false,
    isScopeMini: false,
    isRequired: false,
    maxLength: undefined,
    isContentDigit: false,
    isContentLetter: false,
    isContentChinese: false,
    isContentUnderscore: false,
    businessDescription: undefined,
    technicalDescription: undefined
  };
  proxy.resetForm("metaDataRef");
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

function handleAdd() {
  reset();
  open.value = true;
}

function handleDetail(row) {
  getMetaData(row.id).then(response => {
    detailForm.value = response.data || response;
    detailOpen.value = true;
  });
}

function submitForm() {
  proxy.$refs["metaDataRef"].validate(valid => {
    if (valid) {
      addMetaData(form.value).then(response => {
        proxy.$modal.msgSuccess("新增成功");
        open.value = false;
        getList();
      });
    }
  });
}

getList();
</script>
