import request from './request'

export function getAnalysis(data) {
  return request.post('/api/analysis', data || {}, {
    timeout: 120000  // AI 分析耗时较长，单独设置 2 分钟超时
  })
}
