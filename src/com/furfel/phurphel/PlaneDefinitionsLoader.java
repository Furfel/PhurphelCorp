package com.furfel.phurphel;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class PlaneDefinitionsLoader {

	static Plane tmp;
	static ArrayList<Plane> planes = new ArrayList<Plane>();
	
	public static Plane[] loadPlanes(Context context, int resid) {
		Plane[] planesArray = null;
		try {
			XmlPullParserFactory factory;
			XmlPullParser parser;
			String text="";
			String tag="";
			float r=0,g=0,b=0,a=0;
			float x=-1.0f,y=-1.0f,z=0.0f,x2=1.0f,y2=1.0f,z2=0.0f;
			float rot=0.0f;
			int tex=-1;
			boolean horiz=false,glass=false;
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();
			parser.setInput(context.getResources().openRawResource(resid), null);
			int eventType = parser.getEventType();
			while(eventType!=XmlPullParser.END_DOCUMENT) {
				switch(eventType) {
					case XmlPullParser.START_TAG: {if(tag.equalsIgnoreCase("plane")) {tmp=null;glass=false;horiz=false;x=-1.0f;y=-1.0f;z=0.0f;x2=1.0f;y2=1.0f;z2=0.0f;r=0;g=0;b=0;a=0;rot=0.0f;tex=-1;}} break;
					case XmlPullParser.TEXT: {text = parser.getText();} break;
					case XmlPullParser.END_TAG: {tag = parser.getName();
					if(tag.equalsIgnoreCase("rot")) rot = Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("red")) r=Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("green")) g=Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("blue")) b=Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("alpha")) a=Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("x")) x=Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("y")) y=Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("z")) z=Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("x2")) x2=Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("y2")) y2=Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("z2")) z2=Float.parseFloat(text);
					else if(tag.equalsIgnoreCase("texture")) tex=Integer.parseInt(text);
					else if(tag.equalsIgnoreCase("horizontal")) {if(Integer.parseInt(text)==1) horiz=true; else horiz=false;}
					else if(tag.equalsIgnoreCase("glass")) {if(Integer.parseInt(text)==1) glass=true; else glass=false;}
					else if(tag.equalsIgnoreCase("plane")) {tmp = new Plane(x,y,z,x2,y2,z2,new float[]{r,g,b,a},horiz,tex,rot,glass); planes.add(tmp); Log.d("planes","new plane:"+x+";"+y+";"+z+";"+x2+";"+y2+";"+z2+";"+r+";"+g+";"+b+";"+a+";"+tex+";"+rot+";"+horiz+";");}
					} break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		planesArray = new Plane[planes.size()];
		int i=0;
		for(Plane plane:planes) {
			planesArray[i] = plane; i++;
		}
		
		return planesArray;
	}
	
}
