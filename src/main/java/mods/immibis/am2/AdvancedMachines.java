package mods.immibis.am2;

import mods.immibis.am2.arrays.AM2ArrayModule;
import mods.immibis.am2.arrays.ArrayCableBlock;
import mods.immibis.am2.arrays.ArrayCableItem;
import mods.immibis.am2.arrays.ArrayControllerBlock;
import mods.immibis.am2.arrays.ArrayControllerTile;
import mods.immibis.am2.arrays.ArrayGeneratorBlock;
import mods.immibis.am2.arrays.ArrayGeneratorItem;
import mods.immibis.am2.container.ContainerAM2;
import mods.immibis.am2.container.ContainerCanner;
import mods.immibis.am2.container.ContainerCutter;
import mods.immibis.am2.container.ContainerWasher;
import mods.immibis.am2.gui.GuiAM2;
import mods.immibis.am2.gui.GuiCanner;
import mods.immibis.am2.gui.GuiCutter;
import mods.immibis.am2.gui.GuiWasher;
import mods.immibis.am2.item.ItemAM2;
import mods.immibis.am2.item.ItemSharpPlate;
import mods.immibis.am2.tileentity.TileAM2Canner;
import mods.immibis.am2.tileentity.TileAM2Compressor;
import mods.immibis.am2.tileentity.TileAM2Cutter;
import mods.immibis.am2.tileentity.TileAM2Extractor;
import mods.immibis.am2.tileentity.TileAM2Extruder;
import mods.immibis.am2.tileentity.TileAM2Macerator;
import mods.immibis.am2.tileentity.TileAM2Recycler;
import mods.immibis.am2.tileentity.TileAM2Roller;
import mods.immibis.am2.tileentity.TileAM2Washer;
import mods.immibis.cobaltite.AssignedBlock;
import mods.immibis.cobaltite.AssignedItem;
import mods.immibis.cobaltite.CobaltiteMod;
import mods.immibis.cobaltite.CobaltiteMod.RegisteredTile;
import mods.immibis.cobaltite.Configurable;
import mods.immibis.cobaltite.ModBase;
import mods.immibis.cobaltite.TileGUI;
import mods.immibis.core.api.APILocator;
import mods.immibis.core.api.FMLModInfo;
import mods.immibis.core.api.crossmod.ICrossModIC2;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "AdvancedMachines", version = "59.0.1", dependencies = "required-after:IC2;required-after:ImmibisCore", name = "Advanced Machines")
@CobaltiteMod(tiles = {
	@RegisteredTile(id = "Rotary Macerator", tile = TileAM2Macerator.class),
	@RegisteredTile(id = "Singularity Compressor", tile = TileAM2Compressor.class),
	@RegisteredTile(id = "Centrifuge Extractor", tile = TileAM2Extractor.class),
	@RegisteredTile(id = "immibis.am2.canner", tile = TileAM2Canner.class),
	@RegisteredTile(id = "immibis.am2.washer", tile = TileAM2Washer.class),
	@RegisteredTile(id = "immibis.am2.recycler", tile = TileAM2Recycler.class),
	@RegisteredTile(id = "immibis.am2.arraycontroller", tile = ArrayControllerTile.class),
	@RegisteredTile(id = "immibis.am2.extruder", tile = TileAM2Extruder.class),
	@RegisteredTile(id = "immibis.am2.roller", tile = TileAM2Roller.class),
	@RegisteredTile(id = "immibis.am2.cutter", tile = TileAM2Cutter.class),
})
@FMLModInfo(authors="immibis", description="Upgraded IC2 machines", url="http://www.minecraftforum.net/topic/1001131-/")
public class AdvancedMachines extends ModBase {
	@AssignedBlock(id = "block", item = ItemAM2.class)
	public static BlockAM2 block;
	
	// TODO we have no way to disable this block when enableArrayModule is false
	@AssignedBlock(id = "arrayCable", item = ArrayCableItem.class)
	public static ArrayCableBlock blockArrayCable;
	
	// TODO same for this
	@AssignedBlock(id = "arrayDevice", item = ArrayGeneratorItem.class)
	public static ArrayGeneratorBlock blockArrayDevice;
	
	// TODO same for this
	@AssignedBlock(id = "arrayController")
	public static ArrayControllerBlock blockArrayController;
	
	@AssignedItem(id = "sharpPlate")
	public static ItemSharpPlate sharpPlate;
	
	@TileGUI(container = ContainerAM2.class, gui = GuiAM2.class)
	public static final int GUI_PROCESSOR = 0;
	
	@TileGUI(container = ContainerCanner.class, gui = GuiCanner.class)
	public static final int GUI_CANNER = 1;
	
	@TileGUI(container = ContainerWasher.class, gui = GuiWasher.class)
	public static final int GUI_WASHER = 2;
	
	@TileGUI(container = ContainerCutter.class, gui = GuiCutter.class)
	public static final int GUI_CUTTER = 3;

	@Instance("AdvancedMachines")
	public static AdvancedMachines INSTANCE;
	
	@Configurable("idleEUPerTick")
	public static int idleEUPerTick = 1;
	
	@Configurable("runningEUPerTick")
	public static int runningEUPerTick = 16;
	
