const app = getApp()

Page({
  data: {
    matchId: null,
    userId: null,
    nickname: '',
    messages: [],
    inputValue: '',
    scrollTop: 0,
    socketOpen: false
  },

  onLoad: function(options) {
    // 获取路由参数
    const matchId = options.matchId
    const userId = options.userId
    const nickname = options.nickname
    
    this.setData({
      matchId,
      userId,
      nickname
    })
    
    // 设置导航栏标题
    wx.setNavigationBarTitle({
      title: nickname
    })
    
    // 加载聊天记录
    this.loadChatHistory()
    
    // 连接WebSocket
    this.connectSocket()
  },
  
  onUnload: function() {
    // 页面卸载时断开WebSocket连接
    if (this.data.socketOpen) {
      wx.closeSocket()
    }
  },
  
  // 连接WebSocket
  connectSocket: function() {
    const token = wx.getStorageSync('token')
    
    wx.connectSocket({
      url: 'wss://your-websocket-domain.com/chat',
      header: {
        'Authorization': 'Bearer ' + token
      }
    })
    
    wx.onSocketOpen(() => {
      console.log('WebSocket连接已打开')
      this.setData({
        socketOpen: true
      })
      
      // 发送加入房间消息
      this.sendSocketMessage({
        type: 'join',
        matchId: this.data.matchId
      })
    })
    
    wx.onSocketError((error) => {
      console.error('WebSocket连接错误', error)
      this.setData({
        socketOpen: false
      })
      
      wx.showToast({
        title: '聊天服务连接失败',
        icon: 'none'
      })
    })
    
    wx.onSocketClose(() => {
      console.log('WebSocket连接已关闭')
      this.setData({
        socketOpen: false
      })
    })
    
    wx.onSocketMessage((res) => {
      try {
        const data = JSON.parse(res.data)
        
        if (data.type === 'message') {
          // 收到新消息，添加到消息列表
          const newMessages = [...this.data.messages, data.message]
          this.setData({
            messages: newMessages
          }, () => {
            this.scrollToBottom()
          })
        }
      } catch (e) {
        console.error('解析消息失败', e)
      }
    })
  },
  
  // 发送WebSocket消息
  sendSocketMessage: function(data) {
    if (this.data.socketOpen) {
      wx.sendSocketMessage({
        data: JSON.stringify(data)
      })
    } else {
      wx.showToast({
        title: '聊天服务未连接',
        icon: 'none'
      })
    }
  },

  // 加载聊天记录
  loadChatHistory: function() {
    wx.request({
      url: app.globalData.apiBaseUrl + '/messages',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: {
        matchId: this.data.matchId
      },
      success: res => {
        if (res.data.success) {
          this.setData({
            messages: res.data.data
          }, () => {
            this.scrollToBottom()
          })
        }
      },
      fail: () => {
        // 模拟数据
        const mockData = [
          {
            id: 1,
            senderId: this.data.userId,
            content: '你好，很高兴认识你！',
            time: '10:30',
            isSelf: false
          },
          {
            id: 2,
            senderId: 'self',
            content: '你好！我也很高兴认识你',
            time: '10:32',
            isSelf: true
          },
          {
            id: 3,
            senderId: this.data.userId,
            content: '你平时喜欢做什么？',
            time: '10:35',
            isSelf: false
          }
        ]
        
        this.setData({
          messages: mockData
        }, () => {
          this.scrollToBottom()
        })
      }
    })
  },

  // 监听输入
  onInput: function(e) {
    this.setData({
      inputValue: e.detail.value
    })
  },

  // 发送消息
  sendMessage: function() {
    if (!this.data.inputValue.trim()) return
    
    const messageContent = this.data.inputValue
    
    // 构建消息对象
    const message = {
      id: Date.now(),
      senderId: 'self',
      content: messageContent,
      time: this.formatTime(new Date()),
      isSelf: true
    }
    
    // 清空输入框
    this.setData({
      inputValue: '',
      messages: [...this.data.messages, message]
    }, () => {
      this.scrollToBottom()
    })
    
    // 发送WebSocket消息
    this.sendSocketMessage({
      type: 'message',
      matchId: this.data.matchId,
      content: messageContent
    })
    
    // 发送消息到服务器
    wx.request({
      url: app.globalData.apiBaseUrl + '/messages',
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: {
        matchId: this.data.matchId,
        content: messageContent
      }
    })
  },

  // 滚动到底部
  scrollToBottom: function() {
    wx.createSelectorQuery()
      .select('#message-list')
      .boundingClientRect(rect => {
        if (rect) {
          this.setData({
            scrollTop: rect.height
          })
        }
      })
      .exec()
  },

  // 格式化时间
  formatTime: function(date) {
    const hour = date.getHours()
    const minute = date.getMinutes()
    
    return [hour, minute].map(this.formatNumber).join(':')
  },

  formatNumber: function(n) {
    n = n.toString()
    return n[1] ? n : '0' + n
  },

  // 查看用户详情
  viewUserDetail: function() {
    wx.navigateTo({
      url: '/pages/userDetail/userDetail?id=' + this.data.userId
    })
  }
}) 