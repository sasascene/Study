// 初期設定
void setup() {
  pinMode(13, OUTPUT);  // 13番ピンを出力に指定
  Serial.begin(9600);  // 通信速度を指定
}

// 処理
void loop(){
  // シリアル通信で受信した場合
  if(Serial.available() > 0){
    // 1 を受信した場合LEDを点灯させる
    if(Serial.read() == 1){
      digitalWrite(13, HIGH);
    }else{
      digitalWrite(13, LOW);
    }
  }
}

