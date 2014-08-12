// シリアル通信  ====================================================
import processing.serial.*;  // シリアル通信用のライブラリをインポート
Serial myPort;  // シリアル通信用オブジェクト
// ================================================================

// OSC ============================================================
import oscP5.*;	  // OSC通信用のライブラリをインポート
OscP5 oscP5;  // OSC通信用オブジェクト
// ================================================================

// 初期設定
void setup(){
  // シリアル通信用オブジェクトの生成
  // ポートNoと通信速度を指定する
  myPort = new Serial(this, Serial.list()[2],9600);
  
  // OSC通信用オブジェクトの生成
  // procressingの受信ポート(pdの送信ポート)を指定する
  oscP5 = new OscP5(this, 8000);
}

// 描画
void draw(){
  //描画内容は特になし
}

// シリアル通信
void sendSerial(int pinNo, int val){
	// ピン番号と値を指定してポートに出力
	// ピン番号-値\0 の形式で出力
  myPort.write(str(pinNo) + "-" + str(val) + "\0");
}

// OSC受信イベント
void oscEvent(OscMessage theOscMessage) {

  // prefixの取得
  String prefix = theOscMessage.addrPattern();
  
  // prefixが"/arduino"の場合のみ実行
  if(prefix.equals("/arduino")){

  	int pinNo = theOscMessage.get(0).intValue();	// ピン番号
    int val = theOscMessage.get(1).intValue();		// 値

    // print(pinNo);
    // print(" ");
    // println(val);

    // シリアル通信
    sendSerial(pinNo, val);
  }
}
