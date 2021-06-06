package com.kompetensum.minimalism;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class Sounds {

	public static Array<Sound> hits = new Array<Sound>();
	public static Sound player_hit_wall;
	public static Music music;
	
	public static void load()
	{
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit1.ogg")));
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit2.ogg")));
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit3.ogg")));
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit4.ogg")));
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit6.ogg")));
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit7.ogg")));
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit8.ogg")));
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit9.ogg")));
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit10.ogg")));
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit11.ogg")));
		hits.add(Gdx.audio.newSound(Gdx.files.internal("data/hit12.ogg")));
		player_hit_wall = Gdx.audio.newSound(Gdx.files.internal("data/hit5.ogg"));
		music = Gdx.audio.newMusic(Gdx.files.internal("data/minimal.ogg"));
	}
	
}
