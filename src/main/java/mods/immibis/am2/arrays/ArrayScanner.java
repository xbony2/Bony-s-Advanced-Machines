package mods.immibis.am2.arrays;

import ic2.core.WorldData;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import mods.immibis.am2.AdvancedMachines;
import mods.immibis.core.api.util.XYZ;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

class ArrayScanner {

	private ArrayControllerTile rootTile;
	public ArrayScanner(ArrayControllerTile root) {
		this.rootTile = root;
	}
	
	private Set<XYZ> visited = new HashSet<XYZ>();
	private Queue<XYZ> bfsQueue = new LinkedList<XYZ>();
	private World w;
	
	int numCables, numSolars, numControllers, numWaterBlocks, numBlocksScanned;
	double totalWindGenPower; // EU/tick

	public void tick() {
		if(bfsQueue.isEmpty()) {
			numBlocksScanned = visited.size();
			visited.clear();
			
			rootTile.onScanFinished();
			
			XYZ start = new XYZ(rootTile.xCoord, rootTile.yCoord, rootTile.zCoord);
			visited.add(start);
			for(int dir = 0; dir < 6; dir++)
				bfsQueue.add(start.step(dir));
			
			w = rootTile.getWorldObj();
			numCables = 0;
			numControllers = 0;
			numSolars = 0;
			numWaterBlocks = 0;
			totalWindGenPower = 0;
		}
		
		XYZ xyz;
		do {
			xyz = bfsQueue.poll();
			if(xyz == null)
				return;
		} while(!visited.add(xyz));
		
		if(!w.blockExists(xyz.x, xyz.y, xyz.z))
			return;
		
		Block id = w.getBlock(xyz.x, xyz.y, xyz.z);
		
		if(id == AdvancedMachines.blockArrayCable) {
			for(int dir = 0; dir < 6; dir++)
				bfsQueue.add(xyz.step(dir));
			numCables++;
			
		} else if(id == AdvancedMachines.blockArrayController) {
			numControllers++;
			
		} else if(id == AdvancedMachines.blockArrayDevice) {
			switch(w.getBlockMetadata(xyz.x, xyz.y, xyz.z) & 3) {
			case ArrayGeneratorBlock.META_SOLAR:
				if(checkSolarActive(xyz))
					numSolars++;
				break;
				
			case ArrayGeneratorBlock.META_WATER:
				numWaterBlocks += countWaterBlocks(xyz);
				break;
				
			case ArrayGeneratorBlock.META_WIND:
				
				double genPower = WorldData.get(w).windSim.getWindAt(xyz.y) / 10;
				genPower *= (1 - getObscuredBlockCount(xyz) / 567);
				if(genPower < 5)
					totalWindGenPower += genPower;
				
				break;
			}
		}
		
		
		
	}

	private int getObscuredBlockCount(XYZ p) {
		int rv = 0;
		for(int dz = -4; dz <= 4; dz++)
		for(int dx = -4; dx <= 4; dx++)
		for(int dy = -2; dy <= 4; dy++) {
			int x=p.x+dx, y=p.y+dy, z=p.z+dz;
			if(!w.blockExists(x, y, z) || !w.isAirBlock(x, y, z))
				rv++;
		}
		return rv;
	}

	private boolean checkSolarActive(XYZ p) {
		return w.canBlockSeeTheSky(p.x, p.y+1, p.z) && w.getBlock(p.x, p.y+1, p.z) != Blocks.snow_layer;
	}
	
	private int countWaterBlocks(XYZ p) {
		int rv = 0;
		for(int dz = -1; dz <= 1; dz++)
		for(int dx = -1; dx <= 1; dx++)
		for(int dy = -1; dy <= 1; dy++)
			if(w.blockExists(p.x+dx, p.y+dy, p.z+dz)) {
				Block id = w.getBlock(p.x+dx, p.y+dy, p.z+dz);
				if(id == Blocks.flowing_water || id == Blocks.water)
					rv++;
			}
		return rv;
	}

}
