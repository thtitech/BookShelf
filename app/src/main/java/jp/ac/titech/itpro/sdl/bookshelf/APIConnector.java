package jp.ac.titech.itpro.sdl.bookshelf;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haya on 2017/07/09.
 */

public class APIConnector {

    public static final String baseUrl = "http://iss.ndl.go.jp/api/opensearch?isbn=";
    public static final String UTF8 = "UTF-8";
    public static final String ITEM_TAG = "item";
    public static final String TITLE_TAG = "title";
    public static final String AUTHOR_TAG = "creator";
    public static final String PUBLISHER_TAG = "publisher";
    public static final String SUBJECT_TAG = "subject";

    public List<String> getAttributes(List<String> tagList, String isbn){
        Map<String, String> map = new HashMap<String, String>();
        final List<String> result = new ArrayList<String>();
        try{
            URL url = new URL(baseUrl + isbn);
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF8));
            XmlPullParser xmlParser = Xml.newPullParser();
            xmlParser.setInput(in);
            int eventType = xmlParser.getEventType();
            String tagName = null;
            Log.d("XMLResult", "Start dump XML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = xmlParser.getName();
                if ((eventType == XmlPullParser.START_TAG)){
                    //tag set
                    Log.d("XML", xmlParser.getName());
                    if (tagList.contains(name) && "dc".equals(xmlParser.getPrefix())) {
                        tagName = name;
                    }else{
                        tagName = null;
                    }
                }else if ((eventType == XmlPullParser.TEXT) && tagName != null){
                    //get text
                    Log.d("XML", xmlParser.getText());
                    String content = tagName + ":" + xmlParser.getText();
                    if (map.containsKey(tagName)){
                        map.put(tagName, map.get(tagName) + "," + xmlParser.getText());
                    }else {
                        map.put(tagName, content);
                    }
                }else if (eventType == XmlPullParser.END_TAG){
                    Log.d("XML", xmlParser.getName());
                    if(ITEM_TAG.equals(xmlParser.getName())){
                        break;
                    }
                    tagName = null;
                }
                eventType = xmlParser.next();
            }
        }catch (IOException | XmlPullParserException e){
            e.printStackTrace();
            Log.d("XMLResult", "Cause Exception");
            result.add("Exception" + e.getMessage());
            return result;
        }
        for (Map.Entry<String, String> entry : map.entrySet()){
            result.add(entry.getValue());
            Log.d("Debug", entry.getValue());
        }
        return result;
    }
}
