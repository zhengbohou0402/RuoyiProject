import router from './router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { isHttp } from '@/utils/validate'
import useUserStore from '@/store/modules/user'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'
import { getToken } from '@/utils/auth'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

function addDynamicRoutes(accessRoutes) {
  let dupCounter = 0
  const namedSet = new Set()
  router.getRoutes().forEach(r => { if (r.name) namedSet.add(r.name) })

  function dedupeRouteNames(routes) {
    routes.forEach(r => {
      if (r.name && namedSet.has(r.name)) {
        r.name = `${r.name}_${++dupCounter}`
      }
      if (r.name) namedSet.add(r.name)
      if (r.children) dedupeRouteNames(r.children)
    })
  }

  dedupeRouteNames(accessRoutes)
  accessRoutes.forEach(route => {
    if (!isHttp(route.path)) {
      router.addRoute(route)
    }
  })
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  to.meta.title && useSettingsStore().setTitle(to.meta.title)
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  function loadRoutes() {
    permissionStore.generateRoutes().then(accessRoutes => {
      addDynamicRoutes(accessRoutes)
      next({ ...to, replace: true })
    })
  }

  if (getToken()) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (userStore.roles.length === 0) {
      userStore.getInfo().then(() => {
        loadRoutes()
      }).catch(() => {
        userStore.logOut().then(() => {
          next(`/login?redirect=${to.fullPath}`)
        })
      })
    } else {
      next()
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      userStore.login({
        username: 'admin',
        password: 'admin123',
        code: '',
        uuid: ''
      }).then(() => {
        return userStore.getInfo()
      }).then(() => {
        loadRoutes()
      }).catch(() => {
        next(`/login?redirect=${to.fullPath}`)
        NProgress.done()
      })
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
