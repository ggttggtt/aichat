const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    open: true,
    historyApiFallback: true,
    allowedHosts: "all"
  }
})
