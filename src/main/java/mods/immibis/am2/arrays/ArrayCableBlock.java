package mods.immibis.am2.arrays;

import java.util.List;

import mods.immibis.am2.AdvancedMachines;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArrayCableBlock extends Block {
	public static int model;

	public ArrayCableBlock() {
		super(Material.cloth);
		
		setStepSound(soundTypeCloth);
		setBlockName("advmachine.arraycable");
		setCreativeTab(ic2.core.IC2.tabIC2);
		setHardness(0.3f);
		setBlockTextureName("adv_machines_immibis:arraycable");
	}
	
	public IIcon itemIcon;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		itemIcon = par1IconRegister.registerIcon("adv_machines_immibis:arraycable_item");
		super.registerBlockIcons(par1IconRegister);
	}
	
	@Override
	public int getRenderType() {
		return model;
	}
	
	@Override
	public int getMobilityFlag() {
		return 2; // unpushable
	}
	
	private static AxisAlignedBB[] potentialBoundingBoxes = new AxisAlignedBB[10];
	static {
		double thick = 2/16f;
		double min = 0.5-thick, max=0.5+thick;
		potentialBoundingBoxes[0] = AxisAlignedBB.getBoundingBox(min, min, min, max, max, max);
		potentialBoundingBoxes[1] = AxisAlignedBB.getBoundingBox(0, min, min, 1, max, max);
		potentialBoundingBoxes[2] = AxisAlignedBB.getBoundingBox(0, min, min, max, max, max);
		potentialBoundingBoxes[3] = AxisAlignedBB.getBoundingBox(min, min, min, 1, max, max);
		potentialBoundingBoxes[4] = AxisAlignedBB.getBoundingBox(min, 0, min, max, 1, max);
		potentialBoundingBoxes[5] = AxisAlignedBB.getBoundingBox(min, 0, min, max, max, max);
		potentialBoundingBoxes[6] = AxisAlignedBB.getBoundingBox(min, min, min, max, 1, max);
		potentialBoundingBoxes[7] = AxisAlignedBB.getBoundingBox(min, min, 0, max, max, 1);
		potentialBoundingBoxes[8] = AxisAlignedBB.getBoundingBox(min, min, 0, max, max, max);
		potentialBoundingBoxes[9] = AxisAlignedBB.getBoundingBox(min, min, min, max, max, 1);
	}
	// returns a bitmask of which of potentialBoundingBoxes apply to this block
	private static int getBoundingBoxSet(int connMask) {
		if(connMask == 0)
			return 1;
		
		boolean nx = (connMask & 1) != 0;
		boolean px = (connMask & 2) != 0;
		boolean ny = (connMask & 4) != 0;
		boolean py = (connMask & 8) != 0;
		boolean nz = (connMask & 16) != 0;
		boolean pz = (connMask & 32) != 0;
		
		return (nx && px ? 2 : nx ? 4 : px ? 8 : 0) | (ny && py ? 16 : ny ? 32 : py ? 64 : 0) | (nz && pz ? 128 : nz ? 256 : pz ? 512 : 0); 
	}
	
	@Override
	public void addCollisionBoxesToList(World w, int x, int y, int z, AxisAlignedBB mask, List result, Entity par7Entity) {
		int bbSet = getBoundingBoxSet(getConnectionMask(w, x, y, z));
		mask.offset(-x, -y, -z);
		for(int k = 0; k < 10; k++)
			if((bbSet & (1 << k)) != 0) {
				AxisAlignedBB bb = potentialBoundingBoxes[k];
				if(bb.intersectsWith(mask))
					result.add(bb.copy().offset(x, y, z));
			}
		mask.offset(x, y, z);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World w, int x, int y, int z, Vec3 src, Vec3 dst) {
		src.xCoord -= x; src.yCoord -= y; src.zCoord -= z;
		dst.xCoord -= x; dst.yCoord -= y; dst.zCoord -= z;
		
		int bbSet = getBoundingBoxSet(getConnectionMask(w, x, y, z));
		
		double closest = Double.POSITIVE_INFINITY;
		MovingObjectPosition result = null;
		
		for(int k = 0; k < 10; k++)
			if((bbSet & (1 << k)) != 0) {
				AxisAlignedBB bb = potentialBoundingBoxes[k];
				
				MovingObjectPosition thisResult = bb.calculateIntercept(src, dst);
				if(thisResult == null)
					continue;
				
				double thisDist = thisResult.hitVec.squareDistanceTo(src);
				if(thisDist < closest) {
					closest = thisDist;
					result = thisResult;
				}
			}
		
		if(result != null) {
			result.blockX = x;
			result.blockY = y;
			result.blockZ = z;
			result.hitVec.xCoord += x;
			result.hitVec.yCoord += y;
			result.hitVec.zCoord += z;
			result.typeOfHit = MovingObjectType.BLOCK;
		}
		
		src.xCoord += x; src.yCoord += y; src.zCoord += z;
		dst.xCoord += x; dst.yCoord += y; dst.zCoord += z;
		return result;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World w, int x, int y, int z) {
		double thick = 2/16f;
		double min = 0.5-thick, max=0.5+thick;
		return AxisAlignedBB.getBoundingBox(
			connects(w, x-1, y, z) ? 0 : min,
			connects(w, x, y-1, z) ? 0 : min,
			connects(w, x, y, z-1) ? 0 : min,
			connects(w, x+1, y, z) ? 1 : max,
			connects(w, x, y+1, z) ? 1 : max,
			connects(w, x, y, z+1) ? 1 : max
		).offset(x, y, z);
	}

	public static boolean connects(IBlockAccess world, int x, int y, int z) {
		if(world instanceof World)
			if(!((World)world).blockExists(x, y, z))
				return false;
		Block block = world.getBlock(x, y, z);
		return block == AdvancedMachines.blockArrayCable || block == AdvancedMachines.blockArrayController || block == AdvancedMachines.blockArrayDevice;
	}
	
	public static int getConnectionMask(IBlockAccess world, int x, int y, int z) {
		return (connects(world, x-1, y, z) ? 1 : 0) | (connects(world, x+1, y, z) ? 2 : 0)
			| (connects(world, x, y-1, z) ? 4 : 0) | (connects(world, x, y+1, z) ? 8 : 0)
			| (connects(world, x, y, z-1) ? 16 : 0) | (connects(world, x, y, z+1) ? 32 : 0);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		return true;
	}
}
