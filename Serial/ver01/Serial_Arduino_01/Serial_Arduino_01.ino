// 初期設定
void setup() {
  pinMode(13, OUTPUT);  // 13番ピンを出力に指定
  Serial.begin(9600);  // 通信速度を指定
}

// 処理
void loop(){
  
  char str[20]; // 数字（文字列）の受信用配列
 
  // シリアルからのデータ受信
  receiveString(str);
  
  int iStr = atoi(str); // 文字列を数値に変換
  
  // 1000 を受信した場合LEDを点灯させる
  if(iStr == 1000){
    digitalWrite(13, HIGH);
  }else{
    digitalWrite(13, LOW);
  }
}


// 文字列受信
void receiveString(char *buf)
{
  int index = 0;
  char c;
  
  while (1) {
    // シリアル通信で受信した場合
    if (Serial.available()) {
      c = Serial.read();
      buf[index] = c;
      index++;
      
      // 文字列の終わりは\0で判断
      if (c == '\0'){
        // 文字列の最後尾を取得した場合はループを抜ける
        break;
      }
    }
  }
}
