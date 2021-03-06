
var app = angular.module('myApp', [
	'btford.socket-io',
	'ngRoute',
	'ui.bootstrap',
]).factory('mySocket', function (socketFactory) {
	
	var mySocket = new WebSocket('ws://127.0.0.1:1337/');
	return mySocket;
	
	mySocket.onopen = function (event) {
        console.log("Open!");
    };
	

 
	
	//var mySocket = new EventBus("http://127.0.0.1:1337/eventbus/");
	 //var mySocket = new SockJS('http://127.0.0.1:1337/eventbus/');
	 
	 
	/*
	// Create the IO-Socket
	var myIoSocket = io.connect('http://127.0.0.1:1337/');

	  mySocket = socketFactory({
	    ioSocket: myIoSocket
	  });

	  return mySocket;
	*/
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
		logout : function(){
			console.log("Logging out");
			user = null;
		},
		getDisplayName: function(){
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
	    controller: 'chatController',
	  });
	  $routeProvider.when('/register', {
		    templateUrl: 'register.html',
		    controller: 'registerController',
		  });
	  $routeProvider.otherwise({ redirectTo: '/login' });
	  //$locationProvider.html5Mode({enabled:true, requireBase:false}); //- Can play with this later: Remove the # in address and (Make it not so ugly)
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
            $rootScope.loggedIn = false;
            if($location.path().localeCompare("/login") != 0 && $location.path().localeCompare("/register") != 0){
            	console.log('Redirecting to login');
	            event.preventDefault();
	            $location.path('/login');
			}else{
				console.log("Allowed page");
			}
        }
        else {
        	//Should check if permission
            console.log('Allowed');
            $rootScope.loggedIn = true;
            //Redirect to suitable page
            $location.path('/home');
        }
	});
}]);

app.controller('mainCtrl', ['$scope', 'Auth', '$location', "Page", "mySocket", function ($scope, Auth, $location, Page, mySocket) {
	
	$scope.Page = Page;
	$scope.logout = Auth.logout;
	Page.setTitle("Home Page");
	
	// Watch the value of Auth isLoggedIn function, if it changes this is triggered
	$scope.$watch(Auth.isLoggedIn, function (authenticated, previouslyAuthenticated) {
	console.log("Status of Auth changed, newValue = " + authenticated + " oldValue: " + previouslyAuthenticated);
	
	// If we were Authenticated and now we are not
	if(!authenticated && previouslyAuthenticated) {
		console.log("No longer authenticated, sending logout to server");
		
		// TODO: You can create/define close-event to inform server that it was intended
		//mySocket.onclose = function(){};

		// Inform server that we intend to logout
		var logoutReq = {type: "logout"};
		mySocket.send(JSON.stringify(logoutReq));
		
		// TODO: Consider waiting for a reply (If we want to know we logged out successfully)
		// Redirect to login-page
		$location.path('/login');
	}
	
    if(authenticated) {
    	console.log("User Authenticated");
    	// Redirect to Home
    	$location.path('/main');
    }

	}, true);
}]);


//TODO: Is this an acceptable solution? Removing the listener after getting response? Was accedently creating a new 
// listener every call before, quickly resulted in a bunch of them
function waitforServerResponse($q, $timeout, Auth, mySocket){
    return $q(function(resolve, reject){
		var timeoutPromise = $timeout(function(){
		  console.log("Rejecting: timeout");
		  reject("Timeout");
		}, 5000);
		console.log("in wait");
		mySocket.onmessage = function(data) {
			console.log("Got: " + data);
			console.log("Data: " + data.data);
			var message = JSON.parse(data.data);
			console.log("type: " + message.type);
			if(message.type === "authentication"){
				console.log("Auth reply: " + message.status);
				if(message.status === "success"){
					$timeout.cancel(timeoutPromise);
					resolve("Correct");
				}else{
					$timeout.cancel(timeoutPromise);
					reject("Denied");
				}
			}
		};
	});
}