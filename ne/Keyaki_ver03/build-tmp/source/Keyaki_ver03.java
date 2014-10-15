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

public class Keyaki_ver03 extends PApplet {




 
  
OscP5 oscP5;
NetAddress myRemoteLocation;

float _noiseSeed;     // \u30ce\u30a4\u30ba\u306e\u7a2e

PImage offscr;	// \u30aa\u30d5\u30b9\u30af\u30ea\u30fc\u30f3


float weight = 0.3f;
float col;
float col2;
float beat;
float noi;
float noiz;
float lfosin;
float lfocos;
float synth;
float pointalpha = 0;
int pattern = 0;

float noize_offset = 0.2f;


ArrayList<ring> ringList;  // \u30ea\u30f3\u30b0\u30ea\u30b9\u30c8


// \u521d\u671f\u8a2d\u5b9a
public void setup(){

	size(800, 800, OPENGL);
  // size(1600, 900, OPENGL);  // \u30d5\u30eb\u30b9\u30af\u30ea\u30fc\u30f3\u7528
  noFill();
  smooth();
  noCursor();
  
  // \u30ce\u30a4\u30ba\u306e\u7a2e\u3092\u30e9\u30f3\u30c0\u30e0\u306b\u8a2d\u5b9a
  _noiseSeed = random(10);
  
  // procressing\u306e\u53d7\u4fe1\u30dd\u30fc\u30c8(pd\u306e\u9001\u4fe1\u30dd\u30fc\u30c8)
  oscP5 = new OscP5(this,7702);
  
  // procressing\u306e\u9001\u4fe1\u30dd\u30fc\u30c8(pd\u306e\u53d7\u4fe1\u30dd\u30fc\u30c8)
  //myRemoteLocation = new NetAddress("127.0.0.1",7702);

  colorMode(HSB, 360, 255, 255);

  offscr = createImage(width, height, RGB);

  // \u5e74\u8f2a\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
  makeRing(6);
}

// \u63cf\u753b
public void draw(){

	  // \u30aa\u30d5\u30b9\u30af\u30ea\u30fc\u30f3\u30d0\u30c3\u30d5\u30a1\u306e\u66f4\u65b0
  loadPixels();
  offscr.pixels = pixels;
  offscr.updatePixels();

  // \u80cc\u666f\u306e\u30af\u30ea\u30a2
  background(0, 0, 0);

  // \u901a\u5e38\u306e\u63cf\u753b\u51e6\u7406


  // \u5ea7\u6a19\u8ef8\u306e\u79fb\u52d5
  PVector center = new PVector(width/2, height/2);
  translate(center.x, center.y);

  // \u5e74\u8f2a\u306e\u63cf\u753b
  drawRing();

  // \u30aa\u30d5\u30b9\u30af\u30ea\u30fc\u30f3\u30d0\u30c3\u30d5\u30a1\u306e\u63cf\u753b
  tint(255, 240);
  translate(-center.x, -center.y);
  image(offscr, -3, -3, width + 6, height + 6); // \u5c11\u3057\u3060\u3051\u62e1\u5927\u3057\u3066\u307f\u308b
 	
}

// \u5e74\u8f2a\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u751f\u6210
public void makeRing(int num){

	// \u30ea\u30b9\u30c8\u306e\u521d\u671f\u5316
	ringList = new ArrayList<ring>();

	PVector center = new PVector((random(20)-10) * width/2, (random(20)-10) * height/2);

	for(int i = 0; i < num; i++){
		ring theRing = new ring(center, 10 * i);
		theRing.setWeight(0.5f);
		ringList.add(theRing);
	}
}

// \u5e74\u8f2a\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u63cf\u753b
public void drawRing(){

		// strokeWeight(3);
		// stroke(200, 0, 200, theBar.transparency);
		// point(theBar.center.x, theBar.center.y);
		// point(0, 0);

	for(int i = 0; i < ringList.size(); i++){
		ring theRing = ringList.get(i);
		theRing.update();
		
		// \u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u3054\u3068\u306b\u56de\u8ee2
		// pushMatrix();
			rotateZ(i * frameCount / 10);
			switch (pattern) {
				case 0:
					rotateX(i * frameCount / 30);
					theRing.draw();	// \u901a\u5e38\u30d1\u30bf\u30fc\u30f3
					break;
				case 1:
					rotateX(i * frameCount / 30);
					rotateY(i * frameCount / 50);
					theRing.draw_noise();	// \u30ce\u30a4\u30ba\u30d1\u30bf\u30fc\u30f3
					break;
				case 2:
					// theRing.drawRingFlake();	// \u96ea\u30fb\u96fb\u6c17\u306e\u30a4\u30e1\u30fc\u30b8
					break;
				default:
			}

		theRing.drawSphere();

	}

	// // \u6ce2\u5f62\u306e\u63cf\u753b
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
			strokeWeight(0.5f);
			stroke(synth, noiz, random(255), noiz*70);
			line(i - width/4 - 1, w[i-1], i - width/4, w[i]);
			strokeWeight(2);
			stroke(random(synth), noiz * 0.8f, random(255), noiz*70);
			line(i - width/4 -1, w2[i-1], i - width/4, w2[i]);
			strokeWeight(3);
			stroke(col, noiz * 0.6f, random(255), noiz*70);
			line(i - width/4 -1, w3[i-1], i - width/4, w3[i]);
		}
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
  	// radius += 0.3;
  	if(radius > 300){
  		dead = true;
  	}

