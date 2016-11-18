angular
  .module('app')
  .component('app', {
    templateUrl: 'app/hello.html',
    controller: function ($http, $log) {
      var vm = this;
      vm.home = "This is ML's homepage";
      vm.quotes = [];

      vm.getRequest = function () {
        $log.debug("I've been pressed!");
        // $http.get("data/posts.json")
        $http.get('/api/quote')
          .then(function successCallback(response) {
            vm.response = response;

            $log.debug("Response: " + response.toString());
            var newquote = {
              timestamp: "now",
              language: response.data.language,
              text: response.data.quote,
              ipAddress: response.data.ipAddress
            };

            vm.quotes.push(newquote);
          },
          function errorCallback(/* response */) {
            $log.debug("Unable to perform get request");
          });
      };
    }
  });