	@Configurable("idleEUPerTick_washer")
	public static int idleEUPerTick_washer = 6;
	
	@Configurable("runningEUPerTick_washer")
	public static int runningEUPerTick_washer = 48;
	
	@Configurable("idleEUPerTick_cutter")
	public static int idleEUPerTick_cutter = 1;
	
	@Configurable("runningEUPerTick_cutter")
	public static int runningEUPerTick_cutter = 24;
	
	@Configurable("idleEUPerTick_formers")
	public static int idleEUPerTick_formers = 1;
	
	@Configurable("runningEUPerTick_formers")
	public static int runningEUPerTick_formers = 24;
	
	@Configurable("enableAdvancedMetalFormers")
	public static boolean enableAdvancedMetalFormers = true;
	
	@Configurable("maxVoltage")
	public static int maxVoltage = 128;
	
	@Configurable("spinUpRate")
	public static int spinUpRate = 1;
	
	@Configurable("spinDownRate")
	public static int spinDownRate = 2;
	
	@Configurable("enableArrayModule_actual")
	public static boolean enableArrayModule = true;
	
	@Configurable("maxArrayWindmillEffectiveHeight")
	public static int maxArrayWindmillEffectiveHeight = 147;
	
	@Configurable("enableExplosions")
	public static boolean enableExplosions = false;
	
	@Configurable("useOreDictionaryIronPlate")
	public static boolean useOreDictionaryIronPlate = false;
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void clientInit() throws Exception {
		if(enableArrayModule)
			AM2ArrayModule.clientInit();
	}
	
	@Override
	protected void addRecipes() throws Exception {
		if(enableArrayModule)
			AM2ArrayModule.addRecipes();
		
		ICrossModIC2 items = APILocator.getCrossModIC2();
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(sharpPlate),
			"^^^",
			"^#^",
			"^^^",
			'^', Items.flint,
			'#', useOreDictionaryIronPlate ? "plateIron" : items.getItem("plateiron")
			));
		GameRegistry.addRecipe(new ItemStack(block, 1, BlockAM2.META_MACERATOR),
			"###",
			"#M#",
			"#X#",
			'#', sharpPlate, //items.getItem("refinedIronIngot"),
			'M', items.getItem("macerator"),
			'X', items.getItem("advancedMachine"));
		GameRegistry.addRecipe(new ItemStack(block, 1, BlockAM2.META_EXTRACTOR),
			"###",
			"#M#",
			"#X#",
			'#', items.getItem("electrolyzedWaterCell"),
			'M', items.getItem("extractor"),
			'X', items.getItem("advancedMachine"));
		GameRegistry.addRecipe(new ItemStack(block, 1, BlockAM2.META_COMPRESSOR),
			"###",
			"#M#",
			"#X#",
			'#', Blocks.obsidian,
			'M', items.getItem("compressor"),
			'X', items.getItem("advancedMachine"));
		GameRegistry.addRecipe(new ItemStack(block, 1, BlockAM2.META_CANNER),
			"#P#",
			"#M#",
			"#X#",
			'P', items.getItem("pump"),
			'M', items.getItem("canner"),
			'X', items.getItem("advancedMachine"),
			'#', items.getItem("airCell"));
		GameRegistry.addRecipe(new ItemStack(block, 1, BlockAM2.META_WASHER),
			"###",
			"#M#",
			"#X#",
			'M', items.getItem("orewashingplant"),
			'#', items.getItem("sulfurDust"),
			'X', items.getItem("advancedMachine"));
		GameRegistry.addRecipe(new ItemStack(block, 1, BlockAM2.META_RECYCLER),
			"###",
			"#M#",
			"#X#",
			'M', items.getItem("recycler"),
			'X', items.getItem("advancedMachine"),
			'#', Blocks.piston);
		
		if(enableAdvancedMetalFormers) {
			GameRegistry.addRecipe(new ItemStack(block, 1, BlockAM2.META_EXTRUDER),
				"###",
				"#M#",
				"#X#",
				'M', items.getItem("metalformer"),
				'X', items.getItem("advancedMachine"),
				'#', items.getItem("ironFence"));
			GameRegistry.addRecipe(new ItemStack(block, 1, BlockAM2.META_ROLLER),
				"###",
				"#M#",
				"#X#",
				'M', items.getItem("metalformer"),
				'X', items.getItem("advancedMachine"),
				'#', items.getItem("ForgeHammer"));
			GameRegistry.addRecipe(new ItemStack(block, 1, BlockAM2.META_CUTTER),
				"###",
				"#M#",
				"#X#",
				'M', items.getItem("metalformer"),
				'X', items.getItem("advancedMachine"),
				'#', items.getItem("cutter"));
		}
	}
	
	@EventHandler
	public void base_preinit(FMLPreInitializationEvent evt) {super._preinit(evt);}
	@EventHandler
	public void base_init(FMLInitializationEvent evt) {super._init(evt);}

	private static ICrossModIC2 ic2;
	public static boolean isEmptyBattery(ItemStack stack, int tier) {
		synchronized(AdvancedMachines.class) {
			if(ic2 == null)
				ic2 = APILocator.getCrossModIC2();
		}
		
		return stack != null && ic2.dischargeElectricItem(stack, 1, tier, false, true) == 0;
	}
}
