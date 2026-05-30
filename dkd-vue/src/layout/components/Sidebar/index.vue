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
      { path: 'sku', meta: { title: '商品管理', icon: '商品管理' } },
      { path: 'order', meta: { title: '订单管理', icon: '订单管理' } },
      { path: 'task', meta: { title: '工单管理', icon: '工单管理' } },
      { path: 'partner', meta: { title: '合作商管理', icon: 'peoples' } }
    ]
  },
  {
    path: '/base',
    alwaysShow: true,
    meta: { title: '基础资料', icon: 'component' },
    children: [
      { path: 'vmType', meta: { title: '设备类型', icon: 'tree-table' } },
      { path: 'skuClass', meta: { title: '商品类型', icon: 'shopping' } },
      { path: 'channel', meta: { title: '货道管理', icon: 'list' } },
      { path: 'emp', meta: { title: '人员管理', icon: 'people' } }
    ]
  },
  {
    path: '/data',
    alwaysShow: true,
    meta: { title: '数据管理', icon: 'chart' },
    children: [
      { path: 'realTimeData', meta: { title: '实时数据', icon: 'monitor' } },
      { path: 'metaData', meta: { title: '元数据', icon: 'dict' } },
      { path: 'dataQualityConfig', meta: { title: '质量配置', icon: 'skill' } },
      { path: 'behaviorLog', meta: { title: '行为日志', icon: 'log' } }
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
