package com.furfel.phurphel;

import javax.microedition.khronos.opengles.GL10;

public class Person {
	
	public static final byte NODIR=-1, NORTH=0, SOUTH=2, EAST=1, WEST=3;
	
	public float x=10.0f,z=10.0f,rot=0.0f;
	public float dstx=10.0f, dstz=10.0f, dstrot=0.0f;
	public float Arot=-10.0f;
	public boolean armdir=true;
	public byte migrationDir=-1;
	
	public int[] frames=null; int curframe=0;
	int sleepframes=0;
	
	public Person() {}
	public Person(float x, float z, float rot) {
		this.x=x;
		this.z=z;
		this.rot=rot;
		this.dstx=this.x;
		this.dstz=this.z;
		this.dstrot=this.rot;
	}
	
	public void nextFrame() {
		if(frames!=null) {
			if(x<dstx) {x+=0.1f; if(x>dstx) x=dstx;} else if(x>dstx) {x-=0.1f; if(x<dstx) x=dstx;}
			if(z<dstz) {z+=0.1f; if(z>dstz) z=dstz;} else if(z>dstz) {z-=0.1f; if(z<dstz) z=dstz;}
			if(rot>dstrot) {rot-=4.0f; if(rot<dstrot) rot=dstrot; /*if(rot<0) rot+=360;*/} else if(rot<dstrot) {rot+=4.0f; if(rot>dstrot) rot=dstrot; /*if(rot>360) rot-=360;*/}
			if(dstx!=x || dstz!=z || dstrot!=rot) {
			if(armdir) {
				if(Arot<20.0f) Arot+=4.0f;
				else armdir=!armdir;
			} else {
				if(Arot>-20.0f) Arot-=4.0f; 
				else armdir=!armdir;
				}
			}
			else if(sleepframes>0) {
				sleepframes--;
			}
			else {
				if(curframe<(frames.length-1)) curframe++; else {curframe=0; rot=0; dstrot=0;}
				int frame = frames[curframe];
				if(frame>64000) {sleepframes=frame-64000; Arot=0.0f;}
				else {
				dstx=x+Main.Frames[frame].x;
				//y+=Main.Frames[frame].y;
				dstz=z+Main.Frames[frame].z;
				dstrot=rot+Main.Frames[frame].rot; //if(dstrot>=360) dstrot-=360.0f; else if(dstrot<0) dstrot+=360.0f;
				}
			}
			//if(rot>=360) rot-=360.0f; else if(rot<0) rot+=360.0f;
		}
	}
	
	public void draw(GL10 gl) {
		gl.glTranslatef(0.0f, 1.0f, 0.0f);
		gl.glScalef(0.5f, 0.5f, 0.5f);
		RenderGL.body.draw(gl);
		
		/* XXX arms */
		
		gl.glPushMatrix();
		gl.glRotatef(-25.0f,1.0f,0.0f,0.0f);
		gl.glRotatef(Arot, 0.0f, 0.0f, 1.0f);
		gl.glTranslatef((float)Math.sin(Math.toRadians(Arot)),-1.0f,0.85f);
		RenderGL.arm.draw(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glRotatef(25.0f, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(-Arot, 0.0f, 0.0f, 1.0f);
		gl.glTranslatef((float)Math.sin(Math.toRadians(-Arot)), -1.0f, -0.85f);
		RenderGL.arm.draw(gl);
		gl.glPopMatrix();
		
		/* XXX legs */
		
		gl.glPushMatrix();
		gl.glRotatef(Arot*0.5f, 0.0f, 0.0f, 1.0f);
		gl.glTranslatef((float)Math.sin(Math.toRadians(Arot*0.5f)), -1.5f, -0.3f);
		RenderGL.arm.draw(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glRotatef(-Arot*0.5f, 0.0f, 0.0f, 1.0f);
		gl.glTranslatef((float)Math.sin(Math.toRadians(-Arot*0.5f)), -1.5f, 0.3f);
		RenderGL.arm.draw(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 2.0f, 0.0f);
		RenderGL.head.draw(gl);
		gl.glPopMatrix();
	}
}
