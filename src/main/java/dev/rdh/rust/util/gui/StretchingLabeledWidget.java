package dev.rdh.rust.util.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Contains a widget (right aligned) and a label (left aligned) inside a space.
 * @param <W> the type of widget contained
 */
public class StretchingLabeledWidget<W extends AbstractWidget> extends AbstractContainerWidget {
	public final W widget;
	public final StringWidget label;

	private StretchingLabeledWidget(int x, int y, int width, int height, Component label, Font font, W widget) {
		super(x, y, width, height, label);
		this.widget = widget;

		widget.setPosition(x + width - widget.getWidth(), y);

		this.label = new StringWidget(x, y, width - widget.getWidth(), height, label, font).alignLeft();
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		label.render(graphics, mouseX, mouseY, partialTick);
		widget.render(graphics, mouseX, mouseY, partialTick);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		label.updateNarration(output);
		widget.updateNarration(output);
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return List.of(widget, label);
	}

	public static <W extends AbstractWidget> Builder<W> containing(W widget) {
		return new Builder<>(widget);
	}

	public static final class Builder<W extends AbstractWidget> {
		private int x, y, width, height;
		private Component label;
		private Font font;
		private final W widget;

		private Builder(W widget) {
			this.widget = widget;
			this.x = 0;
			this.y = 0;
			this.width = 200;
			this.height = 20;
		}

		public Builder<W> pos(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder<W> size(int width, int height) {
			this.width = width;
			this.height = height;
			return this;
		}

		public Builder<W> label(Component label, Font font) {
			this.label = label;
			this.font = font;
			return this;
		}

		public StretchingLabeledWidget<W> build() {
			if (label == null || font == null || widget == null) {
				throw new IllegalStateException("Label, font, and widget must be set before building.");
			}
			return new StretchingLabeledWidget<>(x, y, width, height, label, font, widget);
		}
	}
}
