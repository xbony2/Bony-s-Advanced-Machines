package mods.immibis.am2.arrays;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class ArrayCableRenderStatic implements ISimpleBlockRenderingHandler {
	public static int renderID = RenderingRegistry.getNextAvailableRenderId();
	@Override public int getRenderId() {return renderID;}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		
	}
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		boolean nx = ArrayCableBlock.connects(world, x-1, y, z);
		boolean px = ArrayCableBlock.connects(world, x+1, y, z);
		boolean ny = ArrayCableBlock.connects(world, x, y-1, z);
		boolean py = ArrayCableBlock.connects(world, x, y+1, z);
		boolean nz = ArrayCableBlock.connects(world, x, y, z-1);
		boolean pz = ArrayCableBlock.connects(world, x, y, z+1);
		
		double thick = 2/16f;
		double min = 0.5-thick, max=0.5+thick;
		
		if(!nx && !ny && !nz && !px && !py && !pz) {
			renderer.setRenderBounds(min, min, min, max, max, max);
			renderer.renderStandardBlock(block, x, y, z);
			return true;
		}
		
		boolean renderedMiddle = false;
		
		if(nx || px) {
			renderer.setRenderBounds(nx ? 0 : min, min, min, px ? 1 : max, max, max);
			renderer.renderStandardBlock(block, x, y, z);
			renderedMiddle = true;
		}
		
		if(ny || py) {
			if(!renderedMiddle) {
				renderer.setRenderBounds(min, ny ? 0 : min, min, max, py ? 1 : max, max);
				renderer.renderStandardBlock(block, x, y, z);
				renderedMiddle = true;
			} else {
				if(ny) {
					renderer.setRenderBounds(min, 0, min, max, min, max);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if(py) {
					renderer.setRenderBounds(min, max, min, max, 1, max);
					renderer.renderStandardBlock(block, x, y, z);
				}
			}
		}
		
		if(nz || pz) {
			if(!renderedMiddle) {
				renderer.setRenderBounds(min, min, nz ? 0 : min, max, max, pz ? 1 : max);
				renderer.renderStandardBlock(block, x, y, z);
				renderedMiddle = true;
			} else {
				if(nz) {
					renderer.setRenderBounds(min, min, 0, max, max, min);
					renderer.renderStandardBlock(block, x, y, z);
				}
				if(pz) {
					renderer.setRenderBounds(min, min, max, max, max, 1);
					renderer.renderStandardBlock(block, x, y, z);
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelID) {
		return false;
	}
}
