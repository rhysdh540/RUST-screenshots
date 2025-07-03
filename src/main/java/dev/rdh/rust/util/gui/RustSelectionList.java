package dev.rdh.rust.util.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;

import java.io.Closeable;

public class RustSelectionList<E extends RustSelectionList.Entry<E>> extends ObjectSelectionList<E> implements Closeable {
	public RustSelectionList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, width, height, y, #if MC < 21.0 y + height, #endif itemHeight);
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int #if MC < 21.5 getScrollbarPosition #else scrollBarX #endif() {
		#if MC < 21.0
		return this.x1 - 6;
		#else
		return this.getRight() - SCROLLBAR_WIDTH;
		#endif
	}

	#if MC < 21.0
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
	}

	public int getY() {
		return this.y0;
	}
	#endif

	#if MC >= 21.0
	@Override
	protected void renderSelection(GuiGraphics graphics, int top, int width, int height, int outerColor, int innerColor) {
		if (this.scrollbarVisible()) {
			int left = this.getRowLeft() - 2;
			int right = this.getRight() - SCROLLBAR_WIDTH - 1;
			graphics.fill(left, top - 2, right, top + height + 2, outerColor);
			graphics.fill(left + 1, top - 1, right - 1, top + height + 1, innerColor);
		} else {
			super.renderSelection(graphics, top, width, height, outerColor, innerColor);
		}
	}
	#endif

	public E removeSelected() {
		E entry = getSelected();
		if (entry == null) return null;

		int index = children().indexOf(entry);
		children().remove(entry);
		E newSelection;

		if (children().isEmpty()) {
			newSelection = null;
		} else {
			newSelection = children().get(Math.min(index, children().size() - 1));
		}

		setSelected(newSelection);
		#if MC < 21.0
		this.setScrollAmount(this.getScrollAmount());
		#elif MC < 21.5
		this.clampScrollAmount();
		#else
		this.refreshScrollAmount();
		#endif

		return entry;
	}

	@Override
	public void close() {
		for (var child : this.children()) {
			if (child instanceof Closeable c) {
				c.close();
			}
		}
		this.children().clear();
	}

	public static abstract class Entry<E extends RustSelectionList.Entry<E>> extends ObjectSelectionList.Entry<E> {
		#if MC < 21.0
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return true;
		}
		#endif
	}
}
