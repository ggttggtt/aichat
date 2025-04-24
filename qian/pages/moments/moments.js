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
    // 防止重复加载或不需要加载
    // if (!this.data.hasMore || this.data.loading) return;
    
    this.setData({
      loading: true
    });
    
    wx.request({
      url: app.globalData.apiBaseUrl + '/moments',
      method: 'GET',
      data: {
        page: this.data.page,
        size: this.data.pageSize
      },
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: res => {
        console.log('动态数据请求成功，响应数据:', res.data);
        if (res.data.code === "200") {
          let newMoments = res.data.data;
          
          // 避免重复数据
          if (newMoments && newMoments.length > 0) {
            // 过滤掉已经存在的动态
            const existingIds = this.data.moments.map(item => item.id);
            newMoments = newMoments.filter(item => !existingIds.includes(item.id));
          }
          
          // 处理图片数据和时间格式化
          newMoments.forEach(item => {
            // 处理images字段，如果imageUrls是JSON字符串，解析它
            if (item.imageUrls) {
              try {
                // 尝试解析JSON字符串
                const parsedImages = JSON.parse(item.imageUrls);
                if (Array.isArray(parsedImages)) {
                  item.images = parsedImages;
                } else {
                  // 如果不是数组，尝试用逗号分割
                  item.images = item.imageUrls.split(',');
                }
              } catch (e) {
                // 如果解析失败，可能是普通的逗号分隔字符串
                item.images = item.imageUrls.split(',');
              }
            } else {
              item.images = [];
            }
            
            // 格式化时间
            const createDate = new Date(item.createTime);
            item.formattedTime = createDate.getFullYear() + '-' + 
                                (createDate.getMonth() + 1) + '-' + 
                                createDate.getDate();
            
            // 加载每个动态的评论（仅加载前3条）
            if (item.id) {
              this.loadComments(item.id, 3);
            }
          });
          
          this.setData({
            moments: [...this.data.moments, ...newMoments],
            loading: false,
            page: this.data.page + 1,
            hasMore: newMoments.length === this.data.pageSize
          });
        } else {
          this.setData({
            loading: false,
            hasMore: false
          });
          wx.showToast({
            title: '加载失败',
            icon: 'none'
          });
        }
      },
      fail: (error) => {
        console.error('请求失败，详细错误:', error);
        // 测试后端连接
        wx.request({
          url: app.globalData.apiBaseUrl + '/ping',
          method: 'GET',
          success: (pingRes) => {
            console.log('Ping测试成功:', pingRes.data);
          },
          fail: (pingError) => {
            console.error('Ping测试失败，后端可能不可用:', pingError);
          }
        });
        
        this.setData({
          loading: false
        });
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
      },
      complete: () => {
        console.log('请求完成');
      }
    });
  },

  // 加载动态的评论
  loadComments: function(momentId, limit) {
    // 检查该动态是否已有评论
    const momentsData = this.data.moments;
    const index = momentsData.findIndex(m => m.id === momentId);
    
    // 如果已有评论或正在加载，跳过
    if (index !== -1 && momentsData[index].comments) {
      return;
    }
    
    wx.request({
      url: app.globalData.apiBaseUrl + '/moments/' + momentId + '/comments',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: res => {
        if (res.data.code === "200") {
          let comments = res.data.data || [];
          if (limit && comments.length > limit) {
            comments = comments.slice(0, limit);
          }
          
          // 更新对应动态的评论
          const momentsData = this.data.moments;
          const index = momentsData.findIndex(m => m.id === momentId);
          
          if (index !== -1) {
            // 设置评论加载状态
            momentsData[index].commentsLoaded = true;
            momentsData[index].comments = comments;
            this.setData({
              moments: momentsData
            });
          }
        }
      },
      fail: () => {
        console.log('获取评论失败，动态ID:', momentId);
      }
    });
  },

  // 下拉刷新
  onPullDownRefresh: function() {
    // 重置状态，清空数据
    this.setData({
      moments: [],
      page: 1,
      hasMore: true,
      loading: false
    });
    
    // 重新加载第一页数据
    this.loadMoments();
    wx.stopPullDownRefresh();
  },

  // 上拉加载更多
  onReachBottom: function() {
    // 如果还有更多数据且当前不在加载中，则加载下一页
    if (this.data.hasMore && !this.data.loading) {
      this.loadMoments();
    } else if (!this.data.hasMore) {
      wx.showToast({
        title: '没有更多内容了',
        icon: 'none',
        duration: 1500
      });
    }
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
      url: isLiked 
        ? app.globalData.apiBaseUrl + '/moments/unlike/' + momentId
        : app.globalData.apiBaseUrl + '/moments/like/' + momentId,
      method: 'POST',
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