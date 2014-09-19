package mods.immibis.am2.arrays;

import mods.immibis.am2.AdvancedMachines;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArrayCableItem extends ItemBlock {
	public ArrayCableItem(Block p_i45328_1_) {
		super(p_i45328_1_);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return AdvancedMachines.blockArrayCable.itemIcon;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		return AdvancedMachines.blockArrayCable.itemIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return AdvancedMachines.blockArrayCable.itemIcon;
	}
	
	@Override
	public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
		return AdvancedMachines.blockArrayCable.itemIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack par1ItemStack) {
		return AdvancedMachines.blockArrayCable.itemIcon;
	}
}
