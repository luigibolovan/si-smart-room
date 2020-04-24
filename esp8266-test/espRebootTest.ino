// a small test meant to see if the esp is really rebooting
// check if setup sequence is performed

#include <WiFiClientSecureBearSSL.h>
#include <ESP8266WiFiType.h>
#include <WiFiServerSecureAxTLS.h>
#include <WiFiUdp.h>
#include <ESP8266WiFiMulti.h>
#include <ESP8266WiFiScan.h>
#include <WiFiServer.h>
#include <WiFiClientSecure.h>
#include <WiFiServerSecureBearSSL.h>
#include <WiFiClientSecureAxTLS.h>
#include <WiFiClient.h>
#include <BearSSLHelpers.h>
#include <ESP8266WiFi.h>
#include <WiFiServerSecure.h>
#include <ESP8266WiFiGeneric.h>
#include <ESP8266WiFiSTA.h>
#include <CertStoreBearSSL.h>
#include <ESP8266WiFiAP.h>

#include <ArduinoJson.h>

#include <FirebaseError.h>
#include <FirebaseArduino.h>
#include <FirebaseCloudMessaging.h>
#include <FirebaseHttpClient.h>
#include <Firebase.h>
#include <FirebaseObject.h>

#define WIFI_SSID "myWiFiSSID"
#define WIFI_PASSWORD "myPassword"
#define FIREBASE_HOST "myFirebaseHost"
#define FIREBASE_AUTH "myFirebaseSecret"

int set; // write only once to Firebase Realtime Database

void setup() {
  Serial.begin(9600);
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting");
  while(WiFi.status() != WL_CONNECTED){
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.println("Connected to wifi");
  Serial.println(WiFi.localIP());

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  delay(500);

}

void loop() {
  if(set == 0){
    Firebase.setString("auth/testValue", "Arduino was here");
    Serial.println("Arduino was here has been written to Firebase.");
    delay(1000);
    set = 1;
  }
}
