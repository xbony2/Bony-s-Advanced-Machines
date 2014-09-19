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

public class TileAM2Cutter extends TileAM2Base implements IFluidHandler {

	static final int MAX_WATER = 8000;
	static final int WATER_PER_OP = 500;
	static final int WATER_PER_TICK = 2; // at max speed
	
	public static final int SLOT_IN = 0;
	public static final int SLOT_BATTERY = 1;
	public static final int SLOT_OUT = 2;
	
	private int waterAmount;
	private int progress;
	private static final int MAX_PROGRESS = 120000;
	
	@Override public int getInventorySize() {return 3;}
	@Override protected int getBatterySlotNumber() {return SLOT_BATTERY;}

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
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(worldObj.isRemote) return;
		
		waterAmount -= (int)(speed * WATER_PER_TICK / (double)MAX_SPEED + worldObj.rand.nextDouble());
		if(waterAmount < 0)
			waterAmount = 0;
		
		if(waterAmount == 0)
			speed = 0;
		
		ItemStack input = inv.getStackInSlot(SLOT_IN);
		
		boolean spinUp;
		boolean isRunning = false;
		
		RecipeOutput output = Recipes.metalformerCutting.getOutputFor(input, false);
		if(output != null && waterAmount >= WATER_PER_OP && output.items.size() == 1) {
			ItemStack out = output.items.get(0);
			ItemStack inOutSlot = inv.getStackInSlot(SLOT_OUT);
			if(inOutSlot == null || (inOutSlot.stackSize + out.stackSize <= inOutSlot.getMaxStackSize()) && ImmibisCore.areItemsEqual(inOutSlot, out)) {
				spinUp = true;
				progress += speed;
				isRunning = true;
				
				if(progress >= MAX_PROGRESS) {
					progress -= MAX_PROGRESS;
					waterAmount -= WATER_PER_OP;
					Recipes.metalformerCutting.getOutputFor(input, true);
					if(input.stackSize == 0)
						inv.setInventorySlotContents(SLOT_IN, null);
					if(inOutSlot != null)
						inOutSlot.stackSize += out.stackSize;
					else
						inOutSlot = out.copy();
					inv.setInventorySlotContents(SLOT_OUT, inOutSlot);
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
			int energyReq = isRunning ? AdvancedMachines.runningEUPerTick_cutter : AdvancedMachines.idleEUPerTick_cutter;
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

	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(AdvancedMachines.INSTANCE, AdvancedMachines.GUI_CUTTER, worldObj, xCoord, yCoord, zCoord);
		return true;
	}
	
	@Override
	public String getSound() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static int[] inputAndOutputSlots = new int[] {SLOT_IN, SLOT_OUT};
	@Override
	public synchronized int[] getAccessibleSlots(int side) {
		return inputAndOutputSlots;
	}
	
	@Override
	public boolean canInsert(int slot, ItemStack stack) {
		return true;
	}
	
	@Override
	public boolean canInsert(int slot, int side, ItemStack stack) {
		return slot == SLOT_IN;
	}
	
	@Override
	public boolean canExtract(int slot, int side, ItemStack stack) {
		return slot == SLOT_OUT;
	}
	
	
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(resource.getFluid() != FluidRegistry.WATER)
			return 0;
		int amt = Math.min(resource.amount, MAX_WATER - waterAmount);
		if(doFill)
			waterAmount += amt;
		return amt;
	}
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(resource.getFluid() != FluidRegistry.WATER)
			return null;
		return drain(from, resource.amount, doDrain);
	}
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(waterAmount <= 0)
			return null;
		int amt = Math.min(maxDrain, waterAmount);
		if(doDrain)
			waterAmount -= amt;
		return new FluidStack(FluidRegistry.WATER, amt);
	}
	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid == FluidRegistry.WATER;
	}
	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid == FluidRegistry.WATER;
	}
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] {new FluidTankInfo(new FluidStack(FluidRegistry.WATER, waterAmount), MAX_WATER)};
	}
	public int getScaledProgress(int i) {
		return progress * i / MAX_PROGRESS;
	}
	
	public int getScaledWater(int i) {
		return waterAmount * i / MAX_WATER;
	}
}
