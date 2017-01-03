app.controller('loginController', [ '$scope', 'Auth', 'mySocket', '$q', '$timeout', '$location','$rootScope', function ($scope, Auth, mySocket, $q, $timeout, $location,$rootScope) {

	  $scope.pagelink = function(){
		  $location.path("/register");
	  };
		//submit
	  $scope.login = function () {
		
		// Send provided user credentials
		// mySocket.emit("authenticate", {command: "login", user: $scope.credentials.username, pass: $scope.credentials.password});
		  if (mySocket.readyState == WebSocket.OPEN) {
			   console.log("Sending hej");
			    var credentials = {type: "authentication", username: $scope.credentials.username, password: $scope.credentials.password};
	            mySocket.send(JSON.stringify(credentials));
		  }else{
			  console.log("Socket closed");
		  }
		  /*
		  mySocket.publish("chat.to.server", "Hej");
			console.log("Req login");
			var credentials = {type: "authentication", username: $scope.credentials.username, password: $scope.credentials.password};
			*/
			
			//mySocket.send(JSON.stringify(credentials));
		
		
		// Then wait for server response
		var promise = waitforServerResponse($q, $timeout, Auth, mySocket, $rootScope);
		promise.then(function(){
			//If successfull
			var user = {
					name: $scope.credentials.username	
			};
			console.log("LoginController: Authenticated: " + Auth.isLoggedIn());
			console.log("Setting user");
			Auth.setUser(user)
			//mySocket.emit('updateall');
            //waitfordata($q,$rootScope);

		}, function(reason){
			//If auth failed
			//TODO: Something when login fails (check reason, could be timeout or w/e)
			console.log("Promise rejected: " + reason);
			console.log("LoginController: Authenticated: " + Auth.isLoggedIn());
		});
	    
	  };
}]);