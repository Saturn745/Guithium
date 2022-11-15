package net.pl3x.servergui.fabric.gui.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.pl3x.servergui.api.gui.element.Image;
import net.pl3x.servergui.fabric.ServerGUIFabric;
import net.pl3x.servergui.fabric.gui.screen.RenderableScreen;
import net.pl3x.servergui.fabric.gui.texture.Texture;
import org.jetbrains.annotations.NotNull;

public class RenderableImage extends RenderableElement {
    private Texture texture;

    public RenderableImage(Image image, RenderableScreen screen) {
        super(image, screen);
    }

    @Override
    public Image getElement() {
        return (Image) super.getElement();
    }

    public Texture getTexture() {
        return this.texture;
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float delta) {
        Image image = getElement();
        if (image.getSize() == null) {
            return;
        }

        if (getTexture() == null) {
            this.texture = ServerGUIFabric.instance().getTextureManager().get(image.getKey());
            return;
        }

        if (!getTexture().isLoaded()) {
            return;
        }

        matrix.push();

        calcScreenPos(image.getSize().getX(), image.getSize().getY(), setupScaleAndZIndex(matrix));

        float x0 = getScreenPos().getX();
        float y0 = getScreenPos().getY();
        float x1 = x0 + image.getSize().getX();
        float y1 = y0 + image.getSize().getY();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, getTexture().getIdentifier());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        Matrix4f model = matrix.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(model, x0, y0, 0F).texture(0, 0).next();
        bufferBuilder.vertex(model, x0, y1, 0F).texture(0, 1).next();
        bufferBuilder.vertex(model, x1, y1, 0F).texture(1, 1).next();
        bufferBuilder.vertex(model, x1, y0, 0F).texture(1, 0).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());

        matrix.pop();
    }
}
