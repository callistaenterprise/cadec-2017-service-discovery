var testApp = angular.module('testApp', []);

testApp.controller('testController' , function ($scope, $http) {
    $scope.home = "This is ML's homepage";

    $scope.quotes = [
    ];

    $scope.getRequest = function () {
        console.log("I've been pressed!");
        // $http.get("data/posts.json")
        $http.get('http://localhost:9090/quote')
            .then(function successCallback(response){
                $scope.response = response;

                console.log("Response: " + response.toString())
                var newquote = {
                    timestamp: "now",
                    language: response.data.language,
                    text: response.data.quote,
                };

                $scope.quotes.push(newquote);

            }, function errorCallback(response){
                console.log("Unable to perform get request");
            });
    };

});