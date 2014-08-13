import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 
import blobDetection.*; 
import pbox2d.*; 
import org.jbox2d.collision.shapes.*; 
import org.jbox2d.common.*; 
import org.jbox2d.dynamics.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class blobdetection_01 extends PApplet {

// - Super Fast Blur v1.1 by Mario Klingemann <http://incubator.quasimondo.com>
// - BlobDetection library




Capture cam;
BlobDetection theBlobDetection;
PImage img;
boolean newFrame=false;

// ==================================================
// box2D





// A reference to our box2d world
PBox2D box2d;

// A list for all of our rectangles
ArrayList<Box> boxes;
// An object to store information about the uneven surface
Surface surface;
// ==================================================

// ==================================================
// setup()
// ==================================================
public void setup()
{

	colorMode(HSB, 360, 100, 100);
	smooth();

  // Size of applet
  size(640, 480);
  // Capture
  cam = new Capture(this, 40*4, 30*4, 15);
        // Comment the following line if you use Processing 1.5
        cam.start();
        
  // BlobDetection
  // img which will be sent to detection (a smaller copy of the cam frame);
  img = new PImage(80,60); 
  theBlobDetection = new BlobDetection(img.width, img.height);
  // \u660e\u308b\u3044\u90e8\u5206\u3092\u691c\u51fa\u3059\u308b\u304b\u3001\u6697\u3044\u90e8\u5206\u3092\u691c\u51fa\u3059\u308b\u304b
	theBlobDetection.setPosDiscrimination(true);
  // \u6307\u5b9a\u3057\u305f\u660e\u308b\u3055\u4ee5\u4e0a\u306e\u90e8\u5206\u3092\u691c\u51fa\u5bfe\u8c61\u3068\u3059\u308b
  theBlobDetection.setThreshold(0.4f); // will detect bright areas whose luminosity > 0.2f;

  // Initialize box2d physics and create the world
  box2d = new PBox2D(this);
  box2d.createWorld();
  // We are setting a custom gravity
  box2d.setGravity(0, -5);

  // Create ArrayLists	
  boxes = new ArrayList<Box>();

  // Create the surface
  surface = new Surface();
  // surface.create();
}

// ==================================================
// captureEvent()
// ==================================================
public void captureEvent(Capture cam)
{
  cam.read();
  newFrame = true;
}

// ==================================================
// draw()
// ==================================================
public void draw()
{
  if (newFrame)
  {
    newFrame=false;
  	drawBackground(196);
    // image(cam,0,0,width,height);
    img.copy(cam, 0, 0, cam.width, cam.height, 
        0, 0, img.width, img.height);
    fastblur(img, 8);
    theBlobDetection.computeBlobs(img.pixels);
    drawBlobsAndEdges(false, true);

    // \u3072\u3068\u304c\u305f\u306e\u4f5c\u6210
    surface.createHumanShape();
  }



  // We must always step through time!
  box2d.step();

  strokeWeight(1);
  stroke(150,30,100);

  // Draw the surface
  surface.display();

  // When the mouse is clicked, add a new Box object
  if (random(1) < 0.1f) {
    Box p = new Box(random(width),10);
    boxes.add(p);
  }
  
  if (mousePressed) {
    for (Box b: boxes) {
     Vec2 wind = new Vec2(20,0);
     b.applyForce(wind);
    }
  }

  // Display all the boxes
  for (Box b: boxes) {
    b.display();
  }

  // Boxes that leave the screen, we delete them
  // (note they have to be deleted from both the box2d world and our list
  for (int i = boxes.size()-1; i >= 0; i--) {
    Box b = boxes.get(i);
    if (b.done()) {
      boxes.remove(i);
    }
  }
}

// ==================================================
// drawBlobsAndEdges()
// ==================================================
public void drawBlobsAndEdges(boolean drawBlobs, boolean drawEdges)
{
  noFill();
  Blob b;
  EdgeVertex eA,eB;
  for (int n=0 ; n<theBlobDetection.getBlobNb() ; n++)
  {
    b=theBlobDetection.getBlob(n);
    if (b!=null)
    {
      // Edges
      if (drawEdges)
      {
        strokeWeight(4);
        stroke(196,30,100);
        for (int m=0;m<b.getEdgeNb();m++)
        {
          eA = b.getEdgeVertexA(m);
          eB = b.getEdgeVertexB(m);
          if (eA !=null && eB !=null){
            line(
              // eA.x*width, eA.y*height, 
              // eB.x*width, eB.y*height
              // \u5de6\u53f3\u53cd\u8ee2\u306b\u3057\u3066\u63cf\u753b
              width - eA.x*width, eA.y*height, 
              width - eB.x*width, eB.y*height
              );
            // \u9802\u70b9\u306e\u8ffd\u52a0
            float d = dist(width - eA.x*width, eA.y*height, width - eB.x*width, eB.y*height);
            if(d < 5){
              surface.add(width - eA.x*width, eA.y*height);
              // surface.add(width - eB.x*width, eB.y*height);
            }
          }
        }
      }

      // Blobs
      if (drawBlobs)
      {
        strokeWeight(3);
        stroke(100,30,100);
        rect(
          b.xMin*width,b.yMin*height,
          b.w*width,b.h*height
          );
      }
    }
  }
}

// ==================================================
// Super Fast Blur v1.1
// by Mario Klingemann 
// <http://incubator.quasimondo.com>
// ==================================================
public void fastblur(PImage img,int radius)
{
 if (radius<1){
    return;
  }
  int w=img.width;
  int h=img.height;
  int wm=w-1;
  int hm=h-1;
  int wh=w*h;
  int div=radius+radius+1;
  int r[]=new int[wh];
  int g[]=new int[wh];
  int b[]=new int[wh];
  int rsum,gsum,bsum,x,y,i,p,p1,p2,yp,yi,yw;
  int vmin[] = new int[max(w,h)];
  int vmax[] = new int[max(w,h)];
  int[] pix=img.pixels;
  int dv[]=new int[256*div];
  for (i=0;i<256*div;i++){
    dv[i]=(i/div);
  }

  yw=yi=0;

  for (y=0;y<h;y++){
    rsum=gsum=bsum=0;
    for(i=-radius;i<=radius;i++){
      p=pix[yi+min(wm,max(i,0))];
      rsum+=(p & 0xff0000)>>16;
      gsum+=(p & 0x00ff00)>>8;
      bsum+= p & 0x0000ff;
    }
    for (x=0;x<w;x++){

      r[yi]=dv[rsum];
      g[yi]=dv[gsum];
      b[yi]=dv[bsum];

      if(y==0){
        vmin[x]=min(x+radius+1,wm);
        vmax[x]=max(x-radius,0);
      }
      p1=pix[yw+vmin[x]];
      p2=pix[yw+vmax[x]];

      rsum+=((p1 & 0xff0000)-(p2 & 0xff0000))>>16;
      gsum+=((p1 & 0x00ff00)-(p2 & 0x00ff00))>>8;
      bsum+= (p1 & 0x0000ff)-(p2 & 0x0000ff);
      yi++;
    }
    yw+=w;
  }

  for (x=0;x<w;x++){
    rsum=gsum=bsum=0;
    yp=-radius*w;
    for(i=-radius;i<=radius;i++){
      yi=max(0,yp)+x;
      rsum+=r[yi];
      gsum+=g[yi];
      bsum+=b[yi];
      yp+=w;
    }
    yi=x;
    for (y=0;y<h;y++){
      pix[yi]=0xff000000 | (dv[rsum]<<16) | (dv[gsum]<<8) | dv[bsum];
      if(x==0){
        vmin[y]=min(y+radius+1,hm)*w;
        vmax[y]=max(y-radius,0)*w;
      }
      p1=x+vmin[y];
      p2=x+vmax[y];

      rsum+=r[p1]-r[p2];
      gsum+=g[p1]-g[p2];
      bsum+=b[p1]-b[p2];

      yi+=w;
    }
  }

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
// The Nature of Code
// <http://www.shiffman.net/teaching/nature>
// Spring 2012
// PBox2D example

// A fixed boundary class

class Boundary {

  // A boundary is a simple rectangle with x,y,width,and height
  float x;
  float y;
  float w;
  float h;
  
  // But we also have to make a body for box2d to know about it
  Body b;

  Boundary(float x_,float y_, float w_, float h_) {
    x = x_;
    y = y_;
    w = w_;
    h = h_;

    // Define the polygon
    PolygonShape sd = new PolygonShape();
    // Figure out the box2d coordinates
    float box2dW = box2d.scalarPixelsToWorld(w/2);
    float box2dH = box2d.scalarPixelsToWorld(h/2);
    // We're just a box
    sd.setAsBox(box2dW, box2dH);


    // Create the body
    BodyDef bd = new BodyDef();
    bd.type = BodyType.STATIC;
    bd.position.set(box2d.coordPixelsToWorld(x,y));
    b = box2d.createBody(bd);
    
    // Attached the shape to the body using a Fixture
    b.createFixture(sd,1);
  }

  // Draw the boundary, if it were at an angle we'd have to do something fancier
  public void display() {
    fill(0);
    stroke(0);
    rectMode(CENTER);
    rect(x,y,w,h);
  }

}


// The Nature of Code
// <http://www.shiffman.net/teaching/nature>
// Spring 2012
// PBox2D example

// A rectangular box
class Box {

  // We need to keep track of a Body and a width and height
  Body body;
  float w;
  float h;

  // Constructor
  Box(float x, float y) {
    w = random(8, 16);
    h = w;
    // Add the box to the box2d world
    makeBody(new Vec2(x, y), w, h);
  }

  // This function removes the particle from the box2d world
  public void killBody() {
    box2d.destroyBody(body);
  }

  // Is the particle ready for deletion?
  public boolean done() {
    // Let's find the screen position of the particle
    Vec2 pos = box2d.getBodyPixelCoord(body);  
    // Is it off the bottom of the screen?
    if (pos.y > height+w*h) {
      killBody();
      return true;
    }
    return false;
  }

  public void applyForce(Vec2 force) {
    Vec2 pos = body.getWorldCenter();
    body.applyForce(force, pos);
  }

  // Drawing the box
  public void display() {
    // We look at each body and get its screen position
    Vec2 pos = box2d.getBodyPixelCoord(body);
    // Get its angle of rotation
    float a = body.getAngle();

    rectMode(CENTER);
    pushMatrix();
    translate(pos.x, pos.y);
    rotate(-a);
    // fill(175);
    // stroke(0);
    noFill();
    strokeWeight(2);
    stroke(196,30,100);
    rect(0, 0, w, h);
    popMatrix();
  }

  // This function adds the rectangle to the box2d world
  public void makeBody(Vec2 center, float w_, float h_) {

    // Define a polygon (this is what we use for a rectangle)
    PolygonShape sd = new PolygonShape();
    float box2dW = box2d.scalarPixelsToWorld(w_/2);
    float box2dH = box2d.scalarPixelsToWorld(h_/2);
    sd.setAsBox(box2dW, box2dH);

    // Define a fixture
    FixtureDef fd = new FixtureDef();
    fd.shape = sd;
    // Parameters that affect physics
    fd.density = 1;
    fd.friction = 0.3f;
    fd.restitution = 0.2f;

    // Define the body and make it from the shape
    BodyDef bd = new BodyDef();
    bd.type = BodyType.DYNAMIC;
    bd.position.set(box2d.coordPixelsToWorld(center));
    bd.angle = random(TWO_PI);

    body = box2d.createBody(bd);
    body.createFixture(fd);
  }
}

// The Nature of Code
// <http://www.shiffman.net/teaching/nature>
// Spring 2010
// PBox2D example

// An uneven surface boundary

class Surface {
  // We'll keep track of all of the surface points
  ArrayList<Vec2> surface;
  Body body;


  Surface() {
    surface = new ArrayList<Vec2>();
  }

  public void create(){
    // This is what box2d uses to put the surface in its world
    ChainShape chain = new ChainShape();

    // noise
    makeNoiseShape();
    
    // Build an array of vertices in Box2D coordinates
    buildShape(chain);
  }

  // A simple function to just draw the edge chain as a series of vertex points
  public void display() {
    strokeWeight(2);
    stroke(190, 40, 100);
    noFill();
    beginShape();
    for (Vec2 v: surface) {
      vertex(v.x,v.y);
    }
    endShape();

    clear();
  }

  public void makeNoiseShape(){
    // Perlin noise argument
    float xoff = 0.0f;

    // This has to go backwards so that the objects  bounce off the top of the surface
    // This "edgechain" will only work in one direction!
    for (float x = width+10; x > -10; x -= 5) {

      // Doing some stuff with perlin noise to calculate a surface that points down on one side
      // and up on the other
      float y;
      if (x > width/2) {
        y = 100 + (width - x)*1.1f + map(noise(xoff),0,1,-80,80);
      } 
      else {
        y = 100 + x*1.1f + map(noise(xoff),0,1,-80,80);
      }

      // Store the vertex in screen coordinates
      surface.add(new Vec2(x,y));

      // Move through perlin noise
      xoff += 0.1f;

    }
  }

  public void buildShape(ChainShape chain){
    // Build an array of vertices in Box2D coordinates
    // from the ArrayList we made
    Vec2[] vertices = new Vec2[surface.size()];
    for (int i = 0; i < vertices.length; i++) {
      Vec2 edge = box2d.coordPixelsToWorld(surface.get(i));
      vertices[i] = edge;
    }
    
    // Create the chain!
    chain.createChain(vertices,vertices.length);
    
    // The edge chain is now attached to a body via a fixture
    BodyDef bd = new BodyDef();
    bd.position.set(0.0f,0.0f);
    if(body != null){
      killBody();
    }  
    body = box2d.createBody(bd);
    // Shortcut, we could define a fixture if we
    // want to specify frictions, restitution, etc.
    body.createFixture(chain,1);
  }

  public void clear(){
    surface.clear();
  }

  public void add(float x, float y){
    // Store the vertex in screen coordinates
    surface.add(new Vec2(x,y));
  }

  public void createHumanShape(){
    // This is what box2d uses to put the surface in its world
    ChainShape chain = new ChainShape();
    
    // Build an array of vertices in Box2D coordinates
    buildShape(chain);
  }

  // This function removes the particle from the box2d world
  public void killBody() {
    box2d.world.destroyBody(body);
  }

}


  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "blobdetection_01" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
