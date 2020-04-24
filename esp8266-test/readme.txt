
Software-wise
    To be able to use the ESP8266 libraries(e.g. ESP8266WiFi.h) and also to program the ESP8266 you need to install the ESP8266 board via the Arduino IDE Board Manager.
    After the ESP8266 board is installed, restart Arduino IDE. 


Hardware-wise
    ESP8266 connection to Arduino UNO:
            ESP8266         |           Arduino
            ----------------|------------------
            3v3 pin         |           3v3 pin
            EN pin          |           3v3 pin
            GND pin         |           GND pin
            TX              |           TX
            RX              |           RX
                Only in programming mode*
            RST             |           GND
            GPIO0(IN0)      |           GND


    *The programming mode of the ESP8266:
        Steps to enable the programming mode:
            0. Select the ESP8266 board from Arduino IDE > Tools > Board
            1. uC of the Arduino board should be reset(Arduino's RES pin connected to GND)
            2. ESP8266's GPIO0(IN0), the pin that actually puts the ESP into programming mode, must be connected to GND during the programming process
            3. ESP8266's RST pin must be connected manually for aprox 3 sec when the Arduino IDE is trying to connect to the ESP8266 board. After 3 sec, RST must be disconnected from GND.


