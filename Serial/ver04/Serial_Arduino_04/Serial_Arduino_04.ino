#include <string.h>

// 初期設定
void setup() {
  pinMode(10, OUTPUT);  // 11番ピンを出力に指定
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
  char c[3];
  char cNum[20];
  
  // 先頭2文字を取得し、PinNoに変換
  while(index < 2){
    c[index] = buf[index];
    index++;
  }
  c[index] = '\0';
  *pinNo = atoi(c);  // 取得した文字列を数値に変換してPinNoにセット
  
  // PinNo以下の値を返す
  while(1){
    char c1 = buf[index];
    cNum[indexNum] = buf[index];
    // 文字列の終わりは\0で判断
    if (c1 == '\0'){
      // 文字列の最後尾を取得した場合はループを抜ける
      break;
    }
    index++;
    indexNum++;
  }
  cNum[indexNum] = '\0';
  
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

