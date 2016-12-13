var app = angular.module('myApp', [
	'btford.socket-io',
	'ngRoute'
]).factory('mySocket', function (socketFactory) {
	
	// Create the IO-Socket
	var myIoSocket = io.connect('http://127.0.0.1:1337/');

	  mySocket = socketFactory({
	    ioSocket: myIoSocket
	  });

	  return mySocket;
});

//Simple Service to change title (and give me better understanding)
app.factory("Page", function() {
	var title = "Some Title";
	return {
		title: function() {console.log("Returning with title: " + title); return title;},
		setTitle: function(newTitle) {title = newTitle;}
	};
});

app.factory("Auth", function() {
	var user;
	
	// Reveal the function to other javascript-code (We can now use Auth.setUser(user) & Auth.isLoggedIn())
	// The return{...} reveals what should be visible
	return{
		setUser : function(aUser) {
			//Note to self:FUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU...
			//No but seriously... javascript can't handle a this.user = user
			user = aUser;
			console.log("User set: " + user.name);
		},
		isLoggedIn : function() {
			// Ternary operator (Ternary conditional operator of swift), if the former: in this case user casted to user throws an
			// exception set it to the latter (Simply put, if user is not set, return false)
			//return (user) ? user : false;
			if(user == null){
				return false;
			}else{
				return true;
			}
		},
		getDisplayName(){
			return user.name;
		}
	}
});

app.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
	console.log("In route");
	
		// I dont get routing, seem to work on every page and #!/test worked to inject the template '/test'
		// The "base" path seem to be where the script is located (So we can inject the 'test.html', both in webcontent)
		// ADITIONAL NOTE: does not work without a server!!!
	  $routeProvider.when('/', {
	    templateUrl: "login.html",
	    controller: "loginController"
	  });
	  $routeProvider.when('/login', {
		    templateUrl: "login.html",
		    controller: 'loginController'
	  });
	  $routeProvider.when('/home', {
	    templateUrl: 'home.html',
	    controller: 'myCtrl',
	  });
	  $routeProvider.otherwise({ redirectTo: '/login' });
	  //$locationProvider.html5Mode(true); - Can play with this later: Remove the # in address and (Make it not so ugly)
	  //Ps need <base href=""> on html pages
	  
	  // Don't put / infront of templateUrl
}]);

app.run(["$rootScope", "$location", "Auth", function($rootScope, $location, Auth) {
	console.log("In run");
	console.log($location.host());
	$rootScope.$on("$locationChangeStart", function(event) {
		console.log('Running Auth, path = ' + $location.path());
		if (!Auth.isLoggedIn()) {
			//Redirect to login
            console.log('Not logged in');
            if($location.path().localeCompare("/login") != 0){
            	console.log('Redirecting to login');
	            event.preventDefault();
	            $location.path('/login');
			}else{
				console.log("Staying on login");
			}
        }
        else {
        	//Should check if permission
            console.log('Allowed');
            //Redirect to suitable page
            $location.path('/home');
        }
	});
}]);

/*
app.controller('messageCtrl', function($scope, mySocket) {
	console.log("In messageCtrl");
	
	$scope.submit = function() {
		console.log("In submit i guess");
        if ($scope.text) {
        	console.log("Sending text: " + $scope.text);
	    	mySocket.emit('send:message', {
	    		message: $scope.text
	    	});
	    	$scope.text = '';
        }
      };
});
*/
app.controller('mainCtrl', ['$scope', 'Auth', '$location', "Page", function ($scope, Auth, $location, Page) {
	
	$scope.Page = Page;
	Page.setTitle("Home Page");
	
	// Watch the value of Auth isLoggedIn function, if it changes this is triggered
	$scope.$watch(Auth.isLoggedIn, function (authenticated, previouslyAuthenticated) {
	console.log("Status of Auth changed, newValue = " + authenticated + " oldValue: " + previouslyAuthenticated);
	
	// If we were Authenticated and now we are not
	if(!authenticated && previouslyAuthenticated) {
		console.log("Disconnect");
		$location.path('/login');
	}
	
    if(authenticated) {
    	console.log("Connect");
    	$location.path('/main');
    	//Do something when the user is connected
    }

	}, true);
}]);

// Current Experiment, Apperently the [] are to prevent minification to break the code (TODO: read about it and ng-annotate)
app.controller('myCtrl', ["$scope", "mySocket", "Page", "Auth", function($scope, mySocket, Page, Auth) {
	
	$scope.messages = [];
	$scope.Page = Page;
	$scope.name = Auth.getDisplayName()
	Page.setTitle("Lets Chat");
	console.log("In myCtrl");
		
	mySocket.on('send:message', function(data) {
		console.log("Got message: " + data.text.toString() + " from: " + data.user.toString());
		$scope.$apply(function() {
			$scope.messages.push({user: data.user, message: data.text});
		});
	});
	
	$scope.sendMessage = function () {
		console.log("Called SendMessage: " + $scope.message);
		  event.preventDefault();
		  if ($scope.message) {
	        	console.log("Sending text: " + $scope.message);
		    	mySocket.emit('send:message', {
		    		message: $scope.message
		    	});
		    	$scope.message = '';
	        }
	}
}])

function login(name, pass) {
	
}

app.controller('loginController', [ '$scope', 'Auth', 'mySocket', '$q', '$timeout', function ($scope, Auth, mySocket, $q, $timeout) {

	  function authenticate(){
		  
		  return $q(function(resolve, reject){
			  var timeoutPromise = $timeout(function(){
				  console.log("Rejecting: timeout");
				  reject("Timeout");
			  }, 5000);
			  
			  mySocket.on('authenticate', function(data) {
				  console.log("Auth reply: " + status);
				  if(data.status === "success"){
					  $timeout.cancel(timeoutPromise);
					  resolve("Correct");
				  }else{
					  $timeout.cancel(timeoutPromise);
					  reject("Denied");
				  }
			  });
		  });
	  }
		//submit
	  $scope.login = function () {
		
		// Send provided user credentials
		mySocket.emit("authenticate", {user: $scope.credentials.username, pass: $scope.credentials.password});
		// Then wait for server response
		var promise = authenticate();
		promise.then(function(){
			//If successfull
			var user = {
					name: $scope.credentials.username	
			};
			console.log("Setting user");
			Auth.setUser(user)
		}, function(reason){
			//If auth failed
			//TODO: Something when login fails (check reason, could be timeout or w/e)
			console.log("Promise rejected: " + reason);
		});
	    console.log("LoginController: Authenticated: " + Auth.isLoggedIn());
	  };
}]);

/*
console.log("in controller");
var socket = io.connect("http://127.0.0.1:1337");
$http.get("http://127.0.0.1:1337")
.then(function(response) {
	console.log("got response");
    $scope.todos = response.data;
});
console.log("after response");
*/
/*
mySocket.on('init', function(data) {
	console.log("I guess we got a message");
	console.log(data);
	
	//To update page it seem like we need to call $apply (Test by commentin this function)
	$scope.$apply(function() {
		
		$scope.name = data.name.toString();
	});
	
});
*/
