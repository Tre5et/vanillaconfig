package net.treset.vanillaconfig.screen.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.treset.vanillaconfig.config.ListConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.screen.widgets.base.GuiClickableWidget;
import org.lwjgl.glfw.GLFW;

public class GuiListWidget extends GuiClickableWidget {
    public static Identifier SLIDER = Identifier.ofVanilla("textures/gui/sprites/widget/slider.png");
    public static Identifier SLIDER_HANDLE = Identifier.ofVanilla("textures/gui/sprites/widget/slider_handle.png");
    public static Identifier SLIDER_HANDLE_HIGHLIGHTED = Identifier.ofVanilla("textures/gui/sprites/widget/slider_handle_highlighted.png");

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
    public String getChangeSliderNarration() { return this.config.getChangeSliderNarration(); }


    @Override
    public Identifier getTexture(int mouseX, int mouseY) {
        if(!this.config.isSlider()) return super.getTexture(mouseX, mouseY);
        return SLIDER;
    }

    @Override
    public boolean renderTexture(DrawContext ctx, int mouseX, int mouseY) {
        boolean success = super.renderTexture(ctx, mouseX, mouseY);

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

            double sliderPercentage = (this.config.getDoubleValue() - this.config.getMinDoubleValue())
                    / (this.config.getMaxDoubleValue() - this.config.getMinDoubleValue());

            int sliderPos = (int)Math.rint(sliderPercentage * (this.getWidth() - 8));

            Identifier identifier = this.isHoveredOver(mouseX, mouseY) || this.selected ? SLIDER_HANDLE_HIGHLIGHTED : SLIDER_HANDLE;

            ctx.drawTexture(RenderLayer::getGuiTextured, identifier, this.screenX + sliderPos, this.screenY, 0, 0, 8, 20, 8, 20);
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
            if(MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(getChangeSliderNarration());
            }
            this.requestIoInterrupt();
        } else if(key == GLFW.GLFW_KEY_LEFT) {
            this.config.setOptionIndex((this.config.getOptions().length + this.config.getOptionIndex() - 1) % this.config.getOptions().length);
            if(MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(getChangeSliderNarration());
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
