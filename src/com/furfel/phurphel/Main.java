package com.furfel.phurphel;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Main extends Activity {

	GLSurfaceView glView;
	RenderGL glr;
	Context context;
	private AudioManager audio;
	
	private float dx,dy,sx,sy,csx,csy,usx,usy;
	
	private static float speed=0.15f;
	private static float speedx=0.15f;
	private static final float velocity=0.25f;
	private static final float rot=0.35f;
	
	private static int cSize = 80;
	private static int cPos = 320;
	private static int aSize=120;
	private static int aPos=680;
	
	private boolean isDown=false;
	private boolean loaded=false;
	private static boolean actionLock=false;
	private static final boolean DEBUG_ON=false;
	
	public static Frame[] Frames;
	public static int[][] PersonFrames;
	
	public static int[] globalNumbers = new int[32];
	public static boolean[] globalSwitches =  new boolean[32];
	public static ActionPack[] Actions;
	
	public static int ANDROID_VERSION=android.os.Build.VERSION.SDK_INT;
	public static final int ANDROID_GINGERBREAD=android.os.Build.VERSION_CODES.GINGERBREAD;
	public static final int ANDROID_HONEYCOMB=android.os.Build.VERSION_CODES.HONEYCOMB;
	
	public static final int POINTER_NONE=0;
	public static final int POINTER_CONTROL=1;
	public static final int POINTER_TURN=2;
	public static int[] pointers = new int[2];
	
	public static int ml=0;
	
	private float lx=10.0f,ly=1.0f,lz=10.0f;
	
	Timer moveTimer = new Timer();
	Timer personTimer = new Timer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		cSize = metrics.heightPixels/4;
		cPos = metrics.heightPixels/2;
		aSize = metrics.heightPixels/4;
		aPos = metrics.widthPixels - aSize;
		this.setContentView(R.layout.menu_layout);
		context = this;
	}
	
	public void onClick(View v) {
		if(v.getId()==R.id.imageView1) {
			this.setContentView(R.layout.activity_main);
			glView = new GLSurfaceView(this);
			glr = new RenderGL(this);
		    glView.setRenderer(glr);
		    new DataLoader().execute();
		    moveTimer.scheduleAtFixedRate(new TimerTask() {public void run() {if(!actionLock) if(isDown)moveViewer(); }}, 10, 50);
		    personTimer.scheduleAtFixedRate(new TimerTask(){public void run(){
		    if(glr.loaded && glr.MapLevels[ml].persons!=null)
		    	for(int i=0;i<glr.MapLevels[ml].persons.length;i++)
		    	glr.MapLevels[ml].persons[i].nextFrame();
		    } },10,50);
		}
		else if(v.getId()==R.id.imageViewC) {
			
		}
	}
	
	public void moveViewer() {
		float npz,npx;
		int spx=Math.round(glr.px)*-1;
		int spz=Math.round(glr.pz)*-1;
		npz = glr.pz+(float)(speed*Math.cos(Math.toRadians(glr.anglex)));//-(float)(speedx*Math.cos(Math.toRadians(glr.anglex)));
		npx = glr.px-(float)(speed*Math.sin(Math.toRadians(glr.anglex)));//+(float)(speedx*Math.cos(Math.toRadians(glr.anglex)));
		int rpx=Math.round(npx)*-1;
		int rpz=Math.round(npz)*-1;
		int w=glr.MapLevels[ml].map.length-1,h=glr.MapLevels[ml].map[0].length-1;
		if(!glr.MapLevels[ml].collision[rpx][rpz]) {
			if(npz<0 && npz>(h*-1))
			glr.pz=npz;
			if(npx<0 && npx>(w*-1))
			glr.px=npx;
		}
		else if(!glr.MapLevels[ml].collision[rpx][spz]) {
			if(npx<0 && npx>(w*-1))
			glr.px=npx;
			//glr.pz=npz;
		}
		else if(!glr.MapLevels[ml].collision[spx][rpz]) {
			if(npz<0 && npz>(h*-1))
			glr.pz=npz;
			//glr.px=npx;
		}
		if(glr.MapLevels[ml].actions[spx][spz]!=0) {
			if(Actions[glr.MapLevels[ml].actions[spx][spz]-1].autoTrigger)
				{if(!actionLock)processAction(glr.MapLevels[ml].actions[spx][spz]-1);
				/*Log.d("processAction",""+(glr.MapLevels[ml].actions[spx][spz]-1));*/}
		}
		//glr.pz+=(speed*Math.cos(Math.toRadians(glr.anglex)));
		//glr.px-=(speed*Math.sin(Math.toRadians(glr.anglex)));
	}
	
	public void makeAction() {
		int spx=Math.round(glr.px)*-1;
		int spz=Math.round(glr.pz)*-1;
		if(glr.MapLevels[ml].actions[spx][spz]!=0)
			if(!Actions[glr.MapLevels[ml].actions[spx][spz]-1].autoTrigger)
				processAction(glr.MapLevels[ml].actions[spx][spz]-1);
	}
	
	public void processAction(int act) {
		actionLock=true;
		boolean conditionSatisfied=true;
		for(int z=0;z<Actions[act].actions.length;z++) {
			Log.d("processAction","numactions: "+Actions[act].actions.length);
			conditionSatisfied=true;
			if(Actions[act].actions[z].conditions.length>0) {
				for(int i=0;i<Actions[act].actions[z].conditions.length;i++) {
					if(conditionSatisfied)
					switch(Actions[act].actions[z].conditions[i].type) {
						case ActionPack.CONDITION_NONE: { conditionSatisfied=true; } break;
						case ActionPack.CONDITION_PLAYERAT: { /*if(Main.playerx==Actions[act].actions[z].conditions[i].data[0] && Main.playery==Actions[act].actions[z].conditions[i].data[1]) conditionSatisfied=true; else conditionSatisfied=false;*/ } break;
						case ActionPack.CONDITION_ROTATION: { while(glr.anglex>360) glr.anglex-=360.0f; while(glr.anglex<0) glr.anglex+=360.0f; if(glr.anglex>Actions[act].actions[z].conditions[i].data[0] && glr.anglex<Actions[act].actions[z].conditions[i].data[1]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_ROTATION_OR: { while(glr.anglex>360) glr.anglex-=360.0f; while(glr.anglex<0) glr.anglex+=360.0f; if(glr.anglex>Actions[act].actions[z].conditions[i].data[0] || glr.anglex<Actions[act].actions[z].conditions[i].data[1]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_ROTATION_LESS: { while(glr.anglex>360) glr.anglex-=360.0f; while(glr.anglex<0) glr.anglex+=360.0f; if(glr.anglex<Actions[act].actions[z].conditions[i].data[0]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_ROTATION_GREATER: { while(glr.anglex>360) glr.anglex-=360.0f; while(glr.anglex<0) glr.anglex+=360.0f; if(glr.anglex>Actions[act].actions[z].conditions[i].data[0]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_COLLISION_AT_GVAR: { if(glr.MapLevels[ml].collision[globalNumbers[Actions[act].actions[z].conditions[i].data[0]]][globalNumbers[Actions[act].actions[z].conditions[i].data[1]]]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						
						case ActionPack.CONDITION_MAP:{ if(Main.ml==Actions[act].actions[z].conditions[i].data[0]) conditionSatisfied=true; else conditionSatisfied=false;} break;
						case ActionPack.CONDITION_COMPARE_EQUALS: { if(Main.globalNumbers[Actions[act].actions[z].conditions[i].data[0]]==Main.globalNumbers[Actions[act].actions[z].conditions[i].data[1]]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_COMPARE_LEQUAL: { if(Main.globalNumbers[Actions[act].actions[z].conditions[i].data[0]]<=Main.globalNumbers[Actions[act].actions[z].conditions[i].data[1]]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_COMPARE_GEQUAL: { if(Main.globalNumbers[Actions[act].actions[z].conditions[i].data[0]]>=Main.globalNumbers[Actions[act].actions[z].conditions[i].data[1]]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_COMPARE_LESS: { if(Main.globalNumbers[Actions[act].actions[z].conditions[i].data[0]]<Main.globalNumbers[Actions[act].actions[z].conditions[i].data[1]]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_COMPARE_GREATER: { if(Main.globalNumbers[Actions[act].actions[z].conditions[i].data[0]]>Main.globalNumbers[Actions[act].actions[z].conditions[i].data[1]]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_COMPARE_AND: { if(Main.globalSwitches[Actions[act].actions[z].conditions[i].data[0]]==Main.globalSwitches[Actions[act].actions[z].conditions[i].data[1]]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_COMPARE_NOT: { if(Main.globalSwitches[Actions[act].actions[z].conditions[i].data[0]]!=Main.globalSwitches[Actions[act].actions[z].conditions[i].data[1]]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						
						case ActionPack.CONDITION_GLOBAL_EQUALS: { if(Main.globalNumbers[Actions[act].actions[z].conditions[i].data[0]]==Actions[act].actions[z].conditions[i].data[1]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_GLOBAL_LEQUAL: { if(Main.globalNumbers[Actions[act].actions[z].conditions[i].data[0]]<=Actions[act].actions[z].conditions[i].data[1]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_GLOBAL_GEQUAL: { if(Main.globalNumbers[Actions[act].actions[z].conditions[i].data[0]]>=Actions[act].actions[z].conditions[i].data[1]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_GLOBAL_LESS: { if(Main.globalNumbers[Actions[act].actions[z].conditions[i].data[0]]<Actions[act].actions[z].conditions[i].data[1]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_GLOBAL_GREATER: { if(Main.globalNumbers[Actions[act].actions[z].conditions[i].data[0]]>Actions[act].actions[z].conditions[i].data[1]) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_GLOBAL_ISFALSE: { if(Main.globalSwitches[Actions[act].actions[z].conditions[i].data[0]]==false) conditionSatisfied=true; else conditionSatisfied=false; } break;
						case ActionPack.CONDITION_GLOBAL_ISTRUE: { if(Main.globalSwitches[Actions[act].actions[z].conditions[i].data[0]]==true) conditionSatisfied=true; else conditionSatisfied=false; } break;
					}
				}
			} else conditionSatisfied=true;
			Log.d("processAction","satisfied: "+conditionSatisfied);
			if(conditionSatisfied)
			switch(Actions[act].actions[z].type) {
				case ActionPack.ACTION_IDLE: {} break;
				case ActionPack.DESTROY_OBJECT: {glr.MapLevels[ml].map[Actions[act].actions[z].data[0]][Actions[act].actions[z].data[1]].type=0;} break;
				
				case ActionPack.SET_COLOR: {glr.GameObjects[Actions[act].actions[z].data[0]].models[Actions[act].actions[z].data[1]].colorGen(Actions[act].actions[z].data[2], Actions[act].actions[z].data[3], Actions[act].actions[z].data[4], Actions[act].actions[z].data[5]);} break;
				case ActionPack.SET_TEXTURE: {glr.GameObjects[Actions[act].actions[z].data[0]].models[Actions[act].actions[z].data[1]].setTexture(Actions[act].actions[z].data[2]);} break;
				case ActionPack.SET_PLANE_TEXTURE: {glr.Planes[Actions[act].actions[z].data[0]].setTexture(Actions[act].actions[z].data[1]); Log.d("processAction","planeTexture");} break;
				case ActionPack.CHANGE_INSTANCE: {glr.MapLevels[ml].map[Actions[act].actions[z].data[0]][Actions[act].actions[z].data[1]].type=Actions[act].actions[z].data[2]; glr.MapLevels[ml].map[Actions[act].actions[z].data[0]][Actions[act].actions[z].data[1]].id=Actions[act].actions[z].data[3];} break;
				case ActionPack.EXTRA_TRANSLATION: {glr.MapLevels[ml].map[Actions[act].actions[z].data[0]][Actions[act].actions[z].data[1]].x=Actions[act].actions[z].data[2]; glr.MapLevels[ml].map[Actions[act].actions[z].data[0]][Actions[act].actions[z].data[1]].y=Actions[act].actions[z].data[3]; glr.MapLevels[ml].map[Actions[act].actions[z].data[0]][Actions[act].actions[z].data[1]].z=Actions[act].actions[z].data[4];} break;
				case ActionPack.CHANGE_MAP: {ml=Actions[act].actions[z].data[0]; glr.redefineWalls(glr.MapLevels[ml].map.length, glr.MapLevels[ml].map[0].length);} break;
				case ActionPack.SET_ACTION: {glr.MapLevels[ml].actions[Actions[act].actions[z].data[0]][Actions[act].actions[z].data[1]]=Actions[act].actions[z].data[2];} break;
				case ActionPack.PLAY_SOUND: {SoundControl.playSound(Actions[act].actions[z].data[0]);} break;
				case ActionPack.SET_ROTATION: {glr.anglex=Actions[act].actions[z].data[0]*1.0f;} break;
				case ActionPack.SET_ROTATIONY: {glr.angle=Actions[act].actions[z].data[0]*1.0f;} break;
				case ActionPack.ROTATE_VIEWER: {glr.anglex+=Actions[act].actions[z].data[0]*1.0f;} break;
				case ActionPack.COLLISION: {glr.MapLevels[ml].collision[Actions[act].actions[z].data[0]][Actions[act].actions[z].data[1]]=true;} break;
				case ActionPack.NO_COLLISION: {glr.MapLevels[ml].collision[Actions[act].actions[z].data[0]][Actions[act].actions[z].data[1]]=false;} break;
				case ActionPack.TELEPORT_VIEWER: {glr.px=-Actions[act].actions[z].data[0]; glr.pz=-Actions[act].actions[z].data[1]; } break;
				case ActionPack.TELEPORT_VIEWER_RELATIVE: {glr.px-=Actions[act].actions[z].data[0]; glr.pz-=Actions[act].actions[z].data[1]; } break;
				case ActionPack.REMOVE_ALL_ACTION_INSTANCES: {for(int i=0;i<glr.MapLevels[ml].actions.length;i++) for(int j=0;j<glr.MapLevels[ml].actions[0].length;j++) if(glr.MapLevels[ml].actions[i][j]==Actions[act].actions[z].data[0]) glr.MapLevels[ml].actions[i][j]=0;} break;
				case ActionPack.REMOVE_ALL_OBJECT_INSTANCES: {for(int i=0;i<glr.MapLevels[ml].actions.length;i++) for(int j=0;j<glr.MapLevels[ml].actions[0].length;j++) if(glr.MapLevels[ml].map[i][j].type==Actions[act].actions[z].data[0] && glr.MapLevels[ml].map[i][j].id==Actions[act].actions[z].data[1]) glr.MapLevels[ml].map[i][j].type=0;} break;
				case ActionPack.PLAY_HEAVY_SOUND: {SoundControl.playHeavySound(Actions[act].actions[z].data[0]);} break;
				case ActionPack.CHANGE_INSTANCE_GVAR: {glr.MapLevels[ml].map[globalNumbers[Actions[act].actions[z].data[0]]][globalNumbers[Actions[act].actions[z].data[1]]].type=globalNumbers[Actions[act].actions[z].data[2]]; glr.MapLevels[ml].map[globalNumbers[Actions[act].actions[z].data[0]]][globalNumbers[Actions[act].actions[z].data[1]]].id=globalNumbers[Actions[act].actions[z].data[3]];}; break;
				case ActionPack.CHANGE_INSTANCE_AT_GVAR: {glr.MapLevels[ml].map[globalNumbers[Actions[act].actions[z].data[0]]][globalNumbers[Actions[act].actions[z].data[1]]].type=Actions[act].actions[z].data[2]; glr.MapLevels[ml].map[globalNumbers[Actions[act].actions[z].data[0]]][globalNumbers[Actions[act].actions[z].data[1]]].id=Actions[act].actions[z].data[3];}; break;
				case ActionPack.SET_OBJECT_ROTATION: {glr.MapLevels[ml].map[Actions[act].actions[z].data[0]][Actions[act].actions[z].data[1]].rot=Actions[act].actions[z].data[2];} break;
				
				case ActionPack.ACTION_CHAIN: {processActionChain(Actions[act].actions[z].datas[0]);} break;
				
				case ActionPack.GLOBALVARS_SUBSTRACT: { Main.globalNumbers[Actions[act].actions[z].data[0]]-=Actions[act].actions[z].data[1]; } break;
				case ActionPack.GLOBALVARS_ADD: { Main.globalNumbers[Actions[act].actions[z].data[0]]+=Actions[act].actions[z].data[1]; } break;
				case ActionPack.GLOBALVARS_MULTIPLY: { Main.globalNumbers[Actions[act].actions[z].data[0]]*=Actions[act].actions[z].data[1]; } break;
				case ActionPack.GLOBALVARS_FALSE: { Main.globalSwitches[Actions[act].actions[z].data[0]]=false; } break;
				case ActionPack.GLOBALVARS_TRUE: { Main.globalSwitches[Actions[act].actions[z].data[0]]=true; } break;
				case ActionPack.GLOBALVARS_NOT: { Main.globalSwitches[Actions[act].actions[z].data[0]]=!Main.globalSwitches[Actions[act].actions[z].data[0]]; } break;
				case ActionPack.GLOBALVARS_SET: { Main.globalNumbers[Actions[act].actions[z].data[0]]=Actions[act].actions[z].data[1]; }; break;
			}
			conditionSatisfied=true;
		}
		actionLock=false;
	}
	
	public void processActionChain(String script) {
		String[] data = script.split(";");
		int k=0; int cmd=0;
		int[] tmp_data = new int[8];
		while(k<data.length) {
			cmd = ActionDefinitionsLoader.resolveType(data[k]);
			switch(cmd) {
				case ActionPack.ACTION_IDLE: {} break;
				case ActionPack.DESTROY_OBJECT: {for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.MapLevels[ml].map[tmp_data[0]][tmp_data[1]].type=0;} break;
			
				case ActionPack.SET_COLOR: {for(int j=0;j<6;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.GameObjects[tmp_data[0]].models[tmp_data[1]].colorGen(tmp_data[2], tmp_data[3], tmp_data[4], tmp_data[5]);} break;
				case ActionPack.SET_TEXTURE: {for(int j=0;j<3;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.GameObjects[tmp_data[0]].models[tmp_data[1]].setTexture(tmp_data[2]);} break;
				case ActionPack.SET_PLANE_TEXTURE: {for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.Planes[tmp_data[0]].setTexture(tmp_data[1]); } break;
				case ActionPack.CHANGE_INSTANCE: {for(int j=0;j<4;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.MapLevels[ml].map[tmp_data[0]][tmp_data[1]].type=tmp_data[2]; glr.MapLevels[ml].map[tmp_data[0]][tmp_data[1]].id=tmp_data[3];} break;
				case ActionPack.EXTRA_TRANSLATION: {for(int j=0;j<5;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.MapLevels[ml].map[tmp_data[0]][tmp_data[1]].x=tmp_data[2]; glr.MapLevels[ml].map[tmp_data[0]][tmp_data[1]].y=tmp_data[3]; glr.MapLevels[ml].map[tmp_data[0]][tmp_data[1]].z=tmp_data[4];} break;
				case ActionPack.CHANGE_MAP: {for(int j=0;j<1;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} ml=tmp_data[0]; glr.redefineWalls(glr.MapLevels[ml].map.length, glr.MapLevels[ml].map[0].length); } break;
				case ActionPack.SET_ACTION: {for(int j=0;j<3;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.MapLevels[ml].actions[tmp_data[0]][tmp_data[1]]=tmp_data[2];} break;
				case ActionPack.PLAY_SOUND: {for(int j=0;j<1;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} SoundControl.playSound(tmp_data[0]);} break;
				case ActionPack.SET_ROTATION: {for(int j=0;j<1;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.anglex=tmp_data[0]*1.0f;} break;
				case ActionPack.SET_ROTATIONY: {for(int j=0;j<1;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.angle=tmp_data[0]*1.0f;} break;
				case ActionPack.ROTATE_VIEWER: {for(int j=0;j<1;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.anglex+=tmp_data[0]*1.0f;} break;
				case ActionPack.COLLISION: {for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.MapLevels[ml].collision[tmp_data[0]][tmp_data[1]]=true;} break;
				case ActionPack.NO_COLLISION: {for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);}glr.MapLevels[ml].collision[tmp_data[0]][tmp_data[1]]=false;} break;
				case ActionPack.TELEPORT_VIEWER: {for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.px=-tmp_data[0]; glr.pz=-tmp_data[1]; } break;
				case ActionPack.TELEPORT_VIEWER_RELATIVE: {for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.px-=tmp_data[0]; glr.pz-=tmp_data[1]; } break;
				case ActionPack.REMOVE_ALL_ACTION_INSTANCES: {for(int j=0;j<1;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} for(int i=0;i<glr.MapLevels[ml].actions.length;i++) for(int j=0;j<glr.MapLevels[ml].actions[0].length;j++) if(glr.MapLevels[ml].actions[i][j]==tmp_data[0]) glr.MapLevels[ml].actions[i][j]=0;} break;
				case ActionPack.REMOVE_ALL_OBJECT_INSTANCES: {for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} for(int i=0;i<glr.MapLevels[ml].actions.length;i++) for(int j=0;j<glr.MapLevels[ml].actions[0].length;j++) if(glr.MapLevels[ml].map[i][j].type==tmp_data[0] && glr.MapLevels[ml].map[i][j].id==tmp_data[1]) glr.MapLevels[ml].map[i][j].type=0;} break;
				case ActionPack.PLAY_HEAVY_SOUND: {for(int j=0;j<1;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} SoundControl.playHeavySound(tmp_data[0]);} break;
				case ActionPack.CHANGE_INSTANCE_GVAR: {for(int j=0;j<4;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.MapLevels[ml].map[globalNumbers[tmp_data[0]]][globalNumbers[tmp_data[1]]].type=globalNumbers[tmp_data[2]]; glr.MapLevels[ml].map[globalNumbers[tmp_data[0]]][globalNumbers[tmp_data[1]]].id=globalNumbers[tmp_data[3]];}; break;
				case ActionPack.CHANGE_INSTANCE_AT_GVAR: {for(int j=0;j<4;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.MapLevels[ml].map[globalNumbers[tmp_data[0]]][globalNumbers[tmp_data[1]]].type=tmp_data[2]; glr.MapLevels[ml].map[globalNumbers[tmp_data[0]]][globalNumbers[tmp_data[1]]].id=tmp_data[3];}; break;
				case ActionPack.SET_OBJECT_ROTATION: {for(int j=0;j<3;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} glr.MapLevels[ml].map[tmp_data[0]][tmp_data[1]].rot=tmp_data[2];} break;
				
				case ActionPack.GLOBALVARS_SUBSTRACT: { for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} Main.globalNumbers[tmp_data[0]]-=tmp_data[1]; } break;
				case ActionPack.GLOBALVARS_ADD: { for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} Main.globalNumbers[tmp_data[0]]+=tmp_data[1]; } break;
				case ActionPack.GLOBALVARS_MULTIPLY: { for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} Main.globalNumbers[tmp_data[0]]*=tmp_data[1]; } break;
				case ActionPack.GLOBALVARS_FALSE: { for(int j=0;j<1;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} Main.globalSwitches[tmp_data[0]]=false; } break;
				case ActionPack.GLOBALVARS_TRUE: { for(int j=0;j<1;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} Main.globalSwitches[tmp_data[0]]=true; } break;
				case ActionPack.GLOBALVARS_NOT: { for(int j=0;j<1;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} Main.globalSwitches[tmp_data[0]]=!Main.globalSwitches[tmp_data[0]]; } break;
				case ActionPack.GLOBALVARS_SET: { for(int j=0;j<2;j++) {k++; tmp_data[j]=Integer.parseInt(data[k]);} Main.globalNumbers[tmp_data[0]]=tmp_data[1]; }; break;
			}
		k++;
		}
		Log.d("CHAIN","DONE");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(glView!=null) glView.onPause();
		SoundControl.stopSounds();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(glView!=null) glView.onResume();
		SoundControl.resumeSounds();
	}
	
	public boolean onKeyDown(int KeyCode, KeyEvent evt) {
		super.onKeyDown(KeyCode, evt);
		switch(evt.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_UP: {
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
	                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		} break;
		case KeyEvent.KEYCODE_VOLUME_DOWN: {
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
	                AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
		} break;
		case KeyEvent.KEYCODE_DPAD_UP: case KeyEvent.KEYCODE_W: {glr.pz+=(speed*Math.cos(Math.toRadians(glr.anglex))); glr.px-=(speed*Math.sin(Math.toRadians(glr.anglex)));} break;
		case KeyEvent.KEYCODE_DPAD_DOWN: case KeyEvent.KEYCODE_S: {glr.pz-=(speed*Math.cos(Math.toRadians(glr.anglex))); glr.px+=(speed*Math.sin(Math.toRadians(glr.anglex)));} break;
		case KeyEvent.KEYCODE_DPAD_LEFT: case KeyEvent.KEYCODE_A: glr.px+=(0.5f*Math.sin(Math.toRadians(glr.anglex))); break;
		case KeyEvent.KEYCODE_DPAD_RIGHT: case KeyEvent.KEYCODE_D: glr.px-=(0.5f*Math.sin(Math.toRadians(glr.anglex))); break;
		case KeyEvent.KEYCODE_Q: glr.py-=0.5f; break;
		case KeyEvent.KEYCODE_Z: glr.py+=0.5f; break;
		case KeyEvent.KEYCODE_BACK: this.finish(); break;
		case KeyEvent.KEYCODE_MENU: {Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576)); Double available = new Double(Debug.getNativeHeapSize())/1048576.0; Log.d("debug","Heap: "+available+"/"+allocated+";px: "+glr.px+"; pz: "+glr.pz+";X="+((float)(Math.floor(glr.px*(-1.0f))+(1.0f*Math.sin(Math.toRadians(glr.anglex))))) + ";Y=" + glr.py + ";Z=" + ((float)(Math.floor(glr.pz*(-1.0f))-1.0f*Math.cos(Math.toRadians(glr.anglex))))+";lx="+lx+";lz="+lz+";anglex="+glr.anglex );
		String s=""; for(int i=0;i<glr.MapLevels[ml].actions[0].length;i++) {s=""; for(int j=0;j<glr.MapLevels[ml].actions.length;j++) s+=""+glr.MapLevels[ml].actions[j][i]+";"; Log.d("ActionsDump",s);} } break;
		case KeyEvent.KEYCODE_B: glr.showSelector=!glr.showSelector; break;
		}
		return true;
	}
	
	public void setProgressBar(int progress) {
		ProgressBar pb1 = (ProgressBar) findViewById(R.id.progressBar1);
		TextView tv1 = (TextView) findViewById(R.id.textView1);
		if(progress==0) tv1.setText("Loading definitions");
		else if(progress==20) tv1.setText("Loading game objects");
		else if(progress==40) tv1.setText("Loading levels");
		else if(progress==60) tv1.setText("Loading models");
		else if(progress==80) tv1.setText("Loading planes");
		else if(progress==90) tv1.setText("Loading sounds");
		else if(progress==100) glr.loaded=true;
		pb1.setProgress(progress);
	}
	
	public boolean onTouchEvent(MotionEvent evt) {
		int action = evt.getActionMasked();
		if(loaded)
		switch(action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN: {
				int pointerIndex = evt.getActionIndex();
				int pointerId = evt.getPointerId(pointerIndex);
				usx=evt.getX(pointerIndex);
				usy=evt.getY(pointerIndex);
				
				if(usx<cSize && usy>cPos){
					speed=((cPos+cSize-usy)/cSize)*velocity;
					speedx=usx*velocity/cSize;
					isDown=true;
					pointers[pointerId]=POINTER_CONTROL;
					csx=usx; csy=usy;
					Log.d("multi","id:"+pointerId+"=CONTROL");
				}
				else {pointers[pointerId]=POINTER_TURN;sx=usx; sy=usy;Log.d("multi","id:"+pointerId+"=TURN");}
				
				if(usx>aPos && usy<aSize) makeAction();
				
				if(usx<64 && usy<64)
				isDown=!isDown;
				
				if(usx>64 && usx<128 && usy<64) SaveGameControl.loadGame(context);
				if(usy>64 && usy<128 && usx<64) SaveGameControl.saveGame(context, glr.px, glr.pz, glr.anglex, glr.angle, ml);
				
				if(DEBUG_ON)
				{if(usy>400 && usx<256 && usx<336)
					{lx+=0.5f; glr.light_position2=new float[]{lx,ly,lz,1.0f};}
				else if(usy>400 && usx>336 && usx<416)
					{lx-=0.5f; glr.light_position2=new float[]{lx,ly,lz,1.0f};}
				else if(usx>720 && usy>400)
					{lz-=0.5f; glr.light_position2=new float[]{lx,ly,lz,1.0f};}
				else if(usx>720 && usy<400 && usy>320)
					{lz+=0.5f; glr.light_position2=new float[]{lx,ly,lz,1.0f};}
				else if(usx>720 && usy<160 && usy>80)
					{ly-=0.5f; glr.light_position2=new float[]{lx,ly,lz,1.0f};}
				else if(usx>720 && usy<80)
					{ly+=0.5f; glr.light_position2=new float[]{lx,ly,lz,1.0f};}
				else if(usy>160 && usy<320 && usx>720) glr.testLight=!glr.testLight;}
			} break;
			case MotionEvent.ACTION_MOVE:{
				int size = evt.getPointerCount();
				int pointerIndex = evt.getActionIndex();
				for(int i=0;i<size;i++) {
				int pointerId = evt.getPointerId(i);
					if(pointers[pointerId]==POINTER_CONTROL) {
						csx=evt.getX(i);
						csy=evt.getY(i);
						if(csx<cSize*2 && csy>cPos){
							speed=((cPos+cSize-csy)/cSize)*velocity;
							isDown=true;
						}
					}
					if(pointers[pointerId]==POINTER_TURN) {
					dx=sx-evt.getX(i);
					dy=sy-evt.getY(i);
					sx=evt.getX(i);
					sy=evt.getY(i);
					glr.angle+=dy*rot;
					if(glr.angle<-70) glr.angle=-70.0f; else if(glr.angle>70) glr.angle=70.0f;
					glr.anglex+=dx*rot;}
				}
			} break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP: {
				int pointerIndex = evt.getActionIndex();
				int pointerId = evt.getPointerId(pointerIndex);
				if(pointers[pointerId]==POINTER_CONTROL) {
					isDown=false;
				}
				pointers[pointerId]=POINTER_NONE;
			}
		}
		return true;
	}

	private class DataLoader extends AsyncTask<Void,Integer,Integer> {
		protected Integer doInBackground(Void... arg0) {
			publishProgress(0);
			Frames = FrameDefinitionsLoader.loadFrames(context, R.raw.d_frames);
			PersonFrames = PersonsDefinitionsLoader.loadPersons(context, R.raw.d_persons);
			Actions = ActionDefinitionsLoader.loadActions(context, R.raw.d_actionpacks);
			publishProgress(20);
			glr.loadGameObjects();
			publishProgress(40);
			glr.loadLevels();
			publishProgress(60);
			glr.loadModels();
			publishProgress(80);
			glr.loadPlanes();
			publishProgress(90);
			SoundControl.initSounds(context);
			while(!SoundControl.loaded) {try {Thread.sleep(100);} catch (Exception e){}}
			SoundControl.playBackgroundSound(0);
			publishProgress(100);
			return null;
		}
		protected void onProgressUpdate(Integer... progress) {
	        setProgressBar(progress[0]);
	    }
		protected void onPostExecute(Integer result) {
	        glr.loaded=true;
	        loaded=true;
	        setContentView(glView);
	    }
	}
	
}