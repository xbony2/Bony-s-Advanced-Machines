package mods.immibis.am2.arrays;

import ic2.api.energy.tile.IEnergySource;
import mods.immibis.core.api.APILocator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class ArrayControllerTile extends TileEntity implements IEnergySource {

	ArrayScanner scanner = new ArrayScanner(this);
	
	boolean addedToEnet;
	
	double eu_produced;
	double eu_produced_from_wind;
	
	public static final int SCANNED_BLOCKS_PER_TICK = 5;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!worldObj.isRemote) {
			
			for(int k = 0; k < SCANNED_BLOCKS_PER_TICK; k++)
				scanner.tick();
			
			if(!addedToEnet) {
				APILocator.getCrossModIC2().addEnetTile(this);
				addedToEnet = true;
			}
			
			eu_produced = 0;
			
			if(worldObj.isDaytime() && !worldObj.isRaining()) {
				eu_produced += numSolars;
			}
			
			eu_produced += numWaterBlocks * 0.01;
			
		    eu_produced_from_wind = totalWindGenPower;
			
			eu_produced += eu_produced_from_wind;
		}
	}
	
	@Override
	public void onChunkUnload() {
		if(addedToEnet) {
			APILocator.getCrossModIC2().removeEnetTile(this);
			addedToEnet = false;
		}
		
		super.onChunkUnload();
	}
	
	@Override
	public void invalidate() {
		if(addedToEnet) {
			APILocator.getCrossModIC2().removeEnetTile(this);
			addedToEnet = false;
		}
		
		super.invalidate();
	}
	
	public void debug(EntityPlayer ply) {
		ply.addChatMessage(new ChatComponentTranslation("advmachine.arraycontroller.rclick01"));
		ply.addChatMessage(new ChatComponentTranslation("advmachine.arraycontroller.rclickRR", MathHelper.ceiling_double_int(numBlocksScanned / (double)SCANNED_BLOCKS_PER_TICK), numBlocksScanned));
		ply.addChatMessage(new ChatComponentTranslation("advmachine.arraycontroller.rclickSP", numSolars));
		ply.addChatMessage(new ChatComponentTranslation("advmachine.arraycontroller.rclickWA", String.format("%d.%02d", numWaterBlocks/100, numWaterBlocks%100)));
		ply.addChatMessage(new ChatComponentTranslation("advmachine.arraycontroller.rclickWI", String.format("%.2f", eu_produced_from_wind)));
		ply.addChatMessage(new ChatComponentTranslation("advmachine.arraycontroller.rclickCP", String.format("%.2f", eu_produced)));
	}
	
	int numSolars, numWaterBlocks, numBlocksScanned;
	double totalWindGenPower;

	void onScanFinished() {
		if(scanner.numControllers > 0) {
			worldObj.newExplosion(null, xCoord+0.5, yCoord+0.5, zCoord+0.5, 4.0f, false, true);
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			return;
		}
		
		numSolars = scanner.numSolars;
		numWaterBlocks = scanner.numWaterBlocks;
		totalWindGenPower = scanner.totalWindGenPower;
		numBlocksScanned = scanner.numBlocksScanned;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity arg0, ForgeDirection arg1) {
		return true;
	}

	@Override
	public void drawEnergy(double arg0) {
	}
	
	@Override
	public int getSourceTier() {
		return 2;
	}

	@Override
	public double getOfferedEnergy() {
		return eu_produced;
	}

}
