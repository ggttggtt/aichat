<template>
  <div :class="{hidden}">
    <div class="banner">
      <div class="container">
        <h1><a href="/">Vue在线文档查看器<input class="file-select" type="file" @change="handleChange"/></a></h1>
      </div>
    </div>
    <div class="container">
      <div v-show="loading" class="well loading">正在加载中，请耐心等待...</div>
      <div v-show="!loading" class="well" ref="output"></div>
    </div>
  </div>
</template>

<script>
import { getExtend, readBuffer, render } from '@/components/util';
import { parse } from 'qs';

/**
 * 支持嵌入式显示，基于postMessage支持跨域
 * 示例代码：
 *
 */
export default {
  name: 'HelloWorld',
  props: {
    msg: String
  },
  data() {
    return {
      // 加载状态跟踪
      loading: false,
      // 上个渲染实例
      last: null,
      // 隐藏头部，当基于消息机制渲染，将隐藏
      hidden: false,
    }
  },
  methods: {
    async handleChange(e) {
      this.loading = true;
      try {
        const [ file ] = e.target.files;
        const arrayBuffer = await readBuffer(file);
        this.loading = false
        this.last = await this.displayResult(arrayBuffer, file)
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    },
    displayResult(buffer, file) {
      // 取得文件名
      const { name } = file;
      // 取得扩展名
      const extend = getExtend(name);
      // 输出目的地
      const { output } = this.$refs;
      // 生成新的dom
      const node = document.createElement('div');
      // 添加孩子，防止vue实例替换dom元素
      if (this.last) {
        output.removeChild(this.last.$el);
        this.last.$destroy();
      }
      const child = output.appendChild(node);
      // 调用渲染方法进行渲染
      return new Promise((resolve, reject) => render(buffer, extend, child)
          .then(resolve).catch(reject));
    }
  }
}
</script>

<style scoped>
.banner {
  overflow: auto;
  text-align: center;
  background-color: #12b6ff;
  color: #fff;
}

.hidden .banner {
  display: none;
}

.hidden .well {
  height: calc(100vh - 12px);
}

.file-select {
  position: absolute;
  left: 5%;
  top: 17px;
  margin-left: 20px;
}

.banner a {
  color: #fff;
}

.banner h1 {
  font-size: 20px;
  line-height: 2;
  margin: 0.5em 0;
}

.well {
  display: block;
  background-color: #f2f2f2;
  border: 1px solid #ccc;
  margin: 5px;
  width: calc(100% - 12px);
  height: calc(100vh - 73px);
  overflow: auto;
}

.loading {
  text-align: center;
  padding-top: 50px;
}

.messages .warning {
  color: #cc6600;
}
</style>
