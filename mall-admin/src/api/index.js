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
  list: (params) => request.get('/admin/product/list', { params }),
  publicList: (params) => request.get('/public/product/list', { params }),
  detail: (id) => request.get(`/admin/product/${id}`),
  save: (data) => request.post('/admin/product', data),
  update: (data) => request.put(`/admin/product/${data.id}`, data),
  updateStatus: (id, status) => request.put(`/admin/product/${id}/status`, null, { params: { status } }),
  delete: (id) => request.delete(`/admin/product/${id}`)
}

export const categoryApi = {
  tree: () => request.get('/admin/product/category/tree/all'),
  save: (data) => request.post('/admin/product/category', data),
  update: (data) => request.put(`/admin/product/category/${data.id}`, data),
  delete: (id) => request.delete(`/admin/product/category/${id}`)
}

export const skuApi = {
  listByProduct: (productId) => request.get(`/product/sku/list/${productId}`),
  save: (data) => request.post('/admin/product/sku', data),
  delete: (id) => request.delete(`/admin/product/sku/${id}`)
}

// Order
export const orderApi = {
  list: (params) => request.get('/admin/order/list', { params }),
  detail: (id) => request.get(`/admin/order/${id}`),
  ship: (id, data) => request.put(`/admin/order/${id}/ship`, null, { params: data }),
  refund: (id) => request.put(`/admin/order/${id}/refund`)
}

// User
export const userApi = {
  list: (params) => request.get('/admin/user/list', { params }),
  detail: (id) => request.get(`/admin/user/${id}`),
  update: (data) => request.put('/admin/user', data),
  updateStatus: (id, status) => request.put(`/admin/user/${id}/status`, null, { params: { status } }),
  delete: (id) => request.delete(`/admin/user/${id}`)
}

// Points
export const pointsApi = {
  account: (userId) => request.get(`/admin/points/account/${userId}`),
  logs: (params) => request.get(`/admin/points/logs/${params.userId}`, { params })
}

// Coupon
export const couponApi = {
  list: (params) => request.get('/admin/coupon/list', { params }),
  detail: (id) => request.get(`/admin/coupon/${id}`),
  save: (data) => request.post('/admin/coupon', data),
  delete: (id) => request.delete(`/admin/coupon/${id}`),
  updateStatus: (id, status) => request.put(`/admin/coupon/${id}/status`, null, { params: { status } })
}

// Marketing
export const marketingApi = {
  list: (params) => request.get('/admin/marketing/list', { params }),
  detail: (id) => request.get(`/admin/marketing/${id}`),
  save: (data) => request.post('/admin/marketing', data),
  delete: (id) => request.delete(`/admin/marketing/${id}`),
  updateStatus: (id, status) => request.put(`/admin/marketing/${id}/status`, null, { params: { status } })
}

// Prize (奖池)
export const prizeApi = {
  list: (params) => request.get('/prize/admin/list', { params }),
  detail: (id) => request.get(`/prize/admin/${id}`),
  types: () => request.get('/prize/admin/types'),
  save: (data) => request.post('/prize/admin', data),
  delete: (id) => request.delete(`/prize/admin/${id}`),
  updateStatus: (id, status) => request.put(`/prize/admin/${id}/status`, null, { params: { status } })
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
