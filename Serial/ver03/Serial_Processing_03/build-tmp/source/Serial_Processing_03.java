import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Serial_Processing_03 extends PApplet {

// \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1  ====================================================
  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u306e\u30e9\u30a4\u30d6\u30e9\u30ea\u3092\u30a4\u30f3\u30dd\u30fc\u30c8
Serial myPort;  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8
// ================================================================

// OSC ============================================================


  
OscP5 oscP5;
NetAddress myRemoteLocation;

// \u901a\u4fe1\u30aa\u30d6\u30b8\u30a7\u30af\u30c8(\u5f15\u6570\u306bprefix\u3092\u6307\u5b9a)
OscMessage myMessage = new OscMessage("/Pd");
// ================================================================

// \u521d\u671f\u8a2d\u5b9a
public void setup(){
  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
  myPort = new Serial(this, Serial.list()[2],9600);
  
  // procressing\u306e\u53d7\u4fe1\u30dd\u30fc\u30c8(pd\u306e\u9001\u4fe1\u30dd\u30fc\u30c8)
  oscP5 = new OscP5(this,8000);
  
  // procressing\u306e\u9001\u4fe1\u30dd\u30fc\u30c8(pd\u306e\u53d7\u4fe1\u30dd\u30fc\u30c8)
  myRemoteLocation = new NetAddress("127.0.0.1",8001);
}

// \u63cf\u753b
public void draw(){
  //\u63cf\u753b\u5185\u5bb9\u306f\u7279\u306b\u306a\u3057
}

// \u30b7\u30ea\u30a2\u30eb\u901a\u4fe101
public void sendSignal01(int val){

  // \u30b7\u30ea\u30a2\u30eb\u30dd\u30fc\u30c8\u306babc\u3092\u51fa\u529b
  if(val == 1){
    String str1 = "abc";
  }else{
    String str1 = "zz";
  }

  myPort.write(str1 + "\0");
}

// \u30b7\u30ea\u30a2\u30eb\u901a\u4fe102
public void sendSignal02(int val){
  
  // \u30b7\u30ea\u30a2\u30eb\u30dd\u30fc\u30c8\u306bdef\u3092\u51fa\u529b
  if(val == 1){
    String str1 = "def";
  }else{
    String str1 = "zz";
  }

  myPort.write(str1 + "\0");
}

// OSC\u53d7\u4fe1\u30a4\u30d9\u30f3\u30c8
public void oscEvent(OscMessage theOscMessage) {

  // prefix\u306e\u53d6\u5f97
  String prefix = theOscMessage.addrPattern();
  print(prefix + ": ");
  
  if(prefix.equals("/pd/trigger/0")){ // prefix\u304c"/pd/trigger/0"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c

    int val = theOscMessage.get(0).intValue();
    println(val);

    sendSignal01(val);

  }else if(prefix.equals("/pd/trigger/1")){ // prefix\u304c"/pd/trigger/1"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    int val = theOscMessage.get(0).intValue();
    println(val);

    sendSignal02(val);
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Serial_Processing_03" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
