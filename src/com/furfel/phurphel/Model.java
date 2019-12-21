package com.furfel.phurphel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;

public class Model {

	float vertices[];
	float colors[] = {1.0f, 1.0f, 1.0f, 1.0f};
	private FloatBuffer vertexBuffer;
	//private ByteBuffer indexBuffer;
	private FloatBuffer colorBuffer;
	private FloatBuffer texBuffer;
	//byte indices[] = {0, 1, 2};
	boolean texturized=false;
	int tex=0;
	float[] texCoords;
	
	public void colorGen(int r, int g, int b, int a) {
		//float cls[] = new float[colors.length];
		for(int i=0;i<colors.length;i++) {
			if(i%4==0) colors[i]=(float)r/255.0f;
			else if(i%4==1) colors[i]=(float)g/255.0f;
			else if(i%4==2) colors[i]=(float)b/255.0f;
			else if(i%4==3) colors[i]=(float)a/255.0f;
		}
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
	    cbb.order(ByteOrder.nativeOrder());
	    FloatBuffer cB;
	    cB = cbb.asFloatBuffer();
	    cB.put(colors);
	    cB.position(0);
	    colorBuffer = cB;
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
	
	public void loadModel(Context context, int resid) {
		//SimpleObjLoader loader = new SimpleObjLoader();
		SimpleObjLoader.setFile(context.getResources().openRawResource(resid));
		SimpleObjLoader.parseFile();
		SimpleObjLoader.generateTriangles();
		vertices = SimpleObjLoader.returnFloatTriangles();
		colors = SimpleObjLoader.returnWhiteness();
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
	    /*indices = new byte[vertices.length/3];
	    for(int i=0;i<indices.length;i++) {
	    	indices[i]=(byte) (i%3);
	    	}
	    indexBuffer = ByteBuffer.allocateDirect(indices.length);
	    indexBuffer.put(indices);
	    indexBuffer.position(0);*/
	    //loader = null;
	}
	
	public void draw(GL10 gl) {
	    if(!texturized){gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	    gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);}
	    else{gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, RenderGL.textures[tex]);}
	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
	    //if(Main.ANDROID_VERSION>=Main.ANDROID_HONEYCOMB) gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
	    /*else*/ gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertices.length / 3);
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	    if(!texturized){gl.glDisableClientState(GL10.GL_COLOR_ARRAY);}
	    else gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
}
