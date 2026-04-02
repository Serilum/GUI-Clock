package com.natamus.guiclock.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.guiclock.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static boolean mustHaveClockInInventoryForGameTime = true;
	@Entry public static boolean mustHaveClockInInventoryForRealTime = true;
	@Entry public static boolean lowerClockWhenPlayerHasEffects = true;
	@Entry public static boolean _24hourformat = true;
	@Entry public static boolean showOnlyMinecraftClockIcon = false;
	@Entry public static boolean showBothTimes = false;
	@Entry public static boolean showRealTime = false;
	@Entry public static boolean showRealTimeSeconds = false;
	@Entry public static boolean showDaysPlayedWorld = true;
	@Entry public static boolean clockPositionIsLeft = false;
	@Entry public static boolean clockPositionIsCenter = false;
	@Entry public static boolean clockPositionIsRight = true;
	@Entry(min = 0, max = 3000) public static int clockHeightOffset = 5;
	@Entry(min = -3000, max = 3000) public static int clockWidthOffset = 0;
	@Entry public static boolean drawTextShadow = true;
	@Entry(min = 0, max = 255) public static int RGB_R = 255;
	@Entry(min = 0, max = 255) public static int RGB_G = 255;
	@Entry(min = 0, max = 255) public static int RGB_B = 255;

	public static void initConfig() {
		configMetaData.put("mustHaveClockInInventoryForGameTime", Arrays.asList(
			"When enabled, will only show the game time when a clock is present in the inventory."
		));
		configMetaData.put("mustHaveClockInInventoryForRealTime", Arrays.asList(
			"When enabled, will only show the real time when a clock is present in the inventory."
		));
		configMetaData.put("lowerClockWhenPlayerHasEffects", Arrays.asList(
			"Whether the clock in the GUI should be lowered when the player has potion effects to prevent overlap."
		));
		configMetaData.put("_24hourformat", Arrays.asList(
			"Sets the format of the clock to the 24-hour format."
		));
		configMetaData.put("showOnlyMinecraftClockIcon", Arrays.asList(
			"When enabled, shows the clock item icon instead of a clock with numbers."
		));
		configMetaData.put("showBothTimes", Arrays.asList(
			"Show both in-game time and real local time."
		));
		configMetaData.put("showRealTime", Arrays.asList(
			"Show actual local time instead of in-game time."
		));
		configMetaData.put("showRealTimeSeconds", Arrays.asList(
			"Show the seconds in the clock."
		));
		configMetaData.put("showDaysPlayedWorld", Arrays.asList(
			"Show the days played in the world."
		));
		configMetaData.put("clockPositionIsLeft", Arrays.asList(
			"Places the GUI clock on the left."
		));
		configMetaData.put("clockPositionIsCenter", Arrays.asList(
			"Places the GUI clock in the middle."
		));
		configMetaData.put("clockPositionIsRight", Arrays.asList(
			"Places the GUI clock on the right."
		));
		configMetaData.put("clockHeightOffset", Arrays.asList(
			"The vertical offset (y coord) for the Clock. This determines how far down the time should be on the screen. Can be changed to prevent GUIs from overlapping."
		));
		configMetaData.put("clockWidthOffset", Arrays.asList(
			"The horizontal offset (x coord) for the Clock."
		));
		configMetaData.put("drawTextShadow", Arrays.asList(
			"If the text displayed should have a shadow drawn below it."
		));
		configMetaData.put("RGB_R", Arrays.asList(
			"The red RGB value for the clock text."
		));
		configMetaData.put("RGB_G", Arrays.asList(
			"The green RGB value for the clock text."
		));
		configMetaData.put("RGB_B", Arrays.asList(
			"The blue RGB value for the clock text."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}