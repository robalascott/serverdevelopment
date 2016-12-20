
var app = angular.module('myApp', [
	'btford.socket-io',
	'ngRoute',
	'ui.bootstrap',
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
	$rootScope.name = [];
	$rootScope.$on("$locationChangeStart", function(event) {
		console.log('Running Auth, path = ' + $location.path());
		if (!Auth.isLoggedIn()) {
			//Redirect to login
            console.log('Not logged in');
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
            //Redirect to suitable page
            $location.path('/home');
        }
	});
}]);

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

// Chat Controller
// Current Experiment, Apperently the [] are to prevent minification to break the code (TODO: read about it and ng-annotate)
app.controller('myCtrl', ["$scope", "mySocket", "Page", "Auth","$rootScope", function($scope, mySocket, Page, Auth,$rootScope) {
	
	$scope.messages = [];
    $scope.friendsList = $rootScope.name;
	$scope.Page = Page;
	$scope.name = Auth.getDisplayName();
	$scope.activeRoom = "General";
	Page.setTitle("Lets Chat");
	console.log("In myCtrl");
		
	mySocket.on('send:message', function(data) {
		console.log("Got message: " + data.text.toString() + " from: " + data.user.toString());
		
		if(data.room){
			$scope.$apply(function() {
				$scope.messages.push({user: data.user, message: data.text, room: data.room});
			});
		}else{
			$scope.$apply(function() {
				$scope.messages.push({user: data.user, message: data.text, room: "General"});
			});
		}
	});
    mySocket.on('send:update', function(userslist) {
        console.log("Got update: " + userslist);
        alert(userslist);
        $scope.$apply(function() {
            $scope.friendsList.push(userslist);
        });

    });


    // The user click send
	$scope.sendMessage = function () {
		console.log("Called SendMessage: " + $scope.message);
		  event.preventDefault();
		  if ($scope.message) {
			  var changeRoom = "/change "
			  if(($scope.message.substring(0, changeRoom.length) == changeRoom)){
				  console.log("User want to change room");
				  var tmp = $scope.message.substring(changeRoom.length);
				  console.log("Want to change room to: " + tmp);
				  // Should probably check if exist/allowed
				  $scope.activeRoom = tmp;
				  //Should check/handle strange/invalid input
			  }else{
				  console.log("Sending text: " + $scope.message + "to room " + $scope.activeRoom);
		        	
			    	mySocket.emit('send:message', {
			    		message: $scope.message,
			    		room: $scope.activeRoom
			    	});
			  }
			  $scope.message = '';
		  }
	}
}])

//TODO: Is this an acceptable solution? Removing the listener after getting response? Was accedently creating a new 
// listener every call before, quickly resulted in a bunch of them
function waitforServerResponse($q, $timeout, Auth){
    return $q(function(resolve, reject){
		var timeoutPromise = $timeout(function(){
		  console.log("Rejecting: timeout");
		  reject("Timeout");
		}, 5000);
  
		mySocket.on('authenticate', function(data) {
			console.log("Auth reply: " + status);
			if(data.status === "success"){
				$timeout.cancel(timeoutPromise);
				mySocket.removeAllListeners("authenticate");
				resolve("Correct");
			}else{
				$timeout.cancel(timeoutPromise);
				mySocket.removeAllListeners("authenticate");
				reject("Denied");
			}
		});
	});
}

app.directive('myDirective', function($timeout) {
    return {
        restrict: 'A',
        link: function(scope, element) {
        	scope.myStyle = {'background':'black'};
            scope.height = element.prop('offsetHeight').toString().trim();
            scope.width = element.prop('offsetWidth');
        }
    };
});

app.controller('AlertDemoCtrl', function ($scope) {
	  $scope.alerts = [
	    { type: 'danger', msg: 'Oh snap! Change a few things up and try submitting again.' },
	    { type: 'success', msg: 'Well done! You successfully read this important alert message.' }
	  ];

	  $scope.addAlert = function() {
	    $scope.alerts.push({msg: 'Another alert!'});
	  };

	  $scope.closeAlert = function(index) {
	    $scope.alerts.splice(index, 1);
	  };
});

function waitfordata($q,$rootScope){
    return $q(function(resolve){
       console.log("updateall: outer shell" );
        mySocket.on('updateall', function(object) {
            console.log("update: " + object.ob.usersobject);
            var temp = object.ob.usersobject[0];
            if(temp!=null){
                for (var key in temp) {
                    if (temp.hasOwnProperty(key)) {
                        $rootScope.name.push(temp[key]);
                    }
                }

            }
            resolve(true);
        });
        resolve(false);
    });
}