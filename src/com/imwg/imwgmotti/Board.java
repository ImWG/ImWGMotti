package com.imwg.imwgmotti;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;


public class Board extends SurfaceView{
	
	//Bitmap canvas_bitmap = Bitmap.createBitmap(Settings.BOARD_WIDTH, Settings.BOARD_HEIGHT, Bitmap.Config.ARGB_8888);
	private Drawable dw;
	static Bitmap[] pieceimages = new Bitmap[40];
	
	static SoundSpeaker soundspeaker;
	
	private Level level;
	private Level clevel;
	
	int current_piece = -1, tcurrent_piece;
	int current_x, current_y, current_x0, current_y0;
	int current_offx, current_offy;
	
	Paint normal_paint = new Paint();
	int goal_color = Color.RED; //new Color(255, 0, 0, 128);
	Paint goal_paint = new Paint();
	int highlight_color = Color.WHITE; //new Color(255, 255, 255, 128);
	Paint highlight_paint = new Paint();
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public Board(Context context){
		super(context);
		
		
		dw = new BitmapDrawable();
		this.setBackground(dw);
		
		soundspeaker = new SoundSpeaker(getContext());
		
		String pieceskin;
		pieceskin = MainActivity.pref.getString("pieceskin", Settings.DEFAULT_PIECESKIN);

		
		initPiecesImage(FileChoose.getPieceSkin(getContext(), pieceskin));
		
		goal_paint.setColor(goal_color);
		goal_paint.setAlpha(96);
		highlight_paint.setColor(highlight_color);
		highlight_paint.setAlpha(64);
		normal_paint.setColor(Color.WHITE);
		

		this.setOnTouchListener(new OnTouchListener(){
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction()){
					case MotionEvent.ACTION_DOWN:
						current_x = (int) event.getX();
						current_y = (int) event.getY();
			    		int m = (int) (_$$(event.getX(), true) / Settings.PIECE_SIZE);
			    		int n = (int) (_$$(event.getY(), false) / Settings.PIECE_SIZE);
			    		current_piece = getPieceAt(m, n);
			    		
			    		if (current_piece >= 0 && current_piece < clevel.pieces.size()){
			    			Piece piece = clevel.pieces.get(current_piece); 
			    			current_x0 = _$(piece.x * Settings.PIECE_SIZE, true);
			    			current_y0 = _$(piece.y * Settings.PIECE_SIZE, false);
			    			current_offx = (int) (event.getX() - current_x0);
				    		current_offy = (int) (event.getY() - current_y0);
			    		}
			    	//break;
					case MotionEvent.ACTION_MOVE:
						if (current_piece >= 0){
							current_x = (int) (event.getX()-current_offx);
							current_y = (int) (event.getY()-current_offy);
							if (Math.abs(current_x - current_x0) > Math.abs(current_y - current_y0)){
								current_y = current_y0;
							}else{
								current_x = current_x0;
							}
							//draw when dragging
							// slow down the rate to improve
							//if (current_x % 2 == 0 && current_y % 2 == 0){ 
							//highlight_paint.setAlpha((int)(Math.random()*255));
				    		
							if (MainActivity.animated)
								postInvalidate();
							//}
						}
					break;
					case MotionEvent.ACTION_UP:
						
			    		if (current_piece == -1)
			    			return true;
			    		
			    		int m2 = Math.round((float) _$$(current_x, true) / Settings.PIECE_SIZE);
			    		int n2 = Math.round((float) _$$(current_y, false) / Settings.PIECE_SIZE);
			    		
			    		Piece piece = clevel.pieces.get(current_piece);
			    		boolean moveable = true;
			    		
			    		int dm=0, dn=0;
			    		if (m2 > piece.x)
			    			dm = 1;
			    		else if (m2 < piece.x)
			    			dm = -1;
			    		else if (n2 > piece.y)
			    			dn = 1;
			    		else if (n2 < piece.y)
			    			dn = -1;
			    		
			    		int mt=piece.x-dm, nt=piece.y-dn; 
			    		do{
			    			mt+=dm; nt+=dn;
				    		for (int i=0; i<4; ++i){
				    			if (piece.shape[i] >= 0){
						    		if (getPieceAt(mt+Piece.getXOffset(i), nt+Piece.getYOffset(i)) > -1){
						    			moveable = false;
						    			break;
						    		}
				    			}
				    		}
			    		}while(!(mt==m2 && nt==n2));
			    		
			    		if (moveable){
			    			piece.x = m2;
			    			piece.y = n2;
			    			++MainActivity.movedsteps;
			    			MainActivity.updateLevelText();
			    		}
			    		current_piece = -1;
			    		
			    		postInvalidate();
			    		
		    			if (achieved()){
		    				soundspeaker.play("win");
			    			MainActivity.wintoast.show();
			    		}else{
			    			soundspeaker.play("move");
			    		}
					break;
				}
				return true;
			}

		});

	}
	
	
	@Override   
	protected void onDraw(Canvas canvas) {   

		canvas.restore();
        
        if (clevel.pieces.size() > 0){
        	for (Piece piece : clevel.pieces){
        		if (piece.id != current_piece)
        			drawPiece(canvas, piece);
        	}
        	
        	if(current_piece >= 0){
        		drawPiece(canvas, clevel.pieces.get(current_piece), _$$(current_x, true), _$$(current_y,false));
        	}
        }
        
        //Draw goals
        for (int i=0; i<4; ++i){
        	int left = clevel.goal[i]%Settings.BOARD_W*Settings.PIECE_SIZE;
        	int top = clevel.goal[i]/Settings.BOARD_W*Settings.PIECE_SIZE;
        	int right = left+Settings.PIECE_SIZE;
        	int bottom = top+Settings.PIECE_SIZE;
        	
        	drawRect(canvas, left, top, right, bottom, goal_paint);
        }
        
        //Just for Debug
        /*g2.setColor(Color.WHITE);
        for (int i=0;i<Settings.BOARD_W; ++i)
        	for (int j=0;j<Settings.BOARD_H; ++j)
        		g2.drawString(""+getPieceAt(i,j), i*Settings.PIECE_SIZE, j*Settings.PIECE_SIZE+20);
        
        g2.drawString(""+current_piece+","+current_x+","+current_y, 0, 300);
        g2.drawString(level.title, 150, 50);*/
    }
    
    
    public int getPieceAt(int x, int y){
    	if (x>=Settings.BOARD_W || x<0 || y<0 || y>=Settings.BOARD_H)
    		return 255;
    	
        for (Piece piece : clevel.pieces){
        	if (piece.id != current_piece){
        		for (int i=0; i<4; ++i){
        			if (piece.shape[i] >= 0)
        				if (piece.x+Piece.getXOffset(i)==x && piece.y+Piece.getYOffset(i)==y)
        					return piece.id;
        		}
        	}
        }
        return -1;
    }
    public int getPieceAt(int index){
    	return getPieceAt(index % Settings.BOARD_W, index / Settings.BOARD_W);
    }
    
    public boolean achieved(){
		boolean win = true;
		for (int i=0; i<clevel.goal.length; ++i)
			if (clevel.goal[i] >= 0)
				if (getPieceAt(clevel.goal[i]%Settings.BOARD_W, clevel.goal[i]/Settings.BOARD_W)
						!= clevel.goal_piece)
					win = false;
		return win;
    }
    
    public void loadLevel(Level l){
    	this.level = l;
    	this.clevel = l.cloneLevel();
    	postInvalidate();
    }
    
    public void restartLevel(){
    	for (int i=0; i<level.pieces.size(); ++i){
    		clevel.pieces.get(i).x = level.pieces.get(i).x;
    		clevel.pieces.get(i).y = level.pieces.get(i).y;
    	}
    	postInvalidate();
    }
    
    public Level exportLevel(){
    	return new Level(level);
    }
    
    
    private void drawTile(Canvas canvas, int tile, int x, int y){
    	if (tile>=pieceimages.length)
    		return;
    	
    	//drawRect(canvas, x, y, x+Settings.PIECE_SIZE, y+Settings.PIECE_SIZE, highlight_paint);
    	
    	Rect target = new Rect(_$(x, true), _$(y, false), _$(x+Settings.PIECE_SIZE, true), _$(y+Settings.PIECE_SIZE, false));
    	
    	canvas.drawBitmap(pieceimages[tile], 
    			new Rect(0,0,Settings.PIECE_SIZE,Settings.PIECE_SIZE), target,
    			normal_paint);
    }
    
    private void drawPiece(Canvas canvas, Piece p, int x0, int y0){
    	for (int i=0; i<4; ++i){
    		if (p.shape[i] >= 0){
    			int x = x0 + Piece.getXOffset(i)*Settings.PIECE_SIZE;
    			int y = y0 + Piece.getYOffset(i)*Settings.PIECE_SIZE;

	    		drawTile(canvas, p.shape[i], x, y);
	    		
	    		//Highlight
	    		if (p.id == level.goal_piece && p.id != current_piece){
	    			drawRect(canvas, x, y, x+Settings.PIECE_SIZE, y+Settings.PIECE_SIZE, highlight_paint);
        		}
    		}
    	}
    }
    private void drawPiece(Canvas canvas, Piece p){
    	drawPiece(canvas, p, p.x*Settings.PIECE_SIZE, p.y*Settings.PIECE_SIZE);
    }
    
    
    static public void initPiecesImage(Bitmap pimage){
		Settings.setPieceSize(pimage.getWidth() / 2);
		
		for (int i=0; i<pieceimages.length; ++i){
			int x = i % 2 * Settings.PIECE_SIZE;
			int y = i / 2 * Settings.PIECE_SIZE;
			
			//Just for fun
			if (y >= pimage.getHeight())
				y=0;
			
			int[] pixels = new int[Settings.PIECE_SIZE * Settings.PIECE_SIZE * 2];
			pimage.getPixels(pixels, 0, pimage.getWidth(), x, y, Settings.PIECE_SIZE, Settings.PIECE_SIZE);
			
			pieceimages[i] = Bitmap.createBitmap(Settings.PIECE_SIZE, Settings.PIECE_SIZE, Bitmap.Config.ARGB_8888);
			pieceimages[i].setPixels(pixels, 0, pimage.getWidth(), 0, 0, Settings.PIECE_SIZE, Settings.PIECE_SIZE);
		}

    }
    
    private int getActualPosition(int arg, boolean horizontal){
    	if (horizontal){
    		arg = arg * this.getWidth() / Settings.BOARD_WIDTH;
    	}else{
    		arg = arg * this.getHeight() / Settings.BOARD_HEIGHT;
    	}
    	return arg;
    }
    private int _$(int arg, boolean horizontal){
    	return getActualPosition(arg, horizontal);
    }
    
    private int getInvActualPosition(int arg, boolean horizontal){
    	if (horizontal){
    		arg = arg * Settings.BOARD_WIDTH / this.getWidth();
    	}else{
    		arg = arg * Settings.BOARD_HEIGHT / this.getHeight();
    	}
    	return arg;
    }
    private int _$$(int arg, boolean horizontal){
    	return getInvActualPosition(arg, horizontal);
    }
    private int _$$(float arg, boolean horizontal){
    	return getInvActualPosition((int)arg, horizontal);
    }
    
    private void drawRect(Canvas canvas, int left, int top, int right, int bottom, Paint paint){
    	canvas.drawRect(
    			getActualPosition(left, true),  getActualPosition(top, false), 
    			getActualPosition(right, true), getActualPosition(bottom, false),
    			paint);
    }
    
}

