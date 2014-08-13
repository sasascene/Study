// 初期設定
void setup(){
 size(400, 400); 
 colorMode(HSB, 360, 100, 100);
}

// 描画
void draw(){

  // drawBackground(width/2, height/2, 196);
  drawBackground(196);

  text(mouseX, 10, height - 20);
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
