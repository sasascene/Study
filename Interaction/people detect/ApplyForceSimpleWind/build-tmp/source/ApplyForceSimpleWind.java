import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

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

public class ApplyForceSimpleWind extends PApplet {

// The Nature of Code
// <http://www.shiffman.net/teaching/nature>
// Spring 2011
// PBox2D example

// Basic example of falling rectangles






// A reference to our box2d world
PBox2D box2d;

// A list we'll use to track fixed objects
ArrayList<Boundary> boundaries;
// A list for all of our rectangles
ArrayList<Box> boxes;

public void setup() {
  size(640,360);
  smooth();

  // Initialize box2d physics and create the world
  box2d = new PBox2D(this);
  box2d.createWorld();
  // We are setting a custom gravity
  box2d.setGravity(0, -20);

  // Create ArrayLists	
  boxes = new ArrayList<Box>();
  boundaries = new ArrayList<Boundary>();

  // Add a bunch of fixed boundaries
  boundaries.add(new Boundary(width/4,height-5,width/2-100,10));
  boundaries.add(new Boundary(3*width/4,height-5,width/2-100,10));
  boundaries.add(new Boundary(width-5,height/2,10,height));
  boundaries.add(new Boundary(5,height/2,10,height));
}

public void draw() {
  background(255);

  // We must always step through time!
  box2d.step();

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

  // Display all the boundaries
  for (Boundary wall: boundaries) {
    wall.display();
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
  
  fill(0);
  text("Click mouse to apply a wind force.",20,20);
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
    fill(175);
    stroke(0);
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

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ApplyForceSimpleWind" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
