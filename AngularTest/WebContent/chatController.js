// Chat Controller
// Current Experiment, Apperently the [] are to prevent minification to break the code (TODO: read about it and ng-annotate)
app.controller('chatController', ["$scope", "mySocket", "Page", "Auth","$rootScope", function($scope, mySocket, Page, Auth,$rootScope) {
	
	$scope.messages = [];
    $scope.friendsList = [];
    $scope.activeRooms = [];
	$scope.Page = Page;
	$scope.name = Auth.getDisplayName();
	$scope.activeRoom;
	Page.setTitle("Lets Chat");
	console.log("In chatController");

	if($scope.friendsList<=0){
		console.log('empty list');
        mySocket.send(JSON.stringify({type: "command", command: "update", room: 'General'}));
	}
	mySocket.addEventListener("message", function(data) {
		var message = JSON.parse(data.data);
		
		switch(message.type){
			case "message":
				console.log("chatController: message under processing");
				if(message.room){
					$scope.$apply(function() {
					    console.log('room = ' +message.room);
						$scope.messages.push({user: message.user, message: message.text, room: message.room});
					});
				}else{
					$scope.$apply(function() {
						$scope.messages.push({user: message.user, message: message.text, room: "General"});
					});
				}
				break;
            case "changeroom":
                console.log("changeroom" + message.msg);
                $scope.activeRoom = message.msg;
                break;
            case "clearlist":
                console.log("clearlist" + message.data);
                $scope.messages = [];
                break;
            case "update":
                console.log("update" + message.list);
                $scope.$apply(function() {
                $scope.friendsList = message.list;
                if($scope.activeRoom==undefined){
                    $scope.activeRoom = message.room;
				}
                });
                break;
			case "info":
				// Add changeroom-check
				console.log("update: " + message.ob.usersobject);
		        $scope.$apply(function() {
		            $rootScope.name = [];
		            var temp = message.ob.usersobject[0];
		            if (temp != null) {
		                for (var key in temp) {
		                    if (temp.hasOwnProperty(key)) {
		                        $rootScope.name.push(temp[key]);
		                    }
		                }
		            }
		        });
		        $scope.friendsList = $rootScope.name;
				break;
			default: 
				// Ignore message
		}
		
	});
	
	/*
	mySocket.on('send:changeroom', function(data) {
        console.log("Got changeroom: " + " from: " + data.msg.toString());
        $scope.activeRoom = data.msg.toString();

    });
    */
	/*
    mySocket.on('updateall', function(object) {
        console.log("update: " + object.ob.usersobject);
        $scope.$apply(function() {
            $rootScope.name = [];
            var temp = object.ob.usersobject[0];
            if (temp != null) {
                for (var key in temp) {
                    if (temp.hasOwnProperty(key)) {
                        $rootScope.name.push(temp[key]);
                    }
                }
            }
        });
        $scope.friendsList = $rootScope.name;
    });
    */


    // The user click send
	$scope.sendMessage = function () {
		console.log("Called SendMessage: " + $scope.message);
		  event.preventDefault();
		  if ($scope.message) {
			  var changeRoom = "/change "
              var createRoom = "/create "
			  if(($scope.message.substring(0, changeRoom.length) == changeRoom)){
				  console.log("User want to change room");
				  var tmp = $scope.message.substring(changeRoom.length);
				  console.log("Want to change room to: " + tmp);
				  // Should probably check if exist/allowed
				  $scope.activeRoom = tmp;
				  //Should check/handle strange/invalid input
			  }else if(($scope.message.substring(0, createRoom.length) == createRoom)){
                  var tmp = $scope.message.substring(changeRoom.length);
                  console.log('create' + tmp)
                  mySocket.send(JSON.stringify({type: "command", command: "create", room: tmp}));
              }else{
				  console.log("Sending text: " + $scope.message + "to room " + $scope.activeRoom);
		        	mySocket.send(JSON.stringify({type: "message", text: $scope.message, room: $scope.activeRoom}));
			  }
			  $scope.message = '';
		  }
	}
}])