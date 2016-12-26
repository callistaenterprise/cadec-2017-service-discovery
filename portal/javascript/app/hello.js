angular
  .module('app')
  .component('app', {
    templateUrl: 'app/hello.html',
    controller: function ($http, $log, $interval, $scope) {
      var vm = this;
      vm.home = "This is ML's homepage";
      vm.quotes = [];
      vm.running = false;
      vm.interval = null;

      vm.timeoutBetweenRequests = 1000;
      vm.slider = {
        options: {
          floor: 0,
          ceil: 3000
        }
      };

      $scope.$watch('$ctrl.timeoutBetweenRequests', function () {
        $log.debug("Changed timeout!");
        if (vm.running) {
          $interval.cancel(vm.interval);
          vm.interval = $interval(makeRequest, vm.timeoutBetweenRequests);
        }
      });

      $scope.$on('$destroy', function () {
        $interval.cancel(vm.interval);
      });

      vm.getRequest = function () {
        $log.debug("I've been pressed!");

        makeRequest();
      };

      vm.toggleRequests = function () {
        vm.running = !vm.running;

        if (vm.running) {
          vm.interval = $interval(makeRequest, vm.timeoutBetweenRequests);
        } else {
          $interval.cancel(vm.interval);
        }
      };

      function makeRequest() {
        $http.get('/api/quote')
          .then(function successCallback(response) {
            vm.response = response;

            $log.debug("Response: " + response.toString());
            var newquote = {
              timestamp: new Date(),
              language: response.data.language,
              text: response.data.quote,
              ipAddress: response.data.ipAddress
            };

            vm.quotes.unshift(newquote);
          },
          function errorCallback(/* response */) {
            $log.debug("Unable to perform get request");
          });
      }
    }
  });
