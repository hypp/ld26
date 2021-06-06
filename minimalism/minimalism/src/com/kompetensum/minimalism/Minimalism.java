package com.kompetensum.minimalism;

import com.badlogic.gdx.Game;

public class Minimalism extends Game {
	
	@Override
	public void create() {
		// Settings.load
		Art.load();
		Sounds.load();
		setScreen(new IntroScreen(this));
		Sounds.music.setLooping(true);
		Sounds.music.setVolume(1.0f);
		Sounds.music.play();
	}

	@Override
	public void dispose() {
		super.dispose();
		getScreen().dispose();
		
		Sounds.music.stop();
	}

	@Override
	public void render() {
		super.render();
	}

}
