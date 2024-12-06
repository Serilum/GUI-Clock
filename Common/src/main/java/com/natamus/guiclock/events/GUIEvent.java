package com.natamus.guiclock.events;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.natamus.collective.functions.StringFunctions;
import com.natamus.guiclock.config.ConfigHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.awt.*;
import java.util.Collection;

public class GUIEvent {
	private static final Minecraft mc = Minecraft.getInstance();
	private static String daystring = "";
	
	public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		if (mc.gui.getDebugOverlay().showDebugScreen()) {
			return;
		}

		boolean gametimeb = ConfigHandler.mustHaveClockInInventoryForGameTime;
		boolean realtimeb = ConfigHandler.mustHaveClockInInventoryForRealTime;
		boolean found = true;
		
		if (gametimeb || realtimeb) {
			found = mc.player.getOffhandItem().getItem().equals(Items.CLOCK);
			if (!found) {
				Inventory inv = mc.player.getInventory();
				for (int n = 0; n <= 35; n++) {
					if (inv.getItem(n).getItem().equals(Items.CLOCK)) {
						found = true;
						break;
					}
				}
			}
		}

		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		
		Font fontRenderer = mc.font;
		Window scaled = mc.getWindow();
		int width = scaled.getGuiScaledWidth();
		
		int heightoffset = ConfigHandler.clockHeightOffset;
		if (heightoffset < 5) {
			heightoffset = 5;
		}
		
		if (ConfigHandler.lowerClockWhenPlayerHasEffects) {
			Collection<MobEffectInstance> activeeffects = mc.player.getActiveEffects();
			if (activeeffects.size() > 0) {
				boolean haspositive = false;
				boolean hasnegative = false;
				for (MobEffectInstance effect : activeeffects) {
					if (effect.isVisible()) {
						if (effect.getEffect().value().getCategory().equals(MobEffectCategory.BENEFICIAL)) {
							haspositive = true;
						}
						else {
							hasnegative = true;
						}

						if (haspositive && hasnegative) {
							break;
						}
					}
				}

				if (hasnegative && haspositive) {
					heightoffset += 50;
				}
				else if (haspositive && !hasnegative) {
					heightoffset += 25;
				}
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
			
			if (ConfigHandler.clockPositionIsLeft) {
				xcoord = 20;
			}
			else if (ConfigHandler.clockPositionIsCenter) {
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
			
			int stringWidth = fontRenderer.width(time);
			int daystringWidth = fontRenderer.width(daystring);
			
			Color colour = new Color(ConfigHandler.RGB_R, ConfigHandler.RGB_G, ConfigHandler.RGB_B, 255);
			
			if (ConfigHandler.clockPositionIsLeft) {
				xcoord = 5;
				daycoord = 5;
			}
			else if (ConfigHandler.clockPositionIsCenter) {
				xcoord = (width/2) - (stringWidth/2);
				daycoord = (width/2) - (daystringWidth/2);
			}
			else {
				xcoord = width - stringWidth - 5;
				daycoord = width - daystringWidth - 5;
			}
			
			xcoord += ConfigHandler.clockWidthOffset;
			daycoord += ConfigHandler.clockWidthOffset;

			int rgb = colour.getRGB();
			drawText(fontRenderer, guiGraphics, time, xcoord, heightoffset, rgb, ConfigHandler.drawTextShadow);
			if (!daystring.equals("")) {
				drawText(fontRenderer, guiGraphics, daystring, daycoord, heightoffset+10, rgb, ConfigHandler.drawTextShadow);
			}
		}
		
		poseStack.popPose();
	}

	private static void drawText(Font font, GuiGraphics guiGraphics, String content, int x, int y, int rgb, boolean drawShadow) {
		guiGraphics.drawString(font, Component.literal(content), x, y, rgb, drawShadow);
	}
	
	private static String getGameTime() {
		int time;
		int gametime = (int)mc.level.getDayTime();
		int daysplayed = 0;
		
		while (gametime >= 24000) {
			gametime-=24000;
			daysplayed += 1;
		}
		
		if (ConfigHandler.showDaysPlayedWorld) {
			daystring = "Day " + daysplayed;
		}

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