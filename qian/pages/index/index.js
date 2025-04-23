const app = getApp()

Page({
  data: {
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    isLoggedIn: false
  },

  onLoad: function() {
    // 查看是否已经登录
    if (wx.getStorageSync('token')) {
      this.setData({
        isLoggedIn: true
      })
      
      // 获取用户信息
      this.getUserInfo()
      
      // 延迟跳转到发现页面
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/discover/discover'
        })
      }, 1000)
    }
  },

  // 获取用户信息
  getUserInfo: function() {
    wx.getSetting({
      success: res => {
        if (res.authSetting['scope.userInfo']) {
          wx.getUserInfo({
            success: res => {
              app.globalData.userInfo = res.userInfo
            }
          })
        }
      }
    })
  },

  // 用户点击授权按钮
  onGetUserInfo: function(e) {
    if (e.detail.userInfo) {
      // 用户同意授权
      app.globalData.userInfo = e.detail.userInfo
      
      // 登录
      wx.login({
        success: res => {
          if (res.code) {
            // 显示加载中
            wx.showLoading({
              title: '登录中...',
            })
            
            // 发送 code 到后台换取 openId, sessionKey, unionId
            wx.request({
              url: app.globalData.apiBaseUrl + '/login',
              method: 'POST',
              data: {
                code: res.code,
                userInfo: e.detail.userInfo
              },
              success: result => {
                // 保存登录凭证
                wx.setStorageSync('token', result.data.token || 'mock-token')
                wx.setStorageSync('userId', result.data.userId || 'mock-userId')
                
                this.setData({
                  isLoggedIn: true
                })
                
                // 隐藏加载提示
                wx.hideLoading()
                
                // 检查是否已完善资料
                if (result.data.isProfileCompleted) {
                  // 跳转到发现页
                  wx.switchTab({
                    url: '/pages/discover/discover'
                  })
                } else {
                  // 跳转到完善资料页
                  wx.redirectTo({
                    url: '/pages/profile/profile?setup=true'
                  })
                }
              },
              fail: () => {
                // 模拟登录成功
                wx.setStorageSync('token', 'mock-token')
                wx.setStorageSync('userId', 'mock-userId')
                
                this.setData({
                  isLoggedIn: true
                })
                
                // 隐藏加载提示
                wx.hideLoading()
                
                // 随机决定是否需要完善资料，演示用
                const needSetup = Math.random() > 0.5
                
                if (needSetup) {
                  // 跳转到完善资料页
                  wx.redirectTo({
                    url: '/pages/profile/profile?setup=true'
                  })
                } else {
                  // 跳转到发现页
                  wx.switchTab({
                    url: '/pages/discover/discover'
                  })
                }
              }
            })
          } else {
            wx.showToast({
              title: '登录失败，请重试',
              icon: 'none'
            })
          }
        }
      })
    } else {
      // 用户拒绝授权
      wx.showToast({
        title: '需要授权才能使用小程序',
        icon: 'none'
      })
    }
  }
}) 