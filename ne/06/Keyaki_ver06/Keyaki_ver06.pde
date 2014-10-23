import processing.opengl.*;
import oscP5.*;
import netP5.*;
import java.util.ArrayList; 
  
OscP5 oscP5;
NetAddress myRemoteLocation;

float _noiseSeed;     // ノイズの種

PImage offscr;	// オフスクリーン

// グローバル変数
float globalWeight = 0.3;
float col = 0;
float beat = 0;
float noiz = 0;
float lfosin = 0;
float lfo = 0;
float synth = 0;
float pointalpha = 0;
int pattern = 0;
int scene = 0;
float[] pixel_w;

PImage image1;

ArrayList<ring> ringList;  // リングリスト
ArrayList<wall> wallList;  // リングリスト


// 初期設定
void setup(){

  //size(800, 800, OPENGL);
  size(1280, 720, OPENGL);  // フルスクリーン用（プロジェクターの解像度にあわせる）

  noFill();
  smooth();
  noCursor();
  
  // ノイズの種をランダムに設定
  _noiseSeed = random(10);
  
  // procressingの受信ポート(pdの送信ポート)
  oscP5 = new OscP5(this,7702);
  
  // procressingの送信ポート(pdの受信ポート)
  //myRemoteLocation = new NetAddress("127.0.0.1",7702);

  // カラーモードをHSBに指定
  colorMode(HSB, 360, 255, 255);

  // オフスクリーンの初期設定
  offscr = createImage(width, height, RGB);

  pixel_w = new float[width];
  	for(int i = 0; i < width; i++){
		pixel_w[i] = 100 * noise(_noiseSeed);
		_noiseSeed += 0.01;
	}

  // 年輪オブジェクトの生成
  makeRing(6);

  makeWall(3);
}

// 描画
void draw(){

	// オフスクリーンバッファの更新
	loadPixels();
 	offscr.pixels = pixels;
 	offscr.updatePixels();

 	// 背景のクリア
 	background(0, 0, 0);

 	// ======================================
 	// 以下に通常の描画処理を記述

 	// 座標軸の移動
 	PVector center = new PVector(width/2, height/2);
 	translate(center.x, center.y);

	// 年輪の描画
	drawRing();

	// ======================================

	// オフスクリーンバッファの描画
	tint(255, 240);
	translate(-center.x, -center.y);
	image(offscr, -3, -3, width + 6, height + 6); // 少しだけ拡大してみる
}

// 年輪オブジェクトの生成
void makeRing(int num){

	// リストの初期化
	ringList = new ArrayList<ring>();

	// 中心座標を作成（画面中心から無作為にずらした座標を指定）
	PVector center = new PVector((random(20)-10) * width/2, (random(20)-10) * height/2);

	// 年輪オブジェクトの生成
	for(int i = 0; i < num; i++){
		ring theRing = new ring(center, 10 * i);
		theRing.weight = 0.5;
		ringList.add(theRing);
	}
}

// 年輪オブジェクトの生成
void makeWall(int num){

	// リストの初期化
	wallList = new ArrayList<wall>();

	// 中心座標を作成（画面中心から無作為にずらした座標を指定）
	PVector startPoint = new PVector((random(20)-10), (random(20)-10), random(20)-10);

	// 年輪オブジェクトの生成
	for(int i = 0; i < num; i++){
		wall theWall = new wall();
		theWall.startPoint = startPoint;

		for(int j = 0; j < width; j++){
			theWall.edge[j] = (height/4) * noise(_noiseSeed);
			_noiseSeed += 0.01;
		}

		wallList.add(theWall);
	}
}

