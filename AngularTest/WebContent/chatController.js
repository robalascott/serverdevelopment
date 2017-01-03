// Chat Controller
// Current Experiment, Apperently the [] are to prevent minification to break the code (TODO: read about it and ng-annotate)
app.controller('chatController', ["$scope", "mySocket", "Page", "Auth","$rootScope", function($scope, mySocket, Page, Auth,$rootScope) {
	
	$scope.messages = [];
	// TODO: Remove rootscope (replace with init-request)
    $scope.friendsList = $rootScope.name;
    $scope.activeRooms = $rootScope.rooms;
	$scope.Page = Page;
	$scope.name = Auth.getDisplayName();
	$scope.activeRoom;
	Page.setTitle("Lets Chat");
	console.log("In chatController");

	/* // Should request init of groups and friendslist
	if($scope.friendsList<=0){
		console.log('empty list');
        mySocket.send(JSON.stringify({type: "command", command: "update", room: 'General'}));
	}*/

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
            	if(message.status === "OK"){
	                console.log("changeroom " + message.room);
	                $scope.$apply(function() {
	                	$scope.activeRoom = message.room;
	                	$scope.messages = [];
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

    // The user click send
	$scope.sendMessage = function () {
		console.log("Called SendMessage: " + $scope.message);
		  event.preventDefault();
		  if ($scope.message) {
			console.log("Sending text: " + $scope.message + "to room " + $scope.activeRoom);
		   	mySocket.send(JSON.stringify({type: "message", text: $scope.message, room: $scope.activeRoom}));
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