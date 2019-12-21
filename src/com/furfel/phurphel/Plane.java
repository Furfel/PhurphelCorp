package com.furfel.phurphel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Plane {
	float vertices[];
	float colors[] = {1.0f, 1.0f, 1.0f, 1.0f};
	private FloatBuffer vertexBuffer;
	private ByteBuffer indexBuffer;
	private FloatBuffer colorBuffer;
	private FloatBuffer texBuffer;
	byte indices[] = {0, 1, 2};
	boolean texturized=false;
	int tex=-1;
	float[] texCoords;
	float rot;
	
	public boolean glass=false;
	
	public Plane(float[] verts, float[] color) {
		vertices = verts;
		colors = new float[color.length*4];
		for(int i=0;i<colors.length; i++) {
			if(i%4==0) colors[i] = color[0];
			else if(i%4==1) colors[i] = color[1];
			else if(i%4==2) colors[i] = color[2];
			else if(i%4==3) colors[i] = color[3];
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
	    vbb.order(ByteOrder.nativeOrder());
	    vertexBuffer = vbb.asFloatBuffer();
	    vertexBuffer.put(vertices);
	    vertexBuffer.position(0);
	    ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
	    cbb.order(ByteOrder.nativeOrder());
	    colorBuffer = cbb.asFloatBuffer();
	    colorBuffer.put(colors);
	    colorBuffer.position(0);
	    indexBuffer = ByteBuffer.allocateDirect(indices.length);
	    indexBuffer.put(indices);
	    indexBuffer.position(0);
	}
	
	public Plane(float x, float y, float z, float x2, float y2, float z2, float[] color, boolean horizontal) {
		vertices = new float[12];
		if(horizontal) {
		vertices[0]=x; vertices[1]=y; vertices[2]=z;
		vertices[3]=x2; vertices[4]=y; vertices[5]=z2;
		vertices[6]=x; vertices[7]=y2; vertices[8]=z;
		vertices[9]=x2; vertices[10]=y2; vertices[11]=z2;}
		else {vertices[0]=x; vertices[1]=y; vertices[2]=z;
		vertices[3]=x2; vertices[4]=y2; vertices[5]=z;
		vertices[6]=x; vertices[7]=y; vertices[8]=z2;
		vertices[9]=x2; vertices[10]=y2; vertices[11]=z2;}
		colors = new float[color.length*4];
		for(int i=0;i<colors.length; i++) {
			if(i%4==0) colors[i] = color[0];
			else if(i%4==1) colors[i] = color[1];
			else if(i%4==2) colors[i] = color[2];
			else if(i%4==3) colors[i] = color[3];
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
	    vbb.order(ByteOrder.nativeOrder());
	    vertexBuffer = vbb.asFloatBuffer();
	    vertexBuffer.put(vertices);
	    vertexBuffer.position(0);
	    ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
	    cbb.order(ByteOrder.nativeOrder());
	    colorBuffer = cbb.asFloatBuffer();
	    colorBuffer.put(colors);
	    colorBuffer.position(0);
	    indexBuffer = ByteBuffer.allocateDirect(indices.length);
	    indexBuffer.put(indices);
	    indexBuffer.position(0);
	}
	
	public Plane(float x, float y, float z, float x2, float y2, float z2, float[] color, boolean horizontal, int texture) {
		vertices = new float[12];
		if(horizontal) {vertices[0]=x; vertices[1]=y; vertices[2]=z;
		vertices[3]=x2; vertices[4]=y; vertices[5]=z2;
		vertices[6]=x; vertices[7]=y2; vertices[8]=z;
		vertices[9]=x2; vertices[10]=y2; vertices[11]=z2;}
		else {
		vertices[0]=x; vertices[1]=y; vertices[2]=z;
		vertices[3]=x2; vertices[4]=y2; vertices[5]=z;
		vertices[6]=x; vertices[7]=y; vertices[8]=z2;
		vertices[9]=x2; vertices[10]=y2; vertices[11]=z2;}
		colors = new float[color.length*4];
		for(int i=0;i<colors.length; i++) {
			if(i%4==0) colors[i] = color[0];
			else if(i%4==1) colors[i] = color[1];
			else if(i%4==2) colors[i] = color[2];
			else if(i%4==3) colors[i] = color[3];
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
	    vbb.order(ByteOrder.nativeOrder());
	    vertexBuffer = vbb.asFloatBuffer();
	    vertexBuffer.put(vertices);
	    vertexBuffer.position(0);
	    ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
	    cbb.order(ByteOrder.nativeOrder());
	    colorBuffer = cbb.asFloatBuffer();
	    colorBuffer.put(colors);
	    colorBuffer.position(0);
	    indexBuffer = ByteBuffer.allocateDirect(indices.length);
	    indexBuffer.put(indices);
	    indexBuffer.position(0);
	    if(texture!=-1)setTexture(texture);
	}
	
	public Plane(float x, float y, float z, float x2, float y2, float z2, float[] color, boolean horizontal, int texture, float rotation) {
		vertices = new float[12];
		if(horizontal) {vertices[0]=x; vertices[1]=y; vertices[2]=z;
		vertices[3]=x2; vertices[4]=y; vertices[5]=z2;
		vertices[6]=x; vertices[7]=y2; vertices[8]=z;
		vertices[9]=x2; vertices[10]=y2; vertices[11]=z2;}
		else {
		vertices[0]=x; vertices[1]=y; vertices[2]=z;
		vertices[3]=x2; vertices[4]=y2; vertices[5]=z;
		vertices[6]=x; vertices[7]=y; vertices[8]=z2;
		vertices[9]=x2; vertices[10]=y2; vertices[11]=z2;}
		colors = new float[color.length*4];
		for(int i=0;i<colors.length; i++) {
			if(i%4==0) colors[i] = color[0];
			else if(i%4==1) colors[i] = color[1];
			else if(i%4==2) colors[i] = color[2];
			else if(i%4==3) colors[i] = color[3];
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
	    vbb.order(ByteOrder.nativeOrder());
	    vertexBuffer = vbb.asFloatBuffer();
	    vertexBuffer.put(vertices);
	    vertexBuffer.position(0);
	    ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
	    cbb.order(ByteOrder.nativeOrder());
	    colorBuffer = cbb.asFloatBuffer();
	    colorBuffer.put(colors);
	    colorBuffer.position(0);
	    indexBuffer = ByteBuffer.allocateDirect(indices.length);
	    indexBuffer.put(indices);
	    indexBuffer.position(0);
	    if(texture!=-1)setTexture(texture);
	    rot=rotation;
	}
	
	public Plane(float x, float y, float z, float x2, float y2, float z2, float[] color, boolean horizontal, int texture, float rotation, boolean glass) {
		vertices = new float[12];
		if(horizontal) {vertices[0]=x; vertices[1]=y; vertices[2]=z;
		vertices[3]=x2; vertices[4]=y; vertices[5]=z2;
		vertices[6]=x; vertices[7]=y2; vertices[8]=z;
		vertices[9]=x2; vertices[10]=y2; vertices[11]=z2;}
		else {
		vertices[0]=x; vertices[1]=y; vertices[2]=z;
		vertices[3]=x2; vertices[4]=y2; vertices[5]=z;
		vertices[6]=x; vertices[7]=y; vertices[8]=z2;
		vertices[9]=x2; vertices[10]=y2; vertices[11]=z2;}
		colors = new float[color.length*4];
		for(int i=0;i<colors.length; i++) {
			if(i%4==0) colors[i] = color[0];
			else if(i%4==1) colors[i] = color[1];
			else if(i%4==2) colors[i] = color[2];
			else if(i%4==3) colors[i] = color[3];
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
	    vbb.order(ByteOrder.nativeOrder());
	    vertexBuffer = vbb.asFloatBuffer();
	    vertexBuffer.put(vertices);
	    vertexBuffer.position(0);
	    ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
	    cbb.order(ByteOrder.nativeOrder());
	    colorBuffer = cbb.asFloatBuffer();
	    colorBuffer.put(colors);
	    colorBuffer.position(0);
	    indexBuffer = ByteBuffer.allocateDirect(indices.length);
	    indexBuffer.put(indices);
	    indexBuffer.position(0);
	    if(texture!=-1)setTexture(texture);
	    rot=rotation;
	    this.glass=glass;
	}
	
	public void setTexture(int texture) {
		tex=texture;
		texCoords = new float[]{ 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
	    tbb.order(ByteOrder.nativeOrder());
	    texBuffer = tbb.asFloatBuffer();
	    texBuffer.put(texCoords);
	    texBuffer.position(0);
	    texturized=true;
	}
	
	public void draw(GL10 gl) {
		gl.glRotatef(rot, 0.0f, 1.0f, 0.0f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
	    if(!texturized) {gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	    gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);}
	    else {gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
	    if(tex!=-1)gl.glBindTexture(GL10.GL_TEXTURE_2D, RenderGL.textures[tex]);}
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	    if(!texturized) gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	    else gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
}
