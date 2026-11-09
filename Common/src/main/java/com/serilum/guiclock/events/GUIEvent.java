package com.serilum.guiclock.events;

import com.mojang.blaze3d.platform.Window;
import com.natamus.collective.functions.ColourFunctions;
import com.natamus.collective.functions.GUIFunctions;
import com.natamus.collective.functions.PlayerFunctions;
import com.natamus.collective.functions.StringFunctions;
import com.serilum.guiclock.config.ConfigHandler;
import com.serilum.guiclock.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.awt.*;

public class GUIEvent {
	private static final Minecraft mc = Minecraft.getInstance();
	
	public static void renderOverlay(GuiGraphics guiGraphics, float tickDelta) {
		if (GUIFunctions.shouldHideGUI()) {
			return;
		}

		boolean gametimeb = ConfigHandler.mustHaveClockInInventoryForGameTime;
		boolean realtimeb = ConfigHandler.mustHaveClockInInventoryForRealTime;
		boolean found = true;
		
		if (gametimeb || realtimeb) {
			found = PlayerFunctions.hasItemInInventory(mc.player, Items.CLOCK);
		}

		Font fontRenderer = mc.font;
		Window scaled = mc.getWindow();
		int width = scaled.getGuiScaledWidth();
		
		int heightoffset = ConfigHandler.clockHeightOffset;
		if (heightoffset < 5) {
			heightoffset = 5;
		}
		
		if (ConfigHandler.lowerClockWhenPlayerHasEffects) {
			int effectIconRowsHeight = Util.getEffectIconRowsHeight(mc.player);
			if (heightoffset < effectIconRowsHeight) {
				heightoffset += effectIconRowsHeight;
			}
		}
		
		int xcoord;
		int daycoord;
		if (ConfigHandler.showOnlyMinecraftClockIcon) {
			if (gametimeb) {
				if (!found) {
					return;
				}
			}
			
			if (ConfigHandler.clockPosition.equals("Left")) {
				xcoord = 20;
			}
			else if (ConfigHandler.clockPosition.equals("Center")) {
				xcoord = (width/2) - 8;
			}
			else {
				xcoord = width - 20;
			}
			
			xcoord += ConfigHandler.clockWidthOffset;

			guiGraphics.renderItem(new ItemStack(Items.CLOCK), xcoord, heightoffset);
		}
		else {
			String time;
			String realtime = StringFunctions.getPCLocalTime(ConfigHandler._24hourformat, ConfigHandler.showRealTimeSeconds);
			if (ConfigHandler.showBothTimes) {
				if (gametimeb && realtimeb) {
					if (!found) {
						return;
					}
					time = getGameTime() + " | " + realtime;
				}
				else if (!found && gametimeb) {
					time = realtime;
				}
				else if (!found && realtimeb) {
					time = getGameTime();
				}
				else {
					time = getGameTime() + " | " + realtime;
				}
			}
			else if (ConfigHandler.showRealTime) {
				if (realtimeb) {
					if (!found) {
						return;
					}
				}
				time = realtime;
			}
			else {
				if (gametimeb) {
					if (!found) {
						return;
					}
				}
				time = getGameTime();
			}
			
			if (time.equals("")) {
				return;
			}

			boolean mayShowGameTime = found || !gametimeb;
			String dayString = "";
			if (ConfigHandler.showDaysPlayedWorld && mayShowGameTime) {
				dayString = Util.getDayString(mc.level);
			}

			int stringWidth = fontRenderer.width(time);
			int dayStringWidth = fontRenderer.width(dayString);
			
			Color colour = new Color(ColourFunctions.getRgbColour(ConfigHandler.RGB));
			
			if (ConfigHandler.clockPosition.equals("Left")) {
				xcoord = 5;
				daycoord = 5;
			}
			else if (ConfigHandler.clockPosition.equals("Center")) {
				xcoord = (width/2) - (stringWidth/2);
				daycoord = (width/2) - (dayStringWidth/2);
			}
			else {
				xcoord = width - stringWidth - 5;
				daycoord = width - dayStringWidth - 5;
			}
			
			xcoord += ConfigHandler.clockWidthOffset;
			daycoord += ConfigHandler.clockWidthOffset;

			int rgb = colour.getRGB();
			drawText(fontRenderer, guiGraphics, time, xcoord, heightoffset, rgb, ConfigHandler.drawTextShadow);
			if (!dayString.isEmpty()) {
				drawText(fontRenderer, guiGraphics, dayString, daycoord, heightoffset+10, rgb, ConfigHandler.drawTextShadow);
			}
		}
	}

	private static void drawText(Font font, GuiGraphics guiGraphics, String content, int x, int y, int rgb, boolean drawShadow) {
		guiGraphics.drawString(font, Component.literal(content), x, y, rgb, drawShadow);
	}
	
	private static String getGameTime() {
		long time;
		long gametime = mc.level.getDayTime() % 24000;

		if (gametime >= 18000) {
			time = gametime-18000;
		}
		else {
			time = 6000+gametime;
		}
		
		String suffix = "";
		if (!ConfigHandler._24hourformat) {
			if (time >= 13000) {
				time = time - 12000;
				suffix = " PM";
			}
			else {
				if (time >= 12000) {
					suffix = " PM";
				}
				else {
					suffix = " AM";
					if (time <= 999) {
						time += 12000;
					}
				}
			}
		}
		
		StringBuilder stringtime = new StringBuilder(time / 10 + "");
		for (int n = stringtime.length(); n < 4; n++) {
			stringtime.insert(0, "0");
		}

		String[] strsplit = stringtime.toString().split("");
		
		int minutes = (int)Math.floor(Double.parseDouble(strsplit[2] + strsplit[3])/100*60);
		minutes -= minutes % ConfigHandler.minuteStep;

		String sm = minutes + "";
		if (minutes < 10) {
			sm = "0" + minutes;
		}
		
		if (!ConfigHandler._24hourformat && strsplit[0].equals("0")) {
			stringtime = new StringBuilder(strsplit[1] + ":" + sm.charAt(0) + sm.charAt(1));
		}
		else {
			stringtime = new StringBuilder(strsplit[0] + strsplit[1] + ":" + sm.charAt(0) + sm.charAt(1));
		}
		
		return stringtime + suffix;
	}
}