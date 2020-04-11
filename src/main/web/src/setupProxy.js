const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(createProxyMiddleware('/api/v1/ws', { target: 'http://localhost:7001/', ws: true }));
};
