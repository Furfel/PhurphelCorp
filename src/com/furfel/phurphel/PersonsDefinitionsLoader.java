package com.furfel.phurphel;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class PersonsDefinitionsLoader {
	static ArrayList<int[]> personFrames = new ArrayList<int[]>();
	static int[] tmp;
	static ArrayList<Integer> forTmp = new ArrayList<Integer>();
	static int tmp2;
	
	public static int[][] loadPersons(Context context, int resid) {
		int[][] personsArray = null;
		try {
			XmlPullParserFactory factory;
			XmlPullParser parser;
			String text="";
			String tag="";
			int j=0;
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();
			parser.setInput(context.getResources().openRawResource(resid), null);
			int eventType = parser.getEventType();
			while(eventType!=XmlPullParser.END_DOCUMENT) {
				switch(eventType) {
					case XmlPullParser.START_TAG: {tag = parser.getName(); if(tag.equalsIgnoreCase("person")) {forTmp.clear(); tmp=null;} } break;
					case XmlPullParser.TEXT: { text=parser.getText(); } break;
					case XmlPullParser.END_TAG: {tag = parser.getName();
					if(tag.equalsIgnoreCase("frame")) {tmp2 = Integer.parseInt(text); forTmp.add(tmp2);}
					else if(tag.equalsIgnoreCase("sleep")) {tmp2=64000+Integer.parseInt(text); forTmp.add(tmp2);}
					else if(tag.equalsIgnoreCase("person")) {tmp = new int[forTmp.size()]; j=0; for(int num:forTmp) {tmp[j]=num; j++;} personFrames.add(tmp); } } break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {e.printStackTrace();}
	personsArray = new int[personFrames.size()][];
	int i=0;
	for(int[] ints:personFrames) {
		personsArray[i] = ints; i++;
	}
		return personsArray;
	}
}
