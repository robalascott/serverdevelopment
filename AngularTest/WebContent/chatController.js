// Chat Controller
// Current Experiment, Apperently the [] are to prevent minification to break the code (TODO: read about it and ng-annotate)
app.controller('chatController', ["$scope", "mySocket", "Page", "Auth", function($scope, mySocket, Page, Auth) {
	
	// Variables
	$scope.messages = [];
    $scope.friendsList = [];
    $scope.activeRooms = [];
    $scope.activeRoom;
	$scope.name = Auth.getDisplayName();
	$scope.Page = Page;
	Page.setTitle("Lets Chat");

	// Request data from server
    mySocket.send(JSON.stringify({type: "command", command: "init"}));

    // Add listener/handler for messages from server
	mySocket.addEventListener("message", function(data) {
		var message = JSON.parse(data.data);
		
		// Proccess message according to type
		switch(message.type){
			case "message":
				console.log("Got a message from server: (Room: " + message.room + ")" + "(From: " + message.user + " Message: " + message.text + ")");
				// TODO: Only create room messages got attribute room (All others are undefined), not really a problem since we dont use it
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
			case "init":
				// Requested update from server
				$scope.$apply(function() {
			            $scope.activeRooms = message.roomList;
			            $scope.friendsList = message.userList;
			            $scope.activeRoom = message.currentRoom;
		        });
				break;
			case "updateRooms":
				$scope.$apply(function() {
		            $scope.activeRooms = message.roomList;
				});
				break;
			default: 
				// Ignore message (type not handled by this listener)
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