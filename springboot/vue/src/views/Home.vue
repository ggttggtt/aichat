<template>
    <div>
      <button @click="generatePPTX">生成PPTX</button>
      <br>
      <a target="_blank" href="http://view.officeapps.live.com/op/view.aspx?src=https://sdy-upload-file.oss-cn-chengdu.aliyuncs.com/%E6%9C%8D%E5%8A%A1%E4%BF%9D%E6%8A%A4%E5%92%8C%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1.pptx">点击查看ppt</a>
      <div id="pptx" v-if="detailItem.fileType === 'pptx'"></div>
    </div>
  </template>
  
  <script>
  import pptxgen from 'pptxgenjs';

  export default {
    data() {
    return {
      string: ''
    };
  },
  mounted() {
    // 在组件挂载后调用 handlePPtx
    this.handlePPtx();
  },
    methods: {
      handlePPtx() {
      // 在这里调用pptxToHtml
      $("#pptx").pptxToHtml({ 
        pptxFileUrl: "http://47.92.165.44:9090/file/268239e8a8d5409094439267b4c28eea.pptx",
        slidesScale: "100%", 
      });
    },
      async generatePPTX() {
        // 提供的ppt文档链接
        const pptURL = 'http://47.92.165.44:9090/file/268239e8a8d5409094439267b4c28eea.pptx';
  
        try {
          // 从提供的ppt文档链接中获取信息
          const pptResponse = await fetch(pptURL);
          const pptBuffer = await pptResponse.arrayBuffer();
  
          // 创建pptxgen对象
          const pptx = new pptxgen();
  
          // 将ppt文档的二进制数据添加到PPTX文档中
          const slide = pptx.addSlide();
          slide.addImage({ data: pptBuffer, x: 0, y: 0, w: 10, h: 7 });
  
          // 保存PPTX文档
          pptx.writeFile('generated_presentation.pptx');
        } catch (error) {
          console.error('生成PPTX时出错：', error);
        }
      },
    },
  };
  </script>
  
  <style>
  /* 样式可以根据需要进行自定义 */
  </style>
  