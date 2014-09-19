package mods.immibis.am2.tileentity;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import mods.immibis.am2.AdvancedMachines;
import mods.immibis.core.TileCombined;
import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.crossmod.ICrossModIC2;
import mods.immibis.core.api.traits.IInventoryTrait;
import mods.immibis.core.api.traits.IInventoryTraitUser;
import mods.immibis.core.api.traits.TraitField;
import mods.immibis.core.api.traits.UsesTraits;
import mods.immibis.core.api.util.Dir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@UsesTraits
public abstract class TileAM2Base extends TileCombined implements IWrenchable, IEnergySink, IInventoryTraitUser {
	
	protected abstract int getBatterySlotNumber();
	
	@TraitField
	public IInventoryTrait inv;
	
	protected final ICrossModIC2 ic2 = APILocator.getCrossModIC2();

	private static final int MAX_STORAGE = 10000;
	protected int storedEnergy = 0;
	protected static final int MAX_SPEED = 10000;
	public int speed;
	protected final int spinUpRate = AdvancedMachines.spinUpRate;
	protected final int spinDownRate = AdvancedMachines.spinDownRate;
	protected static final int TIER = 2;
	private int facing = 2;
	protected boolean visuallyActive;
	private boolean addedToEnet = false;
	protected int soundState;
	private int lastSoundState;
	
	protected static final int SND_OFF = 0;
	protected static final int SND_ON = 1;
	protected static final int SND_INTERRUPT = 2;
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("energy", storedEnergy);
		tag.setShort("facing", (short)facing);
		tag.setInteger("speed", speed);
		inv.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		try {storedEnergy = tag.getInteger("energy");} catch(Exception e) {}
		try {facing = tag.getShort("facing");} catch(Exception e) {}
		try {speed = tag.getInteger("speed");} catch(Exception e) {}
		inv.readFromNBT(tag);
	}

	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, facing | (visuallyActive ? 8 : 0) | (soundState << 4), null);
	}

	public String getSound() {return null;}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void onDataPacket(S35PacketUpdateTileEntity packet) {
		facing = packet.func_148853_f() & 7;
		visuallyActive = (packet.func_148853_f() & 8) != 0;
		soundState = (packet.func_148853_f() & 48) >> 4;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if(soundState != lastSoundState)
			updateSound();
	}

	private AudioSource audioSource;
	@SideOnly(Side.CLIENT)
	private void updateSound() {
		lastSoundState = soundState;
		switch(soundState) {
		case SND_ON:
			audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, getSound(), true, false, IC2.audioManager.defaultVolume);
			if(audioSource != null) {
				audioSource.play();
				audioSource.updatePosition();
				audioSource.updateVolume(Minecraft.getMinecraft().thePlayer);
			}
			break;
		case SND_INTERRUPT:
			IC2.audioManager.playOnce(this, "Machines/InterruptOne.ogg");
		case SND_OFF:
			if(audioSource != null)
				audioSource.stop();
			
			break;
		}
	}

	@Override
	public void invalidate() {
		IC2.audioManager.removeSources(this);
		if(!worldObj.isRemote && addedToEnet) {
			ic2.removeEnetTile(this);
			addedToEnet = false;
		}
		super.invalidate();
	}

	@Override
	public void onChunkUnload() {
		IC2.audioManager.removeSources(this);
		if(!worldObj.isRemote && addedToEnet) {
			ic2.removeEnetTile(this);
			addedToEnet = false;
		}
		super.onChunkUnload();
	}
	
	protected void setSound(boolean run) {
		if(run)
			soundState = SND_ON;
		else if(soundState == SND_ON)
			soundState = SND_OFF;
	}
	
	protected void playInterruptSound() {
		if(soundState == SND_ON)
			soundState = SND_INTERRUPT;
	}
	
	

	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(worldObj.isRemote) {
			return;
		}
		
		if(soundState != lastSoundState) {
			lastSoundState = soundState;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		if(!addedToEnet) {
			ic2.addEnetTile(this);
			addedToEnet = true;
		}
		
		int batterySlot = getBatterySlotNumber();
		
		ItemStack battery = inv.getStackInSlot(batterySlot);
		if(battery != null && ic2.isElectricItem(battery)) {
			storedEnergy += ic2.dischargeElectricItem(battery, MAX_STORAGE - storedEnergy, TIER, false, false);
			if(battery.stackSize == 0)
				inv.setInventorySlotContents(batterySlot, null);
		}
	}
	
	@Override
	public boolean acceptsEnergyFrom(TileEntity arg0, ForgeDirection arg1) {
		return true;
	}

	@Override
	public double getDemandedEnergy() {
		return storedEnergy < MAX_STORAGE ? AdvancedMachines.maxVoltage : 0;
	}

	@Override
	public int getSinkTier() {
		return 2;
	}

	@Override
	public double injectEnergy(ForgeDirection arg0, double amount, double voltage) {
		if(AdvancedMachines.enableExplosions) {
			if(voltage > AdvancedMachines.maxVoltage) {
				float boomStrength;
				if(voltage > 8192)
					boomStrength = 16;
				else if(voltage > 2048)
					boomStrength = 5;
				else if(voltage > 512)
					boomStrength = 4;
				else if(voltage > 128)
					boomStrength = 3;
				else
					boomStrength = 2;
				
				worldObj.newExplosion(null, xCoord+0.5, yCoord+0.5, zCoord+0.5, boomStrength, false, true);
				return 0;
			}
		}
		if(storedEnergy >= MAX_STORAGE)
			return amount;
		
		int used = Math.min(AdvancedMachines.maxVoltage, (int)amount);
		
		storedEnergy += used;
		return amount - used;
	}

	@Override
	public short getFacing() {
		return (short)facing;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer arg0) {
		return new ItemStack(getBlockType(), 1, getBlockMetadata());
	}

	@Override
	public float getWrenchDropRate() {
		return 1.0f;
	}

	@Override
	public void setFacing(short arg0) {
		facing = arg0;
		resendDescriptionPacket();
	}

	@Override
	public boolean wrenchCanRemove(EntityPlayer arg0) {
		return true;
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer arg0, int arg1) {
		return arg1 > 2 && arg1 != facing;
	}

	@Override
	public void onPlaced(EntityLivingBase player, int _) {
		Vec3 look = player.getLook(1.0f);
		
	    double absx = Math.abs(look.xCoord);
	    double absz = Math.abs(look.zCoord);
	    
	    if(absx > absz) {
	    	if(look.xCoord < 0)
	    		facing = Dir.PX;
	    	else
	    		facing = Dir.NX;
	    } else {
	    	if(look.zCoord < 0)
	    		facing = Dir.PZ;
	    	else
	    		facing = Dir.NZ;
	    }
	}

	public int getScaledEnergy(int i) {
		return storedEnergy * i / MAX_STORAGE;
	}

	public int getSpeed() {
		return speed;
	}

	public boolean isVisuallyActive() {
		return visuallyActive;
	}
}
