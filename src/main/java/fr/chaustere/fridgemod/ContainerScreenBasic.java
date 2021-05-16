package fr.chaustere.fridgemod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class ContainerScreenBasic extends ContainerScreen<ContainerBasic>
{
    public ContainerScreenBasic(ContainerBasic containerBasic, PlayerInventory playerInventory, ITextComponent title) {
        super(containerBasic, playerInventory, title);

        xSize = 256;
        ySize = 133;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        final float LABEL_XPOS = 5;
        final float FONT_Y_SPACING = 12;
        final float CHEST_LABEL_YPOS = ContainerBasic.TILE_INVENTORY_YPOS - FONT_Y_SPACING;
        //this.font.drawString(matrixStack, TITLE,
        //        LABEL_XPOS, CHEST_LABEL_YPOS, Color.darkGray.getRGB());
        this.font.drawString(matrixStack, TITLE,
                LABEL_XPOS, CHEST_LABEL_YPOS, Color.darkGray.getRGB());
        drawWrappedString(matrixStack, DESC_TEXT, 175, 20, Color.darkGray.getRGB(), 14, 10);

        final float PLAYER_INV_LABEL_YPOS = ContainerBasic.PLAYER_INVENTORY_YPOS - FONT_Y_SPACING;
        this.font.drawString(matrixStack, this.playerInventory.getDisplayName().getString(),
                LABEL_XPOS, PLAYER_INV_LABEL_YPOS, Color.darkGray.getRGB());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);

        int edgeSpacingX = (this.width - this.xSize) / 2;
        int edgeSpacingY = (this.height - this.ySize) / 2;
        this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, this.xSize, this.ySize);
    }

    public void drawWrappedString(MatrixStack matrixStack, String str, float x, float y, int color, int maxCharactersInLine, int spaceBetweenLines) {
        String[] words = str.split(" ");
        int j = 0;
        int i = 0;
        //for (int i = 0; i < (str.length() / maxCharactersInLine); i++)
        while (j < words.length)
        {
            /*
            int min = i * maxCharactersInLine;
            int max = min + maxCharactersInLine;

            if (max > str.length()-1) {
                max = str.length()-1;
            }
            */
            //String wrappedText = str.substring(min, max);

            String wrappedText = words[j];
            j++;
            boolean canAddOneMore = true;
            while (canAddOneMore && j < words.length) {
                if (wrappedText.length() + words[j].length() < maxCharactersInLine) {
                    wrappedText = wrappedText + " " + words[j];
                    j++;
                }
                else {
                    canAddOneMore = false;
                }
            }

            this.font.drawString(matrixStack, wrappedText, x, y + spaceBetweenLines * i, color);
            i++;
        }
    }

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("fridgemod", "textures/gui/fridge_gui.png");
    private static final String TITLE = "Fridge Made In France by Chaustere(C)";
    private static final String DESC_TEXT = "Ca m'a mis une semaine pour faire ca, mais j'y suis arrive ! C'est complique de se mettre au modding, mais je pense que je vais continuer ^^";
}
