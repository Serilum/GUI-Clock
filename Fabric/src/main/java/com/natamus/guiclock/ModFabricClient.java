package com.natamus.guiclock;

import net.fabricmc.api.ClientModInitializer;
import com.natamus.guiclock.util.Reference;
import com.natamus.collective.check.ShouldLoadCheck;

public class ModFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() { 
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		registerEvents();
	}
	
	private void registerEvents() {

	}
}
