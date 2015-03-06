package com.imwg.imwgmotti;

import java.util.ArrayList;
import java.util.List;

public class Level {
	String title;
	List<Piece> pieces;
	int goal_piece;
	int[] goal = {-1,-1,-1,-1};
	
	public Level(){
		title = "";
		pieces = new ArrayList<Piece>();
		goal_piece = -1;
	}
	
	public Level(String title, List<Piece> pcs, int goal_piece, int[] goal){
		this.title = title;
		pieces = new ArrayList<Piece>();
    	for (Piece piece : pcs){
    		this.addPiece(piece);
    	}
    	this.goal_piece = goal_piece;
    	for (int i=0; i<goal.length; ++i){
    		this.goal[i] = goal[i];
    	}
	}
	
	public Level(Level l){
		this(l.title, l.pieces, l.goal_piece, l.goal);
	}
	
	public byte[] encodeToByte(){
		/*Structure:
			0  4  head(2 0x7F and 2 for all length) 
			4  54 title
			58 1  target piece id
			59 4  goal points
			63 1  length of pieces
			64 *  pieces (7 bytes per)
		*/
		
		String _title = new String(title);
		int length = Settings.LEVEL_TITLE_LENGTH + 3 + pieces.size() * 7;
		
		byte[] stream = new byte[length+2];
		
		stream[0] = '\177';
		stream[1] = '\177';
		stream[2] = (byte) (length / 128);
		stream[3] = (byte) (length % 128);
				
		int i=4, ti=0;
		byte[] titleb = _title.getBytes();
		for (; ti<titleb.length; ++i,++ti){
			stream[i] = (byte) titleb[ti];
		}
		
		stream[58] = (byte) goal_piece;
		
		for (i=59; i<63; ++i){
			stream[i] = (byte) goal[i-59];
		}
		stream[i++] = (byte) pieces.size();
		for (Piece piece : pieces){
			stream[i++] = (byte) piece.id;
			stream[i++] = (byte) piece.x;
			stream[i++] = (byte) piece.y;
			for (int j=0; j<4; ++j){
				stream[i++] = (byte) piece.shape[j];
			}
		}
		
		return stream;
	}
	
	static public Level decodeFromByte(byte[] stream){
		//Hint: no head 0x7F or length
		String title = new String(stream, 0, 54);
		
			List<Piece> pieces = new ArrayList<Piece>();
			
			int[] goal = {stream[55], stream[56], stream[57], stream[58]};
			
			int num = stream[59];
			final int pieces_length = num*7+60; 
			for (int i=60; i<pieces_length; i+=7){
				pieces.add(new Piece(stream[i], stream[i+1], stream[i+2], stream[i+3],
						stream[i+4], stream[i+5], stream[i+6]));
			}
			Level level = new Level(title, pieces, stream[54], goal);
			return level;
	}


	public Level cloneLevel(){
		Level level = new Level();
		level.title = new String(title);
		level.pieces = new ArrayList<Piece>();
    	for (Piece piece : pieces){
    		level.clonePiece(piece);
    	}
    	level.goal_piece = goal_piece;
    	for (int i=0; i<goal.length; ++i){
    		level.goal[i] = goal[i];
    	}
    	return level;
	}
	
	static public Level demoLevel(){
		List<Piece> pieces = new ArrayList<Piece>();
		
		pieces.add(new Piece(0, 1,0, 0, 1, 2, 3)); // Kisu
		pieces.add(new Piece(1, 0,0, 4,-1, 6,-1)); // Naru
		pieces.add(new Piece(2, 3,0, 5,-1, 7,-1)); // Haku
		pieces.add(new Piece(3, 1,2,12,13,-1,-1)); // Luka 
		pieces.add(new Piece(4, 0,2, 8,-1,10,-1)); // Kaito
		//pieces.add(new Piece(5, 3,2, 9,-1,11,-1)); // Meiko
		//pieces.add(new Piece(6, 0,4,14,-1,-1,-1)); // Miku
		//pieces.add(new Piece(7, 1,3,15,-1,-1,-1)); // Teto
		//pieces.add(new Piece(8, 2,3,16,-1,-1,-1)); // Len
		//pieces.add(new Piece(9, 3,4,17,-1,-1,-1)); // Rin
		
		int[] goal = {17, 18}; 
		
		return new Level("Demo Level", pieces, 0, goal);
	}
	
    public void addPiece(Piece p){
    	//p.id = pieces.size();
    	pieces.add(p);
    }
    public void clonePiece(Piece p){
    	//p.id = pieces.size();
    	pieces.add(new Piece(p));
    }
}
