// シリアル通信  ====================================================
import processing.serial.*;  // シリアル通信用のライブラリをインポート
Serial myPort0;  // シリアル通信用オブジェクト
Serial myPort1;  // シリアル通信用オブジェクト
Serial myPort2;  // シリアル通信用オブジェクト
// ================================================================

// OSC ============================================================
import oscP5.*;	  // OSC通信用のライブラリをインポート
OscP5 oscP5;  // OSC通信用オブジェクト
// ================================================================

String[] devices;   // デバイス格納用配列
boolean[] portStatus;  // ポート使用状況格納用配列

// 初期設定
void setup(){

  size(340, 200);

  // シリアル通信用のデバイスを取得
  devices = Serial.list();

  // ポート使用状況の初期化
  portStatus = new boolean[devices.length];
  for(int i = 0; i < portStatus.length; i++){
    portStatus[i] = false;
  }

  // シリアル通信の通信速度
  int speed = 115200;

  // ポートにデバイスを順番に割り当てる
  // ポート0
  for(int i = 0; i < devices.length; i++){

    // USBでない場合は次のデバイスを参照
    String[] m = match(devices[i], "usb");
    if(m == null){
      continue;
    }

    // デバイスを設定
    // 例外発生時（使用中など）は次のデバイスを参照
    try {
      myPort0 = new Serial(this, devices[i], speed);
    }catch (Exception e){
      continue;
    }

    // デバイスを設定したらステータスを更新してループを抜ける
    portStatus[i] = true;
    i = devices.length;
  }

  // ポート1
  for(int i = 0; i < devices.length; i++){

    // USBでない場合は次のデバイスを参照
    String[] m = match(devices[i], "usb");
    if(m == null){
      continue;
    }

    // デバイスを設定
    // 例外発生時（使用中など）は次のデバイスを参照
    try {
      myPort1 = new Serial(this, devices[i], speed);
    }catch (Exception e){
      continue;
    }

    // デバイスを設定したらステータスを更新してループを抜ける
    portStatus[i] = true;
    i = devices.length;
  }

  // ポート2
  for(int i = 0; i < devices.length; i++){

    // USBでない場合は次のデバイスを参照
    String[] m = match(devices[i], "usb");
    if(m == null){
      continue;
    }

    // デバイスを設定
    // 例外発生時（使用中など）は次のデバイスを参照
    try {
      myPort2 = new Serial(this, devices[i], speed);
    }catch (Exception e){
      continue;
    }

    // デバイスを設定したらステータスを更新してループを抜ける
    portStatus[i] = true;
    i = devices.length;
  }

  // デバイスリストを表示（使用中のデバイスに*を表示）
  text("devices (*:busy)", 10, 20);
  for(int i = 0; i < devices.length; i++){
    text(i + ": " + devices[i], 20, 45 + (i * 15));
    if(portStatus[i]){
      text("*", 10, 45 + (i * 15));
    }
  }
  
  // OSC通信用オブジェクトの生成
  // procressingの受信ポート(pdの送信ポート)を指定する
  oscP5 = new OscP5(this, 8000);
}

// 描画
void draw(){
  //描画内容は特になし
}

// シリアル通信
void sendSerial(int boardNo, int pinNo, float val){
	// ピン番号と値を指定してポートに出力
	// ピン番号-値\0 の形式で出力

  if(boardNo == 0){
    myPort0.write(str(pinNo) + "-" + str(val) + "\0");
  }else if(boardNo == 1){
    myPort1.write(str(pinNo) + "-" + str(val) + "\0");
  }else{
    myPort2.write(str(pinNo) + "-" + str(val) + "\0");
  }
}

// OSC受信イベント
void oscEvent(OscMessage theOscMessage) {

  // prefixの取得
  String prefix = theOscMessage.addrPattern();
  
  // prefixが"/arduino"の場合のみ実行
  if(prefix.equals("/arduino")){

    try{
      int boardNo = theOscMessage.get(0).intValue();  // ボード番号
      int pinNo = theOscMessage.get(1).intValue();	// ピン番号
      int val = theOscMessage.get(2).intValue();		// 値

      // シリアル通信
      sendSerial(boardNo, pinNo, val);

    }catch(Exception e){
      text("error! reboot app!", 10, height-10);
    }
  }
}
