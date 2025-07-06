package dev.rdh.rust.util.gui;


import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents;

import java.io.Closeable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

#if MC <= 20.1
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
#endif

public abstract class RustContainerWidget
	#if MC <= 20.1
		extends AbstractWidget implements ContainerEventHandler,
	#else
	extends net.minecraft.client.gui.components.AbstractContainerWidget implements
	#endif
	Closeable
{

	protected final List<AbstractWidget> children;

	public RustContainerWidget(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
		this.children = new ObjectArrayList<>();
	}

	public RustContainerWidget(int x, int y, int width, int height) {
		this(x, y, width, height, CommonComponents.EMPTY);
	}

	@Override
	public List<AbstractWidget> children() {
		return new ObjectImmutableList<>(children);
	}

	protected final <T extends AbstractWidget> T addChild(T child) {
		children.add(child);
		return child;
	}

	protected final void addChildrenFromFields() {
		MethodHandles.Lookup L = MethodHandles.privateLookupIn(getClass(), MethodHandles.lookup());
		for (Field field : getClass().getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers()) && AbstractWidget.class.isAssignableFrom(field.getType())) {
				MethodHandle handle = L.unreflectGetter(field);
				AbstractWidget widget = (AbstractWidget) handle.invoke(this);
				if (widget != null) {
					addChild(widget);
				}
			}
		}
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		for (AbstractWidget child : children()) {
			child.render(graphics, mouseX, mouseY, partialTick);
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		for (AbstractWidget child : children()) {
			child.updateNarration(narrationElementOutput);
		}
	}

	@Override
	public void close() {
		for (AbstractWidget child : children()) {
			if (child instanceof Closeable closeable) {
				closeable.close();
			}
		}
	}

	#if MC <= 20.1
	private GuiEventListener focused;
	private boolean isDragging;

	@Override
	public final boolean isDragging() {
		return this.isDragging;
	}

	@Override
	public final void setDragging(boolean isDragging) {
		this.isDragging = isDragging;
	}

	@Override
	public GuiEventListener getFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(GuiEventListener focused) {
		if (this.focused != null) {
			this.focused.setFocused(false);
		}

		if (focused != null) {
			focused.setFocused(true);
		}

		this.focused = focused;
	}

	@Override
	public ComponentPath nextFocusPath(FocusNavigationEvent event) {
		return ContainerEventHandler.super.nextFocusPath(event);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return ContainerEventHandler.super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return ContainerEventHandler.super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		return ContainerEventHandler.super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean isFocused() {
		return ContainerEventHandler.super.isFocused();
	}

	@Override
	public void setFocused(boolean focused) {
		ContainerEventHandler.super.setFocused(focused);
	}
	#endif

	#if MC >= 21.5
	@Override
	protected int contentHeight() {
		return this.getHeight();
	}

	@Override
	protected double scrollRate() {
		return 0;
	}
	#endif
}
