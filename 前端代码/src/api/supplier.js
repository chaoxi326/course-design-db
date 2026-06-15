import request from './request'

export function getSuppliers() {
  return request.get('/api/suppliers')
}

export function saveSupplierBatch(data) {
  return request.post('/api/suppliers/batch', data)
}

export function updateSupplier(data) {
  return request.put('/api/suppliers', data)
}

export function deleteSupplier(sId) {
  return request.delete(`/api/suppliers/${sId}`)
}
