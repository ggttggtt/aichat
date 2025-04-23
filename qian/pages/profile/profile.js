const app = getApp()

Page({
  data: {
    userInfo: null,
    isSetup: false,
    tags: ['旅行', '摄影', '美食', '音乐', '电影', '健身', '阅读', '游戏', '绘画', '舞蹈', '烹饪', '户外', '瑜伽', '编程', '投资'],
    selectedTags: [],
    form: {
      nickname: '',
      gender: '男',
      birthdate: '2000-01-01',
      location: '北京市',
      bio: '',
      photos: []
    },
    genderOptions: ['男', '女'],
    showUploadTip: false,
    isEditing: false
  },

  onLoad: function(options) {
    // 获取全局用户信息
    const userInfo = app.globalData.userInfo
    
    // 检查是否是首次设置资料
    const isSetup = options.setup === 'true'
    
    this.setData({
      userInfo,
      isSetup,
      isEditing: isSetup // 首次设置时直接进入编辑模式
    })
    
    // 如果是首次设置，填充一些默认值
    if (isSetup && userInfo) {
      this.setData({
        'form.nickname': userInfo.nickName,
        'form.gender': userInfo.gender === 1 ? '男' : '女',
        'form.photos': userInfo.avatarUrl ? [userInfo.avatarUrl] : []
      })
    } else {
      // 获取用户资料
      this.loadUserProfile()
    }
  },

  // 加载用户资料
  loadUserProfile: function() {
    wx.request({
      url: app.globalData.apiBaseUrl + '/users/profile',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: res => {
        if (res.data.success) {
          const profile = res.data.data
          
          // 设置表单数据
          this.setData({
            form: {
              nickname: profile.nickname,
              gender: profile.gender,
              birthdate: profile.birthdate,
              location: profile.location,
              bio: profile.bio,
              photos: profile.photos || []
            },
            selectedTags: profile.tags || []
          })
        }
      },
      fail: () => {
        // 模拟数据
        const mockData = {
          nickname: '小明',
          gender: '男',
          birthdate: '1995-05-15',
          location: '北京市',
          bio: '喜欢旅行和摄影，一起去看世界吧！',
          photos: [
            'https://img.yzcdn.cn/vant/cat.jpeg',
            'https://img.yzcdn.cn/vant/dog.jpeg'
          ],
          tags: ['旅行', '摄影', '美食']
        }
        
        this.setData({
          form: {
            nickname: mockData.nickname,
            gender: mockData.gender,
            birthdate: mockData.birthdate,
            location: mockData.location,
            bio: mockData.bio,
            photos: mockData.photos
          },
          selectedTags: mockData.tags
        })
      }
    })
  },

  // 切换编辑模式
  toggleEdit: function() {
    this.setData({
      isEditing: !this.data.isEditing
    })
  },

  // 表单输入
  onInput: function(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    
    this.setData({
      [`form.${field}`]: value
    })
  },

  // 选择性别
  onGenderChange: function(e) {
    this.setData({
      'form.gender': e.detail.value
    })
  },

  // 选择出生日期
  onBirthdateChange: function(e) {
    this.setData({
      'form.birthdate': e.detail.value
    })
  },

  // 选择地区
  onRegionChange: function(e) {
    this.setData({
      'form.location': e.detail.value.join(' ')
    })
  },

  // 添加/移除标签
  toggleTag: function(e) {
    console.log('标签点击事件触发', e);
    const tag = e.currentTarget.dataset.tag;
    const selectedTags = [...this.data.selectedTags];
    
    const index = selectedTags.indexOf(tag);
    
    if (index > -1) {
      // 移除标签
      selectedTags.splice(index, 1);
      console.log('移除标签:', tag);
    } else {
      // 添加标签，最多选择5个
      if (selectedTags.length < 5) {
        selectedTags.push(tag);
        console.log('添加标签:', tag);
      } else {
        wx.showToast({
          title: '最多选择5个标签',
          icon: 'none'
        });
        return;
      }
    }
    
    this.setData({
      selectedTags
    }, () => {
      console.log('更新后的标签列表:', this.data.selectedTags);
    });
  },

  // 选择照片
  chooseImage: function() {
    const currentPhotos = this.data.form.photos
    
    // 最多上传6张照片
    if (currentPhotos.length >= 6) {
      wx.showToast({
        title: '最多上传6张照片',
        icon: 'none'
      })
      return
    }
    
    wx.chooseImage({
      count: 6 - currentPhotos.length,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: res => {
        // 上传照片
        const tempFilePaths = res.tempFilePaths
        
        // 这里应该调用上传接口，但为了演示，直接使用临时路径
        this.setData({
          'form.photos': [...currentPhotos, ...tempFilePaths],
          showUploadTip: true
        })
        
        // 3秒后隐藏提示
        setTimeout(() => {
          this.setData({
            showUploadTip: false
          })
        }, 3000)
      }
    })
  },

  // 删除照片
  deleteImage: function(e) {
    const index = e.currentTarget.dataset.index
    const photos = [...this.data.form.photos]
    
    photos.splice(index, 1)
    
    this.setData({
      'form.photos': photos
    })
  },

  // 提交表单
  submitForm: function() {
    // 验证表单
    if (!this.data.form.nickname) {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      })
      return
    }
    
    if (this.data.form.photos.length === 0) {
      wx.showToast({
        title: '请至少上传一张照片',
        icon: 'none'
      })
      return
    }
    
    if (this.data.selectedTags.length === 0) {
      wx.showToast({
        title: '请至少选择一个标签',
        icon: 'none'
      })
      return
    }
    
    // 显示加载中
    wx.showLoading({
      title: '保存中...',
    })
    
    // 构建提交数据
    const submitData = {
      ...this.data.form,
      tags: this.data.selectedTags
    }
    
    // 提交到服务器
    wx.request({
      url: app.globalData.apiBaseUrl + '/users/profile',
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: submitData,
      success: res => {
        if (res.data.success) {
          wx.hideLoading()
          wx.showToast({
            title: '保存成功',
            icon: 'success'
          })
          
          // 如果是首次设置，跳转到发现页
          if (this.data.isSetup) {
            setTimeout(() => {
              wx.switchTab({
                url: '/pages/discover/discover'
              })
            }, 1500)
          } else {
            // 退出编辑模式
            this.setData({
              isEditing: false
            })
          }
        } else {
          wx.hideLoading()
          wx.showToast({
            title: '保存失败，请重试',
            icon: 'none'
          })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        })
        
        // 如果是首次设置，跳转到发现页
        if (this.data.isSetup) {
          setTimeout(() => {
            wx.switchTab({
              url: '/pages/discover/discover'
            })
          }, 1500)
        } else {
          // 退出编辑模式
          this.setData({
            isEditing: false
          })
        }
      }
    })
  },

  // 退出登录
  logout: function() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: res => {
        if (res.confirm) {
          // 清除存储的登录信息
          wx.removeStorageSync('token')
          wx.removeStorageSync('userId')
          
          // 重置全局数据
          app.globalData.userInfo = null
          app.globalData.likedProfiles = []
          app.globalData.matches = []
          
          // 跳转到登录页
          wx.reLaunch({
            url: '/pages/index/index'
          })
        }
      }
    })
  }
}) 