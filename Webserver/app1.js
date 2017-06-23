/*
  made by : JUNHEE PARK , South Korea
  company : Qualcomm Institute Korea IoT team
  supervisor : SEOKHEON CHO, Qualcomm institute princeple network architect
  title : Web server for Smart Cities Social Parking Service.
*/
var _webPort = 3000;
global.value = [];
const curl = require('curl');

var ml = require('machine_learning');
global.redis = require('redis');
var request = require('request');
global.express = require("express");
global.bodyParser = require('body-parser');
global.flow = require('flow-maintained');
global.app = express();
global.router = express.Router(); //라우터 객체 생성
global.path = __dirname;
var allowCORS = function(req, res, next) {
  res.header('Acess-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Methods', 'GET, PUT, POST, DELETE, OPTIONS');
  res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization, Content-Length, X-Requested-With');
  (req.method === 'OPTIONS') ?
    res.send(200) :
    next();
};
global.redis_client = redis.createClient();

// 이 부분은 app.use(router) 전에 추가하도록 하자
app.use(allowCORS);

router.use(function (req,res,next) { //page routiong
  console.log("client Request : " + req.method);
  next();
});

app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies
require('./controller/common.js'); //공통 데이터베이스 쿼리
//app.use("/",router);
app.listen(_webPort, function(){
  console.log("Server Running ..");
});
//잠깐정지
getGEparikingZoneInfo()

var token = ''; //token param

