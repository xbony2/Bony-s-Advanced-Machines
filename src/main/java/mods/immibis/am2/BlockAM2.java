package mods.immibis.am2;

import ic2.core.IC2;

import java.util.List;

import mods.immibis.am2.tileentity.TileAM2Base;
import mods.immibis.am2.tileentity.TileAM2Canner;
import mods.immibis.am2.tileentity.TileAM2Compressor;
import mods.immibis.am2.tileentity.TileAM2Cutter;
import mods.immibis.am2.tileentity.TileAM2Extractor;
import mods.immibis.am2.tileentity.TileAM2Extruder;
import mods.immibis.am2.tileentity.TileAM2Macerator;
import mods.immibis.am2.tileentity.TileAM2Recycler;
import mods.immibis.am2.tileentity.TileAM2Roller;
import mods.immibis.am2.tileentity.TileAM2Washer;
import mods.immibis.core.BlockCombined;
import mods.immibis.core.RenderUtilsIC;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAM2 extends BlockCombined {
	
	public static final int META_MACERATOR = 0;
	public static final int META_COMPRESSOR = 1;
	public static final int META_EXTRACTOR = 2;
	public static final int META_CANNER = 3;
	public static final int META_WASHER = 4;
	public static final int META_RECYCLER = 5;
	public static final int META_EXTRUDER = 6;
	public static final int META_ROLLER = 7;
	public static final int META_CUTTER = 8;
	//public static final int META_ELECTROLYZER = 3;
	//public static final int META_DEELECTROLYZER = 4;

	public BlockAM2() {
		super(Material.iron);
		setCreativeTab(IC2.tabIC2);
	}
	
	private IIcon[][] icons;
	private int[][] sideAndFacingToSpriteOffset = { { 3, 2, 0, 0, 0, 0 }, { 2, 3, 1, 1, 1, 1 }, { 1, 1, 3, 2, 5, 4 }, { 0, 0, 2, 3, 4, 5 }, { 4, 5, 4, 5, 3, 2 }, { 5, 4, 5, 4, 2, 3 } };
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		
		icons = new IIcon[16][12];
		
		String[] names = {"macerator", "compressor", "extractor", "canner", "washer", "recycler", "former", "former", "former", null, null, null, null, null, null, null};
		
		for(int k = 0; k < 16; k++)
			if(names[k] != null) {
				for(int i = 0; i < 12; i++) {
					String iName = "adv_machines_immibis:" + names[k] + "!" + i;
					icons[k][i] = RenderUtilsIC.loadIcon(par1IconRegister, iName);
				}
			}
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int data, int facing, boolean active) {
		return icons[data][sideAndFacingToSpriteOffset[side][facing] + (active ? 6 : 0)];
	}
	
	@Override
	public IIcon getIcon(int side, int data) {
		return getIcon(side, data, Dir.PZ, false);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess par1iBlockAccess, int par2, int par3, int par4, int par5) {
		try {
			TileAM2Base te = (TileAM2Base)par1iBlockAccess.getTileEntity(par2, par3, par4);
			return getIcon(par5, par1iBlockAccess.getBlockMetadata(par2, par3, par4), te.getFacing(), te.isVisuallyActive());
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public TileEntity getBlockEntity(int data) {
		switch(data) {
		case META_MACERATOR: return new TileAM2Macerator();
		case META_COMPRESSOR: return new TileAM2Compressor();
		case META_EXTRACTOR: return new TileAM2Extractor();
		case META_CANNER: return new TileAM2Canner();
		case META_WASHER: return new TileAM2Washer();
		case META_RECYCLER: return new TileAM2Recycler();
		case META_EXTRUDER: return new TileAM2Extruder();
		case META_ROLLER: return new TileAM2Roller();
		case META_CUTTER: return new TileAM2Cutter();
		}
		return null;
	}

	@Override
	public void getCreativeItems(List<ItemStack> arraylist) {
		arraylist.add(new ItemStack(this, 1, META_MACERATOR));
		arraylist.add(new ItemStack(this, 1, META_COMPRESSOR));
		arraylist.add(new ItemStack(this, 1, META_EXTRACTOR));
		arraylist.add(new ItemStack(this, 1, META_CANNER));
		arraylist.add(new ItemStack(this, 1, META_WASHER));
		arraylist.add(new ItemStack(this, 1, META_RECYCLER));
		if(AdvancedMachines.enableAdvancedMetalFormers) {
			arraylist.add(new ItemStack(this, 1, META_EXTRUDER));
			arraylist.add(new ItemStack(this, 1, META_ROLLER));
			arraylist.add(new ItemStack(this, 1, META_CUTTER));
		}
	}
	
	@Override
	public boolean canProvidePower() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return true;
	}
}
