import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.ArrayList; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class draw_ring extends PApplet {

 
float _noiseSeed;	// \u30ce\u30a4\u30ba\u306e\u7a2e
ArrayList<ring> ringList;  // \u30ea\u30f3\u30b0\u30ea\u30b9\u30c8

public void setup(){
	// size(1280, 800, OPENGL);  // \u30d5\u30eb\u30b9\u30af\u30ea\u30fc\u30f3\u7528\uff08\u30d7\u30ed\u30b8\u30a7\u30af\u30bf\u30fc\u306e\u89e3\u50cf\u5ea6\u306b\u3042\u308f\u305b\u308b\uff09
	size(600, 400, OPENGL);
	
	noFill();
	smooth();
	noCursor();

	// \u30ce\u30a4\u30ba\u306e\u7a2e\u3092\u30e9\u30f3\u30c0\u30e0\u306b\u8a2d\u5b9a
	_noiseSeed = random(10);

	// \u30ab\u30e9\u30fc\u30e2\u30fc\u30c9\u3092HSB\u306b\u6307\u5b9a
 	colorMode(HSB, 360, 255, 255);

 	// \u30ea\u30b9\u30c8\u306e\u521d\u671f\u5316
	ringList = new ArrayList<ring>();

 	// \u5ea7\u6a19\u8ef8\u306e\u79fb\u52d5
 	PVector center = new PVector(width/2, height/2);
 	translate(center.x, center.y);

 	for(int i = 20; i < 160; i += random(15, 30)){
 		ring theRing = new ring();
 		theRing.center = new PVector(width/2, height/2);
 		theRing.radius = i;
 		theRing.calcRadius();
 		ringList.add(theRing);
 	}
}

public void draw(){
	// \u80cc\u666f\u306e\u30af\u30ea\u30a2
 	background(0, 0, 0);

 	// \u5ea7\u6a19\u8ef8\u306e\u79fb\u52d5
 	PVector center = new PVector(width/2, height/2);
 	translate(center.x, center.y);

 	for(int i = 0; i < ringList.size(); i++){
 		ring theRing = ringList.get(i);
 		pushMatrix();
 			rotateZ(theRing.rotation);
 			theRing.drawRing2();
 			// theRing.update();
 		popMatrix();
 	}

 	// for(int i = 40; i < 160; i += 30){
 	// 	drawRing(i);
 	// }
}

// \u63cf\u753b
public void drawRing(float radius){

	// \u7dda\u306e\u8272\u3001\u592a\u3055\u306e\u8a2d\u5b9a
	stroke(random(360), random(50), random(20));
	strokeWeight(1);

	PVector pPrev = new PVector(radius, 0);
	PVector p1st = new PVector(radius, 0);

	for(float ang = 0; ang < 360; ang += 4){

	  float rad = radians(ang);

	  // \u534a\u5f84\u3092\u30ce\u30a4\u30ba\u3067\u52a0\u5de5
	  float effected_radius = radius + (radius / 4) * noise(_noiseSeed);
	  // _noiseSeed += 0.001;
	  _noiseSeed += 0.2f;
	  
	  // x: radius * cosT
	  float x = effected_radius * cos(rad);
	  // y: radius * sinT
	  float y = effected_radius * sin(rad);

	  PVector pCurrent = new PVector(x, y);

	  // \u521d\u671f\u5024\u306e\u8a2d\u5b9a
	  if(ang == 0){
	    pPrev = pCurrent;
	    p1st = pCurrent;
	  }

	  line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
	  pPrev = pCurrent;
	}

	line(pPrev.x, pPrev.y, p1st.x, p1st.y);
}

// \u5e74\u8f2a\u30af\u30e9\u30b9
class ring{
	PVector center;  // \u4e2d\u5fc3\u5ea7\u6a19
	float radius;    // \u534a\u5f84
	float[] noiseList;
	float[] radiusList;
	float[] angleList;;
	float _seed;
	int step;
	float rotation;

	// \u30b3\u30f3\u30b9\u30c8\u30e9\u30af\u30bf
	ring(){
		step = 4;
		noiseList = new float[360/step];
		radiusList = new float[360/step];
		angleList = new float[360/step];
		_seed = random(10);
		rotation = random(-10, 10);
	}

	public void update(){
		rotation += rotation/1000;
	}

	public void calcNoise(){
		int index = 0;

		for(float ang = 0; ang < 360; ang += step){

		  float rad = radians(ang);

		  // \u534a\u5f84\u3092\u30ce\u30a4\u30ba\u3067\u52a0\u5de5
		  float noiseValue = (radius / 4) * noise(_seed);
		  // float effected_radius = radius;
		  noiseList[index] = noiseValue;
		  _seed += random(0.1f);

		  angleList[index] = rad;
		  index++;
		}
	}

	public void calcRadius(){

		int index = 0;

		for(float ang = 0; ang < 360; ang += step){

		  float rad = radians(ang);

		  // \u534a\u5f84\u3092\u30ce\u30a4\u30ba\u3067\u52a0\u5de5
		  float effected_radius = radius + (radius / 4) * noise(_seed);
		  // float effected_radius = radius;
		  _seed += 0.1f;

		  radiusList[index] = effected_radius;
		  angleList[index] = rad;
		  index++;
		}
	}

	public void drawRing(){

		// \u7dda\u306e\u8272\u3001\u592a\u3055\u306e\u8a2d\u5b9a
		// stroke(random(360), random(150, 200), random(80, 120));
		strokeWeight(3);

		PVector pPrev = new PVector(radius, 0);
		PVector p1st = new PVector(radius, 0);

		for(int i = 0; i < radiusList.length; i++){

		  float radius = radiusList[i];
		  float rad = angleList[i];
		  
		  // x: radius * cosT
		  float x = radius * cos(rad);
		  // y: radius * sinT
		  float y = radius * sin(rad);

		  PVector pCurrent = new PVector(x, y);

		  // \u521d\u671f\u5024\u306e\u8a2d\u5b9a
		  if(i == 0){
		    pPrev = pCurrent;
		    p1st = pCurrent;
		  }

		  line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
		  pPrev = pCurrent;
		}

		line(pPrev.x, pPrev.y, p1st.x, p1st.y);
	}

	public void drawRing2(){

		// \u7dda\u306e\u8272\u3001\u592a\u3055\u306e\u8a2d\u5b9a
		// stroke(random(360), random(150, 200), random(80, 120));
		stroke(0, 0, 255);
		strokeWeight(3);

		PVector pPrev = new PVector(radius, 0);
		PVector p1st = new PVector(radius, 0);

		for(int i = 0; i < radiusList.length; i++){

		  float e_radius = radius + noiseList[i];
		  float rad = angleList[i];
		  
		  // x: radius * cosT
		  float x = e_radius * cos(rad);
		  // y: radius * sinT
		  float y = e_radius * sin(rad);

		  PVector pCurrent = new PVector(x, y);

		  // \u521d\u671f\u5024\u306e\u8a2d\u5b9a
		  if(i == 0){
		    pPrev = pCurrent;
		    p1st = pCurrent;
		  }

		  line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
		  pPrev = pCurrent;
		}

		line(pPrev.x, pPrev.y, p1st.x, p1st.y);
	}
}

public void mousePressed(){
	for(int i = 0; i < ringList.size(); i++){
 		ring theRing = ringList.get(i);
 		// theRing.calcRadius();
 		theRing.calcNoise();
 	}
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--hide-stop", "draw_ring" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