//Get paring zone information from the GE server.
function getGEparikingZoneInfo() {
  var grant_type_ = 'client_credentials';
  var client_id_ = 'hackathon';
  var client_secrit_ = '@hackathon';
  var uri = 'https://890407d7-e617-4d70-985f-01792d693387.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token?grant_type=client_credentials';
  var body = "client_id=hackathon&client_secret=@hackathon"
  //get token
  curl.post(uri,body,'', function(err, response, body){
    token = JSON.parse(body).access_token; //save the token
    var eventurl = 'https://ic-metadata-service.run.aws-usw02-pr.ice.predix.io/v2/metadata';
    var bbox = '32.715675:-117.161230,32.708498:-117.151681';
    var url_ = eventurl+'/locations/search?q=locationType:PARKING_ZONE&bbox='+bbox+'&page=0&size=50';
    var options_ = {
      headers : {
        "Authorization": "Bearer " + token,
        "Predix-Zone-Id" : "SDSIM-IE-ENVIRONMENTAL"
      }
    }
    var gpsData
    curl.get(url_, options_, function(err, response, body) {
      gpsData = JSON.parse(body);

      parsingGPSData(gpsData);
      makeDataFormat(gpsData);

      //for showing sample data
      for(var i =0; i<gpsData.content.length; i++){
        data = gpsData.content[i]
        setParkingZoneInfo(data.locationUid, data.type ,data.coordinates, data.midCoordinates, data.locationTitle, data.price, data.totalQty , data.availableQty, data.availableStartTime, data.availableEndTime);

      }
      //update all data
      for(var i =0; i<gpsData.content.length; i++){
        data = gpsData.content[i];
        setStandardPKINinfo(data.locationUid);
        setStandardPKOUTinfo(data.locationUid);
      }

    });
    for(var i = 144 ; i > 1; i --){
      machine_learning_LOCATION_307(i);
    }
    setInterval(function() {
      for(var i =0; i<gpsData.content.length; i++){
        data = gpsData.content[i];
        UpdatePKOUTinfo(data.locationUid);
        updatePKINinfo(data.locationUid);
      }
      console.log('mornitoring every 5 seconds');
    }, 5000);
  });
}
//Get the location park in info.
function setStandardPKINinfo(locationUid) {
  var eventUrl = 'https://ic-event-service.run.aws-usw02-pr.ice.predix.io/v2/locations/';
  var urlQuery1 = '/events?eventType=PKIN&startTime=';
  var startTime = Date.now() - 3600000;
  var urlQuery2 = '&endTime='
  var endTime = Date.now();
  var url = eventUrl+locationUid+urlQuery1+startTime.toString()+urlQuery2+endTime.toString();
  var options = {
    headers : {
      "Authorization": "Bearer " + token,
      "Predix-Zone-Id" : "SDSIM-IE-PARKING"
    }
  }
  curl.get(url, options, function(err, response, body) {
    var parkingData = JSON.parse(body);
    var count = 0;
    //console.log(parkingData.content);
    for ( var i in parkingData.content) {
      count ++;
    }
    try{
      console.log('update ', parkingData.content[i].locationUid, ' -', count, 'from ', endTime);
      var user_string = 'location:' + parkingData.content[i].locationUid;
      redis_client.hget(user_string, 'availableQty', function(err, replie){

        var counter = parseInt(replie) - count;
        redis_client.hset(user_string, 'availableQty', counter.toString());
      });
  } catch (e){

  }
  });
}
//update location park in info.
function updatePKINinfo(locationUid) {
  var eventUrl = 'https://ic-event-service.run.aws-usw02-pr.ice.predix.io/v2/locations/';
  var urlQuery1 = '/events?eventType=PKIN&startTime=';
  var startTime = Date.now() - 5000;
  var urlQuery2 = '&endTime='
  var endTime = Date.now();
  var url = eventUrl+locationUid+urlQuery1+startTime.toString()+urlQuery2+endTime.toString();
  var options = {
    headers : {
      "Authorization": "Bearer " + token,
      "Predix-Zone-Id" : "SDSIM-IE-PARKING"
    }
  }
  curl.get(url, options, function(err, response, body) {
    var parkingData = JSON.parse(body);
    var count = 0;
    for ( var i in parkingData.content) {
      count ++;
    }
    try{
      console.log('update ', parkingData.content[i].locationUid, ' -', count, 'from ', endTime);
      var user_string = 'location:' + parkingData.content[i].locationUid;
      redis_client.hget(user_string, 'availableQty', function(err, replie){

        var counter = parseInt(replie) - count;
        redis_client.hset(user_string, 'availableQty', counter.toString());
      });
  } catch (e){

  }
  });
}
//Get the location park out info.
function setStandardPKOUTinfo(locationUid) {
  var eventUrl = 'https://ic-event-service.run.aws-usw02-pr.ice.predix.io/v2/locations/';
  var urlQuery1 = '/events?eventType=PKOUT&startTime=';
  var startTime = Date.now() - 3600000;
  var urlQuery2 = '&endTime='
  var endTime = Date.now();
  var url = eventUrl+locationUid+urlQuery1+startTime.toString()+urlQuery2+endTime.toString();
  var options = {
    headers : {
      "Authorization": "Bearer " + token,
      "Predix-Zone-Id" : "SDSIM-IE-PARKING"
    }
  }
  curl.get(url, options, function(err, response, body) {
    var parkingData = JSON.parse(body);
    var count = 0;
    for ( var i in parkingData.content) {
      count ++;
    }
    try{
      console.log('update ', parkingData.content[i].locationUid, ' +', count, 'from ', endTime);
      var user_string = 'location:' + parkingData.content[i].locationUid;
      redis_client.hget(user_string, 'availableQty', function(err, replie){

        var counter = parseInt(replie) + count;
        redis_client.hset(user_string, 'availableQty', counter.toString());
      });
  } catch (e){

  }
  });
}
//update location park out info.
function UpdatePKOUTinfo(locationUid) {
  var eventUrl = 'https://ic-event-service.run.aws-usw02-pr.ice.predix.io/v2/locations/';
  var urlQuery1 = '/events?eventType=PKOUT&startTime=';
  var startTime = Date.now() - 5000;
  var urlQuery2 = '&endTime='
  var endTime = Date.now();
  var url = eventUrl+locationUid+urlQuery1+startTime.toString()+urlQuery2+endTime.toString();
  var options = {
    headers : {
      "Authorization": "Bearer " + token,
      "Predix-Zone-Id" : "SDSIM-IE-PARKING"
    }
  }
  curl.get(url, options, function(err, response, body) {
    var parkingData = JSON.parse(body);
    var count = 0;
    //console.log(parkingData.content);
    for ( var i in parkingData.content) {
      count ++;
    }
    try{
      console.log('update ', parkingData.content[i].locationUid, ' +', count, 'from ', endTime);
      var user_string = 'location:' + parkingData.content[i].locationUid;
      redis_client.hget(user_string, 'availableQty', function(err, replie){

        var counter = parseInt(replie) + count;
        redis_client.hset(user_string, 'availableQty', counter.toString());

      });
  } catch (e){

  }
  });
}
//Setup Paring zone information customaly
function setParkingZoneInfo(locationUid, type, coordinates, midCoordinates, locationTitle, price, totalQty, availableQty, availableStartTime, availableEndTime){
  //predix data insert into redis server
  flow.exec(
    function(){
        var user_string = 'location:' + locationUid;
        redis_client.hset(user_string, 'locationUid', locationUid, this.MULTI());
        redis_client.hset(user_string, 'type', type, this.MULTI());
        redis_client.hset(user_string, 'coordinates', coordinates, this.MULTI());
        redis_client.hset(user_string, 'midCoordinates', midCoordinates, this.MULTI());
        redis_client.hset(user_string, 'locationTitle', locationTitle, this.MULTI());
        redis_client.hset(user_string, 'price', price, this.MULTI());
        redis_client.hset(user_string, 'lastupdatedTime', '0', this.MULTI());
        redis_client.hset(user_string, 'totalQty', totalQty, this.MULTI());
        if(locationUid == 'LOCATION-307') {
          redis_client.hset(user_string, 'predictQty', svm.predict([1.3]), this.MULTI());
        } else {
          redis_client.hset(user_string, 'predictQty', availableQty, this.MULTI());
        }

        redis_client.hset(user_string, 'availableQty', availableQty, this.MULTI());
        redis_client.hset(user_string, 'availableStartTime', availableStartTime, this.MULTI());
        redis_client.hset(user_string, 'availableEndTime', availableEndTime, this.MULTI());
        console.log('updated the location info [',locationUid,']');
    }, function(args){
       //console.log('-Redis-added-'+locationUid+'-');
    }
  )
}
//Parsing the GPS Data
function parsingGPSData(gpsData){
  //console.log(gpsData.content);
  //return;
  for(var i =0; i<gpsData.content.length; i++){
    // console.log(gpsData.content[i].coordinates);
    var obj = gpsData.content[i].coordinates;
    var splitData = obj.split(",");
    var splitData2;
    var combined;
    var midLat;
    var midLng;
      for(var j = 0; j<splitData.length; j++){
      if(j == 0){
        splitData2 = splitData[j].split(":");
        midLat = parseFloat(splitData2[0]);
        midLng = parseFloat(splitData2[1]);
        combined =  midLat.toFixed(6)+','+midLng.toFixed(6)+':';
      }
      else if(j == splitData.length-1){
        splitData2 = splitData[j].split(":");
        midLat += parseFloat(splitData2[0]);
        midLng += parseFloat(splitData2[1]);
        combined +=  parseFloat(splitData2[0]).toFixed(6)+','+parseFloat(splitData2[1]).toFixed(6);
      }
      else if(j>0 && j<splitData.length-1){
        splitData2 = splitData[j].split(":");
        midLat += parseFloat(splitData2[0]);
        midLng += parseFloat(splitData2[1]);
        combined +=  parseFloat(splitData2[0]).toFixed(6)+','+parseFloat(splitData2[1]).toFixed(6)+':';
      }
    }
    var newString = combined;
    midLat = (midLat/4).toFixed(6);
    midLng = (midLng/4).toFixed(6);
    gpsData.content[i].coordinates = newString;
    gpsData.content[i].midCoordinates = midLat.toString()+","+midLng.toString();
  }
}
//Make Data format for saving
function makeDataFormat(gpsData){
  for(var i = 0; i<gpsData.content.length; i++){
    gpsData.content[i].type = "public";
    gpsData.content[i].locationTitle = "streetPark"+(i+1);
    if(i%3==1){
    gpsData.content[i].price = "$";
    }
    else if(i%3 ==2){
    gpsData.content[i].price = "$$";
    }
    else{
      gpsData.content[i].price = "FREE";
    }

    var totalQty = 20;
    var availableQty = 0;
    var availableStartTime = 0;
    var availableEndTime = 0;

    gpsData.content[i].totalQty = totalQty.toString();
    gpsData.content[i].availableQty = availableQty.toString();
    gpsData.content[i].availableStartTime = availableStartTime.toString();
    gpsData.content[i].availableEndTime = availableEndTime.toString();

    delete gpsData.content[i].name;
    delete gpsData.content[i].parentLocationUid;
    delete gpsData.content[i].locationType;
    delete gpsData.content[i].coordinatesType;
  }
}

