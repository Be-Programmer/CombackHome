package com.example.songchiyun.comebackhome;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import kr.hyosang.coordinate.CoordPoint;
import kr.hyosang.coordinate.TransCoord;

/**
 * Created by songchiyun on 16. 5. 24..
 */
public class GetSubLocation{  //this class is used to get nearest subway data.

    double sLat = -1;
    double sLng = -1;
    public boolean stop = false;
    private Location subLoc;
    private Location myLoc;
    private String subName;
    private String SubData;
    GetSubLocation(double Lat, double Lng){
        myLoc = new Location("current");
        myLoc.setLatitude(Lat);
        myLoc.setLongitude(Lng);
        subLoc = new Location("Subway");
        SubData = new SubwayCoorData().getData();
    }

    public Location getSubCoor(){
        return subLoc;
    }
    public String getSubName(){ return subName;}
    public  void getSubInfo() {
        while (!stop) {
            double nearlist = 0;
            int index=0;
            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(SubData);
                org.json.simple.JSONArray jsonArray = (org.json.simple.JSONArray) jsonObject.get("subInfo");
                //get all subway data (coordinate and subway station name ans parse them to use json
                JSONObject object = (JSONObject) jsonArray.get(0);

                Location first = new Location("firstLocation");
                String tx = object.get("xcoord").toString();
                String ty = object.get("ycoord").toString();
                first.setLatitude(Double.valueOf(tx));
                first.setLongitude(Double.valueOf(ty));
                nearlist = myLoc.distanceTo(first);
                //compute the nearest subway staion to use computing algoritm(find min)
                for (int i = 1; i < jsonArray.size(); i++) {
                    object = (JSONObject) jsonArray.get(i);
                    tx = object.get("xcoord").toString();
                    ty = object.get("ycoord").toString();
                    Location location = new Location("temp");
                    location.setLatitude(Double.valueOf(tx));
                    location.setLongitude(Double.valueOf(ty));
                    if (myLoc.distanceTo(location) < nearlist) {
                        index = i;
                        subLoc = location;
                        nearlist = myLoc.distanceTo(location);
                    }
                }
                JSONObject result = (JSONObject) jsonArray.get(index);
                subName = result.get("subway").toString();

                Log.d("des",subName);
                stop = true;
                //if get subway data then finish the function
            }
            catch(ParseException e){

            }

        }

    }


}



