<template>
  <div class="login-page">
    <section class="login-panel">
      <div class="brand">
        <img src="@/assets/logo/logo.png" alt="logo" />
        <div>
          <h1>Smart Cabinet</h1>
          <p>Operations management console</p>
        </div>
      </div>

      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            size="large"
            auto-complete="off"
            placeholder="Username"
          >
            <template #prefix><svg-icon icon-class="user" class="input-icon" /></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            size="large"
            type="password"
            auto-complete="off"
            placeholder="Password"
            show-password
            @keyup.enter="handleLogin"
          >
            <template #prefix><svg-icon icon-class="password" class="input-icon" /></template>
          </el-input>
        </el-form-item>

        <el-form-item v-if="captchaEnabled" prop="code" class="captcha-item">
          <el-input
            v-model="loginForm.code"
            size="large"
            auto-complete="off"
            placeholder="Code"
            @keyup.enter="handleLogin"
          >
            <template #prefix><svg-icon icon-class="validCode" class="input-icon" /></template>
          </el-input>
          <button type="button" class="captcha" @click="getCode">
            <img v-if="codeUrl" :src="codeUrl" alt="captcha" />
          </button>
        </el-form-item>

        <el-checkbox v-model="loginForm.rememberMe">Remember me</el-checkbox>

        <el-button :loading="loading" type="primary" size="large" class="submit" @click.prevent="handleLogin">
          {{ loading ? 'Signing in...' : 'Sign in' }}
        </el-button>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import Cookies from 'js-cookie'
import { encrypt, decrypt } from '@/utils/jsencrypt'
import useUserStore from '@/store/modules/user'
import { getCodeImg } from '@/api/login'

const router = useRouter()
const route = useRoute()
const { proxy } = getCurrentInstance()
const userStore = useUserStore()

const codeUrl = ref('')
const loading = ref(false)
const captchaEnabled = ref(true)

const loginForm = ref({
  username: '',
  password: '',
  rememberMe: false,
  code: '',
  uuid: ''
})

const loginRules = {
  username: [{ required: true, trigger: 'blur', message: 'Username is required' }],
  password: [{ required: true, trigger: 'blur', message: 'Password is required' }],
  code: [{ required: true, trigger: 'change', message: 'Code is required' }]
}

function getCookie() {
  const username = Cookies.get('username')
  const password = Cookies.get('password')
  const rememberMe = Cookies.get('rememberMe')
  loginForm.value = {
    username: username === undefined ? loginForm.value.username : username,
    password: password === undefined ? loginForm.value.password : decrypt(password),
    rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
    code: '',
    uuid: ''
  }
}

function getCode() {
  getCodeImg().then(res => {
    captchaEnabled.value = res.captchaEnabled === undefined ? true : res.captchaEnabled
    if (captchaEnabled.value) {
      codeUrl.value = 'data:image/gif;base64,' + res.img
      loginForm.value.uuid = res.uuid
    }
  }).catch(() => {
    captchaEnabled.value = false
  })
}

function handleLogin() {
  proxy.$refs.loginRef.validate(valid => {
    if (!valid) return
    loading.value = true

    if (loginForm.value.rememberMe) {
      Cookies.set('username', loginForm.value.username, { expires: 30 })
      Cookies.set('password', encrypt(loginForm.value.password), { expires: 30 })
      Cookies.set('rememberMe', loginForm.value.rememberMe, { expires: 30 })
    } else {
      Cookies.remove('username')
      Cookies.remove('password')
      Cookies.remove('rememberMe')
    }

    userStore.login(loginForm.value).then(() => {
      router.push({ path: route.query.redirect || '/' })
    }).catch(() => {
      loading.value = false
      if (captchaEnabled.value) getCode()
    })
  })
}

getCode()
getCookie()
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.88), rgba(248, 250, 252, 0.96)),
    #f8fafc;
}

.login-panel {
  width: min(420px, 100%);
  padding: 34px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;

  img {
    width: 38px;
    height: 38px;
    border-radius: 10px;
  }

  h1 {
    margin: 0;
    font-size: 22px;
    font-weight: 750;
    line-height: 28px;
    color: #0f172a;
    letter-spacing: 0;
  }

  p {
    margin: 2px 0 0;
    font-size: 13px;
    color: #64748b;
  }
}

.login-form {
  .input-icon {
    width: 15px;
    height: 15px;
    color: #94a3b8;
  }

  :deep(.el-form-item) {
    margin-bottom: 18px;
  }

  :deep(.el-input__wrapper) {
    height: 44px;
  }
}

.captcha-item {
  :deep(.el-form-item__content) {
    display: grid;
    grid-template-columns: 1fr 116px;
    gap: 10px;
  }
}

.captcha {
  height: 44px;
  padding: 0;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #f8fafc;
  cursor: pointer;
  overflow: hidden;

  img {
    display: block;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.submit {
  width: 100%;
  margin-top: 18px;
}
</style>
