package com.furfel.phurphel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.*;
import android.opengl.ETC1Util.ETC1Texture;
import android.util.Log;

public class RenderGL implements GLSurfaceView.Renderer {

	/* XXX System variables */
	
	Context context;
	public boolean loaded=false;
	int width, height;
	
	float px=-4.0f,py=-1.5f,pz=-18.0f;
	//float px=-4.0f,py=-1.5f,pz=-13.0f;
	float angle=0.0f,anglex=0.0f;
	
	/* XXX Lighting setup */
	
	float[] mat_specular = { 0.9f, 0.9f, 0.9f, 1.0f };
	float[] mat_shininess = { 30.0f };
	float[] light_position = { 1.0f, 2.0f, 1.0f, 1.0f };
	float[] light_position2 = {10.0f,1.0f,10.0f, 1.0f};
	float[] person_light = {1.0f,0.5f,1.0f,1.0f};
	float[] light_diffuse = { 0.85f, 0.85f, 0.85f, 1.0f };
	float[] light_ambient = { 0.0f,0.0f,0.0f,1.0f};
	
	/*XXX  wallcolors*/
	
	final float[] cyanwall = {0.85f,1.0f,1.0f,1.0f};
	final float[] bluewall = {0.85f,0.85f,0.95f,1.0f};
	final float[] whitewall = {1.0f,1.0f,1.0f,1.0f};
	final float[] redwall = {1.0f,0.9f,0.9f,1.0f};
	
	/* XXX Data for drawing*/
	
	HudSquare hud,hudA;
	Model[] Models;
	GameObject[] GameObjects;
	Plane[] Planes;
	MapLevel[] MapLevels;
	Person[] Persons;
	Model selector = new Model(); boolean showSelector=false; boolean testLight=false;
	static Model head, arm, body; 
	Plane ground,wall1,wall2,wall3,wall4,ceil;
	MapLevel level;
	static int[] textures;
	
	/* XXX Lists where to get data*/
	
	static final int[] models = {
		R.raw.m_sphere, //0
		R.raw.m_trashbin, //1
		R.raw.m_crystalball, //2
		R.raw.m_cb_1, R.raw.m_cb_2, //3-4
		R.raw.m_machine1,R.raw.m_machine2,R.raw.m_machine3, //5-7
		R.raw.m_trash1, //8
		R.raw.m_plant1,R.raw.m_plant2,R.raw.m_plant3,R.raw.m_plant4, //9-12
		R.raw.m_bench1,R.raw.m_bench2, //13-14
		R.raw.m_lamp1,R.raw.m_lamp2, //15-16
		R.raw.m_desk1,R.raw.m_desk2, //17-18
		R.raw.m_switch1, R.raw.m_switch2, R.raw.m_switch3, R.raw.m_switch4, R.raw.m_switch5, //19-23
		R.raw.m_shelf1, R.raw.m_shelfm, R.raw.m_shelfbo, //24-26
		R.raw.m_tunnel1, R.raw.m_tunnel2, R.raw.m_tunnel3, R.raw.m_tunnel4, R.raw.m_tunnel5, //27-31
		R.raw.m_token, //32
		R.raw.m_button1, R.raw.m_button2, //33-34
		R.raw.m_block, //35
		R.raw.m_shelf2, R.raw.m_shelf3, R.raw.m_shelf4, //36-38
		R.raw.m_teleport1, R.raw.m_teleport2, //39-40
		R.raw.m_mazeblock, //41
		};
	static final int[] gameobjects = {R.raw.o_crystalbl, //0
		R.raw.o_machine, //1
		R.raw.o_trash, //2
		R.raw.o_plant, //3
		R.raw.o_bench, //4
		R.raw.o_lamp,  //5
		R.raw.o_lampg,  //6
		R.raw.o_desk,   //7
		R.raw.o_switch_up, //8
		R.raw.o_switch_down, //9
		R.raw.o_shelfa, R.raw.o_shelfb, R.raw.o_shelfc, //10-12
		R.raw.o_tunnel, R.raw.o_tunnel2, R.raw.o_tunnel3, R.raw.o_tunnel4, //13-16
		R.raw.o_tokenb, R.raw.o_tokenc, R.raw.o_tokenr, //17-19
		R.raw.o_button, //20
		R.raw.o_block, //21
		R.raw.o_shelfd, //22
		R.raw.o_teleport, //23
		R.raw.o_mazeblock, //24
		};
	static final int[] texes = {R.raw.t_press, //0
		R.drawable.biohazard,
		R.drawable.phurphel,
		R.raw.t_door, 
		R.drawable.controls,
		R.drawable.controls2,//5
		R.drawable.pc1,
		R.raw.t_pc2,
		R.drawable.arrow,
		R.raw.t_message,
		R.raw.t_reception,//10
		R.raw.t_arrow,
		R.raw.t_noentry,
		R.drawable.code,
		R.raw.t_stalker,
		R.raw.t_tokenrules, //15
		R.drawable.redpixel,
		R.drawable.bluepixel,
		R.drawable.cyanpixel,
		R.drawable.blackpixel,
		R.drawable.dirt, //20
		R.raw.t_illusions,
		R.drawable.herobrine,
		R.drawable.mazetex,
		R.raw.t_maze,
		};
	static final int[] levels = {R.raw.l_welcome, R.raw.l_new2, R.raw.l_new1, R.raw.l_new3, R.raw.l_new4,
		R.raw.l_aa01, R.raw.l_aa02, R.raw.l_aa03, R.raw.l_aa04, };
	
