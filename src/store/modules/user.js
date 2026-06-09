import { login, logout, getInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'
import defAva from '@/assets/images/profile.jpg'

function canUsePreviewAuth(error) {
  return import.meta.env.DEV && error?.response?.status === 404
}

const useUserStore = defineStore(
  'user',
  {
    state: () => ({
      token: getToken(),
      id: '',
      name: '',
      avatar: '',
      roles: [],
      permissions: []
    }),
    actions: {
      login(userInfo) {
        const username = userInfo.username.trim()
        const password = userInfo.password
        const code = userInfo.code
        const uuid = userInfo.uuid
        return new Promise((resolve, reject) => {
          login(username, password, code, uuid).then(res => {
            setToken(res.token)
            this.token = res.token
            resolve()
          }).catch(error => {
            if (canUsePreviewAuth(error)) {
              const token = 'local-preview-token'
              setToken(token)
              this.token = token
              resolve()
              return
            }
            reject(error)
          })
        })
      },
      getInfo() {
        return new Promise((resolve, reject) => {
          getInfo(true).then(res => {
            const user = res.user
            const avatar = (user.avatar === '' || user.avatar == null)
              ? defAva
              : import.meta.env.VITE_APP_BASE_API + user.avatar

            if (res.roles && res.roles.length > 0) {
              this.roles = res.roles
              this.permissions = res.permissions
            } else {
              this.roles = ['ROLE_DEFAULT']
            }
            this.id = user.userId
            this.name = user.userName
            this.avatar = avatar
            resolve(res)
          }).catch(error => {
            if (canUsePreviewAuth(error)) {
              this.mockUserInfo()
              resolve({
                user: {
                  userId: this.id,
                  userName: this.name,
                  avatar: ''
                },
                roles: this.roles,
                permissions: this.permissions
              })
              return
            }
            reject(error)
          })
        })
      },
      mockUserInfo() {
        this.id = 1
        this.name = '管理员'
        this.avatar = defAva
        this.roles = ['admin']
        this.permissions = ['*:*:*']
      },
      logOut() {
        return new Promise((resolve) => {
          logout(this.token).finally(() => {
            this.token = ''
            this.roles = []
            this.permissions = []
            removeToken()
            resolve()
          })
        })
      }
    }
  })

export default useUserStore
