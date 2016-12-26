const conf = require('./gulp.conf');

const proxyMiddleware = require('http-proxy-middleware');

module.exports = function () {
  return {
    server: {
      baseDir: [
        conf.paths.tmp,
        conf.paths.src
      ],
      routes: {
        '/bower_components': 'bower_components'
      },
      middleware: [
        proxyMiddleware('/api', {target: 'http://localhost:8080/', changeOrigin: true, secure: false}),
      ]
    },
    open: false
  };
};
