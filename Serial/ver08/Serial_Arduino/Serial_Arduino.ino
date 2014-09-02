#include <string.h>
#define PinNum 14

// 各ピンの出力経過時間格納用配列
unsigned long time[PinNum];
// 各ピンのステータス格納用配列
boolean pin[PinNum];

// 初期設定
void setup() {
  pinMode(6, OUTPUT);   //  6番ピンを出力に指定
  pinMode(7, OUTPUT);   //  7番ピンを出力に指定
  pinMode(8, OUTPUT);   //  8番ピンを出力に指定
  pinMode(11, OUTPUT);  // 11番ピンを出力に指定
  pinMode(12, OUTPUT);  // 12番ピンを出力に指定
  pinMode(13, OUTPUT);  // 13番ピンを出力に指定
  //Serial.begin(9600);  // 通信速度を指定
  Serial.begin(115200);  // 通信速度を指定
  
  // 配列の初期化
  for(int i = 0; i < PinNum; i++){
    time[i] = millis();
    pin[i] = false;
  }
}

// 処理
void loop(){
  
  // 経過時間チェック
  checkPin(5000);
  
  // シリアル通信時のみ処理開始
  if(Serial.available()){
  
    int pinNo = 0;  // Pinナンバー
    int val = 0;    // 値
    
    char str[20]; // 数字（文字列）の受信用配列
   
    // シリアルからのデータ受信
    receiveString(str);
    
    // 値取得
    getValue(&pinNo, &val, str);
    
    // ピン判別
    if(isDigitalPin(pinNo)){
      // HIGH or LOW に値を変換
      castDigitalValue(&val);
      // Digital出力
      digitalWrite(pinNo, val);
    }else{
      // PWM出力
      analogWrite(pinNo, val);
    }
    
    // ステータス更新
    updateStatus(pinNo, val);
  }
}

// 経過時間チェック
void checkPin(long limit){
  // 全ピンのステータスを取得
  for(int i = 0; i < PinNum; i++){
    // ピンがノンアクティブである場合は処理をスキップ
    if(!pin[i]) continue;
    
    // 現在とアクティブになった時刻の差分（経過時間）を取得
    unsigned long dTime = millis() - time[i];
    // 経過時間が限界を超えた場合
    if(dTime > limit){
      // ピンをノンアクティブにし、ステータスを更新
      if(isDigitalPin(i)){
        digitalWrite(i, LOW);
      }else{
        analogWrite(i, 0);
      }
      // ステータスを更新
      pin[i] = false;
    }
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

// 値取得
void getValue(int *pinNo, int *val, char *buf){
  int index = 0;
  int indexNum = 0;
  char cPin[3];  // pinNo格納用配列
  char cNum[20];  // 値格納用配列
  
  // PinNoを取得
  while(1){
    char c = buf[index];
    cPin[index] = buf[index];
    index++;
    
    // 文字列の終わりは\0で判断
    if (c == '-'){
      cPin[index] = '\0';
      // 文字列の最後尾を取得した場合はループを抜ける
      break;
    }
  }
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

// ピン判別（デジタルorPWM）
boolean isDigitalPin(int pinNo){
  
  boolean ret = false;
  
  switch (pinNo) {
    case 0:
    case 1:
    case 2:
    case 4:
    case 7:
    case 8:
    case 12:
    case 13:
      ret = true;
      break;
  }
  
  return ret;
}

// 値をデジタル値に変換
void castDigitalValue(int *val){
  if(*val > 0){
    *val = HIGH;
  }else{
    *val = LOW; 
  }
}

// ステータス更新
void updateStatus(int pinNo, int val){
  
  // ステータス
  boolean st = false;
  
  // デジタルピンの場合
  if(isDigitalPin(pinNo)){
    if(val == HIGH){
      st = true;
      // 前回参照時とステータスに変化がある場合のみ処理実行
      if(pin[pinNo] != st){
        pin[pinNo] = st;        //  ステータス更新
        time[pinNo] = millis(); //  時刻更新
      }
    }else{
      // 前回参照時とステータスに変化がある場合のみ処理実行
      if(pin[pinNo] != st){
        pin[pinNo] = st;        //  ステータス更新
      }
    } 
  }else{ // PWMピンの場合
    if(val > 0){
      st = true;
      //  時刻更新
      time[pinNo] = millis();
    }
    //  ステータス更新
    pin[pinNo] = st;
  }
}

