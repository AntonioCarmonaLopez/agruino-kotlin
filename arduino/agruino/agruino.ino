//include libraries for water temperature sensor
//#include <OneWire.h>
//#include <DallasTemperature.h>
//analogic input pin for moisture sensor
#define moistureSensor A0
//analogic input pin for conductivity sensor
#define conductivitySensor A1
//analogic input pin for turbidity sensor
//#define TurbidySensor A2
//analogic input pin for ph meter
//#define phPin A3
//digital pin for led
#define LED 9
// digital pin for temp
//const int pinDatosDQ = 10;
// Instance of OneWire & DallasTemperature classes
//OneWire oneWireObjeto(pinDatosDQ);
//DallasTemperature sensorDS18B20(&oneWireObjeto);
//define gobal vars
float mois = 0, con = 0, tur = 0, phG = 0, te = 0;

void setup() {
  //set as input moisture sensor
  pinMode(moistureSensor, INPUT);
  //set as input conductivity sensor
  pinMode(conductivitySensor, INPUT);
  //set output pin for led
  pinMode(LED , OUTPUT);
  //set speed of serial port
  Serial.begin(9600);
  // Init bus 1-Wire
  //sensorDS18B20.begin();
}

String turbidity() {
  //input reading maping
  /*float tension,ntu;
    tension = analogRead(TurbidySensor)/1024*5;
    for(int i=0; i<500; i++){
      tension += ((float)analogRead(TurbidSensor)/1024)*5;
      delay(5000);
    }
    tension = tension/500;

    if(tension < 2.50){
      ntu = 3000;
    }else{
      ntu = -1120.4*square(tension)+5742.3*tension-4352.9;
    }
       tur = ntu;
    return convertFloatToString(ntu);*/
  float turbidity = random(5);
  tur = turbidity;
  return convertFloatToString(turbidity);
}
String ph() {
  /*float ph,avgPh;
    avgPh=analogRead(phPin);
    for(int i=0;i<500;i++){
    avgPh + =analogRead(phPin);
    delay(5000);
    }
    //convert the analog into millivolt
    ph=(float)avgValue*5.0/1024/6;
    //convert the millivolt into pH value
    ph=3.5*ph;
    return convertFloatToString(ph);*/
  float ph = random(14) / 1.3;
  phG = ph;
  return convertFloatToString(ph);
}
String temp() {
  /*    //request water temp
    sensorDS18B20.requestTemperatures();
    //read values
    float temp=sensorDS18B20.getTempCByIndex(0));
    return convertFloatToString(temp);*/
  float temp = random(-55, 125) / 1.3;
  te = temp;
  return convertFloatToString(temp);
}
/*for the right set up for the conductivity sensor:
   the nails that will take the readings of the water conductivity should be separated by 1cm
   at firt, we read value received of the input pin and whith this value, we calculates voltage & after
   we calculates the coinductivity at milisiemens by cm²
*/

String conductivity() {
  float sensorValue = 0, voltage = 0, r1 = 0, conductivity = 0;
  //fixed value for resisntance(10kΩ)
  float r2 = 10000;

  sensorValue = analogRead(conductivitySensor);
  // calculates voltage
  voltage = (5 * sensorValue) / 1023;
  // calculates water resistence
  r1 = ((r2 * 5) / voltage) - r2;
  // calculates milisiemens/cm
  conductivity = (1 / r1) * 100;
  con = conductivity;
  return convertFloatToString(conductivity);
}
String moisture() {
  /* read analogic pin A0 & throw to the map function,
    map fuction is for adjust 1024 posible values to 100, values that will
    representing percentages we want use*/
  float moistureValue;
  moistureValue = map(analogRead(moistureSensor), 0, 1023, 100, 0);
  mois = moistureValue;
  return convertFloatToString(moistureValue);
}

void checkRiego() {
  if (mois <= 40 && con <= 2 && tur <= 3 && (phG >= 5.5 || phG <= 7) && (te > 0 || te < 20)) {
    digitalWrite(LED , HIGH);
  } else if (mois <= 40 || (con > 2 && tur > 3 && (phG < 5.5 || phG > 7) && (te <= 0 || te >= 20))) {
    digitalWrite(LED , LOW);
  } else {
    digitalWrite(LED , LOW);
  }
}

String convertFloatToString(float value) {

  char temp[10];
  String toString;

  // value conversion in char array
  dtostrf(value, 1, 2, temp);

  // convert this array in String
  toString = String(temp);
  return toString;

}
void loop() {

  checkRiego();
  Serial.println(moisture() + "," + conductivity() + "," + turbidity() + "," + ph() + "," + temp());

  //wait for a second for the next lecture
  delay(10000);
}
