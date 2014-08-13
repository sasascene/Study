// - Super Fast Blur v1.1 by Mario Klingemann <http://incubator.quasimondo.com>
// - BlobDetection library

import processing.video.*;
import blobDetection.*;

Capture cam;
BlobDetection theBlobDetection;
PImage img;
boolean newFrame=false;

// ==================================================
// box2D
import pbox2d.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

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
void setup()
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
  // 明るい部分を検出するか、暗い部分を検出するか
	theBlobDetection.setPosDiscrimination(true);
  // 指定した明るさ以上の部分を検出対象とする
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
void captureEvent(Capture cam)
{
  cam.read();
  newFrame = true;
}

// ==================================================
// draw()
// ==================================================
void draw()
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

    // ひとがたの作成
    surface.createHumanShape();
  }



  // We must always step through time!
  box2d.step();

  strokeWeight(1);
  stroke(150,30,100);

  // Draw the surface
  surface.display();

  // When the mouse is clicked, add a new Box object
  if (random(1) < 0.1) {
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
void drawBlobsAndEdges(boolean drawBlobs, boolean drawEdges)
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
              // 左右反転にして描画
              width - eA.x*width, eA.y*height, 
              width - eB.x*width, eB.y*height
              );
            // 頂点の追加
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
void fastblur(PImage img,int radius)
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

// 背景描画
void drawBackground(int h){

	// 原点と端の最大距離
	float maxd = dist(0, 0, width, height);

	// 全座標をループ
  for(int y = 0; y < height; y++){
    for(int x = 0; x < width; x++){
    	// 指定した座標との距離
    	float d = abs(height - y);
    	float s = map(d, 0, height, 40, 100);
     	// 色の生成
     	color c = color(h, s, 100);
     	// 色の適用
     	set(x, y, c);
    } 
  }
}

// 背景描画(指定座標との距離)
void drawBackground(int x1, int y1, int h){
	// 指定した座標との距離を彩度の手がかりに使用する

	// 原点と端の最大距離
	float maxd = dist(0, 0, width, height);

	// 全座標をループ
	for(int y = 0; y < height; y++){
  	for(int x = 0; x < width; x++){
  		// 指定した座標との距離
  		float d = dist(x1, y1, x, y);
  		float s = map(d, 0, maxd, 40, 100);	// 彩度
  		// 色の生成
   		color c = color(h, s, 100);
   		// 色の適用
   		set(x, y, c);
  	}
	}
}
