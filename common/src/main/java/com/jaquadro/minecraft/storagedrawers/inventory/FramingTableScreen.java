package com.jaquadro.minecraft.storagedrawers.inventory;

import com.jaquadro.minecraft.storagedrawers.ModConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import com.texelsaurus.minecraft.chameleon.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FramingTableScreen extends AbstractContainerScreen<ContainerFramingTable>
{
    private static final ResourceLocation guiTextires = ModConstants.loc("textures/gui/framing.png");

    private final ResourceLocation background;
    private final Inventory inventory;

    public FramingTableScreen (ContainerFramingTable container, Inventory playerInv, Component name) {
        super(container, playerInv, name, 176, 166);

        background = guiTextires;
        inventory = playerInv;
    }

    @Override
    public void extractContents (@NotNull GuiGraphicsExtractor graphics, int x, int y, float f) {
        renderBg(graphics, f, x, y);
        super.extractContents(graphics, x, y, f);
    }

    @Override
    protected void extractLabels (GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, 8, 6, 0xFF404040, false);
        graphics.text(this.font, this.inventory.getDisplayName().getString(), 8, this.imageHeight - 96 + 2, 0xFF404040, false);
    }

    protected void renderBg (GuiGraphicsExtractor graphics, float partialTicks, int mouseX, int mouseY) {
        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, background.asIdentifier(), guiX, guiY, 0, 0, imageWidth, imageHeight, 256, 256);
    }
}
