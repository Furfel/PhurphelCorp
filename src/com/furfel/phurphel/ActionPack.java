package com.furfel.phurphel;
import java.util.ArrayList;


public class ActionPack {
	
	public static final int CONDITION_NONE=0;
	public static final int CONDITION_PLAYERAT=1;
	public static final int CONDITION_COMPARE_EQUALS=2;
	public static final int CONDITION_COMPARE_LEQUAL=3;
	public static final int CONDITION_COMPARE_GEQUAL=4;
	public static final int CONDITION_COMPARE_LESS=5;
	public static final int CONDITION_COMPARE_GREATER=6;
	public static final int CONDITION_COMPARE_AND=7;
	public static final int CONDITION_COMPARE_NOT=8;
	public static final int CONDITION_GLOBAL_EQUALS=9;
	public static final int CONDITION_GLOBAL_LEQUAL=10;
	public static final int CONDITION_GLOBAL_GEQUAL=11;
	public static final int CONDITION_GLOBAL_LESS=12;
	public static final int CONDITION_GLOBAL_GREATER=13;
	public static final int CONDITION_GLOBAL_ISFALSE=14;
	public static final int CONDITION_GLOBAL_ISTRUE=15;
	public static final int CONDITION_MAP=16;
	public static final int CONDITION_ROTATION_LESS=17;
	public static final int CONDITION_ROTATION_GREATER=18;
	public static final int CONDITION_ROTATION=19;
	public static final int CONDITION_ROTATION_OR=20;
	public static final int CONDITION_COLLISION_AT_GVAR=21;
	
	
	public static final int ACTION_IDLE=0;
	public static final int GLOBALVARS_ADD=1;
	public static final int GLOBALVARS_SUBSTRACT=2;
	public static final int GLOBALVARS_MULTIPLY=3;
	public static final int GLOBALVARS_SET=4;
	public static final int GLOBALVARS_NOT=5;
	public static final int GLOBALVARS_FALSE=6;
	public static final int GLOBALVARS_TRUE=7;
	public static final int DESTROY_OBJECT=8;
	public static final int CREATE_OBJECT=9;
	public static final int NO_COLLISION=10;
	public static final int COLLISION=11;
	public static final int SET_COLOR=12;
	public static final int SET_TEXTURE=13;
	public static final int SET_PLANE_TEXTURE=14;
	public static final int CHANGE_INSTANCE=15;
	public static final int EXTRA_TRANSLATION=16;
	public static final int CHANGE_MAP=17;
	public static final int SET_ACTION=18;
	public static final int PLAY_SOUND=19;
	public static final int SET_ROTATION=20;
	public static final int ROTATE_VIEWER=21;
	public static final int TELEPORT_VIEWER=22;
	public static final int TELEPORT_VIEWER_RELATIVE=23;
	public static final int REMOVE_ALL_ACTION_INSTANCES=24;
	public static final int PLAY_HEAVY_SOUND=25;
	public static final int SET_ROTATIONY=26;
	public static final int REMOVE_ALL_OBJECT_INSTANCES=27;
	public static final int ACTION_CHAIN=28;
	public static final int CHANGE_INSTANCE_GVAR=29;
	public static final int CHANGE_INSTANCE_AT_GVAR=30;
	public static final int SET_OBJECT_ROTATION=31;
	
	Action[] actions;
	
	public boolean autoTrigger=false;
	public boolean playerOnly=false;
	
}

class Action {
	public int type;
	public String[] datas;
	public int[] data;
	public Condition[] conditions;
	
	public Action(int type, int[] data, String[] datas, int conditions) {
		this.type=type;
		
		if(datas!=null){
		this.datas=new String[datas.length];
		for(int i=0;i<datas.length;i++) {
			this.datas[i]=datas[i];
		}}
		
		if(data!=null){
		this.data=new int[data.length];
		for(int i=0;i<data.length;i++) {
			this.data[i]=data[i];
		}}
		
	this.conditions = new Condition[conditions]; 
	}
	
	public Action() {
		
	}
	
	public void makeCondition(int index, int type, int[] data) {
		conditions[index].type=type;
		conditions[index].data=data;
	}
}

class Condition {
	public int type;
	public int[] data;
}