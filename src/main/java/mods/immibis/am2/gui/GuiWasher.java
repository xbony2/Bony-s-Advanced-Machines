package mods.immibis.am2.gui;

import mods.immibis.am2.container.ContainerWasher;
import mods.immibis.am2.tileentity.TileAM2Washer;
import mods.immibis.core.api.util.BaseGuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class GuiWasher extends BaseGuiContainer<ContainerWasher> {
	
	private static final ResourceLocation TEX_PATH = new ResourceLocation("adv_machines_immibis", "textures/gui/GUIWasher.png"); 

	public GuiWasher(ContainerWasher container) {
		super(container, 176, 166, TEX_PATH);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int par1, int par2) {
		super.drawGuiContainerBackgroundLayer(partialTicks, par1, par2);
		
		drawTexturedModalRect(guiLeft + 156, guiTop + 57 - container.energyPixels, 179, 14 - container.energyPixels, 7, container.energyPixels);

		if(container.progress >= 4)
			drawTexturedModalRect(guiLeft + 103, guiTop + 47, 177, 126, 7, 9);
		if(container.progress >= 3)
			drawTexturedModalRect(guiLeft + 111, guiTop + 49, 185, 128, 9, 7);
		if(container.progress >= 2)
			drawTexturedModalRect(guiLeft + 114, guiTop + 39, 188, 118, 7, 9);
		if(container.progress >= 1)
			drawTexturedModalRect(guiLeft + 104, guiTop + 39, 178, 118, 9, 7);
		
		{
			drawTexturedModalRect(guiLeft + 60, guiTop + 20, 176, 15, 20, 55);
			
			// draw water bar
			mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			
			int waterPixels = (container.water * 47) / TileAM2Washer.MAX_WATER;
			
			int cur_y = 71 - waterPixels;
			while(waterPixels > 0) {
				if(waterPixels >= 16) {
					drawTexturedModelRectFromIcon(guiLeft + 64, guiTop + cur_y, Blocks.water.getBlockTextureFromSide(0), 12, 16);
				} else {
					// this isn't quite right, it squashes the bottom section
					drawTexturedModelRectFromIcon(guiLeft + 64, guiTop + cur_y, Blocks.water.getBlockTextureFromSide(0), 12, waterPixels);
				}
				waterPixels -= 16;
				cur_y += 16;
			}
			
			mc.renderEngine.bindTexture(TEX_PATH);
			drawTexturedModalRect(guiLeft + 64, guiTop + 24, 176, 70, 12, 47);
		}
		
		String name = I18n.format("tile.advmachine.washer.name");
		
		fontRendererObj.drawString(name,
			guiLeft + (xSize - fontRendererObj.getStringWidth(name)) / 2,
			guiTop + 5,
			0x404040);
		
		int y = 25;
		for(String line : I18n.format("gui.advmachine.temp", container.speed/100).replace("\\n", "\n").split("\n")) {
			fontRendererObj.drawString(line, guiLeft + 6, guiTop + y, 0x404040);
			y += 12;
		}
	}

}
