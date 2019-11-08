package com.tas.beaconzz.NoticeAndMain;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class NoticeParser {
    public List<MainActivity.News> parse(String xml) throws XmlPullParserException, IOException {
        List<MainActivity.News> newsList = new ArrayList<>();
        MainActivity.News news = null;
        boolean isItem = false;
        String text = "";
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(xml));
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagName.equals("item")) {
                        news = new MainActivity.News();
                        isItem = true;
                    }
                    break;
                case XmlPullParser.TEXT:
                    if (isItem) {
                        text = parser.getText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (isItem) {
                        if (tagName.equals("item")) {
                            newsList.add(news);
                            isItem = false;
                        } else if (tagName.equals("author")) {
                            news.name = text;
                        } else if (tagName.equals("title")) {
                            news.title = text;
                        } else if (tagName.equals("link")) {
                            news.link = text;
                        } else if (tagName.equals("description")) {
                            news.description = text;
                        } else if (tagName.equals("pubDate")) {
                            news.pubDate = text;
                        }
                    }
                    break;
                default:

            }
            eventType = parser.next();
        }

        return newsList;
    }
}