  	// \u900f\u660e\u5ea6\u3092\u8a2d\u5b9a\u3002\u3057\u304d\u3044\u5024\u3092\u8d85\u3048\u305f\u3089\u6b7b\u4ea1\u6271\u3044\u3068\u3059\u308b\u3002
  	if(transparency >= 20){
  		transparency = pointalpha;
  	}else {
  		transparency = 0;
			dead = true;
  	}
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

    float col1 = random(col);
  
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
		  	strokeWeight(0.4f);
			  float w = random(10);
			  ellipse(pCurrent.x, pCurrent.y, w, w);
		  }else{
		  	// \u70b9\u3068\u7dda
		  	strokeWeight(20 * radius/300);		// \u540c\u3058\u30b5\u30a4\u30ba\u306e\u70b9
		  	point(pCurrent.x, pCurrent.y);
		  	strokeWeight(4 * radius/300);
		  	line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
		  }

		  pPrev = pCurrent;
		}
	}

  // \u63cf\u753b
	public void drawRingFlake(){

		// \u6b7b\u3093\u3067\u3044\u308b\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306f\u63cf\u753b\u3057\u306a\u3044
		if(isDead()){
			return;
		}

	  // \u534a\u5f84\u3092\u30ce\u30a4\u30ba\u3067\u52a0\u5de5
	  PVector pPrev = new PVector(radius, 0);
	  
	  float offset = 0.05f;
  
    for(float ang = 0; ang < 360; ang += 0.4f){

      // \u534a\u5f84\u3092\u30ce\u30a4\u30ba\u3067\u52a0\u5de5
      float effected_radius = radius * noise(_noiseSeed) * beat / 80;	// \u96ea\u30fb\u96fb\u6c17\u306e\u30a4\u30e1\u30fc\u30b8

      float rad = radians(ang);
      
      // x: radius * cosT
      float x = effected_radius * cos(rad);
      // y: radius * sinT
      float y = effected_radius * sin(rad);

      PVector pCurrent = new PVector(x, y);
      
      stroke(200, 0, 255, transparency);

      strokeWeight(random(30));

      if(ang == 0){
        pPrev = pCurrent;
      }

      line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
      pPrev = pCurrent;
      
      offset = noi * noise(_noiseSeed);
      _noiseSeed += 0.01f;
      if(_noiseSeed > 300){
        _noiseSeed = random(10);
      }
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

	      if(lastPos.x != 0){
	        line(thisPos.x, thisPos.y, thisPos.z, lastPos.x, lastPos.y, lastPos.z);
	        point(thisPos.x, thisPos.y, thisPos.z); 
	      }

	      lastPos = thisPos.get();
	    }
	  popMatrix();

	  // // \u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u30b5\u30a4\u30ba\u3092\u7e2e\u5c0f
	  // float rate = 0.95;
	  // oscval[n] = radius / rad * rate;
	}
}


// OSC\u53d7\u4fe1\u30a4\u30d9\u30f3\u30c8
public void oscEvent(OscMessage theOscMessage) {

  // prefix\u306e\u53d6\u5f97
  String prefix = theOscMessage.addrPattern();

  // prefix\u304c"/nseq/"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
  if(prefix.equals("/temp/")){
    // \u30c8\u30e9\u30c3\u30afNo\u3092\u53d6\u5f97
    int index = theOscMessage.get(0).intValue();
    // \u30c8\u30ea\u30ac\u30fc\u3092\u53d6\u5f97
    float fval = (float)theOscMessage.get(1).intValue();
  }else if(prefix.equals("/noi")){ // prefix\u304c"/noi"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    float fval = (float)theOscMessage.get(0).intValue();
    noi = map(fval, 0, 127, 10, 30);
  }else if(prefix.equals("/weight")){ // prefix\u304c"/weight"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    float fval = (float)theOscMessage.get(0).intValue();
    weight = map(fval, 0, 127, 1, 8);
  }else if(prefix.equals("/col")){ // prefix\u304c"/col"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    float fval = (float)theOscMessage.get(0).intValue();
    col = map(fval, 0, 127, 140, 180);
  }else if(prefix.equals("/beat")){ // prefix\u304c"/beat"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    float fval = (float)theOscMessage.get(0).intValue();
    beat = map(fval, 0, 127, 2, 500);
  }else if(prefix.equals("/piano")){ // prefix\u304c"/piano"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    float fval = (float)theOscMessage.get(0).intValue();
    weight = map(fval, 0, 127, 0.3f, 30);
    noize_offset = 1 - map(fval, 0, 127, 0.5f, 1.3f);
    pointalpha = map(fval, 0, 127, 0, 250);

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

  }else if(prefix.equals("/synth")){ // prefix\u304c"/synth"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    float fval = (float)theOscMessage.get(0).intValue();
    synth = map(fval, 0, 127, 0, 3);
  }else if(prefix.equals("/noise")){ // prefix\u304c"/noise"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    float fval = (float)theOscMessage.get(0).intValue();
    noiz = map(fval, 0, 127, 1, 250);
    col2 = map(fval, 0, 127, 0, 360);
  }else if(prefix.equals("/sin")){ // prefix\u304c"/sin"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    float fval = (float)theOscMessage.get(0).intValue();
    lfosin = map(fval, 0, 127, 10, 30);
  }else if(prefix.equals("/cos")){ // prefix\u304c"/cos"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    float fval = (float)theOscMessage.get(0).intValue();
    lfocos = map(fval, 0, 127, -1, 1);
  }else if(prefix.equals("/pattern")){ // prefix\u304c"/pattern"\u306e\u5834\u5408\u306e\u307f\u5b9f\u884c
    pattern = theOscMessage.get(0).intValue();
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--hide-stop", "Keyaki_ver03" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
