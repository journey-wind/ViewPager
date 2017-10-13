package com.example.Record;

public class MsgTypeUtil {
	public String name;
	public String time;
	public String context;
	public String lisenNum;
	public String likeNum;
	public String imgPath;
	public String musicPath;
	
	public MsgTypeUtil(String name, String time, String context, String lisenNum, String likeNum, String imgPath,
			String musicPath) {
		super();
		this.name = name;
		this.time = time;
		this.context = context;
		this.lisenNum = lisenNum;
		this.likeNum = likeNum;
		this.imgPath = imgPath;
		this.musicPath = musicPath;
	}
}
