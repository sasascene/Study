#include <string.h>

// 初期設定
void setup() {
  pinMode(12, OUTPUT);  // 12番ピンを出力に指定
  pinMode(13, OUTPUT);  // 13番ピンを出力に指定
  Serial.begin(9600);  // 通信速度を指定
}

// 処理
void loop(){
  
  char str[20]; // 数字（文字列）の受信用配列
 
  // シリアルからのデータ受信
  receiveString(str);
  
  // pinAのコントロール
  if(!strcmp(str, "pinA-HIGH")){
    digitalWrite(13, HIGH);
  }else if(!strcmp(str, "pinA-LOW")){
    digitalWrite(13, LOW);
  }
  
   // pinBのコントロール
  if(!strcmp(str, "pinB-HIGH")){
    digitalWrite(12, HIGH);
  }else if(!strcmp(str, "pinB-LOW")){
    digitalWrite(12, LOW);
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
      
      // 文字列の終わりは\0で判断
      if (c == '\0'){
        // 文字列の最後尾を取得した場合はループを抜ける
        break;
      }
      
      index++;
    }
  }
  
  // 文字列の最後尾を示す記号を追加
  buf[index] = '\0';
}

