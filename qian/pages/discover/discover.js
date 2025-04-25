const app = getApp()

Page({
  data: {
    userProfiles: [],
    currentProfile: null,
    currentIndex: 0,
    x: 0,
    y: 0,
    animation: {},
    loading: true
  },

  onLoad: function() {
    this.loadUserProfiles()
    this.animation = wx.createAnimation({
      duration: 300,
      timingFunction: 'ease'
    })
  },

  onShow: function() {
    // 如果需要刷新数据
    if (app.globalData.refreshDiscoverPage) {
      this.loadUserProfiles()
      app.globalData.refreshDiscoverPage = false
    }
  },

  loadUserProfiles: function() {
    this.setData({
      loading: true
    })
    
    // 调用API获取推荐用户列表
    wx.request({
      url: app.globalData.apiBaseUrl + '/users/recommend',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: {
        latitude: app.globalData.userLocation ? app.globalData.userLocation.latitude : null,
        longitude: app.globalData.userLocation ? app.globalData.userLocation.longitude : null
      },
      success: res => {
        // 检查数据结构是否正确
        if (res.data.data) {
          // 处理数据，确保所有需要的字段都存在
          const profiles = res.data.data.map(profile => {
            return {
              id: profile.id,
              nickname: profile.nickname || '无名',
              age: profile.age || '?',
              gender: profile.gender || '未知',
              // 确保至少有一张照片
              photos: Array.isArray(profile.photos) && profile.photos.length > 0 
                ? profile.photos 
                : [profile.avatarUrl || '/images/default-avatar.png'],
              // 默认头像
              avatarUrl: profile.avatarUrl || (Array.isArray(profile.photos) && profile.photos.length > 0 
                ? profile.photos[0] 
                : '/images/default-avatar.png'),
              distance: profile.distance != null ? profile.distance : '未知',
              // 确保标签是数组
              tags: Array.isArray(profile.tags) ? profile.tags : [],
              bio: profile.bio || '这个人很懒，什么都没留下'
            };
          });   
          if (profiles.length > 0) {
            this.setData({
              userProfiles: profiles,
              currentProfile: profiles[0],
              currentIndex: 0,
              loading: false
            });
          } else {
            this.setData({
              userProfiles: [],
              currentProfile: null,
              loading: false
            });
            console.log('No profiles returned from API');
          }
        } else {
          console.error('API response format error:', res.data);
          this.setData({
            userProfiles: [],
            currentProfile: null,
            loading: false
          });
          
          // 显示错误提示
          wx.showToast({
            title: '加载失败，请重试',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('API request failed:', err);
        
        // 显示错误提示
        wx.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        });
        
        // 演示数据，实际应该从服务器获取
        const mockData = [
          {
            id: 1,
            nickname: '小红',
            age: 25,
            gender: '女',
            avatarUrl: 'https://img.yzcdn.cn/vant/cat.jpeg',
            photos: [
              'https://img.yzcdn.cn/vant/cat.jpeg',
              'https://img.yzcdn.cn/vant/dog.jpeg'
            ],
            distance: 3.5,
            tags: ['旅行', '摄影', '美食'],
            bio: '喜欢旅行和摄影的达人，一起去看世界吧！'
          },
          {
            id: 2,
            nickname: '小明',
            age: 28,
            gender: '男',
            avatarUrl: 'https://img.yzcdn.cn/vant/dog.jpeg',
            photos: [
              'https://img.yzcdn.cn/vant/dog.jpeg',
              'https://img.yzcdn.cn/vant/cat.jpeg'
            ],
            distance: 5.2,
            tags: ['健身', '阅读', '电影'],
            bio: '健身达人，爱好阅读和看电影。'
          }
        ]
        
        this.setData({
          userProfiles: mockData,
          currentProfile: mockData[0],
          currentIndex: 0,
          loading: false
        });
      }
    })
  },

  touchStart: function(e) {
    this.startX = e.touches[0].clientX
    this.startY = e.touches[0].clientY
  },

  touchMove: function(e) {
    const moveX = e.touches[0].clientX - this.startX
    const moveY = e.touches[0].clientY - this.startY
    
    this.setData({
      x: moveX,
      y: moveY
    })
    
    const rotateValue = moveX / 10
    
    this.animation.translateX(moveX).translateY(moveY).rotate(rotateValue).step()
    this.setData({
      animation: this.animation.export()
    })
  },

  touchEnd: function(e) {
    const moveX = e.changedTouches[0].clientX - this.startX
    const moveY = e.changedTouches[0].clientY - this.startY
    
    // 重置位置
    this.setData({
      x: 0,
      y: 0
    })
    
    // 向右滑动超过150像素，表示喜欢
    if (moveX > 150) {
      this.likeProfile()
    } 
    // 向左滑动超过150像素，表示不喜欢
    else if (moveX < -150) {
      this.dislikeProfile()
    } 
    // 不足以触发喜欢或不喜欢，卡片回到原位
    else {
      this.animation.translateX(0).translateY(0).rotate(0).step()
      this.setData({
        animation: this.animation.export()
      })
    }
  },

  // 点击喜欢按钮
  onLike: function() {
    this.animation.translateX(500).opacity(0).rotate(30).step()
    this.setData({
      animation: this.animation.export()
    })
    
    setTimeout(() => {
      this.likeProfile()
    }, 300)
  },

  // 点击不喜欢按钮
  onDislike: function() {
    this.animation.translateX(-500).opacity(0).rotate(-30).step()
    this.setData({
      animation: this.animation.export()
    })
    
    setTimeout(() => {
      this.dislikeProfile()
    }, 300)
  },

  // 处理喜欢
  likeProfile: function() {
    if (!this.data.currentProfile) return
    
    const profileId = this.data.currentProfile.id
    const currentUserId = app.globalData.userId || 1
    
    // 加入到已喜欢列表
    app.globalData.likedProfiles.push(profileId)
    
    // 发送喜欢请求到服务器
    wx.request({
      url: app.globalData.apiBaseUrl + '/likes?userId=' + currentUserId + '&likedUserId=' + profileId + '&type=1',
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + (app.globalData.token || wx.getStorageSync('token')),
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      success: res => {
        // 如果是匹配
        if (res.data && res.data.code === "200" && res.data.isMatch) {
          wx.showToast({
            title: '匹配成功！',
            icon: 'success'
          })
          
          // 添加到匹配列表
          app.globalData.matches.push(res.data.matchInfo)
        }
      },
      fail: err => {
        console.error('Like request failed:', err)
        wx.showToast({
          title: '操作失败，请重试',
          icon: 'none'
        })
      }
    })
    
    this.nextProfile()
  },

  // 处理不喜欢
  dislikeProfile: function() {
    if (!this.data.currentProfile) return
    
    const profileId = this.data.currentProfile.id
    const currentUserId = app.globalData.userId || 1
    
    // 发送不喜欢请求到服务器
    wx.request({
      url: app.globalData.apiBaseUrl + '/likes?userId=' + currentUserId + '&likedUserId=' + profileId + '&type=0',
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + (app.globalData.token || wx.getStorageSync('token')),
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      success: res => {
        // 处理成功响应
      },
      fail: err => {
        console.error('Dislike request failed:', err)
      }
    })
    
    this.nextProfile()
  },

  // 切换到下一个用户
  nextProfile: function() {
    let nextIndex = this.data.currentIndex + 1
    
    // 重置动画
    this.animation.translateX(0).translateY(0).rotate(0).opacity(1).step({ duration: 0 })
    
    if (nextIndex >= this.data.userProfiles.length) {
      // 所有卡片都已滑完，重新加载
      this.loadUserProfiles()
    } else {
      this.setData({
        currentIndex: nextIndex,
        currentProfile: this.data.userProfiles[nextIndex],
        animation: this.animation.export()
      })
    }
  },

  // 查看用户详情
  viewUserDetail: function() {
    if (!this.data.currentProfile) return
    
    wx.navigateTo({
      url: '/pages/userDetail/userDetail?id=' + this.data.currentProfile.id
    })
  }
}) 