import request from './request'

// Auth
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  info: () => request.get('/auth/info'),
  logout: () => request.post('/auth/logout')
}

// Product
export const productApi = {
  list: (params) => request.get('/product/list', { params }),
  publicList: (params) => request.get('/public/product/list', { params }),
  detail: (id) => request.get(`/product/${id}`),
  save: (data) => request.post('/product', data),
  update: (data) => request.put(`/product/${data.id}`, data),
  updateStatus: (id, status) => request.put(`/product/${id}/status`, null, { params: { status } }),
  delete: (id) => request.delete(`/product/${id}`)
}

export const categoryApi = {
  tree: () => request.get('/product/category/tree'),
  save: (data) => request.post('/product/category', data),
  update: (data) => request.put(`/product/category/${data.id}`, data),
  delete: (id) => request.delete(`/product/category/${id}`)
}

export const skuApi = {
  listByProduct: (productId) => request.get(`/product/sku/list/${productId}`),
  save: (data) => request.post('/product/sku', data),
  delete: (id) => request.delete(`/product/sku/${id}`)
}

// Order
export const orderApi = {
  list: (params) => request.get('/order/list', { params }),
  detail: (id) => request.get(`/order/${id}`),
  ship: (id, data) => request.put(`/order/${id}/ship`, null, { params: data }),
  refund: (id) => request.put(`/order/${id}/refund`)
}

// User
export const userApi = {
  list: (params) => request.get('/user/list', { params }),
  detail: (id) => request.get(`/user/${id}`),
  update: (data) => request.put('/user', data),
  updateStatus: (id, status) => request.put(`/user/${id}/status`, null, { params: { status } }),
  delete: (id) => request.delete(`/user/${id}`)
}

// Points
export const pointsApi = {
  account: (userId) => request.get(`/points/account/${userId}`),
  logs: (params) => request.get(`/points/logs/${params.userId}`, { params })
}

// Coupon
export const couponApi = {
  list: (params) => request.get('/coupon/list', { params }),
  detail: (id) => request.get(`/coupon/${id}`),
  save: (data) => request.post('/coupon', data),
  delete: (id) => request.delete(`/coupon/${id}`),
  updateStatus: (id, status) => request.put(`/coupon/${id}/status`, null, { params: { status } })
}

// Marketing
export const marketingApi = {
  list: (params) => request.get('/marketing/list', { params }),
  detail: (id) => request.get(`/marketing/${id}`),
  save: (data) => request.post('/marketing', data),
  delete: (id) => request.delete(`/marketing/${id}`),
  updateStatus: (id, status) => request.put(`/marketing/${id}/status`, null, { params: { status } })
}

// System
export const sysAdminApi = {
  list: (params) => request.get('/system/admin/list', { params }),
  save: (data) => request.post('/system/admin', data),
  update: (data) => request.put('/system/admin', data),
  updateStatus: (id, status) => request.put(`/system/admin/${id}/status`, null, { params: { status } }),
  delete: (id) => request.delete(`/system/admin/${id}`),
  assignRoles: (id, roleIds) => request.put(`/system/admin/${id}/roles`, { roleIds }),
  getRoleIds: (id) => request.get(`/system/admin/${id}/roles`),
  resetPassword: (id, password) => request.put(`/system/admin/${id}/password`, { password })
}

export const sysRoleApi = {
  list: (params) => request.get('/system/role/list', { params }),
  all: () => request.get('/system/role/all'),
  save: (data) => request.post('/system/role', data),
  update: (data) => request.put('/system/role', data),
  delete: (id) => request.delete(`/system/role/${id}`),
  assignPermissions: (id, permissionIds) => request.put(`/system/role/${id}/permissions`, { permissionIds }),
  getPermissionIds: (id) => request.get(`/system/role/${id}/permissions`)
}

export const sysPermissionApi = {
  tree: () => request.get('/system/permission/tree'),
  save: (data) => request.post('/system/permission', data),
  update: (data) => request.put('/system/permission', data),
  delete: (id) => request.delete(`/system/permission/${id}`)
}

export const sysConfigApi = {
  list: (params) => request.get('/system/config/list', { params }),
  all: () => request.get('/system/config/all'),
  save: (data) => request.post('/system/config', data),
  delete: (id) => request.delete(`/system/config/${id}`)
}

export const sysLogApi = {
  list: (params) => request.get('/system/log/list', { params }),
  delete: (id) => request.delete(`/system/log/${id}`)
}

// Dashboard
export const dashboardApi = {
  stats: () => request.get('/dashboard/stats')
}
