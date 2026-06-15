import request from './request'

export function getProducts() {
  return request.get('/api/products')
}

export function saveProductBatch(data) {
  return request.post('/api/products/batch', data)
}

export function updateProduct(data) {
  return request.put('/api/products', data)
}

export function deleteProduct(pId) {
  return request.delete(`/api/products/${pId}`)
}
