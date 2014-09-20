package mods.immibis.am2.tileentity;

import ic2.api.recipe.RecipeOutput;
import mods.immibis.am2.AdvancedMachines;
import mods.immibis.core.BasicInventory;
import mods.immibis.core.ImmibisCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AM2BaseProcessorTileEntity extends AM2BaseTileEntity {
	
	public int runningEUPerTick = AdvancedMachines.runningEUPerTick;
	public int idleEUPerTick = AdvancedMachines.idleEUPerTick;

	private final int MAX_PROGRESS = 120000 / getSpeedMultiplier();
	protected int progress;
	
	protected int getSpeedMultiplier() {return 1;}
	
	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getGUIResource();

	@SideOnly(Side.CLIENT)
	public abstract String getGUIText(int speed);

	public abstract String getMachineName();

	public abstract int getNumOutputSlots();
	
	public abstract RecipeOutput getOutputFor(ItemStack input, boolean consume);
	
	@Override
	public abstract String getSound();
	
	@Override
	public int getBatterySlotNumber() {return 1;}

	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(worldObj.isRemote) return;
		
		ItemStack input = inv.getStackInSlot(0);
		
		boolean spinUp = false;
		boolean isRunning = false;
		
		do {
			if(input == null || storedEnergy < runningEUPerTick)
				break;
			RecipeOutput output = getOutputFor(input, false);
			if(output == null)
				break;
			ItemStack outputStack = output.items.size() > 0 ? output.items.get(0) : null; 
			if(outputStack != null && !hasSpaceForOutput(outputStack.stackSize, outputStack))
				break;
			
			if(!isRunning) {
				isRunning = true;
				progress += speed;
			}
			
			if(progress >= MAX_PROGRESS) {
				progress -= MAX_PROGRESS;
				
				// consume input
				output = getOutputFor(input, true);
				outputStack = output.items.size() > 0 ? output.items.get(0) : null;
				if(input.stackSize == 0)
					inv.setInventorySlotContents(0, null);
				
				if(outputStack != null)
					addOutput(ItemStack.copyItemStack(outputStack));
				continue;
			}
			break;
		} while(true);
			
		if(isRunning) {
			spinUp = true;
			storedEnergy -= runningEUPerTick;
		} else {
			spinUp = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			if(speed > 0 && progress >= speed)
				playInterruptSound();
			progress = 0;
		}
		
		if(spinUp && !isRunning) {
			if(storedEnergy >= idleEUPerTick)
				storedEnergy -= idleEUPerTick;
			else
				spinUp = false;
		}
		
		if(spinUp)
			speed = Math.min(MAX_SPEED, speed + spinUpRate);
		else
			speed = Math.max(0, speed - spinDownRate);
		
		if(visuallyActive != (speed > 0)) {
			visuallyActive = (speed > 0);
			resendDescriptionPacket();
		}
		
		setSound(isRunning);
	}

	private boolean hasSpaceForOutput(int num, ItemStack what) {
		int avail = 0;
		for(int k = getNumOutputSlots() + 1; k >= 2; k--) {
			if(inv.getStackInSlot(k) == null)
				avail += what.getMaxStackSize();
			else if(ImmibisCore.areItemsEqual(inv.getStackInSlot(k), what))
				avail += what.getMaxStackSize() - inv.getStackInSlot(k).stackSize;
		}
		return avail >= num;
	}

	private void addOutput(ItemStack what) {
		
		BasicInventory.mergeStackIntoRange(what, (IInventory) this, 2, 2 + getNumOutputSlots());
	}

	@Override
	public int getInventorySize() {
		return 2 + getNumOutputSlots();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(AdvancedMachines.INSTANCE, AdvancedMachines.GUI_PROCESSOR, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	private int[] inputAndOutputSlots;
	@Override
	public synchronized int[] getAccessibleSlots(int side) {
		if(inputAndOutputSlots == null) {
			inputAndOutputSlots = new int[getInventorySize() - 1];
			for(int k = 1; k < inputAndOutputSlots.length; k++)
				inputAndOutputSlots[k] = k+1;
			inputAndOutputSlots[0] = 0;
		}
		
		return inputAndOutputSlots;
	}
	
	@Override
	public boolean canInsert(int slot, ItemStack stack) {
		return true;
	}
	
	@Override
	public boolean canInsert(int slot, int side, ItemStack stack) {
		return slot < 2;
	}
	
	@Override
	public boolean canExtract(int slot, int side, ItemStack stack) {
		return slot >= 2;
	}

	public int getScaledProgress(int i) {
		return progress * i / MAX_PROGRESS;
	}

	public int getProgressBarWidth() {
		return 22;
	}
	
	public int getProgressBarHeight() {
		return 19;
	}

	public int getProgressBarTop() {
		return 34;
	}

	public int getProgressBarLeft() {
		return 80;
	}

}