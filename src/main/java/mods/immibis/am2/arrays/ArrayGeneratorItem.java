package mods.immibis.am2.arrays;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ArrayGeneratorItem extends ItemBlock {
	public ArrayGeneratorItem(Block b) {
		super(b);
		setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int par1) {
		return par1;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		switch(par1ItemStack.getItemDamage()) {
		case 0: return "tile.advmachine.arraysolar";
		case 1: return "tile.advmachine.arraywater";
		case 2: return "tile.advmachine.arraywind";
		}
		return "";
	}
}
