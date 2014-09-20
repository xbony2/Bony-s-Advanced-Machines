package mods.immibis.am2.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import mods.immibis.am2.AdvancedMachines;
import mods.immibis.core.ImmibisCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityAdvancedWasher extends AM2BaseTileEntity implements IFluidHandler {
	
	public static final int MAX_WATER = 8000;
	static final int WATER_PER_OP = 500;
	
	public static final int SLOT_IN = 0;
	public static final int SLOT_BATTERY = 1;
	public static final int SLOT_OUT1 = 2;
	public static final int SLOT_OUT2 = 3;
	public static final int SLOT_OUT3 = 4;
	
	private int waterAmount;
	private int progress;
	private static final int MAX_PROGRESS = 120000;
	private boolean exploding;
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		waterAmount = tag.getInteger("water");
		progress = tag.getInteger("progress");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("water", waterAmount);
		tag.setInteger("progress", progress);
	}
	
	@Override public int getInventorySize() {return 5;}
	@Override protected int getBatterySlotNumber() {return SLOT_BATTERY;}

	private boolean fitsInSlot(ItemStack stack, int slot) {
		ItemStack existing = inv.getStackInSlot(slot);
		if(existing == null || stack == null)
			return true;
		if(!ImmibisCore.areItemsEqual(stack, existing))
			return false;
		return stack.stackSize + existing.stackSize <= stack.getMaxStackSize();
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(worldObj.isRemote) return;
		
		if(exploding)
		{
			worldObj.newExplosion(null, xCoord+0.5, yCoord+0.5, zCoord+0.5, 2.0f, false, true);
			return;
		}
		
		waterAmount -= (speed * 5 / MAX_SPEED);
		if(waterAmount < 0)
			waterAmount = 0;
		
		ItemStack input = inv.getStackInSlot(SLOT_IN);
		
		boolean spinUp;
		boolean isRunning = false;
		
		RecipeOutput output = Recipes.oreWashing.getOutputFor(input, false);
		if(output != null && waterAmount >= WATER_PER_OP) {
			ItemStack out1 = output.items.size() > 0 ? output.items.get(0) : null;
			ItemStack out2 = output.items.size() > 1 ? output.items.get(1) : null;
			ItemStack out3 = output.items.size() > 2 ? output.items.get(2) : null;
			if(fitsInSlot(out1, SLOT_OUT1) && fitsInSlot(out2, SLOT_OUT2) && fitsInSlot(out3, SLOT_OUT3) && storedEnergy >= AdvancedMachines.runningEUPerTick_washer) {
				spinUp = true;
				progress += speed;
				isRunning = true;
				
				if(progress >= MAX_PROGRESS) {
					progress -= MAX_PROGRESS;
					waterAmount -= WATER_PER_OP;
					Recipes.oreWashing.getOutputFor(input, true);
					if(input.stackSize == 0)
						inv.setInventorySlotContents(SLOT_IN, null);
					addToSlot(out1, SLOT_OUT1);
					addToSlot(out2, SLOT_OUT2);
					addToSlot(out3, SLOT_OUT3);
				}
			} else {
				spinUp = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
				progress = 0;
			}
		} else {
			spinUp = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			progress = 0;
		}
		
		if(spinUp || isRunning) {
			int energyReq = isRunning ? AdvancedMachines.runningEUPerTick_washer : AdvancedMachines.idleEUPerTick_washer;
			if(storedEnergy >= energyReq) {
				storedEnergy -= energyReq;
				speed += spinUpRate;
				if(speed > MAX_SPEED)
					speed = MAX_SPEED;
				spinUp = true;
			} else
				spinUp = false;
		}
		if(!spinUp) {
			speed -= spinDownRate;
			if(speed <= 0)
				speed = 0;
		}
		
		if(visuallyActive != (speed > 0)) {
			visuallyActive = (speed > 0);
			resendDescriptionPacket();
		}
	}

	private void addToSlot(ItemStack is, int slot) {
		if(is == null)
			return;
		if(inv.getStackInSlot(slot) == null)
			inv.setInventorySlotContents(slot, is.copy());
		else
			inv.getStackInSlot(slot).stackSize += is.stackSize;
	}

	private int[] acc_slots = {SLOT_IN, SLOT_OUT1, SLOT_OUT2, SLOT_OUT3};
	@Override
	public int[] getAccessibleSlots(int side) {
		return acc_slots;
	}

	@Override
	public boolean canInsert(int slot, int side, ItemStack stack) {
		return slot == SLOT_IN;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public boolean canExtract(int slot, int side, ItemStack stack) {
		return slot != SLOT_IN;
	}
	
	public int getWater() {
		return waterAmount;
	}
	
	public int getScaledProgress(int i) {
		return (progress * i) / MAX_PROGRESS;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(AdvancedMachines.INSTANCE, AdvancedMachines.GUI_WASHER, worldObj, xCoord, yCoord, zCoord);
		return true;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(resource.getFluid() != FluidRegistry.WATER)
			return 0;
		int toAdd = Math.min(MAX_WATER - waterAmount, resource.amount);
		if(doFill)
		{
			if(waterAmount == 0 && toAdd > 0 && speed >= MAX_SPEED/2)
				exploding = true;
			waterAmount += toAdd;
		}
		return toAdd;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}
	
	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid == FluidRegistry.WATER;
	}
	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] {new FluidTankInfo(new FluidStack(FluidRegistry.WATER, waterAmount), MAX_WATER)};
	}
}
