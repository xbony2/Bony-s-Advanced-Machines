package mods.immibis.am2.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileAM2Compressor extends TileAM2BaseProcessor {

	@Override
	public String getSound() {
		return "Machines/CompressorOp.ogg";
	}
	
	@Override
	public int getNumOutputSlots() {
		return 1;
	}

	@Override
	public RecipeOutput getOutputFor(ItemStack input, boolean adjustInput) {
		return Recipes.compressor.getOutputFor(input, adjustInput);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGUIResource() {
		return new ResourceLocation("adv_machines_immibis", "textures/gui/GUISingularity.png");
	}
	
	@Override
	public String getMachineName() {
		return "tile.advmachine.compressor.name";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getGUIText(int speed) {
		return I18n.format("gui.advmachine.pressure", speed*9).replace("\\n", "\n");
	}
}
