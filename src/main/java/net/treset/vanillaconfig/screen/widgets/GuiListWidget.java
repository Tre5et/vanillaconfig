package net.treset.vanillaconfig.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.treset.vanillaconfig.config.ListConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiClickableWidget;
import net.treset.vanillaconfig.tools.TextTools;
import org.lwjgl.glfw.GLFW;

public class GuiListWidget extends GuiClickableWidget {
    ListConfig config;

    boolean isMouseDown = false;
    boolean mouseWentDownOver = false;

    public GuiListWidget(ListConfig config, ConfigScreen screen) {
        super(config, screen);

        this.config = config;
    }

    public String updateMessage(ListConfig config) {
        if(config == null) return "ERROR";
        this.setTitle(config.getKey());
        this.setValue(config.getOption());
        return this.getMessage();
    }
    public String updateMessage() {
        return updateMessage(this.config);
    }

    @Override
    public String getSelectNarration() { return this.config.getSelectNarration(); }
    @Override
    public String getActivateNarration() { return this.config.getActivateNarration(); }
    public String getChangeSliderNarration() { return this.config.getChangeNarration(); }


    @Override
    public int getTextureOffset(int mouseX, int mouseY) {
        if(!this.config.isSlider()) return super.getTextureOffset(mouseX, mouseY);
        return 0;
    }

    @Override
    public boolean renderTexture(DrawableHelper d, MatrixStack m, int mouseX, int mouseY) {
        boolean success = super.renderTexture(d, m, mouseX, mouseY);

        if(this.config.isSlider()) {

            if(this.isMouseDown && this.mouseWentDownOver) {
                double mousePercentage = Math.max(0D, Math.min(1D,
                        (mouseX - this.screenX - 4) / (double)(this.getWidth() - 8)
                ));

                int mouseValue = (int)Math.rint(
                        mousePercentage
                        * (this.config.getMaxDoubleValue() - this.config.getMinDoubleValue())
                        + this.config.getMinDoubleValue());

                this.config.setOptionIndex(mouseValue);


            }

            RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            double sliderPercentage = (this.config.getDoubleValue() - this.config.getMinDoubleValue())
                    / (this.config.getMaxDoubleValue() - this.config.getMinDoubleValue());

            int sliderPos = (int)Math.rint(sliderPercentage * (this.getWidth() - 8));

            int offset = (this.isHoveredOver(mouseX, mouseY) || this.selected ? 2 : 1) * 20;

            d.drawTexture(m, this.screenX + sliderPos, this.screenY, 0, 46 + offset, 4, 20);
            d.drawTexture(m, this.screenX + sliderPos + 4, this.screenY, 196, 46 + offset, 4, 20);
        } else isMouseDown = false;

        return success;
    }

    @Override
    public void onRender() {
        updateMessage();
        super.onRender();
    }

    @Override
    public void onKeyDown(int key, int scancode) {
        if(!this.config.isSlider()) super.onKeyDown(key, scancode);

        if(!this.selected) return;
        if(key == GLFW.GLFW_KEY_RIGHT) {
            this.config.setOptionIndex((this.config.getOptionIndex() + 1) % this.config.getOptions().length);
            if(NarratorManager.INSTANCE.isActive()) {
                NarratorManager.INSTANCE.narrate(getChangeSliderNarration());
            }
            this.requestIoInterrupt();
        } else if(key == GLFW.GLFW_KEY_LEFT) {
            this.config.setOptionIndex((this.config.getOptions().length + this.config.getOptionIndex() - 1) % this.config.getOptions().length);
            if(NarratorManager.INSTANCE.isActive()) {
                NarratorManager.INSTANCE.narrate(getChangeSliderNarration());
            }
            this.requestIoInterrupt();
        }
    }

    @Override
    public void onClickL() {
        if(!this.config.isSlider()) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            config.increment(true);
        }
        this.mouseWentDownOver = true;
    }

    @Override
    public void onClickR() {
        if(!this.config.isSlider()) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            config.increment(false);
        }
    }

    @Override
    public void onMouseDown(int button) {
        super.onMouseDown(button);
        if(this.config.isSlider() && button == 0) isMouseDown = true;
    }
    @Override
    public void onMouseUp(int button) {
        super.onMouseUp(button);
        if(this.config.isSlider() && button == 0 && isMouseDown) {
            isMouseDown = false;
            if(mouseWentDownOver) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                mouseWentDownOver = false;
            }
        }
    }
}
