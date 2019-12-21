package com.furfel.phurphel;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class GameObject {

	Model[] models;
	ArrayList<Model> modell = new ArrayList<Model>();
	Model tmp;
	
	public void loadObject(Context context, int resid) {
		try {
			XmlPullParserFactory factory;
			XmlPullParser parser;
			String text="";
			String tag="";
			int r=0,g=0,b=0,a=0;
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();
			parser.setInput(context.getResources().openRawResource(resid), null);
			int eventType = parser.getEventType();
			while(eventType!=XmlPullParser.END_DOCUMENT) {
				switch(eventType) {
					case XmlPullParser.START_TAG: {tag = parser.getName();
					if(tag.equalsIgnoreCase("model")) tmp = new Model();} break;
					case XmlPullParser.TEXT: {text = parser.getText();} break;
					case XmlPullParser.END_TAG: {tag = parser.getName();
					if(tag.equalsIgnoreCase("id")) tmp.loadModel(context, RenderGL.models[Integer.parseInt(text)]);
					else if(tag.equalsIgnoreCase("red")) r=Integer.parseInt(text);
					else if(tag.equalsIgnoreCase("green")) g=Integer.parseInt(text);
					else if(tag.equalsIgnoreCase("blue")) b=Integer.parseInt(text);
					else if(tag.equalsIgnoreCase("alpha")) a=Integer.parseInt(text);
					else if(tag.equalsIgnoreCase("colors")) tmp.colorGen(r, g, b, a);
					else if(tag.equalsIgnoreCase("texture")) tmp.setTexture(Integer.parseInt(text));
					else if(tag.equalsIgnoreCase("model")) {modell.add(tmp);}
					} break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!modell.isEmpty())
			{models = new Model[modell.size()];
			int i=0;
			for(Model mdl : modell) {models[i]=mdl; i++;}
			}
		tmp=null; modell=null;
	}
	
	public void draw(GL10 gl) {
		if(models!=null)
			if(models.length>0)
				for(int i=0;i<models.length;i++)
					models[i].draw(gl);
	}
}
