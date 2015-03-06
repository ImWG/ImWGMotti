package com.imwg.imwgmotti;

import com.example.imwgmotti.R;

import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "ShowToast" })
public class MainActivity extends Activity {

	static RelativeLayout boardlayout;
	static TextView levelinfo;
	
	static Board board;
	static Levels levels;
	static int current_level;
	static int movedsteps;
	
	static Toast wintoast;
	static Thread flashthread;
	
	static SharedPreferences pref;
	static boolean animated;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Load Preferences
		pref = getSharedPreferences("vocaloidmotti", Context.MODE_PRIVATE);
		current_level = MainActivity.pref.getInt("currentlevel", 1);
		SoundSpeaker.enabled = pref.getBoolean("sound", true);
		animated = pref.getBoolean("animate", true);
		
		setContentView(R.layout.activity_main);
		
		boardlayout = (RelativeLayout) findViewById(R.id.board_layout); 
		boardlayout.removeAllViews();
		
		// Create the Board
		board = new Board(this);
		boardlayout.addView(board);
		RelativeLayout.LayoutParams params =
				new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		board.setLayoutParams(params);
		
		levelinfo = (TextView) findViewById(R.id.levelinfo);
		
		wintoast = Toast.makeText(this, "ÄãÓ®ÁË", Toast.LENGTH_LONG);

		
		// Load Levels
		levels = new Levels();
		//levels.addLevel(Level.demoLevel());
		levels.loadLevels(FileChoose.getLevels(this, ""));
		board.loadLevel(levels.getLevel(current_level));
		updateLevelText();
		
		
		findViewById(R.id.prevbutton).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (current_level > 1){
					gotoLevel(current_level-1);
					Board.soundspeaker.play("swap");
				}
			}
		});
		
		findViewById(R.id.nextbutton).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if (current_level < levels.getLevelNum()){
					gotoLevel(current_level+1);
					Board.soundspeaker.play("swap");
				}
			}
		});
		
		findViewById(R.id.restartbutton).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				restartLevel();
				Board.soundspeaker.play("swap");
			}
		});
	
		// Listener allowing player to jump to specific level
		levelinfo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				FileChoose.buildLevelSelector(MainActivity.this).show();
			}
		});
	
		
		// This is used for flash the main piece; 
		flashthread = new Thread(new Runnable(){
			final int MAXALPHA = 192;
			int alpha = MAXALPHA;
			
			@Override
			public void run(){
				try {
					while (MainActivity.this != null){
						if (MainActivity.animated){
							alpha = MAXALPHA;
							do{
								Thread.sleep(50);
								board.highlight_paint.setAlpha(alpha);
								board.postInvalidate();
								alpha -= 16;
							}while (alpha >= 0);
							
							while(board.current_piece >= 0);
							
							Thread.sleep(1000);
							alpha = 0;
							
							do{
								Thread.sleep(50);
								board.highlight_paint.setAlpha(alpha);
								board.postInvalidate();
								alpha += 16;
							}while (alpha < MAXALPHA);
						}else{
							board.highlight_paint.setAlpha(MAXALPHA);
							Thread.sleep(1000);
						}
						
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		flashthread.start();
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		menu.findItem(R.id.setpieceskin).setOnMenuItemClickListener(new OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				FileChoose.build(MainActivity.this).show();
				return false;
			}
		});
		
		final MenuItem playsound = menu.findItem(R.id.playsound); 
		playsound.setTitle(SoundSpeaker.enabled ? R.string.menu_playsound : R.string.menu_playsound_ );
		playsound.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				SoundSpeaker.enabled = !SoundSpeaker.enabled;
				
				playsound.setTitle(SoundSpeaker.enabled ? R.string.menu_playsound : R.string.menu_playsound_ );

				Editor editor = MainActivity.pref.edit();
				editor.putBoolean("sound", SoundSpeaker.enabled);
				editor.commit();
				return false;
			}
		});
		
		final MenuItem animate = menu.findItem(R.id.animate); 
		animate.setTitle(animated ? R.string.menu_animate : R.string.menu_animate_);
		animate.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				animated = !animated;
				
				animate.setTitle(animated ? R.string.menu_animate : R.string.menu_animate_);
				
				Editor editor = MainActivity.pref.edit();
				editor.putBoolean("animate", animated);
				editor.commit();
				return false;
			}
		});
		
		return true;
	}
	
	//Adjusting the board, to make pieces square.
	@Override
	public void onWindowFocusChanged(boolean hasFocus){ 
		float width = boardlayout.getWidth();
		float height = boardlayout.getHeight();
		float pwidth = width / Settings.BOARD_W;
		float pheight = height / Settings.BOARD_H;
		//boardlayout.setLayoutParams(new RelativeLayout.LayoutParams(200, 250));
		if (pwidth > pheight){
			boardlayout.setLayoutParams(new RelativeLayout.LayoutParams((int)pheight*Settings.BOARD_W, (int)height));
		}else{
			float height1 = pwidth*Settings.BOARD_H;
			boardlayout.setLayoutParams(new RelativeLayout.LayoutParams((int)width, (int)height1));
			boardlayout.setY(boardlayout.getY()+(height - height1)/2);
		}
		
		int residue_width = findViewById(R.id.bottom_layout).getWidth() - findViewById(R.id.prevbutton).getWidth()
				- findViewById(R.id.nextbutton).getWidth() - findViewById(R.id.restartbutton).getWidth();
		
		levelinfo.setLayoutParams(new LinearLayout.LayoutParams(residue_width, levelinfo.getHeight()));
		
	}
	
	static void updateLevelText(){
		levelinfo.setText(String.format("%03d/%03d", current_level, levels.getLevelNum()));
	}
	
	static void gotoLevel(int levelid){
		current_level = levelid;
		movedsteps = 0;
		board.loadLevel(levels.getLevel(levelid));
		updateLevelText();
		Editor editor = MainActivity.pref.edit();
		editor.putInt("currentlevel", current_level);
		editor.commit();
	}
	static void restartLevel(){
		board.restartLevel();
		movedsteps = 0;
	}

}
