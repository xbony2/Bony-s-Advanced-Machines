package mods.immibis.am2.arrays;

import java.util.List;

import mods.immibis.core.RenderUtilsIC;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArrayGeneratorBlock extends Block {
	public ArrayGeneratorBlock() {
		super(Material.iron);
		setHardness(0.5f);
		setCreativeTab(ic2.core.IC2.tabIC2);
	}
	
	@Override
	public int damageDropped(int par1) {
		return par1 & 3;
	}
	
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		int type = par2 & 3, front = (par2 >> 2) + 2;
		if(type == 3)
			return icons[0]; // invalid
		
		if(par1 < 2)
			return icons[type*6 + par1]; // top/bottom
		
		if(par1 == front)
			return icons[type*6 + 4]; // front
		if(par1 == (front^1))
			return icons[type*6 + 5]; // back
		if(par1 == ((front-2)^2)+2) // arbitrarily choose which sides are 1 and 2
			return icons[type*6 + 2]; // side 1
		return icons[type*6 + 3]; // side 2
	}
	
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
    {
    	int meta = par6ItemStack.getItemDamage();
        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
        	meta |= 0 << 2;
        if (l == 1)
        	meta |= 3 << 2;
        if (l == 2)
        	meta |= 1 << 2;
        if (l == 3)
        	meta |= 2 << 2;
        
        par1World.setBlockMetadataWithNotify(par2, par3, par4, meta, 3);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		icons = new IIcon[18];
		for(int side = 0; side < 6; side++) {
			icons[side   ] = RenderUtilsIC.loadIcon(par1IconRegister, "adv_machines_immibis:arraysolar!"+side);
			icons[side+6 ] = RenderUtilsIC.loadIcon(par1IconRegister, "adv_machines_immibis:arraywater!"+side);
			icons[side+12] = RenderUtilsIC.loadIcon(par1IconRegister, "adv_machines_immibis:arraywind!"+side);
		}
	}
	
	@Override
	public int getMobilityFlag() {
		return 2; // unpushable
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		par3List.add(new ItemStack(this, 1, META_SOLAR));
		par3List.add(new ItemStack(this, 1, META_WATER));
		par3List.add(new ItemStack(this, 1, META_WIND));
	}
	
	public static final int META_SOLAR = 0;
	public static final int META_WATER = 1;
	public static final int META_WIND = 2;
}
