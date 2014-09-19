package mods.immibis.am2.gui;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import mods.immibis.am2.container.ContainerCutter;
import mods.immibis.core.api.util.BaseGuiContainer;

public class GuiCutter extends BaseGuiContainer<ContainerCutter> {

	private static final ResourceLocation TEX_PATH = new ResourceLocation("adv_machines_immibis", "textures/gui/GUICutter.png");
	
	public GuiCutter(ContainerCutter container) {
		super(container, 176, 166, TEX_PATH);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int par1, int par2) {
		super.drawGuiContainerBackgroundLayer(partialTicks, par1, par2);
		
		drawTexturedModalRect(guiLeft + 59, guiTop + 50 - container.energyPixels, 179, 14 - container.energyPixels, 7, container.energyPixels);
		drawTexturedModalRect(guiLeft + 80, guiTop + 34, 177, 14, container.progressPixels, 17);
		
		{
			drawTexturedModalRect(guiLeft + 22, guiTop + 16, 176, 31, 20, 55);
			
			// draw water bar
			mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			
			int waterPixels = Math.min(47, container.waterPixels);
			
			int cur_y = 67 - waterPixels;
			while(waterPixels > 0) {
				if(waterPixels >= 16) {
					drawTexturedModelRectFromIcon(guiLeft + 26, guiTop + cur_y, Blocks.water.getBlockTextureFromSide(0), 12, 16);
				} else {
					// this isn't quite right, it squashes the bottom section
					drawTexturedModelRectFromIcon(guiLeft + 26, guiTop + cur_y, Blocks.water.getBlockTextureFromSide(0), 12, waterPixels);
				}
				waterPixels -= 16;
				cur_y += 16;
			}
			
			mc.renderEngine.bindTexture(TEX_PATH);
			drawTexturedModalRect(guiLeft + 26, guiTop + 20, 196, 35, 12, 47);
		}
		
		String name = I18n.format("tile.advmachine.cutter.name");
		
		fontRendererObj.drawString(name,
			guiLeft + (xSize - fontRendererObj.getStringWidth(name)) / 2,
			guiTop + 5,
			0x404040);
		
		int y = 58;
		for(String line : I18n.format("gui.advmachine.pressure", container.speed).replaceFirst("\\\\n", "\n").replace("\\n"," ").split("\n")) {
			fontRendererObj.drawString(line, guiLeft + 76, guiTop + y, 0x404040);
			y += 12;
		}
	}
	
}
