package mods.immibis.am2.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import mods.immibis.am2.AdvancedMachines;
import mods.immibis.core.ImmibisCore;
import mods.immibis.core.api.util.Dir;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityAdvancedCanner extends AM2BaseTileEntity {
	
	private ItemStack[] movingCans = new ItemStack[8];
	private int canMoveProgress;
	private int canFillProgress;
	private ItemStack fillResult;
	private boolean isRunning;
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		for(int k = 0; k < movingCans.length; k++) {
			if(movingCans[k] != null) {
				NBTTagCompound t = new NBTTagCompound();
				movingCans[k].writeToNBT(t);
				tag.setTag("movingCan"+k, t);
			}
		}
		tag.setInteger("canMProg", canMoveProgress);
		tag.setInteger("canFProg", canFillProgress);
		if(fillResult != null) {
			NBTTagCompound t = new NBTTagCompound();
			fillResult.writeToNBT(t);
			tag.setTag("fillResult", t);
		}
		tag.setBoolean("isRunning", isRunning);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		for(int k = 0; k < movingCans.length; k++)
			if(tag.hasKey("movingCan"+k))
				movingCans[k] = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("movingCan"+k));

		canMoveProgress = tag.getInteger("canMProg");
		canFillProgress = tag.getInteger("canFProg");
		 	
		if(tag.hasKey("fillResult")) {
			fillResult = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("fillResult"));
		}
		
		isRunning = tag.getBoolean("isRunning");
	}
	
	private int CAN_MOVE_TICKS = 40;
	private int CAN_FILL_TICKS = 40;
	
	public static final int SLOT_BATTERY = 0;
	public static final int SLOT_CAN = 1;
	public static final int SLOT_ITEM = 2;
	public static final int SLOT_OUT = 3;
	
	@Override public int getBatterySlotNumber() {return SLOT_BATTERY;}
	@Override public int getInventorySize() {return 4;}
	public ItemStack[] getMovingCans() {return movingCans;}
	public int getCanPositionShift() {return (int)(canMoveProgress * 16) / CAN_MOVE_TICKS;}
	public int getScaledFill(int i) {return (int)(canFillProgress * i) / CAN_FILL_TICKS;}
	
	private void checkRunning() {
		isRunning = (inv.getStackInSlot(SLOT_CAN) != null);
		if(!isRunning) {
			for(ItemStack is : movingCans)
				if(is != null) {
					isRunning = true;
					break;
				}
		}
		if(movingCans[4] != null && inv.getStackInSlot(SLOT_ITEM) == null && canFillProgress == 0 && canMoveProgress == 0)
			isRunning = false;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(worldObj.isRemote) return;
		
		if(!isRunning) {
			if(movingCans[4] != null && canFillProgress == 0 && canMoveProgress == 0)
				isRunning = (inv.getStackInSlot(SLOT_ITEM) != null);
			else
				isRunning = (inv.getStackInSlot(SLOT_CAN) != null);
		}
		
		if(visuallyActive != (speed > 0)) {
			visuallyActive = (speed > 0);
			resendDescriptionPacket();
		}
		
		if((!isRunning && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
		  || storedEnergy < AdvancedMachines.runningEUPerTick) {
			speed -= spinDownRate;
			if(speed <= 0) {
				speed = 0;
				return;
			}
		} else {
			storedEnergy -= isRunning ? AdvancedMachines.runningEUPerTick : AdvancedMachines.idleEUPerTick;
			speed += spinUpRate;
			if(speed > MAX_SPEED)
				speed = MAX_SPEED;
		}
		
		if(speed <= 0) return; // shouldn't happen
		
		CAN_FILL_TICKS = (MAX_SPEED * 5) / speed;
		CAN_MOVE_TICKS = (MAX_SPEED * 5) / speed;
		
		
		if(canFillProgress > 0) {
			canFillProgress++;
			if(canFillProgress >= CAN_FILL_TICKS) {
				canFillProgress = 0;
				canMoveProgress = 1;
				
				movingCans[4] = fillResult;
			}
			
		} else if(canMoveProgress > 0) {
			if(canMoveProgress == 1) {
				movingCans[0] = inv.decrStackSize(SLOT_CAN, 1);
			}
			
			canMoveProgress++;
			if(canMoveProgress >= CAN_MOVE_TICKS) {
				
				ItemStack outslot = inv.getStackInSlot(SLOT_OUT);
				ItemStack last = movingCans[movingCans.length-1];
				if(last != null && outslot != null && (!ImmibisCore.areItemsEqual(outslot, last) || outslot.stackSize + last.stackSize > outslot.getMaxStackSize())) {
					canMoveProgress--;
				
				} else {
					canMoveProgress = 0;
					
					if(last != null) {
						if(outslot == null)
							inv.setInventorySlotContents(SLOT_OUT, last);
						else
							outslot.stackSize += last.stackSize;
					}
					
					for(int k = movingCans.length-1; k >= 1; k--)
						movingCans[k] = movingCans[k-1];
					
					movingCans[0] = null;
					
					checkRunning();
				}
			}
		
		} else if(movingCans[4] == null) {
			// nothing to fill yet
			canMoveProgress = 1;
		
		} else {
			ItemStack item = inv.getStackInSlot(SLOT_ITEM);
			if(item == null) return;
			
			ItemStack savedCan = movingCans[4].copy();
			
			RecipeOutput output = Recipes.cannerBottle.getOutputFor(movingCans[4], item, true, false);
			
			if(output != null && output.items.size() == 1) {
				if(item.stackSize == 0)
					inv.setInventorySlotContents(SLOT_ITEM, null);
				
				// don't fill the can until the progress bar is full
				movingCans[4] = savedCan;
				fillResult = output.items.get(0).copy();
				
				canFillProgress = 1;
			
			} else {
				canMoveProgress = 1;
			}
		}
		
		/*ItemStack input = inv.getStackInSlot(0);
		
		boolean spinUp = false;
		boolean isRunning = false;
		
		do {
			if(input == null || storedEnergy < AdvancedMachines.runningEUPerTick)
				break;
			RecipeOutput output = getOutputFor(input, false);
			if(output == null || output.items.size() != 1)
				break;
			ItemStack outputStack = output.items.get(0); 
			if(!hasSpaceForOutput(outputStack.stackSize, outputStack))
				break;
			
			if(!isRunning) {
				isRunning = true;
				progress += speed;
			}
			
			if(progress >= MAX_PROGRESS) {
				progress -= MAX_PROGRESS;
				
				// consume input
				getOutputFor(input, true);
				if(input.stackSize == 0)
					inv.setInventorySlotContents(0, null);
				
				addOutput(ItemStack.copyItemStack(outputStack));
				continue;
			}
			break;
		} while(true);
			
		if(isRunning) {
			spinUp = true;
			storedEnergy -= AdvancedMachines.runningEUPerTick;
		} else {
			spinUp = !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			progress = 0;
		}
		
		if(spinUp && !isRunning) {
			if(storedEnergy >= AdvancedMachines.idleEUPerTick)
				storedEnergy -= AdvancedMachines.idleEUPerTick;
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
		}*/
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(AdvancedMachines.INSTANCE, AdvancedMachines.GUI_CANNER, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	private static int[] can_slot = {SLOT_CAN}, item_slot = {SLOT_ITEM}, out_and_can_slots = {SLOT_CAN, SLOT_OUT};
	@Override
	public int[] getAccessibleSlots(int side) {
		if(side == Dir.NY)
			return can_slot;
		else if(side == Dir.PY)
			return item_slot;
		else
			return out_and_can_slots;
	}
	@Override
	public boolean canInsert(int slot, int side, ItemStack stack) {
		return slot != SLOT_OUT;
	}
	@Override
	public boolean canInsert(int slot, ItemStack stack) {
		return true;
	}
	@Override
	public boolean canExtract(int slot, int side, ItemStack stack) {
		return !(side != Dir.NY && slot == SLOT_CAN);
	}
	
	public String getGUIText(int speed) {
		return I18n.format("gui.advmachine.canner.speed", speed / 100).replace("\\n", "\n");
	}
}
