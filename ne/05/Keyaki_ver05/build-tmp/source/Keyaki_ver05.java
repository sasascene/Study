import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.opengl.*; 
import oscP5.*; 
import netP5.*; 
import java.util.ArrayList; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Keyaki_ver05 extends PApplet {




 
  
OscP5 oscP5;
NetAddress myRemoteLocation;

float _noiseSeed;     // \u30ce\u30a4\u30ba\u306e\u7a2e

PImage offscr;	// \u30aa\u30d5\u30b9\u30af\u30ea\u30fc\u30f3

// \u30b0\u30ed\u30fc\u30d0\u30eb\u5909\u6570
float globalWeight = 0.3f;
float col = 0;
float beat = 0;
float noiz = 0;
float lfosin = 0;
float synth = 0;
float pointalpha = 0;
int pattern = 0;
int scene = 0;
float[] pixel_w;

PImage image1;

ArrayList<ring> ringList;  // \u30ea\u30f3\u30b0\u30ea\u30b9\u30c8
ArrayList<wall> wallList;  // \u30ea\u30f3\u30b0\u30ea\u30b9\u30c8


// \u521d\u671f\u8a2d\u5b9a
public void setup(){

  //size(800, 800, OPENGL);
  size(1280, 720, OPENGL);  // \u30d5\u30eb\u30b9\u30af\u30ea\u30fc\u30f3\u7528\uff08\u30d7\u30ed\u30b8\u30a7\u30af\u30bf\u30fc\u306e\u89e3\u50cf\u5ea6\u306b\u3042\u308f\u305b\u308b\uff09

  noFill();
  smooth();
  noCursor();
  
  // \u30ce\u30a4\u30ba\u306e\u7a2e\u3092\u30e9\u30f3\u30c0\u30e0\u306b\u8a2d\u5b9a
  _noiseSeed = random(10);
  
  // procressing\u306e\u53d7\u4fe1\u30dd\u30fc\u30c8(pd\u306e\u9001\u4fe1\u30dd\u30fc\u30c8)
  oscP5 = new OscP5(this,7702);
  
  // procressing\u306e\u9001\u4fe1\u30dd\u30fc\u30c8(pd\u306e\u53d7\u4fe1\u30dd\u30fc\u30c8)
  //myRemoteLocation = new NetAddress("127.0.0.1",7702);

  // \u30ab\u30e9\u30fc\u30e2\u30fc\u30c9\u3092HSB\u306b\u6307\u5b9a
  colorMode(HSB, 360, 255, 255);

  // \u30aa\u30d5\u30b9\u30af\u30ea\u30fc\u30f3\u306e\u521d\u671f\u8a2d\u5b9a
  offscr = createImage(width, height, RGB);

  pixel_w = new float[width];
  	for(int i = 0; i < width; i++){
		pixel_w[i] = 100 * noise(_noiseSeed);
		_noiseSeed += 0.01f;
	}

  // \u5e74\u8f2a\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
  makeRing(6);

  makeWall(3);
}

// \u63cf\u753b
public void draw(){

	// \u30aa\u30d5\u30b9\u30af\u30ea\u30fc\u30f3\u30d0\u30c3\u30d5\u30a1\u306e\u66f4\u65b0
	loadPixels();
 	offscr.pixels = pixels;
 	offscr.updatePixels();

 	// \u80cc\u666f\u306e\u30af\u30ea\u30a2
 	background(0, 0, 0);

 	// ======================================
 	// \u4ee5\u4e0b\u306b\u901a\u5e38\u306e\u63cf\u753b\u51e6\u7406\u3092\u8a18\u8ff0

 	// \u5ea7\u6a19\u8ef8\u306e\u79fb\u52d5
 	PVector center = new PVector(width/2, height/2);
 	translate(center.x, center.y);

	// \u5e74\u8f2a\u306e\u63cf\u753b
	drawRing();

	// ======================================

	// \u30aa\u30d5\u30b9\u30af\u30ea\u30fc\u30f3\u30d0\u30c3\u30d5\u30a1\u306e\u63cf\u753b
	tint(255, 240);
	translate(-center.x, -center.y);
	image(offscr, -3, -3, width + 6, height + 6); // \u5c11\u3057\u3060\u3051\u62e1\u5927\u3057\u3066\u307f\u308b
}

// \u5e74\u8f2a\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
public void makeRing(int num){

	// \u30ea\u30b9\u30c8\u306e\u521d\u671f\u5316
	ringList = new ArrayList<ring>();

	// \u4e2d\u5fc3\u5ea7\u6a19\u3092\u4f5c\u6210\uff08\u753b\u9762\u4e2d\u5fc3\u304b\u3089\u7121\u4f5c\u70ba\u306b\u305a\u3089\u3057\u305f\u5ea7\u6a19\u3092\u6307\u5b9a\uff09
	PVector center = new PVector((random(20)-10) * width/2, (random(20)-10) * height/2);

	// \u5e74\u8f2a\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
	for(int i = 0; i < num; i++){
		ring theRing = new ring(center, 10 * i);
		theRing.setWeight(0.5f);
		ringList.add(theRing);
	}
}

// \u5e74\u8f2a\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
public void makeWall(int num){

	// \u30ea\u30b9\u30c8\u306e\u521d\u671f\u5316
	wallList = new ArrayList<wall>();

	// \u4e2d\u5fc3\u5ea7\u6a19\u3092\u4f5c\u6210\uff08\u753b\u9762\u4e2d\u5fc3\u304b\u3089\u7121\u4f5c\u70ba\u306b\u305a\u3089\u3057\u305f\u5ea7\u6a19\u3092\u6307\u5b9a\uff09
	PVector startPoint = new PVector((random(20)-10), (random(20)-10), random(20)-10);

	// \u5e74\u8f2a\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
	for(int i = 0; i < num; i++){
		wall theWall = new wall();
		theWall.startPoint = startPoint;

		for(int j = 0; j < width; j++){
			theWall.edge[j] = (height/4) * noise(_noiseSeed);
			_noiseSeed += 0.01f;
		}

		wallList.add(theWall);
	}
}

// \u5e74\u8f2a\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u63cf\u753b
public void drawRing(){

	pushMatrix();

		for(int i = 0; i < ringList.size(); i++){
			ring theRing = ringList.get(i);
			theRing.update();


			switch (pattern) {
				case 0:
					rotateZ(i * frameCount / 10);
					theRing.draw();	// \u901a\u5e38\u30d1\u30bf\u30fc\u30f3
					theRing.draw_side();	// \u30ce\u30a4\u30ba\u30d1\u30bf\u30fc\u30f3
					break;
				case 1:
					// \u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u3054\u3068\u306b\u56de\u8ee2
					rotateZ(i * frameCount / 10);
					theRing.draw_side();	// \u30ce\u30a4\u30ba\u30d1\u30bf\u30fc\u30f3
					theRing.draw();	// \u901a\u5e38\u30d1\u30bf\u30fc\u30f3
					theRing.draw_noise();	// \u30ce\u30a4\u30ba\u30d1\u30bf\u30fc\u30f3
					break;
				case 2:
					// \u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u3054\u3068\u306b\u56de\u8ee2
					rotateZ(i * frameCount / 10);
	      			// pushMatrix();
					rotateX(i * frameCount / 30);
					rotateY(i * frameCount / 50);
					theRing.draw_noise();	// \u30ce\u30a4\u30ba\u30d1\u30bf\u30fc\u30f3
	      			// popMatrix();
					break;
				case 3:
					// \u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u3054\u3068\u306b\u56de\u8ee2
					rotateZ(i * frameCount / 10);
					theRing.draw_side();	// \u30ce\u30a4\u30ba\u30d1\u30bf\u30fc\u30f3
	      			pushMatrix();
						theRing.drawWideRing();	// \u8f2a\u306e\u30a4\u30e1\u30fc\u30b8
	      			popMatrix();
					break;
				default:
			}

			// theRing.drawSphere();

		}

		// \u6ce2\u5f62\u306e\u63cf\u753b
		// drawWave();

	popMatrix();

	drawFall();

}

public void drawFall(){

	// if(scene > 4){
	// 	return;
	// }

	for(int n = 0; n < wallList.size(); n++){
		for(int m = 0; m < width; m++){
			wallList.get(n).edge[m] = wallList.get(n).edge[m] + 0.3f;
		}
	}

	pushMatrix();
	translate(-width/2, -height/2, 0);

	for(int n = 0; n < wallList.size(); n++){
		wall theWall = wallList.get(n);
		translate(theWall.startPoint.x/10, theWall.startPoint.y/10, theWall.startPoint.z/10);
		strokeWeight(2);
		stroke(synth, 0, random(200), random(20)+10);
		for(int m = 0; m < width; m += random(synth * 10)){
			line(m, -height, m, theWall.edge[m]);
		}
	}
	popMatrix();
}

// \u6ce2\u5f62\u306e\u63cf\u753b
public void drawWave(){
	float[] w = new float[width];
	float[] w2 = new float[width];
	float[] w3 = new float[width];
	for(int i = 0; i < width; i++){
		w[i] = -noiz * random(10) * 0.1f * noise(_noiseSeed) * sin(radians(i+random(30)));
		_noiseSeed += 0.01f;
		w2[i] = -pointalpha * random(10) * 0.2f * noise(_noiseSeed) * cos(radians(i+random(30)));
		_noiseSeed += 0.0001f;
		w3[i] = -noiz * random(10) * 0.3f * noise(_noiseSeed) * sin(radians(i)) * cos(radians(i));
		_noiseSeed += 0.001f;
	}

	for(int i = (int)random(30); i < width/2; i += lfosin){
		if(i > 0){
			strokeWeight(2);
			stroke(synth, noiz, random(255), noiz*70);
			line(i - width/4 - 1, w[i-1], i - width/4, w[i]);
			strokeWeight(4);
			stroke(random(synth), noiz * 0.8f, random(255), noiz*70);
			line(i - width/4 -1, w2[i-1], i - width/4, w2[i]);
			strokeWeight(8);
			stroke(col, noiz * 0.6f, random(255), noiz*70);
			line(i - width/4 -1, w3[i-1], i - width/4, w3[i]);
		}
	}
}

// \u30b9\u30af\u30ea\u30fc\u30f3\u30af\u30e9\u30b9
class wall{
	PVector startPoint;
	float[] edge;

	wall(){
		startPoint = new PVector(0, 0, 0);
		edge = new float[width];
	}

}

// \u5e74\u8f2a\u30af\u30e9\u30b9
class ring{
	PVector center;  // \u4e2d\u5fc3\u5ea7\u6a19
	float radius;    // \u534a\u5f84
	float weight;	   // \u7dda\u306e\u592a\u3055
	boolean dead;	   // \u751f\u6b7b\u5224\u5225
	float transparency; // \u8272
	float dice;			 // \u30d1\u30bf\u30fc\u30f3

	// \u30b3\u30f3\u30b9\u30c8\u30e9\u30af\u30bf
	ring(){}

	ring(float x, float y, float z){
	center = new PVector(x, y, z);
	transparency = 255;
	dead = false;
	dice = (int)random(6) + 1;
	}

	ring(PVector p1){
	center = new PVector(p1.x, p1.y, p1.z);
	transparency = 255;
	dead = false;
	dice = (int)random(6) + 1;
	}

	ring(PVector p1, float d1){
	center = new PVector(p1.x, p1.y, p1.z);
	radius = d1;
	transparency = 255;
	dead = false;
	dice = (int)random(6) + 1;
	}

	// \u5ea7\u6a19\u306e\u8a2d\u5b9a
	public void setPos(PVector p1){
	center = p1;
	}

	public void setRadius(float r){
		radius = r;
	}

	public float getRadius(){
		return radius;
	}

	public void setWeight(float w){
		weight = w;
	}

	public float getWeight(){
		return weight;
	}

	public boolean isDead(){
		return dead;
	}

	public void update(){

		// \u534a\u5f84\u3092\u52a0\u7b97\u3002\u3057\u304d\u3044\u5024\u3092\u8d85\u3048\u305f\u3089\u6b7b\u4ea1\u6271\u3044\u3068\u3059\u308b
		radius += 0.3f;
		if(radius > 300){
			dead = true;
		}

		// \u900f\u660e\u5ea6\u3092\u8a2d\u5b9a\u3002\u3057\u304d\u3044\u5024\u3092\u8d85\u3048\u305f\u3089\u6b7b\u4ea1\u6271\u3044\u3068\u3059\u308b\u3002
		if(pattern == 0){
			if(transparency >= 20){
			transparency = synth * 120 * 0.4f;
			}else {
				transparency = 0;
				dead = true;
			}
		}else{
			if(transparency >= 20){
			transparency = pointalpha * 0.4f;
			}else {
				transparency = 0;
				dead = true;
			}
		}

		weight = globalWeight;
	}

	public void reset(int index){
		radius = random(10 * (index+1));
		transparency = 255;
		dead = false;
		dice = (int)random(6) + 1;
	}

	// \u63cf\u753b
	public void draw(){

		// \u6b7b\u3093\u3067\u3044\u308b\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306f\u63cf\u753b\u3057\u306a\u3044
		if(isDead()){
			return;
		}

		PVector pPrev = new PVector(radius, 0);

		for(float ang = 0; ang < 360; ang += 4){

		  // \u534a\u5f84\u3092\u30ce\u30a4\u30ba\u3067\u52a0\u5de5
		  float effected_radius = radius * 4 + 20 * noise(_noiseSeed);
		  _noiseSeed += 0.01f;
		  if(_noiseSeed > 500){
		  	_noiseSeed = random(10);
		  }

		  float rad = radians(ang);
		  
		  // x: radius * cosT
		  float x = effected_radius * cos(rad);
		  // y: radius * sinT
		  float y = effected_radius * sin(rad);

		  PVector pCurrent = new PVector(x, y);
		  
		  // \u7dda\u306e\u8272\u3001\u592a\u3055\u306e\u8a2d\u5b9a
		  stroke(0, 0, 255, transparency);
		  strokeWeight(weight);

		  // \u521d\u671f\u5024\u306e\u8a2d\u5b9a
		  if(ang == 0){
		    pPrev = pCurrent;
		  }

		  line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
		  pPrev = pCurrent;
		}
	}

  	// \u63cf\u753b
	public void draw_noise(){

		// \u6b7b\u3093\u3067\u3044\u308b\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306f\u63cf\u753b\u3057\u306a\u3044
		if(isDead()){
			return;
		}

		PVector pPrev = new PVector(radius, 0);

		for(float ang = 0; ang < 360; ang += random(10)){

		  // \u534a\u5f84\u3092\u30ce\u30a4\u30ba\u3067\u52a0\u5de5
		  float effected_radius = radius * 4 + (beat * noise(_noiseSeed)) * 0.2f;
		  _noiseSeed += 0.01f;
		  if(_noiseSeed > 500){
		  	_noiseSeed = random(10);
		  }

		  float rad = radians(ang);
		  
		  // x: radius * cosT
		  float x = effected_radius * cos(rad);
		  // y: radius * sinT
		  float y = effected_radius * sin(rad);

		  PVector pCurrent = new PVector(x, y);
		  
		  // \u7dda\u306e\u8272\u3001\u592a\u3055\u306e\u8a2d\u5b9a
		  stroke(200, 0, 255, transparency);
		  strokeWeight(weight);

		  // \u521d\u671f\u5024\u306e\u8a2d\u5b9a
		  if(ang == 0){
		    pPrev = pCurrent;
		  }

		  // \u30d1\u30bf\u30fc\u30f3\u9078\u629e
		  if(dice < 3 && radius > 20){	// \u534a\u5f84\u306e\u5927\u304d\u306a\u3082\u306e\u3092\u4f4e\u78ba\u7387\u3067\u5186\u306e\u96c6\u5408\u3068\u3059\u308b
		  	// \u5186\u306e\u96c6\u5408
		  	strokeWeight(weight);
			float w = random(20);
			ellipse(pCurrent.x, pCurrent.y, w, w);
		  }else{
		  	// \u70b9\u3068\u7dda
		  	strokeWeight(weight);
		  	point(pCurrent.x, pCurrent.y);
		  	line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
		  }

		  pPrev = pCurrent;
		}
	}

	// \u63cf\u753b
	public void draw_side(){

		// \u6b7b\u3093\u3067\u3044\u308b\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306f\u63cf\u753b\u3057\u306a\u3044
		if(isDead()){
			return;
		}

		PVector pPrev = new PVector(radius, 0);

		pushMatrix();

		rotateY(30);
		translate(200, 0, -width);

		for(float ang = 0; ang < 3600; ang += 1){

			translate(0, 0, 1);
			pushMatrix();

				rotateX(random(30)-15);

				// \u534a\u5f84\u3092\u30ce\u30a4\u30ba\u3067\u52a0\u5de5
				float effected_radius = radius * 4 + 20 * noise(_noiseSeed);
				_noiseSeed += 0.01f;
				if(_noiseSeed > 500){
					_noiseSeed = random(10);
				}

				float rad = radians(ang);

				// x: radius * cosT
				float x = effected_radius * cos(rad);
				// y: radius * sinT
				float y = effected_radius * sin(rad);

				PVector pCurrent = new PVector(x, y);

				// \u7dda\u306e\u8272\u3001\u592a\u3055\u306e\u8a2d\u5b9a
				stroke(0, 0, 255, transparency);
				strokeWeight(5);

				// \u521d\u671f\u5024\u306e\u8a2d\u5b9a
				if(ang == 0){
				pPrev = pCurrent;
				}

				line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
				pPrev = pCurrent;
			popMatrix();
		}
		popMatrix();
	}


  // \u63cf\u753b
  public void drawWideRing(){

    // \u6b7b\u3093\u3067\u3044\u308b\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306f\u63cf\u753b\u3057\u306a\u3044
    if(isDead()){
      return;
    }

    PVector pPrev = new PVector(radius, 0);

    strokeWeight((int)random(5) + 5);
  
    for(float ang = 30; ang < 360; ang += 4){

      rotateX(frameCount / 40);
      rotateY(frameCount / 15);

      // \u534a\u5f84\u3092\u30ce\u30a4\u30ba\u3067\u52a0\u5de5
      float effected_radius = radius * 4;

      float rad = radians(ang);
      
      // x: radius * cosT
      float x = effected_radius * cos(rad);
      // y: radius * sinT
      float y = effected_radius * sin(rad);

      PVector pCurrent = new PVector(x, y);
      
      // \u7dda\u306e\u8272\u3001\u592a\u3055\u306e\u8a2d\u5b9a
      stroke(0, 0, 255, pointalpha * 0.4f);

      // \u521d\u671f\u5024\u306e\u8a2d\u5b9a
      if(ang == 30){
        pPrev = pCurrent;
      }

      line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
      pPrev = pCurrent;
    }
  }

	// \u7403\u306e\u63cf\u753b
	// \u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u63cf\u753b
	public void drawSphere(){

	  stroke(random(30)+100);
	  strokeWeight(0.5f);

	  float radius_effected = radius * synth;

	  pushMatrix();
	    rotateX(frameCount * 0.03f * random(10));
	    rotateY(frameCount * 0.04f * random(10));
	    float s = 0;
	    float t = 0;

	    PVector lastPos = new PVector(0, 0, 0);
	    PVector thisPos = new PVector(0, 0, 0);

	    while(t < 180){
	      s += noise(random(10))*random(360);
	      t += noise(random(10))*random(20);
	      float radianS = radians(s);
	      float radianT = radians(t);

	      thisPos.x = 0 + (radius_effected * cos(radianS) * sin(radianT));
	      thisPos.y = 0 + (radius_effected * sin(radianS) * sin(radianT));
	      thisPos.z = 0 + (radius_effected * cos(radianT));

	      stroke(col, noiz * 0.6f, random(255), noiz*70);

	      if(lastPos.x != 0){
	        line(thisPos.x, thisPos.y, thisPos.z, lastPos.x, lastPos.y, lastPos.z);
	        point(thisPos.x, thisPos.y, thisPos.z); 
	      }

	      lastPos = thisPos.get();
	    }
	  popMatrix();
	}
}


// OSC\u53d7\u4fe1\u30a4\u30d9\u30f3\u30c8
public void oscEvent(OscMessage theOscMessage) {

	// \u63cf\u753b\u30d1\u30bf\u30fc\u30f3
 	pattern = getValue(theOscMessage, "/Pattern", pattern);
 	scene = getValue(theOscMessage, "/scene", scene);

 	beat = getValue(theOscMessage, "/HeartBeat", 1, 500, beat);
 	globalWeight = getValue(theOscMessage, "/piano", 2, 8, globalWeight);

 	pointalpha = getValue(theOscMessage, "/piano", 0, 250, pointalpha);

 	noiz = getValue(theOscMessage, "/noise", 1, 250, noiz);
 	synth = getValue(theOscMessage, "/synth", 0, 3, synth);
 	lfosin = getValue(theOscMessage, "/sin", 10, 30, lfosin);
 	beat = getValue(theOscMessage, "/beat", 2, 500, beat);
 	col = getValue(theOscMessage, "/col", 2, 500, col);

}

// OSC\u30e1\u30c3\u30bb\u30fc\u30b8\u304b\u3089\u306e\u30c7\u30fc\u30bf\u53d6\u5f97\u51e6\u7406
public float getValue(OscMessage theOscMessage, String thePrefix, float minValue, float maxValue, float ret){

	// prefix\u306e\u53d6\u5f97
	String prefix = theOscMessage.addrPattern();

	// prefix\u304c\u6307\u5b9a\u3057\u305f\u5024\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
	if(prefix.equals(thePrefix)){
    	// \u5024\u3092\u53d6\u5f97
    	float fval = (float)theOscMessage.get(0).intValue();
    	ret = map(fval, 0, 127, minValue, maxValue);

    	if(prefix.equals("/piano")){
	    	// \u5e74\u8f2a\u306e\u8a2d\u5b9a
		    if(fval > 30){
		    	// \u5168\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u5b58\u78ba\u8a8d\u3002\u6b7b\u4ea1\u3057\u3066\u308b\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u3092\u518d\u751f\u3055\u305b\u308b\u3002
		    	for(int i = 0; i < ringList.size(); i++){
			    	ring theRing = ringList.get(i);
			    	if(theRing.isDead()){
			    		theRing.reset(i);
			    	}
			    }
		    }
		}
	}
	return ret;
}

// OSC\u30e1\u30c3\u30bb\u30fc\u30b8\u304b\u3089\u306e\u30c7\u30fc\u30bf\u53d6\u5f97\u51e6\u7406
public int getValue(OscMessage theOscMessage, String thePrefix, int ret){

	// prefix\u306e\u53d6\u5f97
	String prefix = theOscMessage.addrPattern();

	// prefix\u304c\u6307\u5b9a\u3057\u305f\u5024\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
	if(prefix.equals(thePrefix)){
    	// \u5024\u3092\u53d6\u5f97
    	int ival = theOscMessage.get(0).intValue();
    	ret = ival;
	}

	return ret;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--hide-stop", "Keyaki_ver05" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
