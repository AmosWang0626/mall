import request from './request'

// ===== Auth =====
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  info: () => request.get('/auth/info'),
  logout: () => request.post('/auth/logout')
}

// ===== Product (public) =====
export const productApi = {
  list: (params) => request.get('/public/product/list', { params }),
  detail: (id) => request.get(`/public/product/${id}`)
}

// ===== Cart =====
export const cartApi = {
  list: () => request.get('/cart/list'),
  add: (data) => request.post('/cart', data),
  updateQuantity: (id, quantity) => request.put(`/cart/${id}/quantity`, null, { params: { quantity } }),
  updateSelected: (selected) => request.put('/cart/selected', null, { params: { selected } }),
  remove: (id) => request.delete(`/cart/${id}`),
  clear: () => request.delete('/cart/clear'),
  count: () => request.get('/cart/count')
}

// ===== Order =====
export const orderApi = {
  create: (data) => request.post('/order', data),
  myList: (params) => request.get('/order/my', { params }),
  detail: (id) => request.get(`/order/${id}`),
  pay: (id, payType) => request.put(`/order/${id}/pay`, null, { params: { payType } }),
  cancel: (id) => request.put(`/order/${id}/cancel`),
  receive: (id) => request.put(`/order/${id}/receive`)
}

// ===== Coupon =====
export const couponApi = {
  available: () => request.get('/coupon/available'),
  mine: (status) => request.get('/coupon/mine', { params: { status } }),
  receive: (couponId) => request.post(`/coupon/receive/${couponId}`)
}

// ===== Points =====
export const pointsApi = {
  account: () => request.get('/points/account'),
  sign: () => request.post('/points/sign'),
  logs: (params) => request.get('/points/logs', { params })
}

// ===== User =====
export const userApi = {
  info: () => request.get('/user/info'),
  updateProfile: (data) => request.put('/user/profile', data),
  changePassword: (data) => request.put('/user/password', data)
}

// ===== Address =====
export const addressApi = {
  list: () => request.get('/user/address/list'),
  save: (data) => request.post('/user/address', data),
  delete: (id) => request.delete(`/user/address/${id}`),
  getDefault: () => request.get('/user/address/default')
}