function machine_learning_LOCATION_307 (count){
  var eventUrl = 'https://ic-event-service.run.aws-usw02-pr.ice.predix.io/v2/locations/';
  var urlQuery1 = '/events?eventType=PKIN&startTime=';
  var urlQuery2 = '/events?eventType=PKOUT&startTime=';
  var startTime = Date.now() - 600000 * count;
  var urlQuery2 = '&endTime='
  var endTime = startTime + 600000;
  var urlType1 = eventUrl+'LOCATION-307'+urlQuery1+startTime.toString()+urlQuery2+endTime.toString();
  var capacity = 0;
  var urlType2 = eventUrl+'LOCATION-307'+urlQuery2+startTime.toString()+urlQuery2+endTime.toString();
  var options = {
    headers : {
      "Authorization": "Bearer " + token,
      "Predix-Zone-Id" : "SDSIM-IE-PARKING"
    }
  }
  curl.get(urlType1, options, function(err, response, body) {
    var parkingData = JSON.parse(body);
    var count = 0;
    //console.log(parkingData.content);
    for ( var i in parkingData.content) {
      count ++;
    };
    curl.get(urlType2, options, function(err, response, body) {
      var parkingData = JSON.parse(body);
      //console.log(parkingData.content);
      for ( var i in parkingData.content) {
        count --;
      };
      if(count > 0) {
        if(parseInt(count/10) > 3){
          value.push(3);
        }else {
          value.push(parseInt(count/10));
        }
      }
    });
  });
}
var x = [];
var y = [];
for (var i = 0 ; i < value.length-1; i++){
  x.push([value[i],value[i+1]])
  y.push(value[i+1])
}
var svm = new ml.SVM({
    x : x,
    y : y
});

svm.train({
    C : 1.1, // default : 1.0. C in SVM.
    tol : 1e-5, // default : 1e-4. Higher tolerance --> Higher precision
    max_passes : 20, // default : 20. Higher max_passes --> Higher precision
    alpha_tol : 1e-5, // default : 1e-5. Higher alpha_tolerance --> Higher precision

    kernel : { type: "polynomial", c: 1, d: 5}
    // default : {type : "gaussian", sigma : 1.0}
    // {type : "gaussian", sigma : 0.5}
    // {type : "linear"} // x*y
    // {type : "polynomial", c : 1, d : 8} // (x*y + c)^d
    // Or you can use your own kernel.
    // kernel : function(vecx,vecy) { return dot(vecx,vecy);}
});
