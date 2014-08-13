import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import gab.opencv.*; 
import processing.video.*; 
import java.awt.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class face_detect extends PApplet {





Capture video;
OpenCV opencv;

// \u521d\u671f\u8a2d\u5b9a
public void setup() {
  size(640, 480);
  colorMode(HSB, 360, 100, 100);
  smooth();
  video = new Capture(this, 640/2, 480/2);
  opencv = new OpenCV(this, 640/2, 480/2);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);
  // opencv.loadCascade(OpenCV.CASCADE_FULLBODY);

  video.start();
}

// \u63cf\u753b
public void draw() {
  scale(2); // \u753b\u50cf\u3092\u753b\u9762\u3044\u3063\u3071\u3044\u306b\u30b9\u30b1\u30fc\u30ea\u30f3\u30b0
  opencv.loadImage(video);

  // image(video, 0, 0);  // \u30ad\u30e3\u30d7\u30c1\u30e3\u753b\u50cf\u306e\u63cf\u753b

  //  \u30ad\u30fc\u30ab\u30e9\u30fc\u306e\u6307\u5b9a
  int h = 196;

  // \u80cc\u666f\u63cf\u753b
  drawBackground(h);

  // \u63cf\u753b\u8a2d\u5b9a
  noFill();
  stroke(h, 40, 100);
  strokeWeight(2);

  // \u9854\u306e\u691c\u51fa
  Rectangle[] faces = opencv.detect();

  // \u691c\u51fa\u3057\u305f\u9854\u306e\u6570
  // println("faces: " + faces.length);

  for (int i = 0; i < faces.length; i++) {
    // \u691c\u51fa\u3057\u305f\u9854\u306e\u5ea7\u6a19
    println("face" + i + ";" + faces[i].x + "," + faces[i].y);
    ellipse(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
  }
}

// \u30ad\u30e3\u30d7\u30c1\u30e3
public void captureEvent(Capture c) {
  c.read();
}

// \u80cc\u666f\u63cf\u753b
public void drawBackground(int h){

  // \u539f\u70b9\u3068\u7aef\u306e\u6700\u5927\u8ddd\u96e2
  float maxd = dist(0, 0, width, height);

  // \u5168\u5ea7\u6a19\u3092\u30eb\u30fc\u30d7
  for(int y = 0; y < height; y++){
    for(int x = 0; x < width; x++){
      // \u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u3068\u306e\u8ddd\u96e2
      float d = abs(height - y);
      float s = map(d, 0, height, 40, 100);
      // \u8272\u306e\u751f\u6210
      int c = color(h, s, 100);
      // \u8272\u306e\u9069\u7528
      set(x, y, c);
    } 
  }
}

// \u80cc\u666f\u63cf\u753b(\u6307\u5b9a\u5ea7\u6a19\u3068\u306e\u8ddd\u96e2)
public void drawBackground(int x1, int y1, int h){
  // \u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u3068\u306e\u8ddd\u96e2\u3092\u5f69\u5ea6\u306e\u624b\u304c\u304b\u308a\u306b\u4f7f\u7528\u3059\u308b

  // \u539f\u70b9\u3068\u7aef\u306e\u6700\u5927\u8ddd\u96e2
  float maxd = dist(0, 0, width, height);

  // \u5168\u5ea7\u6a19\u3092\u30eb\u30fc\u30d7
  for(int y = 0; y < height; y++){
    for(int x = 0; x < width; x++){
      // \u6307\u5b9a\u3057\u305f\u5ea7\u6a19\u3068\u306e\u8ddd\u96e2
      float d = dist(x1, y1, x, y);
      float s = map(d, 0, maxd, 40, 100); // \u5f69\u5ea6
      // \u8272\u306e\u751f\u6210
      int c = color(h, s, 100);
      // \u8272\u306e\u9069\u7528
      set(x, y, c);
    }
  }
}


  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "face_detect" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
