/**
 * Created by robscott on 2016-12-27.
 */

// Chat Controller
// Current Experiment, Apperently the [] are to prevent minification to break the code (TODO: read about it and ng-annotate)
app.controller('chatController', ["$scope", "mySocket", "Page", "Auth","$rootScope", function($scope, mySocket, Page, Auth,$rootScope) {

    $scope.messages = [];
    $scope.friendsList = $rootScope.name;
    $scope.activeRooms = [];
    $scope.Page = Page;
    $scope.name = Auth.getDisplayName();
    $scope.activeRoom = "General";
    Page.setTitle("Lets Chat");
    console.log("In myCtrl");

    if($scope.friendsList===undefined){

    }

    mySocket.on('send:message', function(data) {
        console.log("Got message: " + data.text + " from: " + data.user.toString());

        if(data.room){
            if(data.room === $scope.activeRoom) {
                $scope.$apply(function () {
                    $scope.messages.push({user: data.user, message: data.text, room: data.room});
                });
            }
        }
    });
    mySocket.on('send:changeroom', function(data) {
        console.log("Got changeroom: " + " from: " + data.msg.toString());
        $scope.activeRoom = data.msg.toString();

    });
    mySocket.on('updateall', function(object) {
        console.log("update: " + object.ob.usersobject);
        $scope.$apply(function() {
            $rootScope.name = [];
            var temp = object.ob.usersobject[0];
            if (temp != null ) {
                for (var key in temp) {
                    if (temp.hasOwnProperty(key)) {
                         $rootScope.name.push(temp[key]);
                    }
                }
            }
        });
        $scope.friendsList = $rootScope.name;
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