// 年輪オブジェクトの描画
void drawRing(){

	pushMatrix();

		for(int i = 0; i < ringList.size(); i++){
			ring theRing = ringList.get(i);
			theRing.update();

			// 後半は雪を描画
			if(scene > 12){
				theRing.draw_snow();	// 雪パターン
			}

			// Z軸まわりの回転（共通）
			rotateZ(i * frameCount / 10);

			switch (pattern) {
				case 0:
					pushMatrix();
						theRing.draw();	// 通常パターン
						// theRing.draw_snow();	// 雪パターン
					popMatrix();
					break;
				case 1:
					// オブジェクトごとに回転
					pushMatrix();
						theRing.draw_noise(); // ノイズパターン
					popMatrix();
					break;
				case 2:
					// 軸を回転（球のイメージ）
					pushMatrix();
						rotateX(i * frameCount / 30);
						rotateY(i * frameCount / 50);
						theRing.draw_noise(); // ノイズパターン
					popMatrix();
					break;
				case 3:
					// // オブジェクトごとに回転
					pushMatrix();
						theRing.drawWideRing();	// 輪のイメージ
					popMatrix();
					break;
				default:
			}

			// はじめのうちは球とビートを描画
			if(scene < 5){
				theRing.drawSphere();
				theRing.draw_beat();
			}
		}

		drawWave();

	popMatrix();

	// ピアノが入る前
	// if(scene < 5){

	// 	for(int i = 0; i < ringList.size(); i++){
	// 		ring theRing = ringList.get(i);
	// 		theRing.update();

	// 		// theRing.draw_beat();
	// 		theRing.drawWideRing();
	// 	}

	// 	// 壁の描画
	// 	// drawWall();
	// }

}

// 壁の描画
void drawWall(){

	for(int n = 0; n < wallList.size(); n++){
		for(int m = 0; m < width; m++){
			wallList.get(n).edge[m] = wallList.get(n).edge[m] + 0.3;
		}
	}

	pushMatrix();
	translate(-width/2, -height/2, 0);

	for(int n = 0; n < wallList.size(); n++){
		wall theWall = wallList.get(n);
		translate(theWall.startPoint.x/10, theWall.startPoint.y/10, theWall.startPoint.z/10);
		strokeWeight(random(beat/100)+2);
		stroke(synth, 0, random(200), random(20)+10);
		for(int m = 0; m < width; m += random(synth * 10)){
			line(m, -height, m, theWall.edge[m]);
		}
	}
	popMatrix();
}

// 波形の描画
void drawWave(){
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
			strokeWeight(2);
			stroke(synth, noiz, random(255), noiz*70);
			line(i - width/4 - 1, w[i-1], i - width/4, w[i]);
			strokeWeight(4);
			stroke(random(synth), noiz * 0.8, random(255), noiz*70);
			line(i - width/4 -1, w2[i-1], i - width/4, w2[i]);
			strokeWeight(8);
			stroke(col, noiz * 0.6, random(255), noiz*70);
			line(i - width/4 -1, w3[i-1], i - width/4, w3[i]);
		}
	}
}

// スクリーンクラス
class wall{
	PVector startPoint;
	float[] edge;

