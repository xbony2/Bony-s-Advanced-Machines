package mods.immibis.am2.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.Ic2Items;
import mods.immibis.core.ImmibisCore;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAdvancedRecycler extends AM2BaseProcessorTileEntity {
	
	@Override
	public String getSound() {
		return "Machines/RecyclerOp.ogg";
	}
	
	@Override
	protected int getSpeedMultiplier() {
		return 12; // 1 item/tick at max speed
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGUIResource() {
		return new ResourceLocation("adv_machines_immibis", "textures/gui/GUIRecycler.png");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getGUIText(int speed) {
		return I18n.format("gui.advmachine.pressure", speed*9).replace("\\n", "\n");
	}

	@Override
	public String getMachineName() {
		return "tile.advmachine.recycler.name";
	}

	@Override
	public int getNumOutputSlots() {
		return 1;
	}

	private static RecipeOutput scrapbox_out = new RecipeOutput(null, new ItemStack[] {Ic2Items.scrapBox});
	private static RecipeOutput scrap_out = new RecipeOutput(null, new ItemStack[] {Ic2Items.scrap});
	private static RecipeOutput null_out = new RecipeOutput(null, new ItemStack[] {null});
	
	@Override
	public RecipeOutput getOutputFor(ItemStack input, boolean consume) {
		if(ImmibisCore.areItemsEqual(input, Ic2Items.scrap)) {
			if(input.stackSize < 9)
				return null;
			if(consume)
				input.stackSize -= 9;
			return scrapbox_out;
		}
		if(!consume)
			return scrap_out;
		
		input.stackSize--;
		if(Recipes.recyclerBlacklist.contains(input))
			return null_out;
		return worldObj.rand.nextInt(8) == 0 ? scrap_out : null_out;
	}


}
