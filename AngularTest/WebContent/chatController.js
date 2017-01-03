// Chat Controller
// Current Experiment, Apperently the [] are to prevent minification to break the code (TODO: read about it and ng-annotate)
app.controller('chatController', ["$scope", "mySocket", "Page", "Auth","$rootScope", function($scope, mySocket, Page, Auth,$rootScope) {
	
	$scope.messages = [];
    $scope.friendsList = $rootScope.name;
    $scope.activeRooms = $rootScope.rooms;
	$scope.Page = Page;
	$scope.name = Auth.getDisplayName();
	$scope.activeRoom = "thing";
	Page.setTitle("Lets Chat");
	console.log("In chatController");
	
	mySocket.addEventListener("message", function(data) {
		var message = JSON.parse(data.data);
		
		switch(message.type){
			case "message":
				console.log("chatController: message under processing");
				if(message.room){
					$scope.$apply(function() {
						$scope.messages.push({user: message.user, message: message.text, room: message.room});
					});
				}else{
					$scope.$apply(function() {
						$scope.messages.push({user: message.user, message: message.text, room: "General"});
					});
				}
				break;
            case "changeroom":
            	if(message.status === "OK"){
	                console.log("changeroom " + message.room);
	                $scope.$apply(function() {
	                	$scope.activeRoom = message.room;
			        });
            	}else{
            		console.log("Not allowed");
            	}
                break;
			case "updateUsersConnectedList":
				console.log(message.userList[0]);
				console.log("Got listupdate (Items: " + message.userList.length + ")");
				// Add changeroom-check
		        $scope.$apply(function() {
		            $scope.friendsList = message.userList;
		        });
				break;
			case "updateRooms":
				console.log("Room update");
				$scope.$apply(function() {
			            $scope.activeRooms = message.roomList;
		        });
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
			  if(($scope.message.substring(0, changeRoom.length) == changeRoom)){
				  console.log("User want to change room");
				  var tmp = $scope.message.substring(changeRoom.length);
				  console.log("Want to change room to: " + tmp);
				  // Should probably check if exist/allowed
				  $scope.activeRoom = tmp;
				  //Should check/handle strange/invalid input
			  }else{
				  console.log("Sending text: " + $scope.message + "to room " + $scope.activeRoom);
		        	mySocket.send(JSON.stringify({type: "message", text: $scope.message, room: $scope.activeRoom}));
			  }
			  $scope.message = '';
		  }
	}
	
	$scope.enterGroup = function(group){
		console.log("Want to enter group: " + group);
		mySocket.send(JSON.stringify({type: "command", command: "changeRoom", room: group}));
	}
	
	$scope.createGroup = function(){
		console.log("Want to create group: " + $scope.newGroup);
		mySocket.send(JSON.stringify({type: "command", command: "createRoom", room: $scope.newGroup}));
		$scope.newGroup = '';
	}
}])