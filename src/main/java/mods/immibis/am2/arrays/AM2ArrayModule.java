package mods.immibis.am2.arrays;

import mods.immibis.am2.AdvancedMachines;
import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.crossmod.ICrossModIC2;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AM2ArrayModule {

	public static void addRecipes() {
		ICrossModIC2 ic2 = APILocator.getCrossModIC2();
		GameRegistry.addRecipe(new ItemStack(AdvancedMachines.blockArrayDevice, 1, ArrayGeneratorBlock.META_SOLAR),
			"A-", 'A', ic2.getItem("solarPanel"), '-', AdvancedMachines.blockArrayCable);
		GameRegistry.addRecipe(new ItemStack(AdvancedMachines.blockArrayDevice, 1, ArrayGeneratorBlock.META_WATER),
			"A-", 'A', ic2.getItem("waterMill"), '-', AdvancedMachines.blockArrayCable);
		/*GameRegistry.addRecipe(new ItemStack(AdvancedMachines.blockArrayDevice, 1, ArrayGeneratorBlock.META_WIND),
			"A-", 'A', ic2.getItem("windMill"), '-', AdvancedMachines.blockArrayCable);*/
		GameRegistry.addRecipe(new ItemStack(AdvancedMachines.blockArrayCable, 8),
			"TTT", "TRT", "TTT", 'T', ic2.getItem("tinCableItem"), 'R', Items.redstone);
		
		GameRegistry.addRecipe(new ItemStack(AdvancedMachines.blockArrayController),
			"---",
			"-C-",
			"DMD",
			'-', AdvancedMachines.blockArrayCable,
			'C', ic2.getItem("advancedCircuit"),
			'M', ic2.getItem("advancedMachine"),
			'D', Items.diamond
		);
	}

	@SideOnly(Side.CLIENT)
	public static void clientInit() {
		RenderingRegistry.registerBlockHandler(new ArrayCableRenderStatic());
		ArrayCableBlock.model = ArrayCableRenderStatic.renderID;
	}
}
