<template>
  <div class="app-container home">
    <div class="page-heading">
      <div>
        <h1>Operations</h1>
        <p>Live vending machine tasks, sales, and equipment health.</p>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="18">
        <el-row :gutter="16">
          <el-col :xs="24" :md="13">
            <home-user-task-stats />
          </el-col>
          <el-col :xs="24" :md="11">
            <home-sku-sale-stats />
          </el-col>
        </el-row>
        <sku-sale-collect-chart />
      </el-col>
      <el-col :xs="24" :lg="6">
        <sku-sale-rank-chart />
      </el-col>
    </el-row>
    <el-row :gutter="16" class="home-row">
      <el-col :xs="24" :lg="14">
        <partner-node-collect-chart />
      </el-col>
      <el-col :xs="24" :lg="10">
        <abnormal-equipment-table />
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import HomeUserTaskStats from './components/home-user-task-stats.vue';
import HomeSkuSaleStats from './components/home-sku-sale-stats.vue';
import SkuSaleRankChart from './components/sku-sale-rank-chart.vue';
import SkuSaleCollectChart from './components/sku-sale-collect-chart.vue';
import PartnerNodeCollectChart from './components/partner-node-collect-chart.vue';
import AbnormalEquipmentTable from './components/abnormal-equipment-table.vue'
</script>

<style scoped lang="scss">
.home {
  .page-heading {
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    margin-bottom: 18px;

    h1 {
      margin: 0;
      font-size: 24px;
      font-weight: 700;
      line-height: 32px;
      color: #0f172a;
      letter-spacing: 0;
    }

    p {
      margin: 4px 0 0;
      font-size: 13px;
      line-height: 20px;
      color: #64748b;
    }
  }

  .home-row {
    margin-top: 16px;
  }

  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 13px;
  color: #475569;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
  :deep(.box) {
    padding: 18px;
    border-radius: 10px;
    background: #fff;
    border: 1px solid #e2e8f0;
    box-shadow: none;
    .title,
    .header {
      display: flex;
      align-items: center;
      .el-icon{
        cursor: pointer;
      }
    }
    .title {
      align-items: center;
      flex: 1;
      font-size: 14px;
      font-weight: 650;
      color: #0f172a;
    }
    .sub-title {
      margin-left: 10px;
      font-size: 12px;
      font-weight: 400;
      color: #64748b;
    }
  }
  .chart {
    position: relative;
    display: inline-block;
    width: 50%;
    height: 100%;
  }
  .sku-sale-rank {
    flex-direction: column;
    height: calc(60vh - 72px);
    min-height: 520px;
    background: #fff;
    border-radius: 10px;
    border: 1px solid #e2e8f0;
  }

  @media (max-width: 992px) {
    .el-col {
      margin-bottom: 16px;
    }

    .sku-sale-rank {
      min-height: 360px;
    }
  }
}
</style>
