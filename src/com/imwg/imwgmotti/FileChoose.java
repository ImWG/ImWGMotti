package com.imwg.imwgmotti;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

// A virtual class
// We use this to load skins, levels, etc.
public class FileChoose {
	
	static AlertDialog.Builder build(final Context context){
		
		final boolean extenal;
		final File custom_path = new File(Settings.EXTENAL_STORAGE_PIECESKINS);
		if (!custom_path.exists())
			extenal = custom_path.mkdirs();
		else
			extenal = true;
				
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);

		final AssetManager assetmanager = context.getAssets();
		
		builder.setTitle("选择一种板块皮肤");
		try {
			final String[] offical_skins = assetmanager.list("pieceskins");
			final String[] skins;
			if (extenal){
				final String[] skins2 = custom_path.list();
				String[] skins1 = new String[offical_skins.length+skins2.length];
				int i=0;
				for (; i<offical_skins.length; ++i)
					skins1[i] = "*"+offical_skins[i];
				
				for (i=0; i<skins2.length; ++i)
					skins1[i+offical_skins.length] = skins2[i];
				
				skins = skins1.clone();
			}else{
				skins = offical_skins.clone();
			}
			
			builder.setItems(skins, new DialogInterface.OnClickListener() {  
			   
				@SuppressLint("NewApi")
				public void onClick(DialogInterface dialog, int item) {
					Editor editor = MainActivity.pref.edit();
					String skinname = skins[item]; 

					editor.putString("pieceskin", skins[item]);
					editor.commit();
					Bitmap image = getPieceSkin(context, skinname);
					Board.initPiecesImage(image);
			    }  
			});  
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder;
	}
	
	static Bitmap getPieceSkin(Context context, String skinname, AssetManager assetmanager){
		try {
			InputStream is = null;
			if (skinname.charAt(0) == '*'){
				// Official skins
				String[] skins = assetmanager.list("pieceskins");
				String skinname1 = skinname.substring(1);
				for (int i=0; i<skins.length; ++i){
					if (skinname1.equals(skins[i])){
						is = assetmanager.open("pieceskins/"+skinname1); break;
					}
				}
			}else{
				// Custom - in external storage
				File skinfile = new File(Settings.EXTENAL_STORAGE_PIECESKINS+"/"+skinname);
				if (skinfile.exists())
					is = new FileInputStream(skinfile);
			}
			if (is == null)
				is = assetmanager.open("pieceskins/"+Settings.DEFAULT_PIECESKIN);
			
			return BitmapFactory.decodeStream(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	static Bitmap getPieceSkin(Context context, String skinname){
		return getPieceSkin(context, skinname, context.getAssets());
	}
	
	
	static InputStream getLevels(Context context, String levelname){
		AssetManager assetmanager = context.getAssets();
		try {
			InputStream is = null;
			String[] levels = assetmanager.list("levels");
			for (int i=0; i<levels.length; ++i){
				if (levelname.equals(levels[i])){
					is = assetmanager.open("levels/"+levelname);	break;
				}
			}
			if (is == null)
				is = assetmanager.open("levels/"+Settings.DEFAULT_LEVELS);
			
			return is;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	static AlertDialog.Builder buildLevelSelector(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		
		builder.setTitle("请输入要切换的关卡(1~"+MainActivity.levels.getLevelNum()+")");
		
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		final EditText et = new EditText(context);
		et.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		
		Button bt = new Button(context);
		bt.setText("Go");
		bt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				try{
					int levelnum= Integer.parseInt(et.getText().toString());
					if (levelnum < 1)
						levelnum = 1;
					else if (levelnum > MainActivity.levels.getLevelNum())
						levelnum = MainActivity.levels.getLevelNum();
					
					MainActivity.gotoLevel(levelnum);
				}catch(NumberFormatException e){
				}
			}
		});
		
		ll.addView(et);
		ll.addView(bt);
		builder.setView(ll);
		
		return builder;
	}
	 
	

}
