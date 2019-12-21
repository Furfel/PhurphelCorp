package com.furfel.phurphel;

import java.util.*;
import java.io.*;

public class SimpleObjLoader {
	static String filename="";
	static File file;
	static InputStream istream;
	
	static T3DPoint[] points;
	static ArrayList<T3DPoint> pointlist = new ArrayList<T3DPoint>();
	static ArrayList<TFace> faces = new ArrayList<TFace>();
	static ArrayList<T3DPoint> triangles = new ArrayList<T3DPoint>();
	
	public static void setFile(String file) {
		filename=file;
	}
	
	public static void setFile(File filex) {
		file=filex;
	}
	
	public static void setFile(InputStream istreamx) {
		istream = istreamx;
	}
	
	public static void parseFile() {
		pointlist.clear(); points=null; faces.clear(); triangles.clear();
		try {
			/*FileInputStream fstream;
			if(file!=null)
			fstream = new FileInputStream(file);
			else fstream = new FileInputStream(filename);*/ 
			DataInputStream in;
			//if(istream!=null)
			in = new DataInputStream(istream);
			//else in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				if(strLine.charAt(0)=='v') parseLine(strLine);
				if(strLine.charAt(0)=='f') parseFace(strLine);
			}
			in.close();
		} catch (Exception e) { e.printStackTrace(); }
		points = new T3DPoint[pointlist.size()];
		int i=0;
		for(T3DPoint flo:pointlist) {points[i] = new T3DPoint(); points[i]=flo; i++;}
		file=null; istream=null; filename="";
	}
	
	public static void parseLine(String line) {
		String[] s = line.split(" ");
		T3DPoint pnt = new T3DPoint();
		pnt.x=Float.parseFloat(s[1]);
		pnt.y=Float.parseFloat(s[2]);
		pnt.z=Float.parseFloat(s[3]);
		pointlist.add(pnt);
	}
	
	public static void parseFace(String line) {
		String[] s = line.split(" ");
		TFace face = new TFace();
		face.a=Integer.parseInt(s[1]);
		face.b=Integer.parseInt(s[2]);
		face.c=Integer.parseInt(s[3]);
		faces.add(face);
	}
	
	public static void generateTriangles() {
		for(TFace face : faces) {
			T3DPoint[] tmp3d = new T3DPoint[3];
			tmp3d[0]=points[face.a-1];
			tmp3d[1]=points[face.b-1];
			tmp3d[2]=points[face.c-1];
			triangles.add(tmp3d[0]);
			triangles.add(tmp3d[1]);
			triangles.add(tmp3d[2]);
		}
	}
	
	public static T3DPoint[] returnPoints() {
		return points;
	}
	
	public static T3DPoint[] returnTriangles() {
		T3DPoint[] tmpp = new T3DPoint[triangles.size()];
		int i=0;
		for(T3DPoint flo:triangles) {tmpp[i] = new T3DPoint(); tmpp[i]=flo; i++;}
		return tmpp;
	}
	
	public static float[] returnFloatTriangles() {
		float[] trs = new float[triangles.size()*3];
		int i=0;
		for(T3DPoint flo:triangles) {trs[i*3] = flo.x; trs[i*3+1] = flo.y; trs[i*3+2] = flo.z; i++;}
		return trs;
	}
	
	public static float[] returnWhiteness() {
		float[] colors = new float[triangles.size()*4];
		for(int i=0;i<colors.length;i++) {
			if((i+1)%4==0)colors[i]=1.0f;
			else colors[i]=1.0f;
		}
		return colors;
	}
}

class T3DPoint {
	float x,y,z;
}

class TFace {
	int a,b,c;
}
