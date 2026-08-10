<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="订单编号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单编号"
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
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="订单状态" clearable style="width: 200px">
          <el-option label="待支付" :value="0" />
          <el-option label="支付完成" :value="1" />
          <el-option label="出货成功" :value="2" />
          <el-option label="出货失败" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button plain icon="Download" @click="handleExport" v-hasPermi="['manage:order:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单编号" align="center" prop="orderNo" width="200" />
      <el-table-column label="设备编号" align="center" prop="innerCode" />
      <el-table-column label="商品名称" align="center" prop="skuName" />
      <el-table-column label="货道" align="center" prop="channelCode" width="80" />
      <el-table-column label="订单状态" align="center" prop="status">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="warning">待支付</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="primary">支付完成</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="success">出货成功</el-tag>
          <el-tag v-else-if="scope.row.status === 3" type="danger">出货失败</el-tag>
          <el-tag v-else-if="scope.row.status === 4" type="info">已取消</el-tag>
          <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="支付金额(元)" align="center" prop="amount">
        <template #default="scope">
          <span>{{ scope.row.amount ? (scope.row.amount / 100).toFixed(2) : '0.00' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="支付方式" align="center" prop="payType">
        <template #default="scope">
          <el-tag v-if="scope.row.payType === '1'" type="primary">支付宝</el-tag>
          <el-tag v-else-if="scope.row.payType === '2'" type="success">微信</el-tag>
          <span v-else>{{ scope.row.payType }}</span>
        </template>
      </el-table-column>
      <el-table-column label="点位地址" align="center" prop="addr" show-overflow-tooltip />
      <el-table-column label="区域" align="center" prop="regionName" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
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
  </div>
</template>

<script setup name="Order">
import { listOrder } from "@/api/manage/order";

const { proxy } = getCurrentInstance();

const orderList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const total = ref(0);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderNo: undefined,
    innerCode: undefined,
    status: undefined
  }
});

const { queryParams } = toRefs(data);

function getList() {
  loading.value = true;
  listOrder(queryParams.value).then(response => {
    orderList.value = response.rows;
    total.value = response.total;
  }).finally(() => {
    loading.value = false
  });
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
}

function handleExport() {
  proxy.download("manage/order/export", {
    ...queryParams.value
  }, `order_${new Date().getTime()}.xlsx`);
}

getList();
</script>
