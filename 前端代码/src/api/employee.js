import request from './request'

export function login(data) {
  return request.post('/api/employees/login', data)
}

export function getEmployees() {
  return request.get('/api/employees')
}

export function saveEmployeeBatch(data) {
  return request.post('/api/employees/batch', data)
}

export function updateEmployee(data) {
  return request.put('/api/employees', data)
}

export function deleteEmployee(eId) {
  return request.delete(`/api/employees/${eId}`)
}
