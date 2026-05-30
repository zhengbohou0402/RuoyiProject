import axios from 'axios'
import { ElNotification, ElMessage, ElLoading } from 'element-plus'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import { tansParams, blobValidate } from '@/utils/ruoyi'
import cache from '@/plugins/cache'
import { saveAs } from 'file-saver'

let downloadLoadingInstance

export const isRelogin = { show: false }

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 10000
})

service.interceptors.request.use(config => {
  const isToken = (config.headers || {}).isToken === false
  const isRepeatSubmit = (config.headers || {}).repeatSubmit === false

  if (getToken() && !isToken) {
    config.headers.Authorization = `Bearer ${getToken()}`
  }

  if (config.method === 'get' && config.params) {
    let url = `${config.url}?${tansParams(config.params)}`
    url = url.slice(0, -1)
    config.params = {}
    config.url = url
  }

  if (!isRepeatSubmit && (config.method === 'post' || config.method === 'put')) {
    const requestObj = {
      url: config.url,
      data: typeof config.data === 'object' ? JSON.stringify(config.data) : config.data,
      time: Date.now()
    }
    const requestSize = Object.keys(JSON.stringify(requestObj)).length
    const limitSize = 5 * 1024 * 1024

    if (requestSize >= limitSize) {
      console.warn(`[${config.url}]: 请求数据大小超过 5M，跳过重复提交校验。`)
      return config
    }

    const sessionObj = cache.session.getJSON('sessionObj')
    if (sessionObj === undefined || sessionObj === null || sessionObj === '') {
      cache.session.setJSON('sessionObj', requestObj)
    } else {
      const interval = 1000
      if (
        sessionObj.data === requestObj.data &&
        requestObj.time - sessionObj.time < interval &&
        sessionObj.url === requestObj.url
      ) {
        const message = '数据正在处理，请勿重复提交'
        console.warn(`[${sessionObj.url}]: ${message}`)
        return Promise.reject(new Error(message))
      }
      cache.session.setJSON('sessionObj', requestObj)
    }
  }

  return config
}, error => Promise.reject(error))

service.interceptors.response.use(res => {
  const code = res.data.code || 200
  const msg = errorCode[code] || res.data.msg || errorCode.default
  const isSilent = res.config.headers && res.config.headers.silent === true

  if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer') {
    return res.data
  }

  if (code === 401) {
    return Promise.reject(new Error(res.data.msg || '未授权'))
  }
  if (code === 500) {
    if (!isSilent) ElMessage({ message: msg, type: 'error' })
    return Promise.reject(new Error(msg))
  }
  if (code === 601) {
    if (!isSilent) ElMessage({ message: msg, type: 'warning' })
    return Promise.reject(new Error(msg))
  }
  if (code !== 200) {
    if (!isSilent) ElNotification.error({ title: msg })
    return Promise.reject(new Error(msg))
  }

  return Promise.resolve(res.data)
}, error => {
  let { message } = error
  const isSilent = error.config && error.config.headers && error.config.headers.silent === true

  if (message === 'Network Error') {
    message = '后端接口连接异常'
  } else if (message.includes('timeout')) {
    message = '系统接口请求超时'
  } else if (message.includes('Request failed with status code')) {
    message = `系统接口 ${message.substr(message.length - 3)} 异常`
  }

  if (!isSilent) ElMessage({ message, type: 'error', duration: 5 * 1000 })
  return Promise.reject(error)
})

export function download(url, params, filename, config) {
  downloadLoadingInstance = ElLoading.service({
    text: '正在下载数据，请稍候',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  return service.post(url, params, {
    transformRequest: [(params) => tansParams(params)],
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    responseType: 'blob',
    ...config
  }).then(async (data) => {
    const isBlob = blobValidate(data)
    if (isBlob) {
      const blob = new Blob([data])
      saveAs(blob, filename)
    } else {
      const resText = await data.text()
      const rspObj = JSON.parse(resText)
      const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode.default
      ElMessage.error(errMsg)
    }
    downloadLoadingInstance.close()
  }).catch((error) => {
    console.error(error)
    ElMessage.error('下载文件出现错误，请联系管理员')
    downloadLoadingInstance.close()
  })
}

export default service
