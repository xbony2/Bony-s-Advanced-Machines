package mods.immibis.am2.gui;

import mods.immibis.am2.container.ContainerCanner;
import mods.immibis.core.api.util.BaseGuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiCanner extends BaseGuiContainer<ContainerCanner> {

	public GuiCanner(ContainerCanner container) {
		super(container, 176, 166, new ResourceLocation("adv_machines_immibis", "textures/gui/GUICanner.png"));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int par1, int par2) {
		super.drawGuiContainerBackgroundLayer(partialTicks, par1, par2);
		
		drawTexturedModalRect(guiLeft+84, guiTop+24, 176, 14, 6, container.fillPixels);
		drawTexturedModalRect(guiLeft + 124, guiTop + 22 - container.energyPixels, 179, 14 - container.energyPixels, 7, container.energyPixels);
		
		mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
		
		for(int k = 0; k < 8; k++) {
			int id = container.movingCanIDs[k];
			int meta = container.movingCanMeta[k];
			if(id != 0) {
				ItemStack can = new ItemStack(Item.getItemById(id), 1, meta);
				drawTexturedModelRectFromIcon(15 + k*16 + container.canPositionShift + guiLeft, 48 + guiTop, can.getIconIndex(), 16, 16);
			}
		}
		
		String name = I18n.format("tile.advmachine.canner.name");
		
		fontRendererObj.drawString(name,
			guiLeft + (xSize/2 - fontRendererObj.getStringWidth(name)) / 2,
			guiTop + 30,
			0x404040);
		
		int y = 67;
		for(String line : container.getTile().getGUIText(container.speed).split("\n")) {
			fontRendererObj.drawString(line, guiLeft + 8, guiTop + y, 0x404040);
			y += 12;
		}
	}

}
