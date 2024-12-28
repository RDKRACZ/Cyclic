package com.lothrazar.cyclic.gui;

import java.util.ArrayList;
import java.util.List;
import com.lothrazar.cyclic.api.IHasTooltip;
import com.lothrazar.cyclic.net.PacketTileData;
import com.lothrazar.cyclic.registry.PacketRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public class GuiSliderInteger extends AbstractSliderButton implements IHasTooltip {

  public static final int ARROW_LEFT = 263;
  public static final int ARROW_RIGHT = 262;
  static final int ESC = 256;
  private final double min;
  private final double max;
  private final BlockPos pos; // Tile entity location
  private final int field; // field ID for saving value
  private List<Component> tooltip;

  public GuiSliderInteger(int x, int y, int width, int height, int field,
      BlockPos pos, int min, int max,
      double initialVal) {
    super(x, y, width, height, TextComponent.EMPTY, 0);
    this.field = field;
    this.pos = pos;
    this.min = min;
    this.max = max;
    setSliderPercentageOfMax((int) initialVal);
    this.updateMessage();
  }

  //  /**
  //   * exact copy of super() but replaced hardcoded 20 with this.height
  //   */
  //  @Override
  //  protected void renderBg(PoseStack matrixStack, Minecraft minecraft, int mouseX, int mouseY) {
  //    //    minecraft.getTextureManager().bind(WIDGETS_LOCATION);
  //    RenderSystem.setShader(GameRenderer::getPositionTexShader);
  //    RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
  //    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
  //    int i = (this.isHovered ? 2 : 1) * 20;
  //    if (this.height != 20) {
  //      this.blit(matrixStack, this.x + (int) (this.value * (this.width - 8)), this.y, 0, 46 + i + 20 - this.height, 4, this.height);
  //      this.blit(matrixStack, this.x + (int) (this.value * (this.width - 8)) + 4, this.y, 196, 46 + i + 20 - this.height, 4, this.height);
  //      int height = this.height - 2;
  //      this.blit(matrixStack, this.x + (int) (this.value * (this.width - 8)), this.y, 0, 46 + i, 4, height);
  //      this.blit(matrixStack, this.x + (int) (this.value * (this.width - 8)) + 4, this.y, 196, 46 + i, 4, height);
  //    }
  //    else {
  //      this.blit(matrixStack, this.x + (int) (this.value * (this.width - 8)), this.y, 0, 46 + i, 4, this.height);
  //      this.blit(matrixStack, this.x + (int) (this.value * (this.width - 8)) + 4, this.y, 196, 46 + i, 4, this.height);
  //    }
  public double getSliderValue() {
    return this.value; // sliderValue
  }

  public int getField() {
    return this.field;
  }

  /**
   * Call from Screen class to render tooltip during mouseover
   */
  @Override
  public List<Component> getTooltip() {
    return tooltip;
  }

  /**
   * Add a tooltip to first line (second line hardcoded)
   */
  @Override
  public void setTooltip(String tt) {
    tooltip = new ArrayList<>();
    addTooltip(tt);
    this.tooltip.add(new TranslatableComponent("cyclic.gui.sliderkeys").withStyle(ChatFormatting.DARK_GRAY));
  }

  @Override
  public void addTooltip(String ttIn) {
    tooltip.add(new TranslatableComponent(ttIn));
  }

  /**
   * Mouse scrolling
   */
  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
    if (delta != 0) {
      moveSliderAndUpdate((int) delta);
      return true;
    }
    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  /**
   * Fires when control is selected, also I call this from screen class whenever mouse is hovered for extra UX
   */
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == ESC) {
      //close
      LocalPlayer pl = Minecraft.getInstance().player;
      if (pl != null) {
        pl.closeContainer();
      }
      return true;
    }
    if (keyCode == ARROW_LEFT || keyCode == ARROW_RIGHT) {
      // move from arrow keys
      int delta = (keyCode == ARROW_LEFT) ? -1 : 1;
      if (Screen.hasShiftDown()) {
        delta = delta * 5;
      }
      else if (Screen.hasAltDown()) {
        delta = delta * 10;
      }
      moveSliderAndUpdate(delta);
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  /**
   * Refresh display message
   */
  @Override
  protected void updateMessage() { // func_230972_a_();
    int val = getSliderAsInteger();
    this.setMessage(new TranslatableComponent("" + val));
  }

  /**
   * Save to tile entity with packet
   */
  @Override
  protected void applyValue() { // func_230979_b_();
    int val = getSliderAsInteger();
    PacketRegistry.INSTANCE.sendToServer(new PacketTileData(this.field, val, pos));
  }

  @Override
  protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
    super.onDrag(mouseX, mouseY, dragX, dragY);
    applyValue();
    updateMessage();
  }

  /**
   * clamp sliderValue to an integer between min and max
   */
  public int getSliderAsInteger() {
    return Mth.floor(Mth.clampedLerp(min, max, this.value));
  }

  /**
   * move position by delta and save and refresh. adds delta to integer version of slider value and sends both updates
   * 
   * @param delta
   */
  private void moveSliderAndUpdate(int delta) {
    setSliderPercentageOfMax(this.getSliderAsInteger() + delta);
    this.updateMessage();
    this.applyValue();
  }

  /**
   * Set inner [0,1] value relative to maximum and trigger save/ & refresh
   */
  private void setSliderPercentageOfMax(int val) {
    this.value = val / max;
  }
}
