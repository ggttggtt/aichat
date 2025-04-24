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
        'Authorization': 'Bearer ' + wx.getStorageSync('token'),
        'Content-Type': 'application/json'
      },
      data: {
        content: this.data.content
      },
      success: res => {
        wx.hideLoading();
        
        if (res.data && res.data.code === "200") {
          wx.showToast({
            title: '评论成功',
            icon: 'success'
          });
          
          // 设置刷新标记
          app.globalData.refreshMomentsPage = true;
          
          // 延迟返回
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);
        } else {
          wx.showToast({
            title: res.data && res.data.msg ? res.data.msg : '评论失败',
            icon: 'none'
          });
        }
      },
      fail: err => {
        console.error('提交评论失败:', err);
        wx.hideLoading();
        wx.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        });
      }
    })
  },

  // 提交动态
  submitMoment: function() {
    if (this.data.content.trim() === '') {
      wx.showToast({
        title: '请输入动态内容',
        icon: 'none'
      })
      return
    }
    
    wx.showLoading({
      title: '发布动态中...',
    })
    
    // 上传图片函数
    const uploadImages = async () => {
      const uploadedImages = [];
      
      // 如果没有图片，直接返回空数组
      if (this.data.images.length === 0) {
        return uploadedImages;
      }
      
      // 循环上传每一张图片
      for (let i = 0; i < this.data.images.length; i++) {
        try {
          // 显示上传进度
          wx.showLoading({
            title: `上传图片 ${i+1}/${this.data.images.length}`,
          });
          
          const result = await new Promise((resolve, reject) => {
            wx.uploadFile({
              url: app.globalData.apiBaseUrl + '/api/oss/upload',
              filePath: this.data.images[i],
              name: 'file',
              formData: {
                'directory': 'moments' // 指定上传目录
              },
              success: res => {
                console.log('图片上传响应:', res.data);
                // 注意: uploadFile的返回是字符串，需要解析为JSON
                try {
                  const data = JSON.parse(res.data);
                  if (data.code === "200" && data.data && data.data.url) {
                    resolve(data.data.url);
                  } else {
                    console.error('图片上传接口返回错误:', data);
                    reject(new Error(data.msg || '上传失败'));
                  }
                } catch (e) {
                  console.error('解析返回数据错误:', e, res.data);
                  reject(new Error('上传接口返回数据格式错误'));
                }
              },
              fail: err => {
                console.error('图片上传请求失败:', err);
                reject(err);
              }
            });
          });
          
          uploadedImages.push(result);
        } catch (error) {
          console.error('图片上传失败:', error);
          wx.hideLoading();
          wx.showToast({
            title: '图片上传失败',
            icon: 'none'
          });
          return null; // 返回null表示上传失败
        }
      }
      
      return uploadedImages;
    }
    
    // 开始上传图片并发布动态
    uploadImages().then(imageUrls => {
      // 如果图片上传失败，中止发布
      if (imageUrls === null) {
        return;
      }
      
      // 准备发布数据
      const momentData = {
        content: this.data.content,
        imageUrls: imageUrls.join(','),
        location: this.data.showLocation ? this.data.location : '',
        visibility: 0 // 默认所有人可见
      };
      
      console.log('发送动态数据:', momentData);
      
      // 发布动态
      wx.request({
        url: app.globalData.apiBaseUrl + '/moments',
        method: 'POST',
        header: {
          'Authorization': 'Bearer ' + wx.getStorageSync('token'),
          'Content-Type': 'application/json'
        },
        data: momentData,
        success: res => {
          console.log('动态发布响应:', res.data);
          wx.hideLoading();
          
          if (res.data && res.data.code === "200") {
            wx.showToast({
              title: '发布成功',
              icon: 'success'
            });
            
            // 设置刷新标记
            app.globalData.refreshMomentsPage = true;
            
            // 延迟返回
            setTimeout(() => {
              wx.navigateBack();
            }, 1500);
          } else {
            wx.showToast({
              title: res.data && res.data.msg ? res.data.msg : '发布失败',
              icon: 'none'
            });
          }
        },
        fail: err => {
          console.error('发布动态请求失败:', err);
          wx.hideLoading();
          wx.showToast({
            title: '网络错误，请重试',
            icon: 'none'
          });
        }
      });
    }).catch(error => {
      console.error('上传过程发生错误:', error);
      wx.hideLoading();
      wx.showToast({
        title: '发布失败，请重试',
        icon: 'none'
      });
    });
  }
}) 