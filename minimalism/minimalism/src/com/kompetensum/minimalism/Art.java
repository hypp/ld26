package com.kompetensum.minimalism;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Art {
	
	public static Texture intro;
	public static Texture gameover;

	public static void load()
	{
//		Texture.setEnforcePotImages(false);

		intro = new Texture(Gdx.files.internal("data/intro.png"));
		gameover = new Texture(Gdx.files.internal("data/gameover.png"));
	}
	
}
