app.controller("registerController", ["$scope", "mySocket", "$q", "$timeout", "Auth", function($scope, mySocket, $q, $timeout, Auth){
	
		$scope.registerUser = function(){
		
		// Send a register-request to the server
		mySocket.send(JSON.stringify({type: "register", username: $scope.credentials.username, password: $scope.credentials.password}));
		
		// Then wait for server response
		var promise = waitforServerResponse($q, $timeout, Auth, mySocket);
		promise.then(function(){
			//If promise resolved(we get a response)
			var user = {
					name: $scope.credentials.username	
			};
			console.log("RegisterController: User Created Successfully");
			//If user created successfully, login as new user
			Auth.setUser(user)
		}, function(reason){
			//If the promise was rejected
			//TODO: Inform the user that something went wrong(Check reason)
			console.log("Promise rejected: " + reason);
		});
	  };
}]);