package mods.immibis.am2.gui;

import mods.immibis.am2.container.AM2Container;
import mods.immibis.core.api.util.BaseGuiContainer;
import net.minecraft.client.resources.I18n;

public class AM2Gui extends BaseGuiContainer<AM2Container> {

	public AM2Gui(AM2Container container) {
		super(container, 176, 166, container.getTile().getGUIResource());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		
		drawTexturedModalRect(guiLeft + container.getTile().getProgressBarLeft(), guiTop + container.getTile().getProgressBarTop(), 177, 14, container.progressPixels, container.getTile().getProgressBarHeight());
		drawTexturedModalRect(guiLeft + 59, guiTop + 50 - container.energyPixels, 179, 14 - container.energyPixels, 7, container.energyPixels);
		
		String name = I18n.format(container.getTile().getMachineName());
		
		fontRendererObj.drawString(name,
			guiLeft + (xSize - fontRendererObj.getStringWidth(name)) / 2,
			guiTop + 5,
			0x404040);
		
		int y = 20;
		for(String line : container.getTile().getGUIText(container.speed).split("\n")) {
			fontRendererObj.drawString(line, guiLeft + 4, guiTop + y, 0x404040);
			y += 12;
		}
		//drawString(container.getTile().get
	}
}
