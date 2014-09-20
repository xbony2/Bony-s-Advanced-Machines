package mods.immibis.am2.container;

import mods.immibis.am2.tileentity.AM2BaseProcessorTileEntity;
import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AM2Container extends BaseContainer<AM2BaseProcessorTileEntity> {
	public AM2Container(EntityPlayer ply, AM2BaseProcessorTileEntity inv) {
		super(ply, inv);
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(ply.inventory, x, 8 + x*18, 142));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(ply.inventory, x + y*9 + 9, 8 + x*18, 84 + y*18));
		
		addSlotToContainer(new Slot((IInventory)inv, 0, 56, 17)); // input slot
		addSlotToContainer(new Slot((IInventory)inv, 1, 56, 53)); // battery slot
		
		int firstX = inv.getNumOutputSlots() > 1 ? 114 : 116;
		
		for(int k = 0; k < inv.getNumOutputSlots(); k++)
			addSlotToContainer(new Slot((IInventory)inv, 2+k, firstX + k*16, 35));
	}
	
	public int progressPixels;
	public int energyPixels;
	public int speed;
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		setProgressBar(0, progressPixels = inv.getScaledProgress(inv.getProgressBarWidth()));
		setProgressBar(1, energyPixels = inv.getScaledEnergy(13));
		setProgressBar(2, speed = inv.getSpeed());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		switch(par1) {
		case 0: progressPixels = par2; break;
		case 1: energyPixels = par2; break;
		case 2: speed = par2; break;
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(int slot) {
		if(slot < 36) {
			// inventory -> machine
			ItemStack stack = ((Slot)inventorySlots.get(slot)).getStack();
			if(stack == null)
				return null;
			
			boolean isInput = inv.canInsert(0, stack);
			boolean isBattery = inv.canInsert(1, stack);
			
			if(isBattery && isInput) {
				// input first, then battery
				if(!BasicInventory.mergeStackIntoRange(player.inventory, (IInventory)inv, slot, 0, 1))
					BasicInventory.mergeStackIntoRange(player.inventory, (IInventory)inv, slot, 1, 2);
			} else if(isBattery) {
				// battery first, then input
				if(!BasicInventory.mergeStackIntoRange(player.inventory, (IInventory)inv, slot, 1, 2))
					BasicInventory.mergeStackIntoRange(player.inventory, (IInventory)inv, slot, 0, 1);
			} else {
				// input (even if not a valid input)
				BasicInventory.mergeStackIntoRange(player.inventory, (IInventory)inv, slot, 0, 1);
			}
			
		} else {
			// machine -> inventory
			BasicInventory.mergeStackIntoRange((IInventory)inv, player.inventory, slot-36, 0, 36);
		}
		
		return null;
	}

	public AM2BaseProcessorTileEntity getTile() {
		return inv;
	}
}
