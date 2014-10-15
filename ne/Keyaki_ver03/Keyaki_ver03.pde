import processing.opengl.*;
import oscP5.*;
import netP5.*;
import java.util.ArrayList; 
  
OscP5 oscP5;
NetAddress myRemoteLocation;

float _noiseSeed;     // ノイズの種

PImage offscr;	// オフスクリーン


float weight = 0.3;
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

float noize_offset = 0.2;


ArrayList<ring> ringList;  // リングリスト


// 初期設定
void setup(){

	size(800, 800, OPENGL);
  // size(1600, 900, OPENGL);  // フルスクリーン用
  noFill();
  smooth();
  noCursor();
  
  // ノイズの種をランダムに設定
  _noiseSeed = random(10);
  
  // procressingの受信ポート(pdの送信ポート)
  oscP5 = new OscP5(this,7702);
  
  // procressingの送信ポート(pdの受信ポート)
  //myRemoteLocation = new NetAddress("127.0.0.1",7702);

  colorMode(HSB, 360, 255, 255);

  offscr = createImage(width, height, RGB);

  // 年輪オブジェクトの生成
  makeRing(6);
}

// 描画
void draw(){

	  // オフスクリーンバッファの更新
  loadPixels();
  offscr.pixels = pixels;
  offscr.updatePixels();

  // 背景のクリア
  background(0, 0, 0);

  // 通常の描画処理


  // 座標軸の移動
  PVector center = new PVector(width/2, height/2);
  translate(center.x, center.y);

  // 年輪の描画
  drawRing();

  // オフスクリーンバッファの描画
  tint(255, 240);
  translate(-center.x, -center.y);
  image(offscr, -3, -3, width + 6, height + 6); // 少しだけ拡大してみる
 	
}

// 年輪オブジェクトの生成
void makeRing(int num){

	// リストの初期化
	ringList = new ArrayList<ring>();

	PVector center = new PVector((random(20)-10) * width/2, (random(20)-10) * height/2);

	for(int i = 0; i < num; i++){
		ring theRing = new ring(center, 10 * i);
		theRing.setWeight(0.5);
		ringList.add(theRing);
	}
}

// 年輪オブジェクトの描画
void drawRing(){

		// strokeWeight(3);
		// stroke(200, 0, 200, theBar.transparency);
		// point(theBar.center.x, theBar.center.y);
		// point(0, 0);

	for(int i = 0; i < ringList.size(); i++){
		ring theRing = ringList.get(i);
		theRing.update();
		
		// オブジェクトごとに回転
		// pushMatrix();
			rotateZ(i * frameCount / 10);
			switch (pattern) {
				case 0:
					rotateX(i * frameCount / 30);
					theRing.draw();	// 通常パターン
					break;
				case 1:
					rotateX(i * frameCount / 30);
					rotateY(i * frameCount / 50);
					theRing.draw_noise();	// ノイズパターン
					break;
				case 2:
					// theRing.drawRingFlake();	// 雪・電気のイメージ
					break;
				default:
			}

		theRing.drawSphere();

	}

	// // 波形の描画
	float[] w = new float[width];
	float[] w2 = new float[width];
	float[] w3 = new float[width];
	for(int i = 0; i < width; i++){
		w[i] = -noiz * random(10) * 0.1 * noise(_noiseSeed) * sin(radians(i+random(30)));
		_noiseSeed += 0.01;
		w2[i] = -pointalpha * random(10) * 0.2 * noise(_noiseSeed) * cos(radians(i+random(30)));
		_noiseSeed += 0.0001;
		w3[i] = -noiz * random(10) * 0.3 * noise(_noiseSeed) * sin(radians(i)) * cos(radians(i));
		_noiseSeed += 0.001;
	}

	for(int i = (int)random(30); i < width/2; i += lfosin){
		if(i > 0){
			strokeWeight(0.5);
			stroke(synth, noiz, random(255), noiz*70);
			line(i - width/4 - 1, w[i-1], i - width/4, w[i]);
			strokeWeight(2);
			stroke(random(synth), noiz * 0.8, random(255), noiz*70);
			line(i - width/4 -1, w2[i-1], i - width/4, w2[i]);
			strokeWeight(3);
			stroke(col, noiz * 0.6, random(255), noiz*70);
			line(i - width/4 -1, w3[i-1], i - width/4, w3[i]);
		}
	}

}

