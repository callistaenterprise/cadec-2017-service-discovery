angular
  .module('app', ['rzModule']);

angular.module('app').factory('logTimeTaken', function () {
  var logTimeTaken = {
    request: function (config) {
      config.requestTimestamp = new Date().getTime();
      return config;
    },
    response: function (response) {
      response.config.responseTimestamp = new Date().getTime();
      return response;
    }
  };
  return logTimeTaken;
});

angular.module('app').config(function ($httpProvider) {
  $httpProvider.interceptors.push('logTimeTaken');
});
