const app = getApp()

Page({
  data: {
    moments: [],
    loading: true,
    page: 1,
    hasMore: true,
    pageSize: 10
  },

  onLoad: function() {
    this.loadMoments()
  },

  onShow: function() {
    // 如果需要刷新
    if (app.globalData.refreshMomentsPage) {
      this.setData({
        moments: [],
        page: 1,
        hasMore: true
      })
      this.loadMoments()
      app.globalData.refreshMomentsPage = false
    }
  },

  // 加载动态数据
  loadMoments: function() {
    if (!this.data.hasMore || this.data.loading) return;
    
    this.setData({
      loading: true
    });
    
    wx.request({
      url: app.globalData.apiBaseUrl + '/api/dating/moments',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: res => {
        if (res.data.success) {
          let newMoments = res.data.data;
          // 处理图片数据和时间格式化
          newMoments.forEach(item => {
            if (item.images) {
              item.images = item.images.split(',');
            } else {
              item.images = [];
            }
            
            // 加载每个动态的评论（仅加载前3条）
            this.loadComments(item.momentId, 3);
          });
          
          this.setData({
            moments: [...this.data.moments, ...newMoments],
            loading: false,
            page: this.data.page + 1,
            hasMore: newMoments.length === this.data.pageSize
          });
        } else {
          this.setData({
            loading: false
          });
          wx.showToast({
            title: '加载失败',
            icon: 'none'
          });
        }
      },
      fail: () => {
        // 模拟数据
        const newMoments = [];
        // ... 模拟数据保持不变 ...
        
        // 为模拟数据添加评论
        newMoments.forEach(item => {
          // 模拟评论数据
          item.comments = item.comments || [];
          if (item.comments.length > 3) {
            item.comments = item.comments.slice(0, 3);
          }
        });
        
        this.setData({
          moments: [...this.data.moments, ...newMoments],
          loading: false,
          page: this.data.page + 1,
          hasMore: this.data.page < 3 // 模拟3页数据
        });
      }
    });
  },

  // 加载动态的评论
  loadComments: function(momentId, limit) {
    wx.request({
      url: app.globalData.apiBaseUrl + '/api/dating/moments/' + momentId + '/comments',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: res => {
        if (res.data.success) {
          let comments = res.data.data;
          if (limit && comments.length > limit) {
            comments = comments.slice(0, limit);
          }
          
          // 更新对应动态的评论
          const momentsData = this.data.moments;
          const index = momentsData.findIndex(m => m.momentId === momentId);
          
          if (index !== -1) {
            momentsData[index].comments = comments;
            this.setData({
              moments: momentsData
            });
          }
        }
      },
      fail: () => {
        // 模拟错误处理，实际环境应该有更好的处理方式
        console.log('获取评论失败');
      }
    });
  },

  // 下拉刷新
  onPullDownRefresh: function() {
    this.setData({
      moments: [],
      page: 1,
      hasMore: true
    })
    
    this.loadMoments()
    wx.stopPullDownRefresh()
  },

  // 上拉加载更多
  onReachBottom: function() {
    this.loadMoments()
  },

  // 点赞动态
  likeMoment: function(e) {
    const momentId = e.currentTarget.dataset.id
    const momentIndex = this.data.moments.findIndex(moment => moment.id === momentId)
    
    if (momentIndex === -1) return
    
    const isLiked = this.data.moments[momentIndex].isLiked
    
    // 更新UI
    const newMoments = [...this.data.moments]
    newMoments[momentIndex].isLiked = !isLiked
    newMoments[momentIndex].likeCount = isLiked 
      ? newMoments[momentIndex].likeCount - 1 
      : newMoments[momentIndex].likeCount + 1
    
    this.setData({
      moments: newMoments
    })
    
    // 发送请求
    wx.request({
      url: app.globalData.apiBaseUrl + '/moments/' + momentId + '/like',
      method: isLiked ? 'DELETE' : 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      }
    })
  },

  // 发表评论
  showCommentInput: function(e) {
    const momentId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/publishMoment/publishMoment?type=comment&momentId=${momentId}`
    })
  },

  // 查看用户详情
  viewUserDetail: function(e) {
    const userId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/userDetail/userDetail?id=' + userId
    })
  },

  // 查看动态详情（包含全部评论）
  viewMomentDetail: function(e) {
    const momentId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/momentDetail/momentDetail?id=' + momentId
    })
  },

  // 预览图片
  previewImage: function(e) {
    const urls = e.currentTarget.dataset.urls
    const current = e.currentTarget.dataset.current
    
    wx.previewImage({
      urls,
      current
    })
  },

  // 发布新动态
  publishMoment: function() {
    wx.navigateTo({
      url: '/pages/publishMoment/publishMoment'
    })
  }
}) 