import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import oscP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Serial_Processing extends PApplet {

// \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1  ====================================================
  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u306e\u30e9\u30a4\u30d6\u30e9\u30ea\u3092\u30a4\u30f3\u30dd\u30fc\u30c8
Serial myPort0;  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8
Serial myPort1;  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8
Serial myPort2;  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8
// ================================================================

// OSC ============================================================
	  // OSC\u901a\u4fe1\u7528\u306e\u30e9\u30a4\u30d6\u30e9\u30ea\u3092\u30a4\u30f3\u30dd\u30fc\u30c8
OscP5 oscP5;  // OSC\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8
// ================================================================

String[] devices;   // \u30c7\u30d0\u30a4\u30b9\u683c\u7d0d\u7528\u914d\u5217
boolean[] portStatus;  // \u30dd\u30fc\u30c8\u4f7f\u7528\u72b6\u6cc1\u683c\u7d0d\u7528\u914d\u5217

// \u521d\u671f\u8a2d\u5b9a
public void setup(){

  size(340, 200);

  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u306e\u30c7\u30d0\u30a4\u30b9\u3092\u53d6\u5f97
  devices = Serial.list();

  // \u30dd\u30fc\u30c8\u4f7f\u7528\u72b6\u6cc1\u306e\u521d\u671f\u5316
  portStatus = new boolean[devices.length];
  for(int i = 0; i < portStatus.length; i++){
    portStatus[i] = false;
  }

  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u306e\u901a\u4fe1\u901f\u5ea6
  int speed = 115200;

  // \u30dd\u30fc\u30c8\u306b\u30c7\u30d0\u30a4\u30b9\u3092\u9806\u756a\u306b\u5272\u308a\u5f53\u3066\u308b
  // \u30dd\u30fc\u30c80
  for(int i = 0; i < devices.length; i++){

    // USB\u3067\u306a\u3044\u5834\u5408\u306f\u6b21\u306e\u30c7\u30d0\u30a4\u30b9\u3092\u53c2\u7167
    String[] m = match(devices[i], "usb");
    if(m == null){
      continue;
    }

    // \u30c7\u30d0\u30a4\u30b9\u3092\u8a2d\u5b9a
    // \u4f8b\u5916\u767a\u751f\u6642\uff08\u4f7f\u7528\u4e2d\u306a\u3069\uff09\u306f\u6b21\u306e\u30c7\u30d0\u30a4\u30b9\u3092\u53c2\u7167
    try {
      myPort0 = new Serial(this, devices[i], speed);
    }catch (Exception e){
      continue;
    }

    // \u30c7\u30d0\u30a4\u30b9\u3092\u8a2d\u5b9a\u3057\u305f\u3089\u30b9\u30c6\u30fc\u30bf\u30b9\u3092\u66f4\u65b0\u3057\u3066\u30eb\u30fc\u30d7\u3092\u629c\u3051\u308b
    portStatus[i] = true;
    i = devices.length;
  }

  // \u30dd\u30fc\u30c81
  for(int i = 0; i < devices.length; i++){

    // USB\u3067\u306a\u3044\u5834\u5408\u306f\u6b21\u306e\u30c7\u30d0\u30a4\u30b9\u3092\u53c2\u7167
    String[] m = match(devices[i], "usb");
    if(m == null){
      continue;
    }

    // \u30c7\u30d0\u30a4\u30b9\u3092\u8a2d\u5b9a
    // \u4f8b\u5916\u767a\u751f\u6642\uff08\u4f7f\u7528\u4e2d\u306a\u3069\uff09\u306f\u6b21\u306e\u30c7\u30d0\u30a4\u30b9\u3092\u53c2\u7167
    try {
      myPort1 = new Serial(this, devices[i], speed);
    }catch (Exception e){
      continue;
    }

    // \u30c7\u30d0\u30a4\u30b9\u3092\u8a2d\u5b9a\u3057\u305f\u3089\u30b9\u30c6\u30fc\u30bf\u30b9\u3092\u66f4\u65b0\u3057\u3066\u30eb\u30fc\u30d7\u3092\u629c\u3051\u308b
    portStatus[i] = true;
    i = devices.length;
  }

  // \u30dd\u30fc\u30c82
  for(int i = 0; i < devices.length; i++){

    // USB\u3067\u306a\u3044\u5834\u5408\u306f\u6b21\u306e\u30c7\u30d0\u30a4\u30b9\u3092\u53c2\u7167
    String[] m = match(devices[i], "usb");
    if(m == null){
      continue;
    }

    // \u30c7\u30d0\u30a4\u30b9\u3092\u8a2d\u5b9a
    // \u4f8b\u5916\u767a\u751f\u6642\uff08\u4f7f\u7528\u4e2d\u306a\u3069\uff09\u306f\u6b21\u306e\u30c7\u30d0\u30a4\u30b9\u3092\u53c2\u7167
    try {
      myPort2 = new Serial(this, devices[i], speed);
    }catch (Exception e){
      continue;
    }

    // \u30c7\u30d0\u30a4\u30b9\u3092\u8a2d\u5b9a\u3057\u305f\u3089\u30b9\u30c6\u30fc\u30bf\u30b9\u3092\u66f4\u65b0\u3057\u3066\u30eb\u30fc\u30d7\u3092\u629c\u3051\u308b
    portStatus[i] = true;
    i = devices.length;
  }

  // \u30c7\u30d0\u30a4\u30b9\u30ea\u30b9\u30c8\u3092\u8868\u793a\uff08\u4f7f\u7528\u4e2d\u306e\u30c7\u30d0\u30a4\u30b9\u306b*\u3092\u8868\u793a\uff09
  text("devices (*:busy)", 10, 20);
  for(int i = 0; i < devices.length; i++){
    text(i + ": " + devices[i], 20, 45 + (i * 15));
    if(portStatus[i]){
      text("*", 10, 45 + (i * 15));
    }
  }
  
  // OSC\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
  // procressing\u306e\u53d7\u4fe1\u30dd\u30fc\u30c8(pd\u306e\u9001\u4fe1\u30dd\u30fc\u30c8)\u3092\u6307\u5b9a\u3059\u308b
  oscP5 = new OscP5(this, 8000);
}

// \u63cf\u753b
public void draw(){
  //\u63cf\u753b\u5185\u5bb9\u306f\u7279\u306b\u306a\u3057
}

// \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1
public void sendSerial(int boardNo, int pinNo, float val){
	// \u30d4\u30f3\u756a\u53f7\u3068\u5024\u3092\u6307\u5b9a\u3057\u3066\u30dd\u30fc\u30c8\u306b\u51fa\u529b
	// \u30d4\u30f3\u756a\u53f7-\u5024\0 \u306e\u5f62\u5f0f\u3067\u51fa\u529b

  if(boardNo == 0){
    myPort0.write(str(pinNo) + "-" + str(val) + "\0");
  }else if(boardNo == 1){
    myPort1.write(str(pinNo) + "-" + str(val) + "\0");
  }else{
    myPort2.write(str(pinNo) + "-" + str(val) + "\0");
  }
}

// OSC\u53d7\u4fe1\u30a4\u30d9\u30f3\u30c8
public void oscEvent(OscMessage theOscMessage) {

  // prefix\u306e\u53d6\u5f97
  String prefix = theOscMessage.addrPattern();
  
  // prefix\u304c"/arduino"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
  if(prefix.equals("/arduino")){

    try{
      int boardNo = theOscMessage.get(0).intValue();  // \u30dc\u30fc\u30c9\u756a\u53f7
      int pinNo = theOscMessage.get(1).intValue();	// \u30d4\u30f3\u756a\u53f7
      int val = theOscMessage.get(2).intValue();		// \u5024

      // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1
      sendSerial(boardNo, pinNo, val);

    }catch(Exception e){
      text("error! reboot app!", 10, height-10);
    }
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Serial_Processing" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
