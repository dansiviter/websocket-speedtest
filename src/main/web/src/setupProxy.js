const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(createProxyMiddleware('/websocket', { target: 'http://localhost:7001/', ws: true }));
};
