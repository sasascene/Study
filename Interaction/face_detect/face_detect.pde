import gab.opencv.*;
import processing.video.*;
import java.awt.*;

Capture video;
OpenCV opencv;

// 初期設定
void setup() {
  size(640, 480);
  colorMode(HSB, 360, 100, 100);
  smooth();
  video = new Capture(this, 640/2, 480/2);
  opencv = new OpenCV(this, 640/2, 480/2);
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);
  // opencv.loadCascade(OpenCV.CASCADE_FULLBODY);

  video.start();
}

// 描画
void draw() {
  scale(2); // 画像を画面いっぱいにスケーリング
  opencv.loadImage(video);

  // image(video, 0, 0);  // キャプチャ画像の描画

  //  キーカラーの指定
  int h = 196;

  // 背景描画
  drawBackground(h);

  // 描画設定
  noFill();
  stroke(h, 40, 100);
  strokeWeight(2);

  // 顔の検出
  Rectangle[] faces = opencv.detect();

  // 検出した顔の数
  // println("faces: " + faces.length);

  for (int i = 0; i < faces.length; i++) {
    // 検出した顔の座標
    println("face" + i + ";" + faces[i].x + "," + faces[i].y);
    ellipse(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
  }
}

// キャプチャ
void captureEvent(Capture c) {
  c.read();
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
      float s = map(d, 0, maxd, 40, 100); // 彩度
      // 色の生成
      color c = color(h, s, 100);
      // 色の適用
      set(x, y, c);
    }
  }
}


