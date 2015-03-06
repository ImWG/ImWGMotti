package com.imwg.imwgmotti;

import android.os.Environment;

public class Settings {
	protected static int PIECE_SIZE = 80;
	
	final static int BOARD_W = 4;
	final static int BOARD_H = 5;
	
	protected static int BOARD_WIDTH = 320;
	protected static int BOARD_HEIGHT = 400;
	
	final static int FRAME_WIDTH = 800;
	final static int FRAME_HEIGHT = 600;
	
	final static int LEVEL_TITLE_LENGTH = 54;
	
	final static String DEFAULT_PIECESKIN = "Classic.png";

	public static final String DEFAULT_LEVELS = "levels.dat";
	
	public static final String EXTENAL_STORAGE = 
			Environment.getExternalStorageDirectory().getPath()+"/ImWG/Motti";
	public static final String EXTENAL_STORAGE_PIECESKINS =	EXTENAL_STORAGE+"/CustomPieceSkins";
	
	
	static void setPieceSize(int size){
		PIECE_SIZE = size;
		BOARD_WIDTH = size * BOARD_W;
		BOARD_HEIGHT = size * BOARD_H;
	}
}

