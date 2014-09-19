package mods.immibis.am2.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import mods.immibis.am2.tileentity.TileAM2Cutter;
import mods.immibis.core.api.util.BaseContainer;

public class ContainerCutter extends BaseContainer<TileAM2Cutter> {

	public ContainerCutter(EntityPlayer player, TileAM2Cutter inv) {
		super(player, inv);
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, 8+18*x, 142));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x + y*9 + 9, 8+18*x, 84+18*y));
		
		addSlotToContainer(new Slot((IInventory)inv, TileAM2Cutter.SLOT_IN, 56, 17));
		addSlotToContainer(new Slot((IInventory)inv, TileAM2Cutter.SLOT_OUT, 116, 35));
		addSlotToContainer(new Slot((IInventory)inv, TileAM2Cutter.SLOT_BATTERY, 56, 53));
	}
	
	public int energyPixels;
	public int progressPixels;
	public int waterPixels;
	public int speed;
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if(!inv.getWorldObj().isRemote) {
			setProgressBar(0, energyPixels = inv.getScaledEnergy(14));
			setProgressBar(1, progressPixels = inv.getScaledProgress(23));
			setProgressBar(2, waterPixels = inv.getScaledWater(48));
			setProgressBar(3, speed = inv.speed);
		}
	}
	
	@Override
	public void updateProgressBar(int par1, int par2) {
		if(par1 == 0) energyPixels = par2;
		if(par1 == 1) progressPixels = par2;
		if(par1 == 2) waterPixels = par2;
		if(par1 == 3) speed = par2;
	}

}
