import { create } from 'zustand'
import { authApi, userApi } from '../api'

export const useAuthStore = create((set) => ({
  token: localStorage.getItem('h5_token') || '',
  userInfo: JSON.parse(localStorage.getItem('h5_user') || '{}'),
  cartCount: 0,

  setAuth: (token, userInfo) => {
    localStorage.setItem('h5_token', token)
    localStorage.setItem('h5_user', JSON.stringify(userInfo))
    set({ token, userInfo })
  },

  setCartCount: (count) => set({ cartCount: count }),

  fetchUserInfo: async () => {
    try {
      const res = await userApi.info()
      if (res.data) {
        localStorage.setItem('h5_user', JSON.stringify(res.data))
        set({ userInfo: res.data })
      }
    } catch (e) { /* ignore */ }
  },

  logout: async () => {
    try { await authApi.logout() } catch (e) { /* ignore */ }
    localStorage.removeItem('h5_token')
    localStorage.removeItem('h5_user')
    set({ token: '', userInfo: {}, cartCount: 0 })
  }
}))
