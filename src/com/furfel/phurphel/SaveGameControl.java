package com.furfel.phurphel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.content.Context;
import android.util.Log;

public class SaveGameControl {

	static final byte[] k = {10,20,57,42,22,33,91,80,75,45,66,21,123,107,104,22,87,19,20,81,47,61,46,10,12,78,63,110};
	
	static float PX,PZ,ROTX,ROTY;
	static int ML;
	static boolean hasSave=true;
	
	public static void saveGame(Context context, float px, float pz, float rotx, float roty, int ml) {
		String androidId = "C" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		byte[] aId = androidId.getBytes();
		byte[] data = new byte[20];
		byte[] buf = ByteBuffer.allocate(4).putFloat(px).array();
		for(int i=0;i<4;i++) data[i] = buf[i];
		buf = ByteBuffer.allocate(4).putFloat(pz).array();
		for(int i=0;i<4;i++) data[4+i] = buf[i];
		buf = ByteBuffer.allocate(4).putFloat(rotx).array();
		for(int i=0;i<4;i++) data[8+i] = buf[i];
		buf = ByteBuffer.allocate(4).putFloat(roty).array();
		for(int i=0;i<4;i++) data[12+i] = buf[i];
		buf = ByteBuffer.allocate(4).putInt(ml).array();
		for(int i=0;i<4;i++) data[16+i] = buf[i];
		
		int a=0,b;
		byte pdata;
		byte[] ndata = new byte[data.length];
		for(int i=0;i<data.length;i++) {
			pdata = (byte) (data[i] ^ k[i]);
			ndata[i] = (byte)(pdata ^ aId[a]);
			a++; if(a>=aId.length) a=0;
		}
		
		Log.d("SaveGame","Saving... ID:"+androidId);
		
		try {
		OutputStream ostream = context.openFileOutput("userData",Context.MODE_PRIVATE);
		DataOutputStream dostream = new DataOutputStream(ostream);
		dostream.write(ndata);
		dostream.close();
		}
		catch (Exception e) {Log.d("SaveGame","Cannot save game! "+e.getMessage());}
	}
	
	public static void loadGame(Context context) {
		String androidId = "C" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		byte[] aId = androidId.getBytes();
		byte pdata;
		byte[] ndata = new byte[0];
		try {
			InputStream istream = context.openFileInput("userData");
			DataInputStream distream = new DataInputStream(istream);
			ndata = new byte[distream.available()];
			distream.readFully(ndata);
			distream.close();
		} catch(Exception e) {hasSave=false;}
		if(hasSave) {
		byte[] data = new byte[ndata.length];
		int a=0;
		for(int i=0;i<ndata.length;i++) {
			pdata = (byte)(ndata[i] ^ aId[a]);
			a++; if(a>=aId.length) a=0;
			data[i] = (byte) (pdata ^ k[i]);
		}
		byte[] buf = new byte[4];
		for(int i=0;i<4;i++) buf[i] = data[i];
		PX = ByteBuffer.wrap(buf).getFloat();
		buf = new byte[4];
		for(int i=0;i<4;i++) buf[i] = data[i+4];
		PZ = ByteBuffer.wrap(buf).getFloat();
		buf = new byte[4];
		for(int i=0;i<4;i++) buf[i] = data[i+8];
		ROTX = ByteBuffer.wrap(buf).getFloat();
		buf = new byte[4];
		for(int i=0;i<4;i++) buf[i] = data[i+12];
		ROTY = ByteBuffer.wrap(buf).getFloat();
		buf = new byte[4];
		for(int i=0;i<4;i++) buf[i] = data[i+16];
		ML = ByteBuffer.wrap(buf).getInt();Log.d("SaveGame","PX:"+PX+" PZ:"+PZ+" RX:"+ROTX+" RY:"+ROTY+" ML:"+ML);}
		else Log.d("SaveGame","No save data");
	}
	
}
