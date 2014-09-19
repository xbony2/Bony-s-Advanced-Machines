package mods.immibis.am2.container;

import mods.immibis.am2.tileentity.TileAM2Washer;
import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerWasher extends BaseContainer<TileAM2Washer> {
	public ContainerWasher(EntityPlayer ply, TileAM2Washer inv) {
		super(ply, inv);
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(ply.inventory, x, 8 + x*18, 142));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(ply.inventory, x + y*9 + 9, 8 + x*18, 84 + y*18));
		
		addSlotToContainer(new Slot((IInventory)inv, TileAM2Washer.SLOT_IN, 104, 17));
		addSlotToContainer(new Slot((IInventory)inv, TileAM2Washer.SLOT_BATTERY, 152, 62));
		addSlotToContainer(new Slot((IInventory)inv, TileAM2Washer.SLOT_OUT1, 86, 62));
		addSlotToContainer(new Slot((IInventory)inv, TileAM2Washer.SLOT_OUT2, 104, 62));
		addSlotToContainer(new Slot((IInventory)inv, TileAM2Washer.SLOT_OUT3, 122, 62));
	}
	
	public int water = 0;
	public int energyPixels = 0;
	public int progress = 0;
	public int speed = 0;
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		setProgressBar(1, water = inv.getWater());
		setProgressBar(2, energyPixels = inv.getScaledEnergy(13));
		setProgressBar(3, speed = inv.getSpeed());
		setProgressBar(4, progress = inv.getScaledProgress(5));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		switch(par1) {
		case 1: water = par2; break;
		case 2: energyPixels = par2; break;
		case 3: speed = par2; break;
		case 4: progress = par2; break;
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(int slot) {
		if(slot < 36) {
			// inventory -> machine
			ItemStack stack = ((Slot)inventorySlots.get(slot)).getStack();
			if(stack == null)
				return null;
			
			boolean isInput = inv.canInsert(TileAM2Washer.SLOT_IN, stack);
			boolean isBattery = inv.canInsert(TileAM2Washer.SLOT_BATTERY, stack);
			
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
}