// 年輪クラス
class ring{
  PVector center;  // 中心座標
  float radius;    // 半径
  float weight;	   // 線の太さ
  boolean dead;	   // 生死判別
  float transparency; // 色
  float dice;			 // パターン

  // コンストラクタ
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

  // 座標の設定
  void setPos(PVector p1){
    center = p1;
  }

  void setRadius(float r){
  	radius = r;
  }

  float getRadius(){
  	return radius;
  }

  void setWeight(float w){
  	weight = w;
  }

  float getWeight(){
  	return weight;
  }

  boolean isDead(){
  	return dead;
  }

  void update(){

  	// 半径を加算。しきい値を超えたら死亡扱いとする
  	// radius += 0.3;
  	if(radius > 300){
  		dead = true;
  	}

  	// 透明度を設定。しきい値を超えたら死亡扱いとする。
  	if(transparency >= 20){
  		transparency = pointalpha;
  	}else {
  		transparency = 0;
			dead = true;
  	}
  }

  void reset(int index){
  	radius = random(10 * (index+1));
  	transparency = 255;
		dead = false;
		dice = (int)random(6) + 1;
  }

  // 描画
  void draw(){

  	// 死んでいるオブジェクトは描画しない
  	if(isDead()){
  		return;
  	}

  	PVector pPrev = new PVector(radius, 0);

    float col1 = random(col);
  
    for(float ang = 0; ang < 360; ang += 4){

      // 半径をノイズで加工
      float effected_radius = radius * 4 + 20 * noise(_noiseSeed);
      _noiseSeed += 0.01;
      if(_noiseSeed > 500){
      	_noiseSeed = random(10);
      }

      float rad = radians(ang);
      
      // x: radius * cosT
      float x = effected_radius * cos(rad);
      // y: radius * sinT
      float y = effected_radius * sin(rad);

      PVector pCurrent = new PVector(x, y);
      
      // 線の色、太さの設定
      stroke(0, 0, 255, transparency);
      strokeWeight(weight);

      // 初期値の設定
      if(ang == 0){
        pPrev = pCurrent;
      }

      line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
      pPrev = pCurrent;
    }
  }

  // 描画
	void draw_noise(){

		// 死んでいるオブジェクトは描画しない
		if(isDead()){
			return;
		}

		PVector pPrev = new PVector(radius, 0);

		for(float ang = 0; ang < 360; ang += random(10)){

		  // 半径をノイズで加工
		  float effected_radius = radius * 4 + (beat * noise(_noiseSeed)) * 0.2;
		  _noiseSeed += 0.01;
		  if(_noiseSeed > 500){
		  	_noiseSeed = random(10);
		  }

		  float rad = radians(ang);
		  
		  // x: radius * cosT
		  float x = effected_radius * cos(rad);
		  // y: radius * sinT
		  float y = effected_radius * sin(rad);

		  PVector pCurrent = new PVector(x, y);
		  
		  // 線の色、太さの設定
		  stroke(200, 0, 255, transparency);
		  strokeWeight(weight);

		  // 初期値の設定
		  if(ang == 0){
		    pPrev = pCurrent;
		  }

		  // パターン選択
		  if(dice < 3 && radius > 20){	// 半径の大きなものを低確率で円の集合とする
		  	// 円の集合
		  	strokeWeight(0.4);
			  float w = random(10);
			  ellipse(pCurrent.x, pCurrent.y, w, w);
		  }else{
		  	// 点と線
		  	strokeWeight(20 * radius/300);		// 同じサイズの点
		  	point(pCurrent.x, pCurrent.y);
		  	strokeWeight(4 * radius/300);
		  	line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
		  }

		  pPrev = pCurrent;
		}
	}

