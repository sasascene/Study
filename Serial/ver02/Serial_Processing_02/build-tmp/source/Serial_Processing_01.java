import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Serial_Processing_01 extends PApplet {

// \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1

  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u306e\u30e9\u30a4\u30d6\u30e9\u30ea\u3092\u30a4\u30f3\u30dd\u30fc\u30c8
Serial myPort;  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8

// \u521d\u671f\u8a2d\u5b9a
public void setup(){
  // \u30b7\u30ea\u30a2\u30eb\u901a\u4fe1\u7528\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
  myPort = new Serial(this, Serial.list()[2],9600);
}

// \u63cf\u753b
public void draw(){
  //\u63cf\u753b\u5185\u5bb9\u306f\u7279\u306b\u306a\u3057
}

// \u30de\u30a6\u30b9\u30d7\u30ec\u30b9\u6642\u30a4\u30d9\u30f3\u30c8
public void mousePressed(){
  // \u30b7\u30ea\u30a2\u30eb\u30dd\u30fc\u30c8\u306b1\u3092\u51fa\u529b
  myPort.write(1000);
}

// \u30de\u30a6\u30b9\u30ea\u30ea\u30fc\u30b9\u6642\u30a4\u30d9\u30f3\u30c8
public void mouseReleased(){
  // \u30b7\u30ea\u30a2\u30eb\u30dd\u30fc\u30c8\u306b0\u3092\u51fa\u529b
  myPort.write(0);
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Serial_Processing_01" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
