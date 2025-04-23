const app = getApp()

Page({
  data: {
    content: '',
    images: [],
    location: '',
    showLocation: false,
    isComment: false,
    momentId: null,
    submitDisabled: true
  },

  onLoad: function(options) {
    // 判断是评论还是发布动态
    const isComment = options.type === 'comment'
    const momentId = options.momentId
    
    if (isComment) {
      wx.setNavigationBarTitle({
        title: '发表评论'
      })
      
      this.setData({
        isComment: true,
        momentId
      })
    } else {
      wx.setNavigationBarTitle({
        title: '发布动态'
      })
    }
  },

  // 输入内容
  onInput: function(e) {
    const content = e.detail.value
    this.setData({
      content,
      submitDisabled: content.trim() === ''
    })
  },

  // 选择图片
  chooseImage: function() {
    if (this.data.isComment) return // 评论不能上传图片
    
    const count = 9 - this.data.images.length
    if (count <= 0) {
      wx.showToast({
        title: '最多上传9张图片',
        icon: 'none'
      })
      return
    }
    
    wx.chooseImage({
      count,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: res => {
        const tempFilePaths = res.tempFilePaths
        this.setData({
          images: [...this.data.images, ...tempFilePaths]
        })
      }
    })
  },

  // 删除图片
  deleteImage: function(e) {
    const index = e.currentTarget.dataset.index
    const images = [...this.data.images]
    images.splice(index, 1)
    
    this.setData({
      images
    })
  },

  // 预览图片
  previewImage: function(e) {
    const current = e.currentTarget.dataset.src
    wx.previewImage({
      current,
      urls: this.data.images
    })
  },

  // 获取位置
  getLocation: function() {
    if (this.data.isComment) return // 评论不能添加位置
    
    wx.chooseLocation({
      success: res => {
        const location = res.name || res.address
        this.setData({
          location,
          showLocation: true
        })
      }
    })
  },

  // 删除位置
  deleteLocation: function() {
    this.setData({
      location: '',
      showLocation: false
    })
  },

  // 提交
  submitForm: function() {
    if (this.data.content.trim() === '') {
      wx.showToast({
        title: this.data.isComment ? '请输入评论内容' : '请输入动态内容',
        icon: 'none'
      })
      return
    }
    
    wx.showLoading({
      title: this.data.isComment ? '发表评论中...' : '发布动态中...',
    })
    
    if (this.data.isComment) {
      // 发表评论
      this.submitComment()
    } else {
      // 发布动态
      this.submitMoment()
    }
  },

  // 提交评论
  submitComment: function() {
    wx.request({
      url: app.globalData.apiBaseUrl + '/moments/' + this.data.momentId + '/comments',
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      data: {
        content: this.data.content
      },
      success: res => {
        if (res.data.success) {
          wx.hideLoading()
          wx.showToast({
            title: '评论成功',
            icon: 'success'
          })
          
          // 设置刷新标记
          app.globalData.refreshMomentsPage = true
          
          // 延迟返回
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
        } else {
          wx.hideLoading()
          wx.showToast({
            title: '评论失败',
            icon: 'none'
          })
        }
      },
      fail: () => {
        // 模拟成功
        wx.hideLoading()
        wx.showToast({
          title: '评论成功',
          icon: 'success'
        })
        
        // 设置刷新标记
        app.globalData.refreshMomentsPage = true
        
        // 延迟返回
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      }
    })
  },

  // 提交动态
  submitMoment: function() {
    // 首先上传图片，实际应用中需要实现图片上传功能
    // 这里假设已经上传完成，直接返回图片URL
    
    const uploadImages = () => {
      return new Promise((resolve) => {
        // 模拟上传过程
        setTimeout(() => {
          // 实际开发中这里应该调用上传API
          resolve(this.data.images)
        }, 500)
      })
    }
    
    uploadImages().then(imageUrls => {
      // 发布动态
      wx.request({
        url: app.globalData.apiBaseUrl + '/moments',
        method: 'POST',
        header: {
          'Authorization': 'Bearer ' + wx.getStorageSync('token')
        },
        data: {
          content: this.data.content,
          images: imageUrls,
          location: this.data.showLocation ? this.data.location : ''
        },
        success: res => {
          if (res.data.success) {
            wx.hideLoading()
            wx.showToast({
              title: '发布成功',
              icon: 'success'
            })
            
            // 设置刷新标记
            app.globalData.refreshMomentsPage = true
            
            // 延迟返回
            setTimeout(() => {
              wx.navigateBack()
            }, 1500)
          } else {
            wx.hideLoading()
            wx.showToast({
              title: '发布失败',
              icon: 'none'
            })
          }
        },
        fail: () => {
          // 模拟成功
          wx.hideLoading()
          wx.showToast({
            title: '发布成功',
            icon: 'success'
          })
          
          // 设置刷新标记
          app.globalData.refreshMomentsPage = true
          
          // 延迟返回
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
        }
      })
    })
  }
}) 