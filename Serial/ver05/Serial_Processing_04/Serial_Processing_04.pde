// シリアル通信  ====================================================
import processing.serial.*;  // シリアル通信用のライブラリをインポート
Serial myPort;  // シリアル通信用オブジェクト
// ================================================================

// OSC ============================================================
import oscP5.*;
import netP5.*;
  
OscP5 oscP5;
NetAddress myRemoteLocation;

// 通信オブジェクト(引数にprefixを指定)
OscMessage myMessage = new OscMessage("/Pd");
// ================================================================

// 初期設定
void setup(){
  // シリアル通信用オブジェクトの生成
  myPort = new Serial(this, Serial.list()[2],9600);
  
  // procressingの受信ポート(pdの送信ポート)
  oscP5 = new OscP5(this,8000);
  
  // procressingの送信ポート(pdの受信ポート)
  myRemoteLocation = new NetAddress("127.0.0.1",8001);
}

// 描画
void draw(){
  //描画内容は特になし
}

// シリアル通信01
void sendSignal01(int val){

//  String str1 = "pinA";
  String str1 = "11";
  myPort.write(str1 + str(val) + "\0");
}

// シリアル通信02
void sendSignal02(int val){

//  String str1 = "pinB";
  String str1 = "10";
  myPort.write(str1 + str(val) + "\0");
}

// OSC受信イベント
void oscEvent(OscMessage theOscMessage) {

  // prefixの取得
  String prefix = theOscMessage.addrPattern();
  print(prefix + ": ");
  
  if(prefix.equals("/pd/trigger/0")){ // prefixが"/pd/trigger/0"の場合のみ実行

    int val = theOscMessage.get(0).intValue();
    println(val);

    sendSignal01(val);

  }else if(prefix.equals("/pd/trigger/1")){ // prefixが"/pd/trigger/1"の場合のみ実行
    int val = theOscMessage.get(0).intValue();
    println(val);

    sendSignal02(val);
  }
}
