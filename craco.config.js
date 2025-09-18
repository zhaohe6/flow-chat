// craco.config.js
const CracoLessPlugin = require('craco-less');

module.exports = {
  plugins: [
    {
      plugin: CracoLessPlugin,
      options: {
        lessLoaderOptions: {
          lessOptions: {
            modifyVars: { 
              // 在这里覆盖 Less 变量（可选）
              '@primary-color': '#1DA57A' 
            },
            javascriptEnabled: true,
          },
        },
      },
    },
  ],
};