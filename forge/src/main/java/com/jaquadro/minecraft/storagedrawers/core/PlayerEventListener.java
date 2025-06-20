package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IPortable;
import com.jaquadro.minecraft.storagedrawers.config.ModCommonConfig;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeRemote;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StorageDrawers.MOD_ID)
public class PlayerEventListener
{
	private static void applyDebuff(Player plr)
	{
		// slowness IV for 5 seconds
		plr.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 3, true, true));
	}

	@SubscribeEvent
	public static void onPlayerPickup(EntityItemPickupEvent event) {
		if (!ModCommonConfig.INSTANCE.GENERAL.heavyDrawers.get())
			return;

		checkItemDebuf(event.getItem().getItem(), event.getEntity());
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		// every 3 seconds, in the END phase
		if(event.phase != Phase.END || event.player.tickCount % 60 != 0)
			return;

		if (event.side == LogicalSide.SERVER)
			ItemUpgradeRemote.validateInventory(event.player.getInventory(), event.player.level());

		if (!ModCommonConfig.INSTANCE.GENERAL.heavyDrawers.get())
			return;

		// TODO: What is getAllSlots
		//for(var s : event.getEntity().getAllSlots()) {
		//	if (checkItemDebuf(s, event.getEntity()))
		//		return;
		//}

		Inventory inv = event.player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			if (checkItemDebuf(inv.getItem(i), event.player))
				return;
		}
	}

	private static boolean checkItemDebuf (ItemStack stack, Player player) {
		Item item = stack.getItem();
		if (item instanceof IPortable ip) {
			if (ip.isHeavy(player.level().registryAccess(), stack)) {
				applyDebuff(player);
				return true;
			}
		}

		return false;
	}
}
