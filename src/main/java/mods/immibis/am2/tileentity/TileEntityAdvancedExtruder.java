package mods.immibis.am2.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import mods.immibis.am2.AdvancedMachines;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TileEntityAdvancedExtruder extends AM2BaseProcessorTileEntity {

	{
		idleEUPerTick = AdvancedMachines.idleEUPerTick_formers;
		runningEUPerTick = AdvancedMachines.runningEUPerTick_formers;
	}
	
	@Override
	public ResourceLocation getGUIResource() {
		return new ResourceLocation("adv_machines_immibis", "textures/gui/GUIExtruder.png");
	}

	@Override
	public String getGUIText(int speed) {
		return I18n.format("gui.advmachine.temp", (speed*100+MAX_SPEED/2)/MAX_SPEED).replace("\\n", "\n");
	}

	@Override
	public String getMachineName() {
		return "tile.advmachine.extruder.name";
	}

	@Override
	public int getNumOutputSlots() {
		return 3;
	}

	@Override
	public RecipeOutput getOutputFor(ItemStack input, boolean consume) {
		return Recipes.metalformerExtruding.getOutputFor(input, consume);
	}

	@Override
	public String getSound() {
		return null;
	}
	
	@Override
	public int getProgressBarLeft() {
		return 78;
	}
	
	@Override
	public int getProgressBarWidth() {
		return 28;
	}
	
	@Override
	public int getProgressBarTop() {
		return 33;
	}
	
	@Override
	public int getProgressBarHeight() {
		return 19;
	}

}
