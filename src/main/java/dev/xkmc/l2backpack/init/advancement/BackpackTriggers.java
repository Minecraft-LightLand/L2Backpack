package dev.xkmc.l2backpack.init.advancement;

import dev.xkmc.l2backpack.init.L2Backpack;
import net.minecraft.resources.ResourceLocation;

public class BackpackTriggers {

	public static final SlotClickTrigger SLOT_CLICK = new SlotClickTrigger(new ResourceLocation(L2Backpack.MODID, "slot_click"));
	public static final BagInteractTrigger INTERACT = new BagInteractTrigger(new ResourceLocation(L2Backpack.MODID, "bag_interact"));
	public static final DrawerInteractTrigger DRAWER = new DrawerInteractTrigger(new ResourceLocation(L2Backpack.MODID, "drawer_interact"));
	public static final RemoteHopperTrigger REMOTE = new RemoteHopperTrigger(new ResourceLocation(L2Backpack.MODID, "remote_hopper"));
	public static final AnalogSignalTrigger ANALOG = new AnalogSignalTrigger(new ResourceLocation(L2Backpack.MODID, "analog_signal"));
	public static final SharedDriveTrigger SHARE = new SharedDriveTrigger(new ResourceLocation(L2Backpack.MODID, "shared_drive"));

	public static void register() {

	}

}
