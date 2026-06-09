<template>
  <div :class="{ 'has-logo': showLogo }" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar :class="sideTheme" wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground"
        :text-color="sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
        :unique-opened="true"
        :active-text-color="theme"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path + index"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/assets/styles/variables.module.scss'
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

const devPreviewSidebarRoutes = [
  {
    path: '/manage',
    alwaysShow: true,
    meta: { title: '运营管理', icon: 'dashboard' },
    children: [
      { path: 'vm', meta: { title: '设备管理', icon: '设备管理' } },
      { path: 'node', meta: { title: '点位管理', icon: '点位管理' } },
      { path: 'partner', meta: { title: '合作商管理', icon: 'peoples' } },
      { path: 'order', meta: { title: '订单管理', icon: '订单管理' } },
      { path: 'task', meta: { title: '工单管理', icon: '工单管理' } },
      { path: 'job', meta: { title: '自动补货任务', icon: 'job' } }
    ]
  },
  {
    path: '/base',
    alwaysShow: true,
    meta: { title: '基础资料', icon: 'component' },
    children: [
      { path: 'vmType', meta: { title: '设备类型管理', icon: 'tree-table' } },
      { path: 'sku', meta: { title: '商品管理', icon: 'shopping' } },
      { path: 'skuClass', meta: { title: '商品类型', icon: 'shopping' } },
      { path: 'channel', meta: { title: '售货机货道', icon: 'list' } },
      { path: 'region', meta: { title: '区域管理', icon: 'tree' } },
      { path: 'policy', meta: { title: '策略管理', icon: 'skill' } },
      { path: 'emp', meta: { title: '人员列表', icon: 'people' } },
      { path: 'manageRole', meta: { title: '工单角色', icon: 'peoples' } },
      { path: 'taskType', meta: { title: '工单类型', icon: 'dict' } },
      { path: 'taskDetails', meta: { title: '工单详情', icon: 'form' } }
    ]
  },
  {
    path: '/data',
    alwaysShow: true,
    meta: { title: '数据管理', icon: 'chart' },
    children: [
      { path: 'metaData', meta: { title: '元数据管理', icon: 'dict' } },
      { path: 'dataLevelLabel', meta: { title: '数据等级标注', icon: 'tree-table' } },
      { path: 'userBehaviorTrack', meta: { title: '用户行为轨迹查询', icon: 'search' } },
      { path: 'realTimeData', meta: { title: '实时数据核查', icon: 'monitor' } },
      { path: 'behaviorLog', meta: { title: '行为日志', icon: 'log' } },
      { path: 'customizeDataManage', meta: { title: '自定义数据管理', icon: 'data' } },
      { path: 'dataQualityConfig', meta: { title: '数据质量配置', icon: 'skill' } }
    ]
  }
]

const route = useRoute();
const appStore = useAppStore()
const settingsStore = useSettingsStore()
const permissionStore = usePermissionStore()

const sidebarRouters = computed(() => {
  const routes = permissionStore.sidebarRouters || []
  if (!import.meta.env.DEV) {
    return routes
  }
  const missingPreviewRoutes = devPreviewSidebarRoutes.filter(item => {
    return !routes.some(route => !route.hidden && route.meta?.title === item.meta.title)
  })
  return routes.concat(missingPreviewRoutes)
});
const showLogo = computed(() => settingsStore.sidebarLogo);
const sideTheme = computed(() => settingsStore.sideTheme);
const theme = computed(() => settingsStore.theme);
const isCollapse = computed(() => !appStore.sidebar.opened);

const activeMenu = computed(() => {
  const { meta, path } = route;
  // if set path, the sidebar will highlight the path you set
  if (meta.activeMenu) {
    return meta.activeMenu;
  }
  return path;
})

</script>
