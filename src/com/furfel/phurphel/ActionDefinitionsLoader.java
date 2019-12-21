package com.furfel.phurphel;

import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class ActionDefinitionsLoader {
	
		static ArrayList<ActionPack> packs = new ArrayList<ActionPack>();
		/*           /\     */
		static ArrayList<Action> actions = new ArrayList<Action>();
		/*           /\    */
		static ArrayList<Condition> conditions = new ArrayList<Condition>();
		static ArrayList<String> stringData = new ArrayList<String>();
		static ArrayList<Integer> numberData = new ArrayList<Integer>();
		static int type;
		
		static ArrayList<Integer> conditionData = new ArrayList<Integer>();
		static int conditionType;
		
		static ActionPack tmp;
		static Condition tCondition;
		static Action tAction;
		
		static boolean autoTrigger=false;
		static boolean playerOnly=false;
		
		public static int resolveType(String type) {
			int result=0;
			if(type.equalsIgnoreCase("IDLE")) result=0;
			else if(type.equalsIgnoreCase("NONE")) result=0;
			else if(type.equalsIgnoreCase("CMP_EQUALS")) result=2;
			else if(type.equalsIgnoreCase("CMP_LEQUAL")) result=3;
			else if(type.equalsIgnoreCase("CMP_GEQUAL")) result=4;
			else if(type.equalsIgnoreCase("CMP_LESS")) result=5;
			else if(type.equalsIgnoreCase("CMP_GREATER")) result=6;
			else if(type.equalsIgnoreCase("CMP_AND")) result=7;
			else if(type.equalsIgnoreCase("CMP_NOT")) result=8;
			else if(type.equalsIgnoreCase("EQUALS")) result=9;
			else if(type.equalsIgnoreCase("LEQUAL")) result=10;
			else if(type.equalsIgnoreCase("GEQUAL")) result=11;
			else if(type.equalsIgnoreCase("LESS")) result=12;
			else if(type.equalsIgnoreCase("GREATER")) result=13;
			else if(type.equalsIgnoreCase("ISFALSE")) result=14;
			else if(type.equalsIgnoreCase("ISTRUE")) result=15;
			else if(type.equalsIgnoreCase("MAP")) result=16;
			else if(type.equalsIgnoreCase("ROTATION_LESS")) result=17;
			else if(type.equalsIgnoreCase("ROTATION_GREATER")) result=18;
			else if(type.equalsIgnoreCase("ROTATION")) result=19;
			else if(type.equalsIgnoreCase("ROTATION_OR")) result=20;
			else if(type.equalsIgnoreCase("COLLISION_AT_GVAR")) result=21;
			else if(type.equalsIgnoreCase("ADD")) result=1;
			else if(type.equalsIgnoreCase("SUBSTRACT")) result=2;
			else if(type.equalsIgnoreCase("MULTIPLY")) result=3;
			else if(type.equalsIgnoreCase("SET")) result=4;
			else if(type.equalsIgnoreCase("NOT")) result=5;
			else if(type.equalsIgnoreCase("FALSE")) result=6;
			else if(type.equalsIgnoreCase("TRUE")) result=7;
			else if(type.equalsIgnoreCase("DESTROY")) result=8;
			else if(type.equalsIgnoreCase("CREATE")) result=9;
			else if(type.equalsIgnoreCase("REMOVE_COLLISION")) result=10;
			else if(type.equalsIgnoreCase("MAKE_COLLISION")) result=11;
			else if(type.equalsIgnoreCase("SET_COLOR")) result=12;
			else if(type.equalsIgnoreCase("SET_TEXTURE")) result=13;
			else if(type.equalsIgnoreCase("SET_PLANE_TEXTURE")) result=14;
			else if(type.equalsIgnoreCase("CHANGE_INSTANCE")) result=15;
			else if(type.equalsIgnoreCase("EXTRA_TRANSLATION")) result=16;
			else if(type.equalsIgnoreCase("CHANGE_MAP")) result=17;
			else if(type.equalsIgnoreCase("SET_ACTION")) result=18;
			else if(type.equalsIgnoreCase("PLAY_SOUND")) result=19;
			else if(type.equalsIgnoreCase("SET_ROTATION")) result=20;
			else if(type.equalsIgnoreCase("ROTATE_VIEWER")) result=21;
			else if(type.equalsIgnoreCase("TELEPORT_VIEWER")) result=22;
			else if(type.equalsIgnoreCase("TELEPORT_RELATIVE")) result=23;
			else if(type.equalsIgnoreCase("REMOVE_ALL_ACTION_INSTANCES")) result=24;
			else if(type.equalsIgnoreCase("PLAY_HSOUND")) result=25;
			else if(type.equalsIgnoreCase("SET_ROTATIONY")) result=26;
			else if(type.equalsIgnoreCase("REMOVE_ALL_OBJECT_INSTANCES")) result=27;
			else if(type.equalsIgnoreCase("ACTION_CHAIN")) result=28;
			else if(type.equalsIgnoreCase("CHANGE_INSTANCE_GVAR")) result=29;
			else if(type.equalsIgnoreCase("CHANGE_INSTANCE_AT_GVAR")) result=30;
			else if(type.equalsIgnoreCase("SET_OBJECT_ROTATION")) result=31;
			return result;
		}
		
		public static ActionPack[] loadActions(Context context, int resid) {
			ActionPack[] actionsArray = null;
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
						case XmlPullParser.START_TAG: {tag = parser.getName();
						if(tag.equalsIgnoreCase("pack")) {actions.clear(); autoTrigger=false; playerOnly=false; tmp=new ActionPack();}
						else if(tag.equalsIgnoreCase("action")) { conditions.clear(); stringData.clear(); numberData.clear(); tAction = new Action();}
						else if(tag.equalsIgnoreCase("condition")) {conditionData.clear(); tCondition = new Condition();}
						} break;
						
						case XmlPullParser.TEXT: { text=parser.getText(); } break;
						
						case XmlPullParser.END_TAG: {tag = parser.getName();
						if(tag.equalsIgnoreCase("pack")) {
							tmp.actions = new Action[actions.size()]; j=0; for(Action ac:actions) {tmp.actions[j]=ac; j++;}
							tmp.autoTrigger=autoTrigger;
							tmp.playerOnly=playerOnly;
							packs.add(tmp); tmp=null;
							}
						else if(tag.equalsIgnoreCase("action")) {
							tAction.type=type;
							tAction.conditions=new Condition[conditions.size()]; j=0; for(Condition cd:conditions) {tAction.conditions[j]=cd; j++;}
							tAction.data=new int[numberData.size()]; j=0; for(int cd:numberData) {tAction.data[j]=cd; j++;}
							tAction.datas=new String[stringData.size()]; j=0; for(String cd:stringData) {tAction.datas[j]=cd; j++;}
							actions.add(tAction); tAction=null;
							}
						else if(tag.equalsIgnoreCase("condition")) {tCondition.type=conditionType; tCondition.data=new int[conditionData.size()]; j=0; for(int dd:conditionData) {tCondition.data[j]=dd; j++;} conditions.add(tCondition); tCondition=null;}
						else if(tag.equalsIgnoreCase("ctype")) {conditionType=resolveType(text);}
						else if(tag.equalsIgnoreCase("cdata")) {conditionData.add(Integer.parseInt(text));}
						else if(tag.equalsIgnoreCase("number")) {numberData.add(Integer.parseInt(text));}
						else if(tag.equalsIgnoreCase("string")) {stringData.add(text);}
						else if(tag.equalsIgnoreCase("type")) {type = resolveType(text);}
						else if(tag.equalsIgnoreCase("auto")) {if(Integer.parseInt(text)==1) autoTrigger=true; else autoTrigger=false;}
						else if(tag.equalsIgnoreCase("playeronly")) {if(Integer.parseInt(text)==1) playerOnly=true; else playerOnly=false;}
						} break;
					}
					eventType = parser.next();
				}
			} catch (Exception e) {e.printStackTrace();}
		actionsArray = new ActionPack[packs.size()];
		int i=0;
		for(ActionPack ints:packs) {
			actionsArray[i] = ints; i++;
		}
		return actionsArray;
	}
}
