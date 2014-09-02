import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.opengl.*; 
import java.util.*; 
import java.text.SimpleDateFormat; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class getDataFromCSV extends PApplet {

// \u30e9\u30a4\u30d6\u30e9\u30ea\u306e\u30a4\u30f3\u30dd\u30fc\u30c8




// =====================================================
// OSC


OscP5 oscP5;
NetAddress myRemoteLocation;
// =====================================================

// =====================================================
// \u30b0\u30ed\u30fc\u30d0\u30eb\u5909\u6570
String strTimeStamp = "";
ArrayList<ArrayList<dotObj>> dotLists;

// csv\u30d5\u30a1\u30a4\u30eb\u8aad\u307f\u8fbc\u307f\u7528
//String CSV_NAME = "1328299896.csv"; //csv\u30d5\u30a1\u30a4\u30eb\u540d
//String CSV_NAME = "1328299896_6ch.csv"; //csv\u30d5\u30a1\u30a4\u30eb\u540d
String CSV_NAME = "urushi.csv"; //csv\u30d5\u30a1\u30a4\u30eb\u540d
String[] csv; //csv\u30d5\u30a1\u30a4\u30eb\u3092\u8aad\u307f\u8fbc\u3080\u914d\u5217
int csvInd = 0; //csv\u30d5\u30a1\u30a4\u30eb\u8aad\u307f\u8fbc\u307f\u306e\u30a4\u30f3\u30c7\u30c3\u30af\u30b9
int channelCnt = 6; //\u30c1\u30e3\u30cd\u30eb\u6570
// =====================================================

// \u521d\u671f\u8a2d\u5b9a
public void setup(){ 

  size(420, 200, OPENGL);
  background(60);
  frameRate(0.25f);
  smooth();

  // procressing\u306e\u53d7\u4fe1\u30dd\u30fc\u30c8(pd\u306e\u9001\u4fe1\u30dd\u30fc\u30c8)
  oscP5 = new OscP5(this, 7704);
  
  // procressing\u306e\u9001\u4fe1\u30dd\u30fc\u30c8(pd\u306e\u53d7\u4fe1\u30dd\u30fc\u30c8)
  myRemoteLocation = new NetAddress("127.0.0.1", 7703);

  dotLists = new ArrayList<ArrayList<dotObj>>();

  // csv\u30d5\u30a1\u30a4\u30eb\u306e\u8aad\u307f\u8fbc\u307f
  csv = loadStrings(CSV_NAME);
  // \u8aad\u307f\u8fbc\u307f\u5931\u6557
  if (csv == null) {
    exit(); // \u30b9\u30b1\u30c3\u30c1\u3092\u7d42\u4e86\u3059\u308b
  }
}

// \u63cf\u753b
public void draw(){
  
  // \u30c7\u30fc\u30bf\u53d6\u5f97
  getAllData();

  // \u63cf\u753b
  drawAllData();

  // \u30d1\u30e9\u30e1\u30fc\u30bf\u306e\u63cf\u753b
  drawParam();
}

// \u30d1\u30e9\u30e1\u30fc\u30bf\u306e\u63cf\u753b
public void drawParam(){

  stroke(30, 80);

  // \u8ef8
  float zero = map(0, -0.5f, 0.5f, height/4, height);
  line(0, zero, width, zero);

  // \u30d1\u30e9\u30e1\u30fc\u30bf
  text("CSV: " + CSV_NAME, 10, 20);
  //text("KEY: " + MY_KEY, 10, 35);
  //text("UTC: " + strTimeStamp, 10, height-15);
}

// \u30c7\u30fc\u30bf\u53d6\u5f97
public void getAllData(){

  // csv\u30d5\u30a1\u30a4\u30eb\u304b\u3089\u306e\u30c7\u30fc\u30bf\u53d6\u5f97
  String lines[] = getDataFromCsv();

  // \u53d6\u5f97\u3057\u305f\u30c7\u30fc\u30bf\u3092\u30ea\u30b9\u30c8\u306b\u683c\u7d0d
  dotLists = new ArrayList<ArrayList<dotObj>>();

  // \u30c1\u30e3\u30f3\u30cd\u30eb\u306e\u53d6\u5f97
  int max_channel = 0;
  int min_channel = 9999;
  for (int i = 0; i < lines.length; i++) {
    String data[] = split(lines[i], ',');
    int theChannel = PApplet.parseInt(data[0]);
    if(min_channel > theChannel){
      min_channel = theChannel;
    }
    if(max_channel < theChannel){
      max_channel = theChannel;
    }
  }

  // \u5168\u30c1\u30e3\u30f3\u30cd\u30eb\u306e\u30c7\u30fc\u30bf\u3092\u53d6\u5f97\u3057\u3001\u30ea\u30b9\u30c8\u306b\u683c\u7d0d
  for(int channel = min_channel; channel <= max_channel; channel++){
    ArrayList<dotObj> dotList = getDataList(lines, channel);
    dotLists.add(dotList);
  }
}

public void drawAllData(){
  // \u30ea\u30b9\u30c8\u306b\u8981\u7d20\u304c\u3042\u308b\u5834\u5408\u306e\u307f\u753b\u9762\u306e\u521d\u671f\u5316\u304a\u3088\u3073\u63cf\u753b\u306e\u8a2d\u5b9a\u3092\u884c\u3046
  if(dotLists.size() > 0){
    background(60);
  }

  // \u5168\u30c1\u30e3\u30f3\u30cd\u30eb\u306e\u30c7\u30fc\u30bf\u3092\u63cf\u753b\u3001OSC\u3067\u9001\u4fe1
  for(int i = 0; i < dotLists.size(); i++){
    // \u63cf\u753b
    drawData(dotLists.get(i));

    // \u9001\u4fe1
    String prefix = "/prefix" + i;
    OscMessage myMessage = new OscMessage(prefix);
    sendOSC(dotLists.get(i), myMessage, prefix);
  }
}

// csv\u30d5\u30a1\u30a4\u30eb\u304b\u3089\u306e\u30c7\u30fc\u30bf\u53d6\u5f97
public String[] getDataFromCsv(){

  // \u623b\u308a\u5024\u7528\u914d\u5217
  String[] retlines = new String[0];
  String[] dataLines = new String[0];

  // \u6587\u5b57\u5217\u683c\u7d0d\u7528\u306e\u52d5\u7684\u914d\u5217
  ArrayList<String> strList = new ArrayList<String>();

  // csv\u30d5\u30a1\u30a4\u30eb\u3092\u30c1\u30e3\u30cd\u30eb\u6570\u5206\u8aad\u307f\u8fbc\u307f
  dataLines = new String[channelCnt];
  for (int i = 0; i < channelCnt; ++i) {
    dataLines[i] = csv[csvInd];
    csvInd++;
  }

  // csv\u30d5\u30a1\u30a4\u30eb\u306e\u8981\u7d20\u6570\u306b\u9054\u3057\u305f\u3089\u30a4\u30f3\u30c7\u30c3\u30af\u30b9\u3092\u30af\u30ea\u30a2
  if (csvInd >= csv.length) {
    csvInd = 0;
  }

  // csv\u30d5\u30a1\u30a4\u30eb\u304b\u3089\u306e\u30c7\u30fc\u30bf\u3092\u52d5\u7684\u914d\u5217\u306b\u8ffd\u52a0
  for(int i = 0; i < dataLines.length; i++){
    strList.add(dataLines[i]);
  }

  // \u623b\u308a\u5024\u7528\u914d\u5217\u3078\u30c7\u30fc\u30bf\u3092\u30b3\u30d4\u30fc
  int num_samples = strList.size();
  retlines = new String[num_samples];
  for(int i = 0; i < num_samples; i++){
    retlines[i] = strList.get(i);
  }

  return retlines;
}

// \u6307\u5b9a\u3057\u305f\u30c1\u30e3\u30f3\u30cd\u30eb\u306e\u30c7\u30fc\u30bf\u3092\u30ea\u30b9\u30c8\u306b\u683c\u7d0d\u3057\u3066\u53d6\u5f97
public ArrayList<dotObj> getDataList(String[] lines, int targetChannel){

  ArrayList<dotObj> retList = new ArrayList<dotObj>();

  // \u5168\u30c7\u30fc\u30bf\u3092\u53c2\u7167\u3057\u3066\u5fc5\u8981\u306a\u60c5\u5831\u3092\u53d6\u308a\u51fa\u3059
  int index = 0;
  for (int i = 0; i < lines.length; i++) {
    // ','\u3067\u533a\u5207\u308a\u914d\u5217\u306b\u683c\u7d0d
    String data[] = split(lines[i], ',');
    // channel, date, valuse \u306e\u9806\u3067\u30c7\u30fc\u30bf\u304c\u683c\u7d0d\u3055\u308c\u3066\u3044\u308b
    int channel = PApplet.parseInt(data[0]); // \u30c1\u30e3\u30f3\u30cd\u30eb
    String timeStamp = data[1]; // \u65e5\u4ed8
    float val = PApplet.parseFloat(data[2]); // \u5024

    if(channel == targetChannel){
      // println("channel: " + channel);
      // println("date   : " + timeStamp);
      // println("val    : " + val);

      // \u53d6\u5f97\u3057\u305f\u30c7\u30fc\u30bf\u3092\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306b\u30bb\u30c3\u30c8\u3057\u3001\u914d\u5217\u306b\u683c\u7d0d
      float m = map(val, -0.5f, 0.5f, height/4, height);
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

// \u53d6\u5f97\u3057\u305f\u30c7\u30fc\u30bf\u306e\u53ef\u8996\u5316
public void drawData(ArrayList<dotObj> channel){

  // \u73fe\u5728\u53c2\u7167\u3059\u308b\u70b9\u3068\u76f4\u524d\u306b\u53c2\u7167\u3057\u305f\u70b9\u3092\u683c\u7d0d\u3059\u308b\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u3092\u751f\u6210
  PVector currentPoint = new PVector(0, height/2, 0);
  PVector lastPoint = new PVector(0, height/2, 0);

  // \u73fe\u5728\u306e\u70b9\u3068\u76f4\u524d\u306e\u70b9\u3092\u7dda\u3067\u7d50\u3076
  for(int i = 0; i < channel.size(); i++){
    // \u73fe\u5728\u306e\u70b9\u3092\u53d6\u5f97
    currentPoint = channel.get(i).p;
    // \u73fe\u5728\u306e\u70b9\u3068\u76f4\u524d\u306e\u70b9\u3092\u7dda\u3067\u7d50\u3076
    strokeWeight(0.8f);
    stroke(200, 80);
    // stroke(random(30)+200, 20);
    line(lastPoint.x, lastPoint.y, lastPoint.z, currentPoint.x, currentPoint.y, currentPoint.z);
    text(channel.get(i).val, currentPoint.x + 20, currentPoint.y, currentPoint.z);
    // \u76f4\u524d\u306e\u70b9\u3092\u73fe\u5728\u306e\u70b9\u3067\u66f4\u65b0
    lastPoint = currentPoint;

    // \u30bf\u30a4\u30e0\u30b9\u30bf\u30f3\u30d7
    if(i == 0){
      strTimeStamp = channel.get(0).timeStamp;
      text(channel.get(0).channel, channel.get(0).p.x + 10, channel.get(0).p.y, channel.get(0).p.z);
    }
  }
}

// OSC\u9001\u4fe1
public void sendOSC(ArrayList<dotObj> channel, OscMessage message, String prefix){

  if(channel.size() > 0){
    // \u901a\u4fe1\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u306e\u521d\u671f\u5316
    message.clear();
    // prefix\u306e\u6307\u5b9a
    message.setAddrPattern(prefix);
    // \u5024\u306e\u6307\u5b9a(\u4e0b\u8a18\u306e\u3088\u3046\u306b\u7d9a\u3051\u3066\u8ffd\u52a0\u3059\u308b\u3053\u3068\u3067PureData\u3067\u306flist\u3068\u3057\u3066\u6271\u3046\u3053\u3068\u304c\u3067\u304d\u308b)
    // message.add(mouseX);
    // message.add(mouseY);

    // \u30c1\u30e3\u30f3\u30cd\u30eb\u5358\u4f4d\u306e\u5e73\u5747\u5024\u3092\u8a08\u7b97
    float sum = 0;
    float average = 0;
    for(int i = 0; i < channel.size(); i++){
      sum += channel.get(i).val;
    }
    average = sum / channel.size();
    //println(average);

    // \u30c1\u30e3\u30f3\u30cd\u30eb\u3054\u3068\u306b\u30c7\u30fc\u30bf\u3092\u30ea\u30b9\u30c8\u5316\u3057\u3066\u6e21\u3059
    for(int i = 0; i < channel.size(); i++){
      float val = map(channel.get(i).val, -0.48f, -0.31f, 0, 127);
      // float zero_shift_val = channel.get(i).val - average;
      // float val = map(zero_shift_val, -0.02, 0.02, 0, 127);
      message.add(val);
    }

    // OSC\u306e\u9001\u4fe1
    oscP5.send(message, myRemoteLocation);
  }
}

// \u70b9\u30af\u30e9\u30b9
class dotObj{
  PVector p;  // \u5ea7\u6a19
  float val;  // \u5024
  int channel;  // \u30c1\u30e3\u30f3\u30cd\u30eb
  String timeStamp;  // \u65e5\u4ed8

  // \u30b3\u30f3\u30b9\u30c8\u30e9\u30af\u30bf
  dotObj(){}

  dotObj(float x, float y, float z){
    this.p = new PVector(x, y, z);
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "getDataFromCSV" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
