import axios from 'axios'
import { Toast } from 'antd-mobile'
import { useAuthStore } from '../store'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('h5_token')
  if (token) config.headers.Authorization = 'Bearer ' + token
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) return res
    if (res.code === 401) {
      Toast.show({ content: '登录已过期，请重新登录' })
      useAuthStore.getState().logout()
      window.location.href = '/login'
      return Promise.reject(res)
    }
    Toast.show({ content: res.message || '请求失败' })
    return Promise.reject(res)
  },
  error => {
    if (error.response?.status === 401) {
      Toast.show({ content: '登录已过期，请重新登录' })
      useAuthStore.getState().logout()
      window.location.href = '/login'
    } else {
      Toast.show({ content: error.response?.data?.message || '网络错误' })
    }
    return Promise.reject(error)
  }
)

export default request
