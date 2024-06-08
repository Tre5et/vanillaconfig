package net.treset.vanillaconfig.screen.widgets.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.treset.vanillaconfig.config.base.SlideableConfig;
import net.treset.vanillaconfig.screen.ConfigScreen;
import net.treset.vanillaconfig.tools.TextTools;
import org.lwjgl.glfw.GLFW;

public class GuiNumberWidget extends GuiTypableWidget {
    public static Identifier SLIDER = Identifier.ofVanilla("textures/gui/sprites/widget/slider.png");
    public static Identifier SLIDER_HANDLE = Identifier.ofVanilla("textures/gui/sprites/widget/slider_handle.png");
    public static Identifier SLIDER_HANDLE_HIGHLIGHTED = Identifier.ofVanilla("textures/gui/sprites/widget/slider_handle_highlighted.png");

    SlideableConfig slideConfig;

    boolean isMouseDown = false;
    boolean mouseWentDownOver = false;

    public GuiNumberWidget(SlideableConfig config, String title, String value, ConfigScreen screen) {
        super(config, title, value, screen);

        this.slideConfig = config;
    }
    public GuiNumberWidget(SlideableConfig config, ConfigScreen screen) {
        this(config, "", "", screen);
    }

    public String initMessage() { return ""; }

    public String getChangeSliderNarration() { return ""; }

    @Override
    public Identifier getTexture(int mouseX, int mouseY) {
        if(!this.slideConfig.isSlider()) return super.getTexture(mouseX, mouseY);
        return SLIDER;
    }

    @Override
    public void onKeyDown(int key, int scancode) {
        if(!this.slideConfig.isSlider()) super.onKeyDown(key, scancode);

        if(!this.selected) return;
        if(key == GLFW.GLFW_KEY_RIGHT) {
            this.slideConfig.setDoubleValue(this.slideConfig.getDoubleValue() + 1);
            if(MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(getChangeSliderNarration());
            }
            this.requestIoInterrupt();
        } else if(key == GLFW.GLFW_KEY_LEFT) {
            this.slideConfig.setDoubleValue(this.slideConfig.getDoubleValue() - 1);
            if(MinecraftClient.getInstance().getNarratorManager().isActive()) {
                MinecraftClient.getInstance().getNarratorManager().narrate(getChangeSliderNarration());
            }
            this.requestIoInterrupt();
        }
    }
    @Override
    public void onTextReceived(String text) {
        if(!this.slideConfig.isSlider()) super.onTextReceived(text);
    }
    @Override
    public void onClickL() {
        if(!this.slideConfig.isSlider()) {
            super.onClickL();
            return;
        }
        this.mouseWentDownOver = true;
    }
    @Override
    public void onClickR() {
        if(!this.slideConfig.isSlider()) super.onClickR();
    }

    @Override
    public void onMouseDown(int button) {
        super.onMouseDown(button);
        if(this.slideConfig.isSlider() && button == 0) isMouseDown = true;
    }
    @Override
    public void onMouseUp(int button) {
        super.onMouseUp(button);
        if(this.slideConfig.isSlider() && button == 0 && isMouseDown) {
            isMouseDown = false;
            if(mouseWentDownOver) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                mouseWentDownOver = false;
            }
        }
    }

    @Override
    public boolean renderTexture(DrawContext ctx, int mouseX, int mouseY) {
        boolean success = super.renderTexture(ctx, mouseX, mouseY);

        if(this.slideConfig.isSlider()) {
            this.setFocused(false);
            this.save();

            if(this.isMouseDown && this.mouseWentDownOver) {
                double mousePercentage = Math.max(0D, Math.min(1D,
                        (mouseX - this.screenX - 4) / (double)(this.getWidth() - 8)
                ));

                double mouseValue = mousePercentage
                        * (this.slideConfig.getMaxDoubleValue() - this.slideConfig.getMinDoubleValue())
                        + this.slideConfig.getMinDoubleValue();

                this.setDisplayValue(TextTools.roundString(TextTools.doubleToString(mouseValue), 2));

                this.save();
            }

            double sliderPercentage = (this.slideConfig.getDoubleValue() - this.slideConfig.getMinDoubleValue())
                    / (this.slideConfig.getMaxDoubleValue() - this.slideConfig.getMinDoubleValue());

            int sliderPos = (int)Math.rint(sliderPercentage * (this.getWidth() - 8));

            Identifier identifier = this.isHoveredOver(mouseX, mouseY) || this.selected ? SLIDER_HANDLE_HIGHLIGHTED : SLIDER_HANDLE;

            ctx.drawTexture(identifier, this.screenX + sliderPos, this.screenY, 0, 0, 8, 20, 8, 20);
        } else isMouseDown = false;

        return success;
    }
}
