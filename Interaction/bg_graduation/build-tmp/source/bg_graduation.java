import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class bg_graduation extends PApplet {

// \u521d\u671f\u8a2d\u5b9a
public void setup(){
 size(400, 400); 
 colorMode(HSB, 360, 100, 100);
}

// \u63cf\u753b
public void draw(){

  // drawBackground(width/2, height/2, 196);
  drawBackground(196);

  text(mouseX, 10, height - 20);
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
  		float s = map(d, 0, maxd, 40, 100);	// \u5f69\u5ea6
  		// \u8272\u306e\u751f\u6210
   		int c = color(h, s, 100);
   		// \u8272\u306e\u9069\u7528
   		set(x, y, c);
  	}
	}
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "bg_graduation" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
