package com.imwg.imwgmotti;


public class Piece {
	int[] shape = new int[4];
	int x = 0, y = 0;
	int id = -1;
	
	public Piece(int s1, int s2, int s3, int s4){
		super();
		this.shape[0] = s1;
		this.shape[1] = s2;
		this.shape[2] = s3;
		this.shape[3] = s4;
	}
	
	public Piece(){
		this(1, 0, 0, 0);
	}
	
	public Piece(int x, int y, int s1, int s2, int s3, int s4){
		this(s1, s2, s3, s4);
		this.x = x;
		this.y = y;
	}
	
	public Piece(int x, int y){
		this(x,y,1,0,0,0);
	}
	
	public Piece(int id, int x, int y, int s1, int s2, int s3, int s4){
		this(x,y,s1,s2,s3,s4);
		this.id = id;
	}

	
	public Piece(Piece p) {
		this(p.id,p.x,p.y,p.shape[0],p.shape[1],p.shape[2],p.shape[3]);
	}

	static public int getXOffset(int shapeindex){
		if (shapeindex == 1 || shapeindex == 3)
			return 1;
		else
			return 0;
	}
	static public int getYOffset(int shapeindex){
		if (shapeindex == 2 || shapeindex == 3)
			return 1;
		else
			return 0;
	}

}
