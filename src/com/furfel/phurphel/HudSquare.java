package com.furfel.phurphel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class HudSquare {
	private FloatBuffer vertexBuffer;
	private FloatBuffer texBuffer;
	
	int tex=4;
	
	public float[] verticles=
		{
			-1.0f,-1.0f,
			1.0f, -1.0f,
			-1.0f, 1.0f,
			1.0f, 1.0f
		};
	public float[] texcoords=
		{
			0.0f,0.0f,
			1.0f,0.0f,
			0.0f,1.0f,
			1.0f,1.0f
		};
	
	public HudSquare() {
		ByteBuffer vbb = ByteBuffer.allocateDirect(verticles.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(verticles);
		vertexBuffer.position(0);
		ByteBuffer tbb = ByteBuffer.allocateDirect(texcoords.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		texBuffer = tbb.asFloatBuffer();
		texBuffer.put(texcoords);
		texBuffer.position(0);
		tbb.clear();
	}
public void draw(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, RenderGL.textures[tex]);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, verticles.length/2);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
	}
public void setVertex(int id, float pointx, float pointy)
	{
		verticles[id*2]=pointx;
		verticles[id*2+1]=pointy;
		vertexBuffer.clear();
		ByteBuffer vbb = ByteBuffer.allocateDirect(verticles.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(verticles);
		vertexBuffer.position(0);
	}
}
