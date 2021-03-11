//require new install module firebase-admin
const admin = require('firebase-admin');
//file that contains our auth token for firebase connect
let serviceAccount = require('./agruino-firebase-adminsdk-b0dvg-547f805181.json');
//start database
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});
//cath database
let db = admin.firestore();
//require a node module for throw commands in virtual console
var exec = require('child_process').exec, child;
//require new install module for real serial port
const SerialPort = require('serialport');
const { parseIsolatedEntityName } = require('typescript');
const ReadLine = SerialPort.parsers.Readline;
//set serialport & speed
const port = new SerialPort("/dev/ttyACM0", {
  baudRate: 9600
});
//read line by line
const parser = port.pipe(new ReadLine({ delimiter: '\r\n' }));

parser.on('open', function () {
  console.log('connection is opened');
});

parser.on('data', function (data) {
  let values = data.split(',');
  if (values[1] === 'INF') {
    values[1] = '0';
  }
  //set data
  let on = 'false', off = 'false', contOn = 0, contFalse = 0, contBad = 0;
  let moisture = values[0], conductivity = values[1], turbidity = values[2] , ph = values[3], temp = values[4];
  uploadData(moisture, conductivity, turbidity, ph, temp);
  console.log(values);
  if (values[0] <= 40 && values[1] <= 2 && values[2] <= 3 && (values[3] >= 5.5 || values[3] <= 7) && (values[4] > 0 || values[4] < 20)) {
    on = 'true'
  }
  else if (values[0] <= 40 || (values[1] > 2 && values[2] > 3 && (values[3] < 5.5 || values[3] > 7) && (values[4] <= 0 || values[4] >= 20))) {
    on = 'false'
  }
  else if (values[0] > 40) {
    on = 'false'
    off = 'true'
  }
  if (on === 'true' && contOn === 0) {
    contOn++;
    contFalse = 0;
    contBad = 0;
    //execute('Irrigation:on');
  }
  else {
    if (off === 'true' && contFalse === 0) {
      contFalse++;
      //execute('Irrigation_off')
    } else if (off === 'true' && contBad === 0) {
      contBad++;
      //execute('Irrigation_off_caused_by_bad_water_conditions');
    }
    contOn = 0;
  }
});

parser.on('error', (err) => console.log(err));
port.on('error', (err) => console.log(err));

//function for set new data
function uploadData(moisture, conductivity, turbidity, ph, temp) {
  //call funtion for stored log values 3 times per day
  if((new Date().getHours() == 7 || new Date().getHours() == 12 || new Date().getHours() == 24) && new Date().getMinutes() == 28 && new Date().getSeconds == 1){
    let date = new Date();
    let hours = date.getHours();
    let da = year+"-"+month+"-"+day;
    let year = date.getFullYear();
    let month = date.getMonth();
    let day = date.getDay();
    let dat = new Date(year+"-"+month+"-"+day);
    uploadLog(da,hours,dat,moisture, conductivity, turbidity, ph, temp);
  }

  //call function for get new values
  getHistoryValues(moisture,conductivity,turbidity,ph,temp);

  //update values
  uploadValues(moisture,conductivity,turbidity,ph,temp);
}

function uploadValues(moisture,conductivity,turbidity,ph,temp) {
  db.collection('values').doc('YC0HJwj1qynXC12LVpAV').update({
    moisture: parseFloat(moisture),
    conductivity: parseFloat(conductivity),
    turbidity: parseFloat(turbidity),
    ph: parseFloat(ph),
    temp: parseFloat(temp),
  });
}

function getHistoryValues(moisture,conductivity,turbidity,ph,temp) {
  var docRef = db.collection("values_history").doc("59DXbz5zbanZjBRhkTqv");
let mSum;
  docRef.get().then(function(doc) {
      if (doc.exists) {

          moisHis = doc.data().moisture;
          //mSum = moisHis + Number(moisture);
          /*console.log(typeof(moisHis))
          console.log(typeof(moisture))
          console.log("Document data-:",Number(moisture)+moisHis);*/
          conHis = doc.data().conductivity;
          //conHis += conductivity;
          turHis = doc.data().turbidity;
          //turHis += turbidity;
          phHis = doc.data().ph;
          //phHis += ph;
          tempHis = doc.data().temp;
          //tempHis += temp;
          //call function for stored new historical values
  uploadDataHistory(Number(moisture)+moisHis,Number(conductivity)+conHis,Number(turbidity)+turHis,Number(ph)+phHis,Number(temp)+tempHis)
      } else {
          // doc.data() will be undefined in this case
          console.log("No such document!");
      }
  }).catch(function(error) {
      console.log("Error getting document:", error);
  });
  return docRef;
}

//function  for set history data
function uploadDataHistory(moisture,conductivity,turbidity,ph,temp) {

  db.collection('values_history').doc('59DXbz5zbanZjBRhkTqv').update({
    moisture: parseFloat(moisture),
    conductivity: parseFloat(conductivity),
    turbidity: parseFloat(turbidity),
    ph: parseFloat(ph),
    temp: parseFloat(temp),
  });
}

//function  for set log data
function uploadLog(date1,hours,date,moisture,conductivity,turbidity,ph,temp) {

  const data = {
    dateString: date1,
    time: (hours),
    date: parseInt(date),
    moisture: parseFloat(moisture),
    conductivity: parseFloat(conductivity),
    turbidity: parseFloat(turbidity),
    ph: parseFloat(ph),
    temp: parseFloat(temp),
  };
  db.collection(new Date().toDateString).doc('2XO5YCdw8yeXiCZTeOwk').set(data);
}

//function for throw commands(in this case a sell script)
function execute(msg) {
  //execute script with param
  child = exec('sh sendMsg.sh ' + msg,

    function (error, stdout, stderr) {
      // print out
      if (error !== null) {
        console.log('exec error: ' + error);
      }
    });
}
