package mods.immibis.am2.item;

import net.minecraft.block.Block;
import mods.immibis.core.ItemCombined;

public class ItemAM2 extends ItemCombined {

	public ItemAM2(Block id) {
		super(id, "advmachine", new String[] {
			"macerator",
			"compressor",
			"extractor",
			"canner",
			"washer",
			"recycler",
			"extruder",
			"roller",
			"cutter"
		});
	}

}
