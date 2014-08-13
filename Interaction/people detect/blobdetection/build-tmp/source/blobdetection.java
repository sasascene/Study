import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.video.*; 
import blobDetection.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class blobdetection extends PApplet {

// - Super Fast Blur v1.1 by Mario Klingemann <http://incubator.quasimondo.com>
// - BlobDetection library




Capture cam;
BlobDetection theBlobDetection;
PImage img;
boolean newFrame=false;

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
          if (eA !=null && eB !=null)
            line(
              // eA.x*width, eA.y*height, 
              // eB.x*width, eB.y*height
              // \u5de6\u53f3\u53cd\u8ee2\u306b\u3057\u3066\u63cf\u753b
              width - eA.x*width, eA.y*height, 
              width - eB.x*width, eB.y*height
              );
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

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "blobdetection" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
