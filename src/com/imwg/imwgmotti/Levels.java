package com.imwg.imwgmotti;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Levels {
	private List<Level> levels;
	
	public Levels(){
		levels = new ArrayList<Level>();
	}
	
	public void loadLevels(InputStream stream){
		try {
			int levelnum = stream.read();
			
			levelnum = levelnum * 128 + stream.read();
			
			byte[] buffer = new byte[1024];
			
			for (int index=0; index<levelnum; ++index){
				stream.read(buffer, 0, 4);
				if (buffer[0] == '\177' && buffer[1] == '\177'){
					int length = buffer[2]*128+buffer[3] - 2; // 2 bytes show length
					stream.read(buffer, 0, length);
					Level level = Level.decodeFromByte(buffer);
					levels.add(level);
				}
			}
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void loadLevels(File f){
		try {
			/*f.createNewFile();
			FileOutputStream ostream = new FileOutputStream(level_file);
			Level olevel = Level.demoLevel();
			ostream.write(olevel.encodeToByte());
			*/
			FileInputStream stream = new FileInputStream(f);
			loadLevels(stream);
			
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	public void saveLevels(File f){
		try {
			f.createNewFile();
			FileOutputStream stream = new FileOutputStream(f);
			
			int levelnum = levels.size();
			stream.write(levelnum / 128);
			stream.write(levelnum % 128);
			
			for (int index=0; index<levelnum; ++index){
				stream.write(levels.get(index).encodeToByte());
			}
			stream.close();
			
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	public Level getLevel(int i){
		// NOTE: i is started from 1
		if (i > 0){
			return levels.get(i-1); 
		}
		return null;
	}
	
	public int getLevelNum(){
		return levels.size();
	}
	
	public boolean addLevel(Level l){
		return levels.add(l);
	}
}
