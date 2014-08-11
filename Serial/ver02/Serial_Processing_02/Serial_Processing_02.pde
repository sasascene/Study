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
  // シリアルポートにabcを出力
  String str1 = "abc";
  myPort.write(str1 + "\0");
}

// マウスリリース時イベント
void mouseReleased(){
  // シリアルポートにdefを出力
  String str1 = "def";
  myPort.write(str1 + "\0");
}
