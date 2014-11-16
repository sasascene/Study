import java.util.ArrayList; 
float _noiseSeed;	// ノイズの種
ArrayList<ring> ringList;  // リングリスト

void setup(){
	// size(1280, 800, OPENGL);  // フルスクリーン用（プロジェクターの解像度にあわせる）
	size(600, 400, OPENGL);
	
	noFill();
	smooth();
	noCursor();

	// ノイズの種をランダムに設定
	_noiseSeed = random(10);

	// カラーモードをHSBに指定
 	colorMode(HSB, 360, 255, 255);

 	// リストの初期化
	ringList = new ArrayList<ring>();

 	// 座標軸の移動
 	PVector center = new PVector(width/2, height/2);
 	translate(center.x, center.y);

 	for(int i = 20; i < 160; i += random(15, 30)){
 		ring theRing = new ring();
 		theRing.center = new PVector(width/2, height/2);
 		theRing.radius = i;
 		theRing.calcRadius();
 		ringList.add(theRing);
 	}
}

void draw(){
	// 背景のクリア
 	background(0, 0, 0);

 	// 座標軸の移動
 	PVector center = new PVector(width/2, height/2);
 	translate(center.x, center.y);

 	for(int i = 0; i < ringList.size(); i++){
 		ring theRing = ringList.get(i);
 		pushMatrix();
 			rotateZ(theRing.rotation);
 			theRing.drawRing2();
 			// theRing.update();
 		popMatrix();
 	}

 	// for(int i = 40; i < 160; i += 30){
 	// 	drawRing(i);
 	// }
}

// 描画
void drawRing(float radius){

	// 線の色、太さの設定
	stroke(random(360), random(50), random(20));
	strokeWeight(1);

	PVector pPrev = new PVector(radius, 0);
	PVector p1st = new PVector(radius, 0);

	for(float ang = 0; ang < 360; ang += 4){

	  float rad = radians(ang);

	  // 半径をノイズで加工
	  float effected_radius = radius + (radius / 4) * noise(_noiseSeed);
	  // _noiseSeed += 0.001;
	  _noiseSeed += 0.2;
	  
	  // x: radius * cosT
	  float x = effected_radius * cos(rad);
	  // y: radius * sinT
	  float y = effected_radius * sin(rad);

	  PVector pCurrent = new PVector(x, y);

	  // 初期値の設定
	  if(ang == 0){
	    pPrev = pCurrent;
	    p1st = pCurrent;
	  }

	  line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
	  pPrev = pCurrent;
	}

	line(pPrev.x, pPrev.y, p1st.x, p1st.y);
}

// 年輪クラス
class ring{
	PVector center;  // 中心座標
	float radius;    // 半径
	float[] noiseList;
	float[] radiusList;
	float[] angleList;;
	float _seed;
	int step;
	float rotation;

	// コンストラクタ
	ring(){
		step = 4;
		noiseList = new float[360/step];
		radiusList = new float[360/step];
		angleList = new float[360/step];
		_seed = random(10);
		rotation = random(-10, 10);
	}

	void update(){
		rotation += rotation/1000;
	}

	void calcNoise(){
		int index = 0;

		for(float ang = 0; ang < 360; ang += step){

		  float rad = radians(ang);

		  // 半径をノイズで加工
		  float noiseValue = (radius / 4) * noise(_seed);
		  // float effected_radius = radius;
		  noiseList[index] = noiseValue;
		  _seed += random(0.1);

		  angleList[index] = rad;
		  index++;
		}
	}

	void calcRadius(){

		int index = 0;

		for(float ang = 0; ang < 360; ang += step){

		  float rad = radians(ang);

		  // 半径をノイズで加工
		  float effected_radius = radius + (radius / 4) * noise(_seed);
		  // float effected_radius = radius;
		  _seed += 0.1;

		  radiusList[index] = effected_radius;
		  angleList[index] = rad;
		  index++;
		}
	}

	void drawRing(){

		// 線の色、太さの設定
		// stroke(random(360), random(150, 200), random(80, 120));
		strokeWeight(3);

		PVector pPrev = new PVector(radius, 0);
		PVector p1st = new PVector(radius, 0);

		for(int i = 0; i < radiusList.length; i++){

		  float radius = radiusList[i];
		  float rad = angleList[i];
		  
		  // x: radius * cosT
		  float x = radius * cos(rad);
		  // y: radius * sinT
		  float y = radius * sin(rad);

		  PVector pCurrent = new PVector(x, y);

		  // 初期値の設定
		  if(i == 0){
		    pPrev = pCurrent;
		    p1st = pCurrent;
		  }

		  line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
		  pPrev = pCurrent;
		}

		line(pPrev.x, pPrev.y, p1st.x, p1st.y);
	}

	void drawRing2(){

		// 線の色、太さの設定
		// stroke(random(360), random(150, 200), random(80, 120));
		stroke(0, 0, 255);
		strokeWeight(3);

		PVector pPrev = new PVector(radius, 0);
		PVector p1st = new PVector(radius, 0);

		for(int i = 0; i < radiusList.length; i++){

		  float e_radius = radius + noiseList[i];
		  float rad = angleList[i];
		  
		  // x: radius * cosT
		  float x = e_radius * cos(rad);
		  // y: radius * sinT
		  float y = e_radius * sin(rad);

		  PVector pCurrent = new PVector(x, y);

		  // 初期値の設定
		  if(i == 0){
		    pPrev = pCurrent;
		    p1st = pCurrent;
		  }

		  line(pCurrent.x, pCurrent.y, pPrev.x, pPrev.y);
		  pPrev = pCurrent;
		}

		line(pPrev.x, pPrev.y, p1st.x, p1st.y);
	}
}

void mousePressed(){
	for(int i = 0; i < ringList.size(); i++){
 		ring theRing = ringList.get(i);
 		// theRing.calcRadius();
 		theRing.calcNoise();
 	}
}