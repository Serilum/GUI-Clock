package com.natamus.guiclock;

import com.natamus.collective.globalcallbacks.CollectiveGuiCallback;
import com.natamus.guiclock.config.ConfigHandler;
import com.natamus.guiclock.events.GUIEvent;

public class ModCommon {

	public static void init() {
		ConfigHandler.initConfig();
		load();
	}

	private static void load() {
		CollectiveGuiCallback.ON_GUI_RENDER.register(((guiGraphics, tickDelta) -> {
			GUIEvent.renderOverlay(guiGraphics, tickDelta);
		}));
	}
}