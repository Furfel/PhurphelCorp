package com.furfel.phurphel;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class FrameDefinitionsLoader {
	static Frame tmp;
	static ArrayList<Frame> frames = new ArrayList<Frame>();
	
	public static Frame[] loadFrames(Context context, int resid) {
		Frame[] framesArray=null;
		
		try {
			XmlPullParserFactory factory;
			XmlPullParser parser;
			String text="";
			String tag="";
			float x=0.0f,y=0.0f,z=0.0f,rot=0.0f;
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();
			parser.setInput(context.getResources().openRawResource(resid), null);
			int eventType = parser.getEventType();
			while(eventType!=XmlPullParser.END_DOCUMENT) {
				switch(eventType) {
				case XmlPullParser.START_TAG: {tag = parser.getName(); if(tag.equalsIgnoreCase("frame")) {tmp=null;x=0.0f;y=0.0f;z=0.0f;rot=0.0f;}} break;
				case XmlPullParser.TEXT: {text = parser.getText();} break;
				case XmlPullParser.END_TAG: {
					tag = parser.getName();
					if(tag.equalsIgnoreCase("rot")) rot = Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("x")) x = Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("y")) y = Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("z")) z = Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("frame")) {tmp=new Frame(x,y,z,rot); frames.add(tmp);}
					}break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {e.printStackTrace();}
		
		framesArray = new Frame[frames.size()];
		int i=0;
		for(Frame frame:frames) {
			framesArray[i] = frame; i++;
		}
		
		return framesArray;
	}
}
