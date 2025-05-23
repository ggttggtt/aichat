Component({
  properties: {
    show: {
      type: Boolean,
      value: false
    }
  },
  
  data: {
    emojiList: [
      '😀', '😁', '😂', '🤣', '😃', '😄', '😅', '😆', '😉', '😊', 
      '😋', '😎', '😍', '😘', '🥰', '😗', '😙', '😚', '🙂', '🤗',
      '🤩', '🤔', '🤨', '😐', '😑', '😶', '🙄', '😏', '😣', '😥',
      '😮', '🤐', '😯', '😪', '😫', '🥱', '😴', '😌', '😛', '😜',
      '😝', '🤤', '😒', '😓', '😔', '😕', '🙃', '🤑', '😲', '☹️',
      '🙁', '😖', '😞', '😟', '😤', '😢', '😭', '😦', '😧', '😨',
      '😩', '🤯', '😬', '😰', '😱', '🥵', '🥶', '😳', '🤪', '😵',
      '🥴', '😠', '😡', '🤬', '😷', '🤒', '🤕', '🤢', '🤮', '🤧',
      '😇', '🥳', '🥺', '🤠', '🤡', '🤥', '🤫', '🤭', '🧐', '🤓',
      '😈', '👿', '👹', '👺', '💀', '👻', '👽', '🤖', '💩', '😺'
    ]
  },
  
  methods: {
    // 点击表情
    onEmojiTap: function(e) {
      const emoji = e.currentTarget.dataset.emoji;
      this.triggerEvent('select', { emoji });
    }
  }
}) 