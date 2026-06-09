<template>
  <div class="box sku-sale-collect">
    <div class="header">
      <div class="title">
        销售数据<span class="sub-title"
          >{{ datePickerFormat[0] }} ~ {{ datePickerFormat[1] }}</span
        >
      </div>
      <common-week-month-year @handleChange="handleRadioGroupSelChange" />
    </div>
    <div class="charts">
      <sku-sale-collect-line-chart
        id="amount-collect"
        title="销售额趋势图"
        :chart-option="lineChartOption"
      />
      <sku-sale-collect-bar-chart
        id="region-collect"
        title="销售额分布"
        :chart-option="barChartOption"
      />
    </div>
  </div>
</template>
<script setup>
import { onMounted } from 'vue';
import dayjs from 'dayjs';
import CommonWeekMonthYear from '@/components/week-month-year/index.vue';
import SkuSaleCollectLineChart from './sku-sale-collect-line-chart.vue';
import SkuSaleCollectBarChart from './sku-sale-collect-bar-chart.vue';
import { getSaleCollect } from '@/api/manage/dashboard'

const datePickerFormat = ref([]);
const radioGroupSel = ref('week');
const lineChartOption = ref({
  xAxisData: [],
  seriesData: [],
  yAxisName: '单位：元',
});
const barChartOption = ref({
  xAxisData: [],
  seriesData: [],
  yAxisName: '单位：元',
});

function loadSaleCollect() {
  const startTime = dayjs().startOf(radioGroupSel.value).format('YYYY-MM-DD HH:mm:ss');
  const endTime = dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss');
  getSaleCollect({ startTime, endTime }).then(response => {
    const data = response.data || {};
    if (data.lineChartOption) {
      lineChartOption.value = {
        xAxisData: data.lineChartOption.xAxisData || [],
        seriesData: data.lineChartOption.seriesData || [],
        yAxisName: '单位：元'
      };
    }
    if (data.barChartOption) {
      barChartOption.value = {
        xAxisData: data.barChartOption.xAxisData || [],
        seriesData: data.barChartOption.seriesData || [],
        yAxisName: '单位：元'
      };
    }
  }).catch(() => {})
}

onMounted(() => {
  handleRadioGroupSelChange(radioGroupSel.value);
});

const handleRadioGroupSelChange = (radioGroup) => {
  radioGroupSel.value = radioGroup;
  const startFormat = dayjs().startOf(radioGroupSel.value).format('YYYY.MM.DD');
  const endFormat = dayjs().endOf('day').format('YYYY.MM.DD');
  datePickerFormat.value = [startFormat, endFormat];
  loadSaleCollect();
};
</script>
<style lang="scss" scoped>
.sku-sale-collect {
  display: flex;
  flex-direction: column;
  // TODO: 临时解决方案，当前页面的横纵布局需要重新思考
  height: calc((100vh - 120px) * 0.4 - 20px);
  min-height: 352px;
  margin-top: 20px;
  background: #FFFFFF;
  border-radius: 20px;

  .charts {
    flex: 1;
    display: flex;
  }
}
</style>