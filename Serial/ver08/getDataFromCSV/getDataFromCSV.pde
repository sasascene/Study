// ライブラリのインポート
import processing.opengl.*;
import java.util.*;
import java.text.SimpleDateFormat;

// =====================================================
// OSC
import oscP5.*;
import netP5.*;
OscP5 oscP5;
NetAddress myRemoteLocation;
// =====================================================

// =====================================================
// グローバル変数
String strTimeStamp = "";
ArrayList<ArrayList<dotObj>> dotLists;

// csvファイル読み込み用
//String CSV_NAME = "1328299896.csv"; //csvファイル名
//String CSV_NAME = "1328299896_6ch.csv"; //csvファイル名
String CSV_NAME = "urushi.csv"; //csvファイル名
String[] csv; //csvファイルを読み込む配列
int csvInd = 0; //csvファイル読み込みのインデックス
int channelCnt = 6; //チャネル数
// =====================================================

// 初期設定
void setup(){ 

  size(420, 200, OPENGL);
  background(60);
  frameRate(0.25);
  smooth();

  // procressingの受信ポート(pdの送信ポート)
  oscP5 = new OscP5(this, 7704);
  
  // procressingの送信ポート(pdの受信ポート)
  myRemoteLocation = new NetAddress("127.0.0.1", 7703);

  dotLists = new ArrayList<ArrayList<dotObj>>();

  // csvファイルの読み込み
  csv = loadStrings(CSV_NAME);
  // 読み込み失敗
  if (csv == null) {
    exit(); // スケッチを終了する
  }
}

// 描画
void draw(){
  
  // データ取得
  getAllData();

  // 描画
  drawAllData();

  // パラメータの描画
  drawParam();
}

// パラメータの描画
void drawParam(){

  stroke(30, 80);

  // 軸
  float zero = map(0, -0.5, 0.5, height/4, height);
  line(0, zero, width, zero);

  // パラメータ
  text("CSV: " + CSV_NAME, 10, 20);
  //text("KEY: " + MY_KEY, 10, 35);
  //text("UTC: " + strTimeStamp, 10, height-15);
}

// データ取得
void getAllData(){

  // csvファイルからのデータ取得
  String lines[] = getDataFromCsv();

  // 取得したデータをリストに格納
  dotLists = new ArrayList<ArrayList<dotObj>>();

  // チャンネルの取得
  int max_channel = 0;
  int min_channel = 9999;
  for (int i = 0; i < lines.length; i++) {
    String data[] = split(lines[i], ',');
    int theChannel = int(data[0]);
    if(min_channel > theChannel){
      min_channel = theChannel;
    }
    if(max_channel < theChannel){
      max_channel = theChannel;
    }
  }

  // 全チャンネルのデータを取得し、リストに格納
  for(int channel = min_channel; channel <= max_channel; channel++){
    ArrayList<dotObj> dotList = getDataList(lines, channel);
    dotLists.add(dotList);
  }
}

void drawAllData(){
  // リストに要素がある場合のみ画面の初期化および描画の設定を行う
  if(dotLists.size() > 0){
    background(60);
  }

  // 全チャンネルのデータを描画、OSCで送信
  for(int i = 0; i < dotLists.size(); i++){
    // 描画
    drawData(dotLists.get(i));

    // 送信
    String prefix = "/prefix" + i;
    OscMessage myMessage = new OscMessage(prefix);
    sendOSC(dotLists.get(i), myMessage, prefix);
  }
}

// csvファイルからのデータ取得
String[] getDataFromCsv(){

  // 戻り値用配列
  String[] retlines = new String[0];
  String[] dataLines = new String[0];

  // 文字列格納用の動的配列
  ArrayList<String> strList = new ArrayList<String>();

  // csvファイルをチャネル数分読み込み
  dataLines = new String[channelCnt];
  for (int i = 0; i < channelCnt; ++i) {
    dataLines[i] = csv[csvInd];
    csvInd++;
  }

  // csvファイルの要素数に達したらインデックスをクリア
  if (csvInd >= csv.length) {
    csvInd = 0;
  }

  // csvファイルからのデータを動的配列に追加
  for(int i = 0; i < dataLines.length; i++){
    strList.add(dataLines[i]);
  }

  // 戻り値用配列へデータをコピー
  int num_samples = strList.size();
  retlines = new String[num_samples];
  for(int i = 0; i < num_samples; i++){
    retlines[i] = strList.get(i);
  }

  return retlines;
}

