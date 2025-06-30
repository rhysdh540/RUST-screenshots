package dev.rdh.rust.util.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class SelectionList<E extends SelectionList.Entry<E>> extends ObjectSelectionList<E> {
	public SelectionList(Minecraft minecraft, int i, int j, int k, int l) {
		super(minecraft, i, j, k, l);
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	@Override
	protected int #if MC < 21.5 getScrollbarPosition #else scrollBarX #endif() {
		#if MC >= 21.0
		return this.getRight() - SCROLLBAR_WIDTH;
		#else
		return this.x1 - 6;
		#endif
	}

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
		E newSelection = children().get(Math.min(index, children().size() - 1));

		setSelected(newSelection);
		#if MC >= 21.5
		this.refreshScrollAmount();
		#elif MC >= 21.0
		this.clampScrollAmount();
		#else
		this.setScrollAmount(this.list.getScrollAmount());
		#endif

		return entry;
	}

	public static abstract class Entry<E extends SelectionList.Entry<E>> extends ObjectSelectionList.Entry<E> {
		#if MC < 21.0
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0) {
				ConfigListWidget.this.setSelected(this);
				return true;
			}
			return false;
		}
		#endif
	}
}
