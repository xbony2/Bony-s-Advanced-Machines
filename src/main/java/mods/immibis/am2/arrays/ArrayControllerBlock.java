package mods.immibis.am2.arrays;

import mods.immibis.core.RenderUtilsIC;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArrayControllerBlock extends BlockContainer {
	public ArrayControllerBlock() {
		super(Material.iron);
		setHardness(0.5f);
		setCreativeTab(ic2.core.IC2.tabIC2);
		setBlockName("advmachine.arraycontroller");
	}
	
	private IIcon iTop, iBot, iFront, iBack, iSide;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iTop = RenderUtilsIC.loadIcon(par1IconRegister, "adv_machines_immibis:arraycontrol!top");
		iBot = RenderUtilsIC.loadIcon(par1IconRegister, "adv_machines_immibis:arraycontrol!bottom");
		iFront = RenderUtilsIC.loadIcon(par1IconRegister, "adv_machines_immibis:arraycontrol!front");
		iBack = RenderUtilsIC.loadIcon(par1IconRegister, "adv_machines_immibis:arraycontrol!back");
		iSide = RenderUtilsIC.loadIcon(par1IconRegister, "adv_machines_immibis:arraycontrol!side");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		int front = par2 == 0 ? 3 : par2;
		if(par1 == 0) return iTop;
		if(par1 == 1) return iBot;
		if(par1 == front) return iFront;
		if(par1 == (front^1)) return iBack;
		return iSide;
	}
	
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
    {
    	int meta = 0;
        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
        	meta = 2;
        if (l == 1)
        	meta = 5;
        if (l == 2)
        	meta = 3;
        if (l == 3)
        	meta = 4;
        
        par1World.setBlockMetadataWithNotify(par2, par3, par4, meta, 3);
    }

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ArrayControllerTile();
	}
	
	@Override
	public void onPostBlockPlaced(World w, int x, int y, int z, int par5) {
		if(w.isRemote)
			ic2.core.IC2.audioManager.playOnce(w.getTileEntity(x, y, z), "Tools/ODScanner.ogg");
	}
	
	@Override
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer ply, int par6, float par7, float par8, float par9) {
		if(w.isRemote) {
			ic2.core.IC2.audioManager.playOnce(w.getTileEntity(x, y, z), "Tools/ODScanner.ogg");
		} else {
			((ArrayControllerTile)w.getTileEntity(x, y, z)).debug(ply);
		}
		
		return true;
	}
}
