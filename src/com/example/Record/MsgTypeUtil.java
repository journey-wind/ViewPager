package com.example.Record;

public class MsgTypeUtil {
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String musicName =  musicPath.substring(musicPath.lastIndexOf("/")+1);
		String temp = time+"/"+name+"/"+context+" /"+musicName+" /"+musicLegth+" /"+lisenNum+"/"+musicMk+"/<<3>>";
		return temp;
	}

	public String name="";
	public String time="";
	public String context="";
	public String lisenNum="";
	public String likeNum="";
	public String imgPath="";
	public String musicPath="";
	public String musicLegth="";
	public String musicMk="";
	
	public MsgTypeUtil(String name, String time, String context, String lisenNum, String likeNum, String imgPath,
			String musicPath,String musicLegth,String musicMk) {
		super();
		this.name = name;
		this.time = time;
		this.context = context;
		this.lisenNum = lisenNum;
		this.likeNum = likeNum;
		this.imgPath = imgPath;
		this.musicPath = musicPath;
		this.musicLegth=musicLegth;
		this.musicMk=musicMk;
	}
}
