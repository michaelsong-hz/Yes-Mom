// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("createUser", function(request, response) {
Parse.Cloud.useMasterKey();
var newUser = new Parse.User();
var username = "apk";
var password = "toor";

newUser.setUsername( username );    
newUser.setPassword( password );    

newUser.signUp();
});

Parse.Cloud.define("clean", function (request, response) {
	Parse.Cloud.useMasterKey();
	var query = new Parse.Query(Parse.Object.extend("Jobs"));
	var d = new Date();
	var timeNow = d.getTime();
	var timeThen = timeNow - (3600 * 24 * 7 * 1000);   // 1 week. Time is in milliseconds 
	var queryDate = new Date();
	queryDate.setTime(timeThen); 
	query.lessThanOrEqualTo("createdAt", queryDate);
	
	query.limit(1000);
	query.find().then(function (results) {
		var promises = [];
		for (var i = 0; i < results.length - 1; i++) {
			promises.push(results[i].destroy());
		}
		return Parse.Promise.when(promises);
	}).then(function (success) {
		response.success("Alls well");
	}, function (error) {
		response.error(JSON.stringify(error));
	});
});

Parse.Cloud.define("remove", function (request, response) {
	
	var query = new Parse.Query(Parse.Object.extend("Jobs"));
	
	query.equalTo("toDelete", true);
	
	query.find().then(function (results) {
		var promises = [];
		for (var i = 0; i < results.length; i++) {
			promises.push(results[i].destroy());
		}
		return Parse.Promise.when(promises);
	}).then(function (success) {
		response.success("Alls well");
	}, function (error) {
		response.error(JSON.stringify(error));
	});
});




Parse.Cloud.job("delete", function (request, status) {
    Parse.Cloud.run('remove').then(function () {
	status.success("Deleted jobs");
    }, function (error) {
	status.error("Error " + error.code + ": " + error.message);	
    }); 
});

Parse.Cloud.job("expire", function (request, status) {
    //Parse.Cloud.useMasterKey();
    Parse.Cloud.run('clean').then(function () {
        status.success("Changed roles and cleared double");
    }, function (error) {
        status.error("Error " + error.code + ": " + error.message);
    });
});
