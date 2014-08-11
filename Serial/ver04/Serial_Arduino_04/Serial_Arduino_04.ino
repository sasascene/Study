#include <string.h>

// 初期設定
void setup() {
  pinMode(10, OUTPUT);  // 10番ピンを出力に指定
  pinMode(11, OUTPUT);  // 11番ピンを出力に指定
  Serial.begin(9600);  // 通信速度を指定
}

// 処理
void loop(){
  
  int pinNo = 0;  // Pinナンバー
  int val = 0;    // 値
  
  char str[20]; // 数字（文字列）の受信用配列
 
  // シリアルからのデータ受信
  receiveString(str);
  
  // アナログ値取得
  getAnalogValue(&pinNo, &val, str);
  
  // PWM出力
  analogWrite(pinNo, val);
}

// アナログ値取得
void getAnalogValue(int *pinNo, int *val, char *buf){
  int index = 0;
  int indexNum = 0;
  char cPin[3];  // pinNo格納用配列
  char cNum[20];  // 値格納用配列
  
  // 先頭2文字を取得し、PinNoに変換
  while(index < 2){
    cPin[index] = buf[index];
    index++;
  }
  cPin[index] = '\0';
  *pinNo = atoi(cPin);  // 取得した文字列を数値に変換してPinNoにセット
  
  // PinNo以下の値を返す
  while(1){
    char c = buf[index];
    cNum[indexNum] = buf[index];
    index++;
    indexNum++;
    
    // 文字列の終わりは\0で判断
    if (c == '\0'){
      // 文字列の最後尾を取得した場合はループを抜ける
      break;
    }
  }
  *val = atoi(cNum);  // 取得した文字列を数値に変換して値にセット
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

