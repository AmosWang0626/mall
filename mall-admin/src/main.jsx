import React from 'react'
import ReactDOM from 'react-dom/client'
import { ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import App from './App'
import 'dayjs/locale/zh-cn'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <ConfigProvider locale={zhCN} theme={{ token: { colorPrimary: '#4096ff' } }}>
    <App />
  </ConfigProvider>
)
