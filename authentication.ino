//#include <ESP8266WebServer.h>

#include <SerialESP8266wifi.h>


#include <Adafruit_ESP8266.h>
//#include <ESP8266WebServer.h>
#include <deprecated.h>
#include <MFRC522.h>
#include <MFRC522Extended.h>
#include <require_cpp11.h>

#include <SPI.h>

//RFID RC522 setup:
//         SDA - PIN10
//         SCK - PIN13
//         MOSI - PIN11
//         MISO - PIN12
//         GND - Arduino GND
//         RST - PIN9
//         3.3v - 3.3v Arduino

#define SDA   10
#define SCK   13
#define MOSI  11
#define MISO  12
#define RST   9

#define RED   3
#define GREEN 4
#define BLUE  5 

MFRC522 rfid(SDA, RST);
int myCardID[4] = { 0x36, 0x9C, 0x2B, 0xF9 };
bool grantAccess = true;
bool grantedAccess = false;

SerialESP8266wifi wifi(Serial, Serial, 7);

char ssid[5] = "ssid";
char password[11] = "pass";

void setup() {
  Serial.begin(9600);
  boolean esp8266started = wifi.begin();
  if(esp8266started == false){
    Serial.print("WIFI NOK");
  }
  
  boolean connectedToAP = wifi.connectToAP(ssid, password);
  if(wifi.isConnectedToAP() == false){
    Serial.print("Could not establish connection to acces point");
    return;
  }
  Serial.print("Connected To AP");

  boolean serverConnected = wifi.connectToServer("127.0.0.1", "5432");
  if(wifi.isConnectedToServer() == true){
    Serial.print("Connected to server");
  }
  Serial.println("Halo");
  pinMode(RED, OUTPUT);
  pinMode(GREEN, OUTPUT);
  pinMode(BLUE, OUTPUT);
  
  SPI.begin();
  rfid.PCD_Init();
  delay(4);
  rfid.PCD_DumpVersionToSerial();
}

void loop() {
  if ( ! rfid.PICC_IsNewCardPresent()) {
    return;
  }

   if ( ! rfid.PICC_ReadCardSerial()) {
    return;
  }
  //rfid.PICC_DumpToSerial(&(rfid.uid));

  for(int i = 0; i < 4; i++){
    Serial.print(rfid.uid.uidByte[i]);
    Serial.print("\n");
  }
  
  for(int i = 0; i < 4; i++){
    if(rfid.uid.uidByte[i] != myCardID[i]) grantAccess = false;
  }
  
  if(grantAccess == true){
    grantedAccess = true;
    grantAccess = false;
  }

  if(grantedAccess == false){
    digitalWrite(RED, HIGH);
    digitalWrite(GREEN, LOW);
    delay(3000);
    digitalWrite(RED, LOW);
  }

  if(grantedAccess == true){
    boolean sendOk = wifi.send(SERVER, "Authenticated.");
    digitalWrite(GREEN, HIGH);
    digitalWrite(RED, LOW);
    delay(3000);
    digitalWrite(GREEN, LOW);
    grantedAccess = false;
  }

  grantAccess = true;
}
