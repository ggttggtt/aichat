const app = getApp()

Page({
  data: {
    userId: null,
    userDetail: null,
    loading: true,
    currentPhotoIndex: 0
  },

  onLoad: function(options) {
    const userId = options.id
    
    this.setData({
      userId
    })
    
    this.loadUserDetail()
  },

  // 加载用户详情
  loadUserDetail: function() {
    this.setData({
      loading: true
    })
    
    wx.request({
      url: app.globalData.apiBaseUrl + '/users/' + this.data.userId,
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: res => {
        if (res.data.success) {
          this.setData({
            userDetail: res.data.data,
            loading: false
          })
          
          // 设置导航栏标题
          wx.setNavigationBarTitle({
            title: res.data.data.nickname
          })
        } else {
          wx.showToast({
            title: '获取用户信息失败',
            icon: 'none'
          })
        }
      },
      fail: () => {
        // 模拟数据
        const mockData = {
          id: this.data.userId,
          nickname: '小红',
          age: 25,
          gender: '女',
          avatar: 'https://img.yzcdn.cn/vant/cat.jpeg',
          photos: [
            'https://img.yzcdn.cn/vant/cat.jpeg',
            'https://img.yzcdn.cn/vant/dog.jpeg',
            'https://img.yzcdn.cn/vant/cat.jpeg'
          ],
          distance: 3.5,
          tags: ['旅行', '摄影', '美食', '音乐', '电影'],
          bio: '喜欢旅行和摄影的达人，一起去看世界吧！热爱生活，享受每一天的阳光。',
          location: '北京市朝阳区',
          education: '北京大学',
          job: '摄影师',
          height: 165,
          weight: 50,
          constellation: '天秤座',
          interests: ['hiking', 'photography', 'cooking']
        }
        
        this.setData({
          userDetail: mockData,
          loading: false
        })
        
        // 设置导航栏标题
        wx.setNavigationBarTitle({
          title: mockData.nickname
        })
      }
    })
  },

  // 切换照片
  changePhoto: function(e) {
    const index = e.currentTarget.dataset.index
    this.setData({
      currentPhotoIndex: index
    })
  },

  // 照片滑动事件
  swiperChange: function(e) {
    this.setData({
      currentPhotoIndex: e.detail.current
    })
  },

  // 点击喜欢按钮
  onLike: function() {
    const userId = this.data.userId
    
    // 发送喜欢请求到服务器
    wx.request({
      url: app.globalData.apiBaseUrl + '/users/like',
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: {
        targetUserId: userId
      },
      success: res => {
        if (res.data.success) {
          if (res.data.isMatch) {
            wx.showToast({
              title: '匹配成功！',
              icon: 'success'
            })
            
            // 添加到匹配列表
            app.globalData.matches.push(res.data.matchInfo)
            
            // 返回上一页
            setTimeout(() => {
              wx.navigateBack()
            }, 1500)
          } else {
            wx.showToast({
              title: '已喜欢',
              icon: 'success'
            })
            
            // 添加到已喜欢列表
            app.globalData.likedProfiles.push(userId)
            
            // 返回上一页
            setTimeout(() => {
              wx.navigateBack()
            }, 1500)
          }
        }
      },
      fail: () => {
        wx.showToast({
          title: '操作成功',
          icon: 'success'
        })
        
        // 返回上一页
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      }
    })
  },

  // 点击拒绝按钮
  onDislike: function() {
    const userId = this.data.userId
    
    // 发送不喜欢请求到服务器
    wx.request({
      url: app.globalData.apiBaseUrl + '/users/dislike',
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: {
        targetUserId: userId
      },
      success: res => {
        if (res.data.success) {
          wx.showToast({
            title: '已忽略',
            icon: 'success'
          })
          
          // 返回上一页
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
        }
      },
      fail: () => {
        wx.showToast({
          title: '操作成功',
          icon: 'success'
        })
        
        // 返回上一页
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      }
    })
  },

  // 预览照片
  previewPhoto: function() {
    const currentPhoto = this.data.userDetail.photos[this.data.currentPhotoIndex]
    
    wx.previewImage({
      current: currentPhoto,
      urls: this.data.userDetail.photos
    })
  }
}) 