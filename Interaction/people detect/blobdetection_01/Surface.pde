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

  void create(){
    // This is what box2d uses to put the surface in its world
    ChainShape chain = new ChainShape();

    // noise
    makeNoiseShape();
    
    // Build an array of vertices in Box2D coordinates
    buildShape(chain);
  }

  // A simple function to just draw the edge chain as a series of vertex points
  void display() {
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

  void makeNoiseShape(){
    // Perlin noise argument
    float xoff = 0.0;

    // This has to go backwards so that the objects  bounce off the top of the surface
    // This "edgechain" will only work in one direction!
    for (float x = width+10; x > -10; x -= 5) {

      // Doing some stuff with perlin noise to calculate a surface that points down on one side
      // and up on the other
      float y;
      if (x > width/2) {
        y = 100 + (width - x)*1.1 + map(noise(xoff),0,1,-80,80);
      } 
      else {
        y = 100 + x*1.1 + map(noise(xoff),0,1,-80,80);
      }

      // Store the vertex in screen coordinates
      surface.add(new Vec2(x,y));

      // Move through perlin noise
      xoff += 0.1;

    }
  }

  void buildShape(ChainShape chain){
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

  void clear(){
    surface.clear();
  }

  void add(float x, float y){
    // Store the vertex in screen coordinates
    surface.add(new Vec2(x,y));
  }

  void createHumanShape(){
    // This is what box2d uses to put the surface in its world
    ChainShape chain = new ChainShape();
    
    // Build an array of vertices in Box2D coordinates
    buildShape(chain);
  }

  // This function removes the particle from the box2d world
  void killBody() {
    box2d.world.destroyBody(body);
  }

}


