package com.first.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.first.game.MainClass;

import org.lwjgl.opengl.GL30;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("FirstGame");
		config.setAudioConfig(1024, 512, 9);
		//new Lwjgl3Application(new MainClass(), config);
		new Lwjgl3Application(new Main(), config);
	}
}