package com.lothrazar.cyclic.block.generatorsolar;

import com.lothrazar.cyclic.gui.EnergyBar;
import com.lothrazar.cyclic.gui.ScreenBase;
import com.lothrazar.cyclic.registry.TextureRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenGeneratorSolar extends ScreenBase<ContainerGeneratorSolar> {

  private EnergyBar energy;

  public ScreenGeneratorSolar(ContainerGeneratorSolar screenContainer, Inventory inv, Component titleIn) {
    super(screenContainer, inv, titleIn);
    this.energy = new EnergyBar(this, TileGeneratorSolar.MAX);
  }

  @Override
  public void init() {
    super.init();
    energy.guiLeft = leftPos;
    energy.guiTop = topPos;
  }

  @Override
  public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(ms);
    super.render(ms, mouseX, mouseY, partialTicks);
    this.renderTooltip(ms, mouseX, mouseY);
    energy.renderHoveredToolTip(ms, mouseX, mouseY, menu.getEnergy());
  }

  @Override
  protected void renderLabels(PoseStack ms, int mouseX, int mouseY) {
    this.drawButtonTooltips(ms, mouseX, mouseY);
    this.drawName(ms, this.title.getString());
  }

  @Override
  protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(ms, TextureRegistry.INVENTORY);
    energy.draw(ms, menu.getEnergy());
  }
}
