package dev.rdh.rust.util.gui;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import org.joml.Matrix4f;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.CommonComponents;

import java.io.Closeable;

public class ImageWidget extends AbstractWidget implements Closeable {
	private final DynamicTexture texture;

	public ImageWidget(int x, int y, int width, int height, NativeImage image) {
		super(x, y, width, height, CommonComponents.EMPTY);
		this.texture = new DynamicTexture(image);
	}

	public ImageWidget(int x, int y, int width, NativeImage image) {
		this(x, y, width, 0, image);
		double ratio = (double) getWidth() / getImageWidth();
		this.height = (int) (ratio * getImageHeight());
		this.setY(this.getY() + this.height / 2);
	}

	public int getImageWidth() {
		return texture.getPixels().getWidth();
	}

	public int getImageHeight() {
		return texture.getPixels().getHeight();
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, texture.getId());
		RenderSystem.enableBlend();

		BufferBuilder bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

		int x = this.getX();
		int y = this.getY();
		int w = this.getWidth();
		int h = this.getHeight();

		bb.addVertex(x, y + h, 0).setUv(0, 1);
		bb.addVertex(x + w, y + h, 0).setUv(1, 1);
		bb.addVertex(x + w, y, 0).setUv(1, 0);
		bb.addVertex(x, y, 0).setUv(0, 0);

		BufferUploader.drawWithShader(bb.buildOrThrow());
		RenderSystem.disableBlend();
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
	}

	@Override
	public void close() {
		texture.close();
	}
}
