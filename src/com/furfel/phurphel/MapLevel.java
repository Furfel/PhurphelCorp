package com.furfel.phurphel;

import java.io.DataInputStream;
import java.io.InputStream;

public class MapLevel {
	
	public static final int TYPE_NONE=0;
	public static final int TYPE_MODEL=1;
	public static final int TYPE_GAMEOBJECT=2;
	public static final int TYPE_PLANE=3;
	
	TMapPoint[][] map;
	public boolean[][] collision;
	public Person[] persons;
	public int[][] actions;
	
	public MapLevel(InputStream stream) {
		loadMap(stream);
	}
	
	public void loadMap(InputStream stream) {
		int w,h,p=0;
		int x,z,rot,id;
		try {
			DataInputStream ds = new DataInputStream(stream);
			w=ds.readInt();
			h=ds.readInt();
			map = new TMapPoint[w][h];
			collision = new boolean[w][h];
			actions = new int[w][h];
			for(int i=0;i<h;i++)
				for(int j=0;j<w;j++)
					{map[j][i]=new TMapPoint();
					map[j][i].type=ds.readInt();
					if(map[j][i].type!=TYPE_NONE) {
					map[j][i].id=ds.readInt();
					map[j][i].rot=ds.readFloat();
					map[j][i].x=ds.readFloat();
					map[j][i].y=ds.readFloat();
					map[j][i].z=ds.readFloat();
					map[j][i].x2=ds.readFloat();
					map[j][i].y2=ds.readFloat();
					map[j][i].z2=ds.readFloat();}
					}
			for(int i=0;i<h;i++)
				for(int j=0;j<w;j++)
					collision[j][i]=ds.readBoolean();
			if(ds.available()>0) {
				p = ds.readInt();
				persons = new Person[p];
				for(int a=0;a<p;a++) {
					id=ds.readInt(); x=ds.readInt(); z=ds.readInt(); rot=ds.readInt();
					persons[a] = new Person(x*1.0f,z*1.0f,rot*1.0f);
					persons[a].frames=Main.PersonFrames[id];
				}
			}
			if(ds.available()>0) {
				for(int i=0;i<h;i++)
					for(int j=0;j<w;j++)
						actions[j][i]=ds.readInt();
			}
			ds.close();
		} catch (Exception e) {}
	}
}

class TMapPoint {
	int type;
	int id;
	float rot;
	float x,y,z,x2,y2,z2;
}