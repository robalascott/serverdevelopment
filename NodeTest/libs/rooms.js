/**
 * Created by robscott on 2016-12-27.
 */
var self = {
    init:function(roomlist){
        this.insert(roomlist,'General');
        this.insert(roomlist,'Java');
        return roomlist;
    },
    insert:function (roomlist,nameObject) {
        var roomtemp = {
            name: nameObject,
            people: []
        };
        return roomlist[roomtemp.name] = roomtemp;
    }

}



module.exports = self;

