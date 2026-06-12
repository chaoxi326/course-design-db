import request from './request'

export function getOrders() {
  return request.get('/api/purchase/orders')
}

export function getDetails() {
  return request.get('/api/purchase/details')
}

export function savePurchase(data) {
  return request.post('/api/purchase/batch', data)
}

export function updateOrder(data) {
  return request.put('/api/purchase/order', data)
}

export function updateDetail(data) {
  return request.put('/api/purchase/detail', data)
}

export function refreshOrderTotals(oId) {
  return request.put(`/api/purchase/refresh/${oId}`)
}

export function deletePurchase(oId) {
  return request.delete(`/api/purchase/${oId}`)
}