	/* XXX Draw function */
	public void onDrawFrame(GL10 gl) {
		if(loaded) {
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gluPerspective(gl, width, height);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();                // Reset model-view matrix ( NEW )
		gl.glMatrixMode(GL10.GL_MODELVIEW);
			//gl.glTranslatef(-px, -py, -pz);
			gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);
			gl.glRotatef(anglex, 0.0f, 1.0f, 0.0f);
			gl.glTranslatef(px,py,pz);
			gl.glEnable(GL10.GL_LIGHT0);
			light_position=new float[] {10.0f,1.0f,25.0f,1.0f};
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light_position, 0);
			/*gl.glPushMatrix();
			gl.glTranslatef(-px, 0.8f, -pz);
			gl.glRotatef(-anglex, 0.0f, 1.0f, 0.0f);
			GameObjects[17].draw(gl);
			gl.glPopMatrix();*/
			/*XXX room drawing*/
			
			gl.glPushMatrix();
				gl.glTranslatef(1.0f, 0.0f, 1.0f);
				ground.draw(gl);
				wall1.draw(gl);
				wall2.draw(gl);
				wall3.draw(gl);
				wall4.draw(gl);
				ceil.draw(gl);
			gl.glPopMatrix();
			
			/*XXX test light drawing*/
			
