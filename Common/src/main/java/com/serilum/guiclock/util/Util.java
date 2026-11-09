package com.serilum.guiclock.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Util {
	public static int getEffectIconRowsHeight(Player player) {
		boolean hasBeneficialIcon = false;
		boolean hasHarmfulIcon = false;

		for (MobEffectInstance effect : player.getActiveEffects()) {
			if (!effect.showIcon()) {
				continue;
			}

			if (effect.getEffect().getCategory().equals(MobEffectCategory.BENEFICIAL)) {
				hasBeneficialIcon = true;
			}
			else {
				hasHarmfulIcon = true;
			}
		}

		if (hasBeneficialIcon && hasHarmfulIcon) {
			return 50;
		}
		else if (hasBeneficialIcon) {
			return 25;
		}

		return 0;
	}

	public static String getDayString(Level level) {
		long daysPlayed = level.getDayTime() / 24000;
		return Component.translatable("collective.guiclock.gui.day", daysPlayed).getString();
	}
}
