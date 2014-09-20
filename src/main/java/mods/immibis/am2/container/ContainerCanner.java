package mods.immibis.am2.container;

import mods.immibis.am2.tileentity.TileEntityAdvancedCanner;
import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerCanner extends BaseContainer<TileEntityAdvancedCanner> {
	public ContainerCanner(EntityPlayer ply, TileEntityAdvancedCanner inv) {
		super(ply, inv);
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(ply.inventory, x, 8 + x*18, 142));
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(ply.inventory, x + y*9 + 9, 8 + x*18, 84 + y*18));
		
		addSlotToContainer(new Slot((IInventory)inv, TileEntityAdvancedCanner.SLOT_BATTERY, 121, 25));
		addSlotToContainer(new Slot((IInventory)inv, TileEntityAdvancedCanner.SLOT_CAN, 15, 48));
		addSlotToContainer(new Slot((IInventory)inv, TileEntityAdvancedCanner.SLOT_ITEM, 79, 7));
		addSlotToContainer(new Slot((IInventory)inv, TileEntityAdvancedCanner.SLOT_OUT, 144, 48));
	}
	
	public int[] movingCanIDs = new int[8];
	public int[] movingCanMeta = new int[8];
	public int canPositionShift;
	public int fillPixels;
	public int energyPixels;
	public int speed;
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		setProgressBar(1, canPositionShift = inv.getCanPositionShift());
		setProgressBar(2, energyPixels = inv.getScaledEnergy(13));
		setProgressBar(3, speed = inv.getSpeed());
		setProgressBar(4, fillPixels = inv.getScaledFill(27));
		
		ItemStack[] movingCans = inv.getMovingCans();
		for(int k = 0; k < 8; k++) {
			setProgressBar(k*2+5, movingCans[k] == null ? 0 : Item.getIdFromItem(movingCans[k].getItem()));
			if(movingCans[k] != null)
				setProgressBar(k*2+6, movingCans[k].getItemDamage());
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		switch(par1) {
		case 1: canPositionShift = par2; break;
		case 2: energyPixels = par2; break;
		case 3: speed = par2; break;
		case 4: fillPixels = par2; break;
		}
		
		if(par1 > 4) {
			if((par1 & 1) == 0)
				movingCanMeta[(par1 - 6) / 2] = par2;
			else
				movingCanIDs[(par1 - 5) / 2] = par2;
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(int slot) {
		if(slot < 36) {
			// inventory -> machine
			ItemStack stack = ((Slot)inventorySlots.get(slot)).getStack();
			if(stack == null)
				return null;
			
			return null;
			
		} else {
			// machine -> inventory
			BasicInventory.mergeStackIntoRange((IInventory)inv, player.inventory, slot-36, 0, 36);
		}
		
		return null;
	}

	public TileEntityAdvancedCanner getTile() {
		return inv;
	}
}