			if(testLight) {
			gl.glPushMatrix();
			gl.glTranslatef(light_position2[0],light_position2[1],light_position2[2]);
			selector.draw(gl);
			gl.glPopMatrix();}
			if(testLight) {gl.glEnable(GL10.GL_LIGHT1); gl.glDisable(GL10.GL_LIGHT0);} else {gl.glEnable(GL10.GL_LIGHT0); gl.glDisable(GL10.GL_LIGHT1);} 
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, light_position2, 0);
			
			/*XXX level drawing */
			
		/*if(level!=null)*/
		for(int i=0;i<MapLevels[Main.ml].map.length;i++)
			for(int j=0;j<MapLevels[Main.ml].map[0].length;j++){
					if(MapLevels[Main.ml].map[i][j].type!=MapLevel.TYPE_NONE)
						switch(MapLevels[Main.ml].map[i][j].type) {
							case MapLevel.TYPE_MODEL: {
								gl.glPushMatrix();
								gl.glTranslatef(i*1.0f, 0.0f, j*1.0f);
								gl.glRotatef(MapLevels[Main.ml].map[i][j].rot, 0.0f, 1.0f, 0.0f);
								Models[MapLevels[Main.ml].map[i][j].id].draw(gl);
								gl.glPopMatrix();
							}break;
							case MapLevel.TYPE_GAMEOBJECT: {
								gl.glPushMatrix();
								light_position[0]=i*1.0f+MapLevels[Main.ml].map[i][j].x2+MapLevels[Main.ml].map[i][j].x;
								light_position[1]=2.0f+MapLevels[Main.ml].map[i][j].y2+MapLevels[Main.ml].map[i][j].y;
								light_position[2]=j*1.0f+MapLevels[Main.ml].map[i][j].z2+MapLevels[Main.ml].map[i][j].z;
								gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light_position, 0);
								gl.glTranslatef(i*1.0f+MapLevels[Main.ml].map[i][j].x, 0.0f+MapLevels[Main.ml].map[i][j].y, j*1.0f+MapLevels[Main.ml].map[i][j].z);
								gl.glRotatef(MapLevels[Main.ml].map[i][j].rot, 0.0f, 1.0f, 0.0f);
								GameObjects[MapLevels[Main.ml].map[i][j].id].draw(gl);
								gl.glPopMatrix();
							}break;
							case MapLevel.TYPE_PLANE: {
								if(!Planes[MapLevels[Main.ml].map[i][j].id].glass) {
								gl.glPushMatrix();
								light_position[0]=i*1.0f+MapLevels[Main.ml].map[i][j].x2+MapLevels[Main.ml].map[i][j].x;
								light_position[1]=2.0f+MapLevels[Main.ml].map[i][j].y2+MapLevels[Main.ml].map[i][j].y;
								light_position[2]=j*1.0f+MapLevels[Main.ml].map[i][j].z2+MapLevels[Main.ml].map[i][j].z;
								gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light_position, 0);
								gl.glTranslatef(i*1.0f+MapLevels[Main.ml].map[i][j].x, 0.0f+MapLevels[Main.ml].map[i][j].y, j*1.0f+MapLevels[Main.ml].map[i][j].z);
								gl.glRotatef(MapLevels[Main.ml].map[i][j].rot, 0.0f, 1.0f, 0.0f);
								Planes[MapLevels[Main.ml].map[i][j].id].draw(gl);
								gl.glPopMatrix();
								}
							}break;
						}
				}
		if(showSelector) {gl.glPushMatrix();
		gl.glTranslatef((float)(Math.floor(px*(-1.0f)+(1.0f*Math.sin(Math.toRadians(anglex))))), py*(-1.0f), (float)(Math.floor(pz*(-1.0f)-1.0f*Math.cos(Math.toRadians(anglex)))) );
		selector.draw(gl);
		gl.glPopMatrix();}
		
		/* XXX persons */
		
		gl.glDisable(GL10.GL_LIGHT0);
		gl.glEnable(GL10.GL_LIGHT2);
		for(int i=0;i<MapLevels[Main.ml].persons.length;i++) {
			gl.glPushMatrix();
			gl.glTranslatef(MapLevels[Main.ml].persons[i].x, -0.2f, MapLevels[Main.ml].persons[i].z);
			gl.glRotatef(MapLevels[Main.ml].persons[i].rot,0.0f,1.0f,0.0f);
			person_light[0]=MapLevels[Main.ml].persons[i].x+0.5f*(float)Math.cos(Math.toRadians(MapLevels[Main.ml].persons[i].rot));
			person_light[2]=MapLevels[Main.ml].persons[i].z+0.5f*(float)Math.sin(Math.toRadians(MapLevels[Main.ml].persons[i].rot));
			gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, person_light, 0);
			MapLevels[Main.ml].persons[i].draw(gl);
			gl.glPopMatrix();}
			gl.glDisable(GL10.GL_LIGHT2);
			
			gl.glEnable(GL10.GL_LIGHT0);
			
			for(int i=0;i<MapLevels[Main.ml].map.length;i++)
				for(int j=0;j<MapLevels[Main.ml].map[0].length;j++){
					if(MapLevels[Main.ml].map[i][j].type==MapLevel.TYPE_PLANE)
						if(Planes[MapLevels[Main.ml].map[i][j].id].glass) {
							gl.glPushMatrix();
							light_position[0]=i*1.0f+MapLevels[Main.ml].map[i][j].x2+MapLevels[Main.ml].map[i][j].x;
							light_position[1]=2.0f+MapLevels[Main.ml].map[i][j].y2+MapLevels[Main.ml].map[i][j].y;
							light_position[2]=j*1.0f+MapLevels[Main.ml].map[i][j].z2+MapLevels[Main.ml].map[i][j].z;
							gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light_position, 0);
							gl.glTranslatef(i*1.0f+MapLevels[Main.ml].map[i][j].x, 0.0f+MapLevels[Main.ml].map[i][j].y, j*1.0f+MapLevels[Main.ml].map[i][j].z);
							gl.glRotatef(MapLevels[Main.ml].map[i][j].rot, 0.0f, 1.0f, 0.0f);
							Planes[MapLevels[Main.ml].map[i][j].id].draw(gl);
							gl.glPopMatrix();
						}
				}
		/*gl.glPushMatrix();
		gl.glTranslatef(person.x+1.5f*(float)Math.cos(Math.toRadians(person.rot)),0.5f,person.z+1.5f*(float)Math.sin(Math.toRadians(person.rot)));
		selector.draw(gl);
		gl.glPopMatrix();*/
		gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gluOrtho2D(gl, width, height);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		drawHUD(gl);
		gl.glPopMatrix();
		}
	}

	/* XXX Draw preparators */
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		width=w; height=h;
		gl.glViewport(0, 0, w, h);
		gluPerspective(gl,w,h);
		hud = new HudSquare();
		hudA = new HudSquare();
		float hh=0.0f; float cH=h/3;
		float hw=((float)(h/2))/w-1;/*((float)(h/2))/w;*/ Log.d("Controls","hw"+hw);
		//hw-=1;
		hud.setVertex(0, -1.0f, hh);
		hud.setVertex(1, hw, hh);
		hud.setVertex(2, -1.0f, -1.0f);
		hud.setVertex(3, hw, -1.0f);
		
		hw=(float)(h/2)/w;
		hh=(float)(h/2);
		hudA.tex=5;
		hudA.setVertex(0, 1.0f-hw, 1.0f);
		hudA.setVertex(1, 1.0f, 1.0f);
		hudA.setVertex(2, 1.0f-hw, 0.5f);
		hudA.setVertex(3, 1.0f, 0.5f);
	}
	
	public void gluPerspective(GL10 gl, int w, int h) {
		if(h==0) h=1;
		float aspect = (float)w/h;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void gluOrtho2D(GL10 gl, int w, int h) {
	    gl.glMatrixMode( GL10.GL_PROJECTION );
	    gl.glLoadIdentity();
	    //GLU.gluOrtho2D( gl, 0, w, 0, h );
	    gl.glOrthof(-1.0f, 1.0f, -1.0f, 1.0f, -0.1f, 0.1f );
	    gl.glMatrixMode(GL10.GL_MODELVIEW);
	    gl.glLoadIdentity();
	}
	
	public void drawHUD(GL10 gl) {
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_LIGHTING);
		hud.draw(gl);
		hudA.draw(gl);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_LIGHTING);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig glc) {
		gl.glClearColor(0, 0, 0, 1.0f);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glDisable(GL10.GL_DITHER);
		
		/*XXX material setup*/
		gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SPECULAR, mat_specular, 0);
		gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_SHININESS, mat_shininess, 0);
		gl.glMaterialfv(GL10.GL_FRONT, GL10.GL_AMBIENT_AND_DIFFUSE, light_ambient, 0);
		
		/*XXX lights setup*/
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light_position, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, light_ambient,0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, mat_specular,0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, light_diffuse,0);
		
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_POSITION, light_position, 0);
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_AMBIENT, light_ambient,0);
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPECULAR, mat_specular,0);
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_DIFFUSE, light_diffuse,0);
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_CONSTANT_ATTENUATION, new float[]{2.0f},0);
		
		/*XXX testlight setup*/
		
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, light_position2, 0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, light_ambient,0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, mat_specular,0);
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, light_diffuse,0);
		
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glEnable(GL10.GL_LIGHT1);
		gl.glEnable(GL10.GL_LIGHT2);
		
		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		loadTextures(gl);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
	}
	
	/* XXX Constructor */
	
	public RenderGL(Context context) {
		this.context=context;
		selector.loadModel(context, R.raw.m_selector);
		selector.colorGen(0, 0, 255, 255);
		redefineWalls(9,20);
		//redefineWalls(15, 15);
		Persons = new Person[2];
		Persons[0] = new Person();
		Persons[1] = new Person();
		Persons[1].x=6.0f;
		Persons[1].migrationDir=Person.NORTH;
		Persons[1].z=2.0f;
	}
	
	public void redefineWalls(int w, int h) {
		ground = new Plane(-2.0f,0.0f,-2.0f,(w-1)*1.0f,0.0f,(h-1)*1.0f,new float[]{1.0f,1.0f,1.0f,1.0f},false);
		wall1 = new Plane(-2.0f,3.0f,(h-1)*1.0f,(w-1)*1.0f,0.0f,(h-1)*1.0f,redwall,true);
		wall2 = new Plane(-2.0f,0.0f,-2.0f,-2.0f,3.0f,(h-1)*1.0f,whitewall,false);
		wall3 = new Plane((w-1)*1.0f,0.0f,-2.0f,(w-1)*1.0f,3.0f,(h-1)*1.0f,cyanwall,false);
		wall4 = new Plane((w-1)*1.0f,3.0f,-2.0f,-2.0f,0.0f,-2.0f,bluewall,true);
		ceil = new Plane(-2.0f,3.0f,-2.0f,(w-1)*1.0f,3.0f,(h-1)*1.0f,new float[]{0.9f,0.9f,0.9f,1.0f},false);
	}
	
	public void redefineWallsForThis(int map) {
		redefineWalls(MapLevels[map].map.length, MapLevels[map].map[0].length);
	}
	
	/* XXX Loaders */
	
	public void loadGameObjects() {
		GameObjects = new GameObject[gameobjects.length];
		for(int i=0;i<gameobjects.length;i++) {
		GameObjects[i] = new GameObject();
		GameObjects[i].loadObject(context, gameobjects[i]);}
	}
	
	public void loadModels() {
		/*Models = new Model[models.length];
		for(int i=0;i<models.length;i++) {
			Models[i] = new Model();
			Models[i].loadModel(context, models[i]);
		}*/ //no need to load models anymore
		head = new Model();
		head.loadModel(context, R.raw.mp_head);
		body = new Model();
		body.loadModel(context, R.raw.mp_body);
		body.colorGen(200, 200, 200, 255);
		arm = new Model();
		arm.loadModel(context, R.raw.mp_arm);
	}
	
	public void loadLevels() {
		MapLevels = new MapLevel[levels.length];
		for(int i=0;i<levels.length;i++) {
			MapLevels[i] = new MapLevel(context.getResources().openRawResource(levels[i]));
		}
		level = MapLevels[0];
	}
	
	public void loadPlanes() {
		Planes = PlaneDefinitionsLoader.loadPlanes(context, R.raw.d_planes);
	}
	
	public void loadTextures(GL10 gl) {
		textures = new int[texes.length];
		gl.glGenTextures(texes.length, textures, 0);
		for(int i=0;i<texes.length;i++) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			InputStream istream = context.getResources().openRawResource(texes[i]);
			if(context.getResources().getResourceTypeName(texes[i]).equalsIgnoreCase("drawable")) {
		      Bitmap bitmap;
		      try {
		         bitmap = BitmapFactory.decodeStream(istream);
		      } finally {
		         try {
		            istream.close();
		         } catch(IOException e) { }
		      }
		      GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		      bitmap.recycle();
			}
			else if(context.getResources().getResourceTypeName(texes[i]).equalsIgnoreCase("raw")) {
				try{
					Log.d("ETC1","Loading etc1_tex: "+ETC1Util.isETC1Supported());
					ETC1Texture etc1tex = ETC1Util.createTexture(istream);               
				    ByteBuffer bb = etc1tex.getData();              
				    gl.glCompressedTexImage2D(GL10.GL_TEXTURE_2D, 0, ETC1.ETC1_RGB8_OES, etc1tex.getWidth(), etc1tex.getHeight(), 0, bb.capacity(), bb);
	            }
	            catch(IOException e){System.out.println("DEBUG! IOException"+e.getMessage()); Log.e("ETC1", e.getMessage());}
	            finally { try {istream.close();} catch (IOException e) {}}
			}
		}
	}

	/* XXX End of class */
	
}
