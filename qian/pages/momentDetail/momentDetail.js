const app = getApp()

Page({
  data: {
    momentId: null,
    moment: null,
    comments: [],
    commentContent: '',
    commentFocus: false,
    replyToComment: null,
    replyTo: null,
    userInfo: null,
    showEmojiPicker: false,
    inputFocus: false
  },

  onLoad: function(options) {
    const momentId = options.id
    if (!momentId) {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
      return
    }
    
    this.setData({
      momentId,
      userInfo: {
        userId: app.globalData.userInfo ? app.globalData.userInfo.userId : 1
      }
    })
    
    this.loadMomentDetail()
    this.loadComments()
  },

  // 加载动态详情
  loadMomentDetail: function() {
    wx.showLoading({
      title: '加载中'
    })
    
    wx.request({
      url: app.globalData.apiBaseUrl + '/api/dating/moments/' + this.data.momentId,
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: res => {
        wx.hideLoading()
        
        if (res.data.success) {
          const moment = res.data.data
          
          // 处理图片
          if (moment.images) {
            moment.images = moment.images.split(',')
          } else {
            moment.images = []
          }
          
          this.setData({
            moment
          })
        } else {
          wx.showToast({
            title: '获取动态失败',
            icon: 'none'
          })
        }
      },
      fail: () => {
        wx.hideLoading()
        
        // 模拟数据
        this.setData({
          moment: {
            momentId: this.data.momentId,
            userId: 101,
            nickname: '张三',
            avatar: 'https://img.yzcdn.cn/vant/cat.jpeg',
            content: '这是一条动态内容，用于测试详情页',
            images: ['https://img.yzcdn.cn/vant/cat.jpeg'],
            location: '北京市朝阳区',
            createTime: '2023-10-10 10:10:10',
            likeCount: 10,
            commentCount: 5,
            isLiked: false
          }
        })
      }
    })
  },

  // 加载评论
  loadComments: function() {
    wx.request({
      url: app.globalData.apiBaseUrl + '/moments/' + this.data.momentId + '/comments',
      method: 'GET',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      success: res => {
        if (res.data.code === "200") {
          // 处理数据，将id映射为commentId
          const comments = res.data.data.map(comment => ({
            ...comment,
            commentId: comment.id  // 添加commentId字段，映射自id
          }));
          
          this.setData({
            comments: comments
          });
        }
      },
      fail: () => {
        // 模拟数据
        this.setData({
          comments: [
            {
              commentId: 1,
              momentId: this.data.momentId,
              userId: 102,
              nickname: '李四',
              avatar: 'https://img.yzcdn.cn/vant/dog.jpeg',
              content: '这是一条评论',
              createTime: '2023-10-10 10:15:30',
              likeCount: 2
            },
            {
              commentId: 2,
              momentId: this.data.momentId,
              userId: 103,
              nickname: '王五',
              avatar: 'https://img.yzcdn.cn/vant/cat.jpeg',
              content: '这是另一条评论',
              createTime: '2023-10-10 10:20:45',
              likeCount: 1
            }
          ]
        })
      }
    })
  },

  // 查看用户详情
  viewUserDetail: function(e) {
    const userId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/userDetail/userDetail?id=' + userId
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

  // 点赞/取消点赞
  likeMoment: function() {
    const isLiked = this.data.moment.isLiked
    const url = app.globalData.apiBaseUrl + '/api/dating/moments/' + 
                (isLiked ? 'unlike/' : 'like/') + this.data.momentId
    
    // 先更新UI
    const moment = { ...this.data.moment }
    moment.isLiked = !isLiked
    moment.likeCount = isLiked ? moment.likeCount - 1 : moment.likeCount + 1
    
    this.setData({
      moment
    })
    
    // 发送请求
    wx.request({
      url,
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      fail: () => {
        // 请求失败，回滚UI
        const moment = { ...this.data.moment }
        moment.isLiked = isLiked
        moment.likeCount = isLiked ? moment.likeCount + 1 : moment.likeCount - 1
        
        this.setData({
          moment
        })
        
        wx.showToast({
          title: '操作失败',
          icon: 'none'
        })
      }
    })
  },

  // 显示评论输入框
  showCommentInput: function() {
    this.setData({
      replyToComment: '',
      replyTo: '',
      showEmojiPicker: false
    })
  },

  // 回复评论
  replyComment: function(e) {
    const commentId = e.currentTarget.dataset.id || '';
    const nickname = e.currentTarget.dataset.name || '';
    
    this.setData({
      replyToComment: commentId,
      replyTo: nickname,
      showEmojiPicker: false
    }, () => {
      // 设置输入框焦点
      this.setData({
        inputFocus: true
      });
    });
  },

  // 取消回复
  cancelReply: function() {
    this.setData({
      replyToComment: '',
      replyTo: '',
      showEmojiPicker: false
    });
  },

  // 表情相关方法
  toggleEmojiPicker: function() {
    this.setData({
      showEmojiPicker: !this.data.showEmojiPicker
    });
  },
  
  onEmojiSelect: function(e) {
    const emoji = e.detail.emoji;
    this.setData({
      commentContent: this.data.commentContent + emoji
    });
  },
  
  onCommentInput: function(e) {
    this.setData({
      commentContent: e.detail.value
    });
  },
  
  // 输入框获取焦点
  onInputFocus: function() {
    // 如果不是通过回复按钮触发的焦点，清除回复相关状态
    if (!this.data.inputFocus) {
      this.cancelReply();
    }
    // 重置inputFocus状态，避免影响下次手动点击输入框
    this.setData({
      inputFocus: false
    });
  },
  
  // 输入框失去焦点
  onInputBlur: function() {
    // 可以选择是否在这里清除回复状态
    // this.cancelReply();
  },
  
  // 添加评论
  addComment: function() {
    const content = this.data.commentContent.trim();
    if (!content) {
      wx.showToast({
        title: '评论内容不能为空',
        icon: 'none'
      });
      return;
    }
    
    const replyToComment = this.data.replyToComment || '';
    
    wx.showLoading({
      title: '发送中',
    });
    
    const data = {
      content: this.data.commentContent
    };
    
    // 如果是回复评论
    if (this.data.replyToComment && this.data.replyToComment !== '') {
      data.parentId = this.data.replyToComment;
    }
    
    wx.request({
      url: app.globalData.apiBaseUrl + '/moments/' + this.data.momentId + '/comments',
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token'),
        'Content-Type': 'application/json'
      },
      data,
      success: res => {
        wx.hideLoading();
        
        if (res.data.code === "200") {
          wx.showToast({
            title: '评论成功',
            icon: 'success'
          });
          
          this.setData({
            commentContent: '',
            replyToComment: '',
            replyTo: '',
            showEmojiPicker: false
          });
          
          // 重新加载评论
          this.loadComments();
          
          // 更新评论数
          const moment = { ...this.data.moment };
          moment.commentCount = moment.commentCount + 1;
          this.setData({
            moment
          });
        } else {
          wx.showToast({
            title: '评论失败',
            icon: 'none'
          });
        }
      },
      fail: () => {
        wx.hideLoading();
        
        // 模拟成功
        wx.showToast({
          title: '评论成功',
          icon: 'success'
        });
        
        this.setData({
          commentContent: '',
          replyToComment: '',
          replyTo: '',
          showEmojiPicker: false
        });
        
        // 模拟添加新评论
        const newComment = {
          commentId: Date.now(),
          momentId: this.data.momentId,
          userId: this.data.userInfo.userId,
          nickname: '当前用户',
          avatar: app.globalData.userInfo ? app.globalData.userInfo.avatar : '',
          content: this.data.commentContent,
          createTime: '刚刚',
          likeCount: 0
        };
        
        if (this.data.replyToComment && this.data.replyToComment !== '') {
          const replyComment = this.data.comments.find(c => c.commentId === this.data.replyToComment);
          if (replyComment) {
            newComment.parentId = this.data.replyToComment;
            newComment.replyUserId = replyComment.userId;
            newComment.replyUserName = replyComment.nickname;
          }
        }
        
        this.setData({
          comments: [newComment, ...this.data.comments],
          moment: {
            ...this.data.moment,
            commentCount: this.data.moment.commentCount + 1
          }
        });
      }
    });
  },

  // 删除评论
  deleteComment: function(e) {
    const commentId = e.currentTarget.dataset.id
    
    if (!commentId) {
      wx.showToast({
        title: '评论ID不能为空',
        icon: 'none'
      })
      return
    }
    
    wx.showModal({
      title: '提示',
      content: '确定要删除这条评论吗？',
      success: res => {
        if (res.confirm) {
          wx.showLoading({
            title: '删除中'
          })
          
          wx.request({
            url: app.globalData.apiBaseUrl + '/moments/comments/' + commentId,
            method: 'DELETE',
            header: {
              'Authorization': 'Bearer ' + wx.getStorageSync('token')
            },
            success: res => {
              wx.hideLoading()
              
              if (res.data.code === "200") {
                wx.showToast({
                  title: '删除成功',
                  icon: 'success'
                })
                
                // 从列表中移除
                const comments = this.data.comments.filter(c => c.commentId !== commentId)
                const moment = { ...this.data.moment }
                moment.commentCount = moment.commentCount - 1
                
                this.setData({
                  comments,
                  moment
                })
              } else {
                wx.showToast({
                  title: '删除失败',
                  icon: 'none'
                })
              }
            },
            fail: () => {
              wx.hideLoading()
              
              // 模拟成功
              wx.showToast({
                title: '删除成功',
                icon: 'success'
              })
              
              // 从列表中移除
              const comments = this.data.comments.filter(c => c.commentId !== commentId)
              const moment = { ...this.data.moment }
              moment.commentCount = moment.commentCount - 1
              
              this.setData({
                comments,
                moment
              })
            }
          })
        }
      }
    })
  },

  // 点赞/取消点赞评论
  likeComment: function(e) {
    const commentId = e.currentTarget.dataset.id;
    const commentIndex = this.data.comments.findIndex(c => c.commentId == commentId);
    
    if (commentIndex === -1) return;
    
    const isLiked = this.data.comments[commentIndex].isLiked;
    const url = app.globalData.apiBaseUrl + '/api/dating/moments/comments/' + 
                commentId + (isLiked ? '/unlike' : '/like');
    
    // 先更新UI
    const comments = [...this.data.comments];
    comments[commentIndex].isLiked = !isLiked;
    comments[commentIndex].likeCount = isLiked 
      ? Math.max(0, comments[commentIndex].likeCount - 1)
      : (comments[commentIndex].likeCount || 0) + 1;
    
    this.setData({
      comments
    });
    
    // 发送请求
    wx.request({
      url,
      method: 'POST',
      header: {
        'Authorization': 'Bearer ' + wx.getStorageSync('token')
      },
      fail: () => {
        // 请求失败，回滚UI
        const comments = [...this.data.comments];
        comments[commentIndex].isLiked = isLiked;
        comments[commentIndex].likeCount = isLiked 
          ? comments[commentIndex].likeCount + 1
          : Math.max(0, comments[commentIndex].likeCount - 1);
        
        this.setData({
          comments
        });
        
        wx.showToast({
          title: '操作失败',
          icon: 'none'
        });
      }
    });
  },
}) 