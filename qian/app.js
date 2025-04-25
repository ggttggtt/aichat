App({
  globalData: {
    userInfo: null,
    token: null,
    apiBaseUrl: 'http://localhost:8888',
    userLocation: null,
    likedProfiles: [],
    matches: [],
    refreshDiscoverPage: false,
    refreshMatchPage: false
  },
  
  onLaunch: function() {
    // 测试后端连接
    console.log('小程序启动，测试后端连接:', this.globalData.apiBaseUrl);
    wx.request({
      url: this.globalData.apiBaseUrl + '/ping',
      method: 'GET',
      success: (res) => {
        console.log('后端连接测试成功:', res.data);
      },
      fail: (error) => {
        console.error('后端连接测试失败:', error);
        wx.showToast({
          title: '服务器连接失败',
          icon: 'none',
          duration: 2000
        });
      }
    });
    
    // 从本地存储中恢复数据
    this.globalData.token = wx.getStorageSync('token') || null
    this.globalData.userInfo = wx.getStorageSync('userInfo') || null
    
    // 获取用户位置
    this.getUserLocation()
  },
  
  // 获取用户位置
  getUserLocation: function() {
    wx.getLocation({
      type: 'gcj02',
      success: res => {
        this.globalData.userLocation = {
          latitude: res.latitude,
          longitude: res.longitude
        }
      }
    })
  },
  
  // 保存登录信息
  saveLoginInfo: function(token, userInfo) {
    this.globalData.token = token
    this.globalData.userInfo = userInfo
    
    // 保存到本地存储
    wx.setStorageSync('token', token)
    wx.setStorageSync('userInfo', userInfo)
  },
  
  // 清除登录信息
  clearLoginInfo: function() {
    this.globalData.token = null
    this.globalData.userInfo = null
    
    // 清除本地存储
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
  }
}) 