import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
import Layout from '@/layout'

export const devPreviewRoutes = import.meta.env.DEV ? [
  {
    path: '/manage',
    component: Layout,
    redirect: '/manage/vm',
    alwaysShow: true,
    meta: { title: '运营管理', icon: 'dashboard' },
    children: [
      { path: 'vm', component: () => import('@/views/manage/vm/index.vue'), name: 'DevPreviewVm', meta: { title: '设备管理', icon: '设备管理' } },
      { path: 'node', component: () => import('@/views/manage/node/index.vue'), name: 'DevPreviewNode', meta: { title: '点位管理', icon: '点位管理' } },
      { path: 'partner', component: () => import('@/views/manage/partner/index.vue'), name: 'DevPreviewPartner', meta: { title: '合作商管理', icon: 'peoples' } },
      { path: 'order', component: () => import('@/views/manage/order/index.vue'), name: 'DevPreviewOrder', meta: { title: '订单管理', icon: '订单管理' } },
      { path: 'task', component: () => import('@/views/manage/task/index.vue'), name: 'DevPreviewTask', meta: { title: '工单管理', icon: '工单管理' } },
      { path: 'job', component: () => import('@/views/manage/job/index.vue'), name: 'DevPreviewJob', meta: { title: '自动补货任务', icon: 'job' } }
    ]
  },
  {
    path: '/base',
    component: Layout,
    redirect: '/base/vmType',
    alwaysShow: true,
    meta: { title: '基础资料', icon: 'component' },
    children: [
      { path: 'vmType', component: () => import('@/views/manage/vmType/index.vue'), name: 'DevPreviewVmType', meta: { title: '设备类型管理', icon: 'tree-table' } },
      { path: 'sku', component: () => import('@/views/manage/sku/index.vue'), name: 'DevPreviewSku', meta: { title: '商品管理', icon: 'shopping' } },
      { path: 'skuClass', component: () => import('@/views/manage/skuClass/index.vue'), name: 'DevPreviewSkuClass', meta: { title: '商品类型', icon: 'shopping' } },
      { path: 'channel', component: () => import('@/views/manage/channel/index.vue'), name: 'DevPreviewChannel', meta: { title: '售货机货道', icon: 'list' } },
      { path: 'region', component: () => import('@/views/manage/region/index.vue'), name: 'DevPreviewRegion', meta: { title: '区域管理', icon: 'tree' } },
      { path: 'policy', component: () => import('@/views/manage/policy/index.vue'), name: 'DevPreviewPolicy', meta: { title: '策略管理', icon: 'skill' } },
      { path: 'emp', component: () => import('@/views/manage/emp/index.vue'), name: 'DevPreviewEmp', meta: { title: '人员列表', icon: 'people' } },
      { path: 'manageRole', component: () => import('@/views/manage/manageRole/index.vue'), name: 'DevPreviewManageRole', meta: { title: '工单角色', icon: 'peoples' } },
      { path: 'taskType', component: () => import('@/views/manage/taskType/index.vue'), name: 'DevPreviewTaskType', meta: { title: '工单类型', icon: 'dict' } },
      { path: 'taskDetails', component: () => import('@/views/manage/taskDetails/index.vue'), name: 'DevPreviewTaskDetails', meta: { title: '工单详情', icon: 'form' } }
    ]
  },
  {
    path: '/data',
    component: Layout,
    redirect: '/data/metaData',
    alwaysShow: true,
    meta: { title: '数据管理', icon: 'chart' },
    children: [
      { path: 'metaData', component: () => import('@/views/manage/metaData/index.vue'), name: 'DevPreviewMetaData', meta: { title: '元数据管理', icon: 'dict' } },
      { path: 'dataLevelLabel', component: () => import('@/views/manage/dataLevelLabel/index.vue'), name: 'DevPreviewDataLevelLabel', meta: { title: '数据等级标注', icon: 'tree-table' } },
      { path: 'userBehaviorTrack', component: () => import('@/views/manage/userBehaviorTrack/index.vue'), name: 'DevPreviewUserBehaviorTrack', meta: { title: '用户行为轨迹查询', icon: 'search' } },
      { path: 'realTimeData', component: () => import('@/views/manage/realTimeData/index.vue'), name: 'DevPreviewRealTimeData', meta: { title: '实时数据核查', icon: 'monitor' } },
      { path: 'behaviorLog', component: () => import('@/views/manage/behaviorLog/index.vue'), name: 'DevPreviewBehaviorLog', meta: { title: '行为日志', icon: 'log' } },
      { path: 'customizeDataManage', component: () => import('@/views/manage/customizeDataManage/index.vue'), name: 'DevPreviewCustomizeDataManage', meta: { title: '自定义数据管理', icon: 'data' } },
      { path: 'dataQualityConfig', component: () => import('@/views/manage/dataQualityConfig/index.vue'), name: 'DevPreviewDataQualityConfig', meta: { title: '数据质量配置', icon: 'skill' } }
    ]
  }
] : []

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: '/index',
        component: () => import('@/views/home/index'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  },
  ...devPreviewRoutes,
  {
    path: "/:pathMatch(.*)*",
    component: () => import('@/views/error/404'),
    hidden: true
  }
]

// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
});

export default router;
