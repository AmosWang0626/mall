import { create } from 'zustand'

export const useAuthStore = create((set) => ({
  token: localStorage.getItem('token') || '',
  userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
  permissions: JSON.parse(localStorage.getItem('permissions') || '[]'),
  setAuth: (token, userInfo) => {
    localStorage.setItem('token', token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
    set({ token, userInfo })
  },
  setPermissions: (permissions) => {
    localStorage.setItem('permissions', JSON.stringify(permissions))
    set({ permissions })
  },
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('permissions')
    set({ token: '', userInfo: {}, permissions: [] })
  }
}))
