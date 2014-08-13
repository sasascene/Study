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

public class Serial_Processing_07 extends PApplet {

// \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1  ====================================================
  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u306e\u30e9\u30a4\u30d6\u30e9\u30ea\u3092\u30a4\u30f3\u30dd\u30fc\u30c8
Serial myPort;  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8
// ================================================================

// OSC ============================================================
	  // OSC\u901a\u4fe1\u7528\u306e\u30e9\u30a4\u30d6\u30e9\u30ea\u3092\u30a4\u30f3\u30dd\u30fc\u30c8
OscP5 oscP5;  // OSC\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8
// ================================================================

// \u521d\u671f\u8a2d\u5b9a
public void setup(){
  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
  // \u30dd\u30fc\u30c8No\u3068\u901a\u4fe1\u901f\u5ea6\u3092\u6307\u5b9a\u3059\u308b
  myPort = new Serial(this, Serial.list()[2],9600);
  
  // OSC\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
  // procressing\u306e\u53d7\u4fe1\u30dd\u30fc\u30c8(pd\u306e\u9001\u4fe1\u30dd\u30fc\u30c8)\u3092\u6307\u5b9a\u3059\u308b
  oscP5 = new OscP5(this, 8000);

  size(220, 130);
  text("translate OSC into Serial message.", 10, height - 30);
}

// \u63cf\u753b
public void draw(){
  //\u63cf\u753b\u5185\u5bb9\u306f\u7279\u306b\u306a\u3057
}

// \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1
public void sendSerial(int pinNo, int val){
	// \u30d4\u30f3\u756a\u53f7\u3068\u5024\u3092\u6307\u5b9a\u3057\u3066\u30dd\u30fc\u30c8\u306b\u51fa\u529b
	// \u30d4\u30f3\u756a\u53f7-\u5024\0 \u306e\u5f62\u5f0f\u3067\u51fa\u529b
  myPort.write(str(pinNo) + "-" + str(val) + "\0");
}

// OSC\u53d7\u4fe1\u30a4\u30d9\u30f3\u30c8
public void oscEvent(OscMessage theOscMessage) {

  // prefix\u306e\u53d6\u5f97
  String prefix = theOscMessage.addrPattern();
  
  // prefix\u304c"/arduino"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
  if(prefix.equals("/arduino")){

  	int pinNo = theOscMessage.get(0).intValue();	// \u30d4\u30f3\u756a\u53f7
    int val = theOscMessage.get(1).intValue();		// \u5024

    // print(pinNo);
    // print(" ");
    // println(val);

    // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1
    sendSerial(pinNo, val);
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Serial_Processing_07" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