	wall(){
		startPoint = new PVector(0, 0, 0);
		edge = new float[width];
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

	ring(PVector p1, float d1){
		center = new PVector(p1.x, p1.y, p1.z);
		radius = d1;
		transparency = 255;
		dead = false;
		dice = (int)random(6) + 1;
	}


	boolean isDead(){
		return dead;
	}

	void update(){

		// 半径を加算。しきい値を超えたら死亡扱いとする
		radius += 0.3;
		if(radius > 200){
			radius = random(50);
			dead = true;
		}

		// 透明度を設定。しきい値を超えたら死亡扱いとする。
		if(pattern == 0){
			if(transparency >= 20){
				transparency = synth * 120 * 0.4;
			}else {
				transparency = 0;
				dead = true;
			}
		}else{
			if(transparency >= 20){
				transparency = pointalpha * 0.9;
			}else {
				transparency = 0;
				dead = true;
			}
		}

		// if(scene < 5){
		// 	if(transparency >= 20){
		// 		transparency = beat * 0.4;
		// 	}else {
		// 		transparency = 0;
		// 		dead = true;
		// 	}
		// }

		weight = globalWeight;
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
		  	strokeWeight(weight);
			float w = random(20);
			ellipse(pCurrent.x, pCurrent.y, w, w);
		  }else{
		  	// 点と線
		  	strokeWeight(weight);
		  	point(pCurrent.x, pCurrent.y);
		  	line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
		  }

		  pPrev = pCurrent;
		}
	}

	// 描画
	void draw_beat(){

		PVector pPrev = new PVector(radius, 0);

		for(float ang = 0; ang < 360; ang += random(10)){

		  // 半径をノイズで加工
		  float effected_radius = beat * noise(_noiseSeed);
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
		  stroke(200, 0, beat / 2, beat / 2);

		  // 初期値の設定
		  if(ang == 0){
		    pPrev = pCurrent;
		  }

		  // パターン選択
		  if(dice < 3 && radius > 20){	// 半径の大きなものを低確率で円の集合とする
		  	// 円の集合
		  	strokeWeight(random(2));
			float w = random(20);
			ellipse(pCurrent.x, pCurrent.y, w, w);
		  }else{
		  	// 点と線
		  	strokeWeight(random(2)+0.8);
		  	line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
		  }

		  pPrev = pCurrent;
		}
	}

	// 描画
	void draw_snow(){

		// 死んでいるオブジェクトは描画しない
		if(isDead()){
			return;
		}

		PVector pPrev = new PVector(radius, 0);

		pushMatrix();

		rotateY(30);
		translate(200, 0, -width);

		for(float ang = 0; ang < 1800; ang += 1){

			translate(0, 0, 1);
			pushMatrix();

				rotateX(random(30)-15);

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
				stroke(0, 0, 255, 10 + synth * 40);
				strokeWeight(5);

				// 初期値の設定
				if(ang == 0){
					pPrev = pCurrent;
				}

				line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
				pPrev = pCurrent;
			popMatrix();
		}
		popMatrix();
	}


 	// 描画
	void drawWideRing(){

		// 死んでいるオブジェクトは描画しない
		if(isDead()){
		  return;
		}

		PVector pPrev = new PVector(radius, 0);

		strokeWeight((int)random(5) + 5);

		rotateZ(frameCount / 15);

		for(float ang = 0; ang < 360; ang += 4){

			rotateX(frameCount / 40);
			rotateY(frameCount / 15);

			// 半径をノイズで加工
			float effected_radius = radius * 4;

			float rad = radians(ang);

			// x: radius * cosT
			float x = effected_radius * cos(rad);
			// y: radius * sinT
			float y = effected_radius * sin(rad);

			PVector pCurrent = new PVector(x, y);

			// 線の色、太さの設定
			stroke(0, 0, 255, pointalpha * 0.4);

			// 初期値の設定
			if(ang == 0){
			pPrev = pCurrent;
			}

			line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
			pPrev = pCurrent;
		}
	}

	// 球体の描画
	void drawSphere(){

	  stroke(random(30)+100);
	  strokeWeight(0.5 + synth * 0.8);

	  float radius_effected = radius / 3 * synth;

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

	      stroke(col, noiz * 0.6, random(255), noiz * 70);

	      if(lastPos.x != 0){
	        line(thisPos.x, thisPos.y, thisPos.z, lastPos.x, lastPos.y, lastPos.z);
	        point(thisPos.x, thisPos.y, thisPos.z); 
	      }

	      lastPos = thisPos.get();
	    }
	  popMatrix();
	}
}


// OSC受信イベント
void oscEvent(OscMessage theOscMessage) {

	// 描画パターン
 	pattern = getValue(theOscMessage, "/Pattern", pattern);
 	scene = getValue(theOscMessage, "/scene", scene);

 	beat = getValue(theOscMessage, "/HeartBeat", 1, 500, beat);
 	globalWeight = getValue(theOscMessage, "/piano", 2, 8, globalWeight);

 	pointalpha = getValue(theOscMessage, "/piano", 0, 250, pointalpha);

 	noiz = getValue(theOscMessage, "/noise", 1, 250, noiz);
 	synth = getValue(theOscMessage, "/synth", 0, 3, synth);
 	lfo = getValue(theOscMessage, "/sin", -1, 1, lfo);
 	lfosin = getValue(theOscMessage, "/sin", 10, 30, lfosin);
 	// beat = getValue(theOscMessage, "/beat", 2, 500, beat);
 	col = getValue(theOscMessage, "/col", 2, 500, col);

}

// OSCメッセージからのデータ取得処理
float getValue(OscMessage theOscMessage, String thePrefix, float minValue, float maxValue, float ret){

	// prefixの取得
	String prefix = theOscMessage.addrPattern();

	// prefixが指定した値の場合のみ実行
	if(prefix.equals(thePrefix)){
    	// 値を取得
    	float fval = (float)theOscMessage.get(0).intValue();
    	ret = map(fval, 0, 127, minValue, maxValue);

    	if(prefix.equals("/piano")){
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
		}
	}
	return ret;
}

// OSCメッセージからのデータ取得処理
int getValue(OscMessage theOscMessage, String thePrefix, int ret){

	// prefixの取得
	String prefix = theOscMessage.addrPattern();

	// prefixが指定した値の場合のみ実行
	if(prefix.equals(thePrefix)){
    	// 値を取得
    	int ival = theOscMessage.get(0).intValue();
    	ret = ival;
	}

	return ret;
}
