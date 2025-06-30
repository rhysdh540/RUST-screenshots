package dev.rdh.rust.util.gui;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceImmutableList;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents;

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

public abstract class AbstractContainerWidget
	#if MC <= 20.1
		extends AbstractWidget implements ContainerEventHandler
	#else
	extends net.minecraft.client.gui.components.AbstractContainerWidget
	#endif
{

	private final List<AbstractWidget> children;

	public AbstractContainerWidget(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
		this.children = new ReferenceArrayList<>();
	}

	public AbstractContainerWidget(int x, int y, int width, int height) {
		this(x, y, width, height, CommonComponents.EMPTY);
	}

	@Override
	public List<AbstractWidget> children() {
		return new ReferenceImmutableList<>(children);
	}

	protected final <T extends AbstractWidget> T addChild(T child) {
		children.add(child);
		return child;
	}

	protected final void addChildrenFromFields() {
		try {
			for (Field field : getClass().getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers()) && AbstractWidget.class.isAssignableFrom(field.getType())) {
					AbstractWidget widget = (AbstractWidget) field.get(this);
					if (widget != null) {
						addChild(widget);
					}
				}
			}

		} catch (Throwable t) {
			throw new RuntimeException("Failed to access widget fields", t);
		}
	}

	#if MC <= 20.1
	@javax.annotation.Nullable
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

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(@Nullable GuiEventListener focused) {
		if (this.focused != null) {
			this.focused.setFocused(false);
		}

		if (focused != null) {
			focused.setFocused(true);
		}

		this.focused = focused;
	}

	@Nullable
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