  // 描画
	void drawRingFlake(){

		// 死んでいるオブジェクトは描画しない
		if(isDead()){
			return;
		}

	  // 半径をノイズで加工
	  PVector pPrev = new PVector(radius, 0);
	  
	  float offset = 0.05;
  
    for(float ang = 0; ang < 360; ang += 0.4){

      // 半径をノイズで加工
      float effected_radius = radius * noise(_noiseSeed) * beat / 80;	// 雪・電気のイメージ

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
      _noiseSeed += 0.01;
      if(_noiseSeed > 300){
        _noiseSeed = random(10);
      }
    }
	}

	// 球の描画
	// オブジェクトの描画
	void drawSphere(){

	  stroke(random(30)+100);
	  strokeWeight(0.5);

	  float radius_effected = radius * synth;

	  pushMatrix();
	    rotateX(frameCount * 0.03 * random(10));
	    rotateY(frameCount * 0.04 * random(10));
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

	  // // オブジェクトのサイズを縮小
	  // float rate = 0.95;
	  // oscval[n] = radius / rad * rate;
	}
}


// OSC受信イベント
void oscEvent(OscMessage theOscMessage) {

  // prefixの取得
  String prefix = theOscMessage.addrPattern();

  // prefixが"/nseq/"の場合のみ実行
  if(prefix.equals("/temp/")){
    // トラックNoを取得
    int index = theOscMessage.get(0).intValue();
    // トリガーを取得
    float fval = (float)theOscMessage.get(1).intValue();
  }else if(prefix.equals("/noi")){ // prefixが"/noi"の場合のみ実行
    float fval = (float)theOscMessage.get(0).intValue();
    noi = map(fval, 0, 127, 10, 30);
  }else if(prefix.equals("/weight")){ // prefixが"/weight"の場合のみ実行
    float fval = (float)theOscMessage.get(0).intValue();
    weight = map(fval, 0, 127, 1, 8);
  }else if(prefix.equals("/col")){ // prefixが"/col"の場合のみ実行
    float fval = (float)theOscMessage.get(0).intValue();
    col = map(fval, 0, 127, 140, 180);
  }else if(prefix.equals("/beat")){ // prefixが"/beat"の場合のみ実行
    float fval = (float)theOscMessage.get(0).intValue();
    beat = map(fval, 0, 127, 2, 500);
  }else if(prefix.equals("/piano")){ // prefixが"/piano"の場合のみ実行
    float fval = (float)theOscMessage.get(0).intValue();
    weight = map(fval, 0, 127, 0.3, 30);
    noize_offset = 1 - map(fval, 0, 127, 0.5, 1.3);
    pointalpha = map(fval, 0, 127, 0, 250);

    // 年輪の設定
    if(fval > 30){
    	// 全オブジェクトの生存確認。死亡してるオブジェクトを再生させる。
    	for(int i = 0; i < ringList.size(); i++){
	    	ring theRing = ringList.get(i);
	    	if(theRing.isDead()){
	    		theRing.reset(i);
	    	} 
	    }
    }

  }else if(prefix.equals("/synth")){ // prefixが"/synth"の場合のみ実行
    float fval = (float)theOscMessage.get(0).intValue();
    synth = map(fval, 0, 127, 0, 3);
  }else if(prefix.equals("/noise")){ // prefixが"/noise"の場合のみ実行
    float fval = (float)theOscMessage.get(0).intValue();
    noiz = map(fval, 0, 127, 1, 250);
    col2 = map(fval, 0, 127, 0, 360);
  }else if(prefix.equals("/sin")){ // prefixが"/sin"の場合のみ実行
    float fval = (float)theOscMessage.get(0).intValue();
    lfosin = map(fval, 0, 127, 10, 30);
  }else if(prefix.equals("/cos")){ // prefixが"/cos"の場合のみ実行
    float fval = (float)theOscMessage.get(0).intValue();
    lfocos = map(fval, 0, 127, -1, 1);
  }else if(prefix.equals("/pattern")){ // prefixが"/pattern"の場合のみ実行
    pattern = theOscMessage.get(0).intValue();
  }
}
