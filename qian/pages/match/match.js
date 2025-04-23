const app = getApp()

Page({
  data: {
    matches: [],
    loading: true
  },

  onLoad: function() {
    this.loadMatches()
  },

  onShow: function() {
    // 检查是否有新的匹配，如果有则刷新列表
    this.loadMatches()
  },

  loadMatches: function() {
    this.setData({
      loading: true
    })
    
    // 获取匹配列表
    wx.request({
      url: app.globalData.apiBaseUrl + '/users/matches',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: res => {
        if (res.data.success) {
          this.setData({
            matches: res.data.data,
            loading: false
          })
        } else {
          this.setData({
            matches: [],
            loading: false
          })
        }
      },
      fail: () => {
        // 演示数据，实际应该从服务器获取
        const mockData = [
          {
            id: 1,
            userId: 101,
            nickname: '小红',
            avatar: 'https://img.yzcdn.cn/vant/cat.jpeg',
            lastMessage: {
              content: '你好，很高兴认识你！',
              time: '10:30'
            },
            unreadCount: 2
          },
          {
            id: 2,
            userId: 102,
            nickname: '小明',
            avatar: 'https://img.yzcdn.cn/vant/dog.jpeg',
            lastMessage: {
              content: '你平时喜欢做什么？',
              time: '昨天'
            },
            unreadCount: 0
          }
        ]
        
        this.setData({
          matches: mockData,
          loading: false
        })
      }
    })
  },

  // 跳转到聊天页面
  gotoChat: function(e) {
    const matchId = e.currentTarget.dataset.id
    const userId = e.currentTarget.dataset.userid
    const nickname = e.currentTarget.dataset.nickname
    
    wx.navigateTo({
      url: `/pages/chat/chat?matchId=${matchId}&userId=${userId}&nickname=${nickname}`
    })
  },

  // 下拉刷新
  onPullDownRefresh: function() {
    this.loadMatches()
    wx.stopPullDownRefresh()
  }
}) 