// 指定したチャンネルのデータをリストに格納して取得
ArrayList<dotObj> getDataList(String[] lines, int targetChannel){

  ArrayList<dotObj> retList = new ArrayList<dotObj>();

  // 全データを参照して必要な情報を取り出す
  int index = 0;
  for (int i = 0; i < lines.length; i++) {
    // ','で区切り配列に格納
    String data[] = split(lines[i], ',');
    // channel, date, valuse の順でデータが格納されている
    int channel = int(data[0]); // チャンネル
    String timeStamp = data[1]; // 日付
    float val = float(data[2]); // 値

    if(channel == targetChannel){
      // println("channel: " + channel);
      // println("date   : " + timeStamp);
      // println("val    : " + val);

      // 取得したデータをオブジェクトにセットし、配列に格納
      float m = map(val, -0.5, 0.5, height/4, height);
      dotObj theObj = new dotObj(index * width/70, m, 0);
      theObj.channel = channel;
      theObj.timeStamp = timeStamp;
      theObj.val = val;
      retList.add(theObj);

      index++;
    }
  }
  
  return retList;
}

// 取得したデータの可視化
void drawData(ArrayList<dotObj> channel){

  // 現在参照する点と直前に参照した点を格納するオブジェクトを生成
  PVector currentPoint = new PVector(0, height/2, 0);
  PVector lastPoint = new PVector(0, height/2, 0);

  // 現在の点と直前の点を線で結ぶ
  for(int i = 0; i < channel.size(); i++){
    // 現在の点を取得
    currentPoint = channel.get(i).p;
    // 現在の点と直前の点を線で結ぶ
    strokeWeight(0.8);
    stroke(200, 80);
    // stroke(random(30)+200, 20);
    line(lastPoint.x, lastPoint.y, lastPoint.z, currentPoint.x, currentPoint.y, currentPoint.z);
    text(channel.get(i).val, currentPoint.x + 20, currentPoint.y, currentPoint.z);
    // 直前の点を現在の点で更新
    lastPoint = currentPoint;

    // タイムスタンプ
    if(i == 0){
      strTimeStamp = channel.get(0).timeStamp;
      text(channel.get(0).channel, channel.get(0).p.x + 10, channel.get(0).p.y, channel.get(0).p.z);
    }
  }
}

// OSC送信
void sendOSC(ArrayList<dotObj> channel, OscMessage message, String prefix){

  if(channel.size() > 0){
    // 通信オブジェクトの初期化
    message.clear();
    // prefixの指定
    message.setAddrPattern(prefix);
    // 値の指定(下記のように続けて追加することでPureDataではlistとして扱うことができる)
    // message.add(mouseX);
    // message.add(mouseY);

    // チャンネル単位の平均値を計算
    float sum = 0;
    float average = 0;
    for(int i = 0; i < channel.size(); i++){
      sum += channel.get(i).val;
    }
    average = sum / channel.size();
    //println(average);

    // チャンネルごとにデータをリスト化して渡す
    for(int i = 0; i < channel.size(); i++){
      float val = map(channel.get(i).val, -0.48, -0.31, 0, 127);
      // float zero_shift_val = channel.get(i).val - average;
      // float val = map(zero_shift_val, -0.02, 0.02, 0, 127);
      message.add(val);
    }

    // OSCの送信
    oscP5.send(message, myRemoteLocation);
  }
}

// 点クラス
class dotObj{
  PVector p;  // 座標
  float val;  // 値
  int channel;  // チャンネル
  String timeStamp;  // 日付

  // コンストラクタ
  dotObj(){}

  dotObj(float x, float y, float z){
    this.p = new PVector(x, y, z);
  }
}