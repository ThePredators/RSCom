# RSCom
Sample App that shows how to establish a connection between an Android Application and Arduino Card using OTG cable.

## The file ArduinoExample.java :
Contains an exemple of code that blink a let on Arduino Card. This code can be used in the Arduino Editor.
https://www.arduino.cc/en/Main/Software

## The file UsbControllerActivity.java :
Expose 3 Method :
-- startRSConnection : to start the connection between Android Device and Arduino Card using OTG cable.
-- sendRsData : to send data to the Arduino Card.
-- stopRSConnection : to stop the connection between Android Device and Arduino Card using OTG cable.
