package com.natamus.guiclock.events;

import com.mojang.blaze3d.platform.Window;
import org.joml.Matrix3x2fStack;
import com.natamus.collective.functions.GUIFunctions;
import com.natamus.collective.functions.StringFunctions;
import com.natamus.guiclock.config.ConfigHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
	
	public static void renderOverlay(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
		if (GUIFunctions.shouldHideGUI()) {
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

		Matrix3x2fStack matrixStack = guiGraphics.pose();
		matrixStack.pushMatrix();
		
		Font fontRenderer = mc.font;
		Window scaled = mc.getWindow();
		int width = scaled.getGuiScaledWidth();
		
		int heightoffset = ConfigHandler.clockHeightOffset;
		if (heightoffset < 5) {
			heightoffset = 5;
		}
		
		if (ConfigHandler.lowerClockWhenPlayerHasEffects) {
			Collection<MobEffectInstance> activeeffects = mc.player.getActiveEffects();
			if (!activeeffects.isEmpty()) {
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

			guiGraphics.item(new ItemStack(Items.CLOCK), xcoord, heightoffset);
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
			
			if (time.isEmpty()) {
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
			if (!daystring.isEmpty()) {
				drawText(fontRenderer, guiGraphics, daystring, daycoord, heightoffset+10, rgb, ConfigHandler.drawTextShadow);
			}
		}
		
		matrixStack.popMatrix();
	}

	private static void drawText(Font font, GuiGraphicsExtractor guiGraphics, String content, int x, int y, int rgb, boolean drawShadow) {
		guiGraphics.text(font, Component.literal(content), x, y, rgb, drawShadow);
	}
	
	private static String getGameTime() {
		int time;
		int gameTime = (int)mc.level.getOverworldClockTime();
		int daysPlayed = 0;
		
		while (gameTime >= 24000) {
			gameTime-=24000;
			daysPlayed += 1;
		}
		
		if (ConfigHandler.showDaysPlayedWorld) {
			daystring = "Day " + daysPlayed;
		} else {
			daystring = "";
		}

		if (gameTime >= 18000) {
			time = gameTime-18000;
		}
		else {
			time = 6000+gameTime;
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
		
		StringBuilder stringTime = new StringBuilder(time / 10 + "");
		for (int n = stringTime.length(); n < 4; n++) {
			stringTime.insert(0, "0");
		}

		String[] strSplit = stringTime.toString().split("");
		
		int minutes = (int)Math.floor(Double.parseDouble(strSplit[2] + strSplit[3])/100*60);
		String sm = minutes + "";
		if (minutes < 10) {
			sm = "0" + minutes;
		}
		
		if (!ConfigHandler._24hourformat && strSplit[0].equals("0")) {
			stringTime = new StringBuilder(strSplit[1] + ":" + sm.charAt(0) + sm.charAt(1));
		}
		else {
			stringTime = new StringBuilder(strSplit[0] + strSplit[1] + ":" + sm.charAt(0) + sm.charAt(1));
		}
		
		return stringTime + suffix;
	}
}