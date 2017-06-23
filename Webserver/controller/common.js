app.post('/getAllZoneInfo', function(req, res){
  var resObject = [];
  var keySet = []
  flow.exec(
    function(){
      redis_client.multi().keys("location:*", function(err, replies){
        if (replies.length != 0)
          redis_client.mget(replies);
      }).dbsize().exec(function(err, replies){
        keySet = replies[0];
        flow.serialForEach(replies[0], function(el){
          redis_client.hgetall(el,this);
        },function (err, obj){
          resObject.push(obj);
        }, function(){
          //console.log('-Redis-searched-all device-');
          //console.log(resObject);
          res.send(resObject); //이때 콜백함수의 결과를 돌려줌
        });
      });
    }
  )
});
app.post('/insertNewLocation', function(req,res) {
  flow.exec(
    function(){
        var user_string = 'location:' + 'QUALCOMM_INSTITUTE';
        redis_client.hset(user_string, 'locationUid', 'QUALCOMM_INSTITUTE', this.MULTI());
        redis_client.hset(user_string, 'type', 'private', this.MULTI());
        redis_client.hset(user_string, 'coordinates', '32.724780, -117.163575:32.724780, -117.163575:32.724780, -117.163575:32.724780, -117.163575', this.MULTI());
        redis_client.hset(user_string, 'midCoordinates', '32.724780, -117.163575', this.MULTI());
        redis_client.hset(user_string, 'locationTitle', 'ALPHA_QI', this.MULTI());
        redis_client.hset(user_string, 'price', '$3000', this.MULTI());
        redis_client.hset(user_string, 'lastupdatedTime', '0', this.MULTI());
        redis_client.hset(user_string, 'predictQty', '0', this.MULTI());
        redis_client.hset(user_string, 'totalQty', '1', this.MULTI());
        redis_client.hset(user_string, 'availableQty', '1', this.MULTI());
        redis_client.hset(user_string, 'availableStartTime', '8:00', this.MULTI());
        redis_client.hset(user_string, 'availableEndTime', '18:00', this.MULTI());
        console.log('>>>>>>    added the location info [','QUALCOMM_INSTITUTE',']');
    }, function(args){
       //console.log('-Redis-added-'+locationUid+'-');
    }
  )
});

app.post('/deleteLocation', function(req, res){
  flow.exec(
    function(){
        var user_string = 'location:' + 'QUALCOMM_INSTITUTE';
        redis_client.del(user_string, function(err, o) {
          console.log('>>>>>>    deleted the location info [','QUALCOMM_INSTITUTE',']');
        });
    }
  );
})

app.post('/updatePKIN', function (req, res) {
  var user_string = 'location:' + 'QUALCOMM_INSTITUTE';
  redis_client.hget(user_string, 'availableQty', function(err, replie){
    if(parseInt(replie) == 0) return;
    var counter = parseInt(replie) - 1;
    redis_client.hset(user_string, 'availableQty', counter.toString());
    console.log('>>>>>>    update ', 'QUALCOMM_INSTITUTE', ' -', '1', 'from ', Date.now());
    res.send('PKIN done');
  });
});

app.post('/updatePKOUT', function (req, res) {
  var user_string = 'location:' + 'QUALCOMM_INSTITUTE';
  redis_client.hget(user_string, 'availableQty', function(err, replie){
    if(parseInt(replie) == 1) return;
    var counter = parseInt(replie) + 1;
    redis_client.hset(user_string, 'availableQty', counter.toString());
    console.log('>>>>>>    update ', 'QUALCOMM_INSTITUTE', ' +', '1', 'from ', Date.now());
    res.send('PKOUT done');
  });
});
