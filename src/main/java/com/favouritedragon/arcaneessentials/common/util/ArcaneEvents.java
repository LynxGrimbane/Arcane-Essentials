package com.favouritedragon.arcaneessentials.common.util;

import com.favouritedragon.arcaneessentials.ArcaneEssentials;
import electroblob.wizardry.util.IElementalDamage;
import electroblob.wizardry.util.MagicDamage;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.favouritedragon.arcaneessentials.common.util.ArcaneEnums.WATER;

@Mod.EventBusSubscriber(modid = ArcaneEssentials.MODID)
public class ArcaneEvents {

	@SubscribeEvent
	public static void onHurtEvent(LivingHurtEvent event) {
		if (event.getSource() instanceof IElementalDamage) {
			if (((IElementalDamage) event.getSource()).getType() == WATER) {
				if (event.getEntity() instanceof EntityEnderman) {
					event.setAmount(event.getAmount() * 1.5F);
				}
				if (event.getEntity() instanceof EntityGuardian) {
					event.setAmount(event.getAmount() * 0.5F);
				}
			}
		}
	}
}