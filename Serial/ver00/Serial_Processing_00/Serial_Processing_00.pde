// シリアル通信

import processing.serial.*;  // シリアル通信用のライブラリをインポート
Serial myPort;  // シリアル通信用オブジェクト

// 初期設定
void setup(){
  // シリアル通信用オブジェクトの生成
  myPort = new Serial(this, Serial.list()[2],9600);
}

// 描画
void draw(){
  //描画内容は特になし
}

// マウスプレス時イベント
void mousePressed(){
  // シリアルポートに1を出力
  myPort.write(1);
}

// マウスリリース時イベント
void mouseReleased(){
  // シリアルポートに0を出力
  myPort.write(0);
}
