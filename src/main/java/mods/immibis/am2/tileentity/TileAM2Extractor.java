package mods.immibis.am2.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileAM2Extractor extends TileAM2BaseProcessor {
	
	@Override
	public String getSound() {
		return "Machines/ExtractorOp.ogg";
	}

	@Override
	public int getNumOutputSlots() {
		return 3;
	}

	@Override
	public RecipeOutput getOutputFor(ItemStack input, boolean adjustInput) {
		return Recipes.extractor.getOutputFor(input, adjustInput);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getGUIResource() {
		return new ResourceLocation("adv_machines_immibis", "textures/gui/GUICenterfuge.png");
	}
	
	@Override
	public String getMachineName() {
		return "tile.advmachine.extractor.name";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getGUIText(int speed) {
		return I18n.format("gui.advmachine.speed", speed).replace("\\n", "\n");
	}
	
}
