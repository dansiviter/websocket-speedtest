const proxy = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(proxy('/websocket', { target: 'http://localhost:7001/', ws: true }));
};
