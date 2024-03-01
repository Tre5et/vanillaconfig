# Config Screen

### Creating a screen
 - A screen can be created using the constructor `new ConfigScreen(PageConfig config, Screen parentScreen)`.
 - `config` is the `PageConfig` that contains the options that should be displayed.
 - `parentScreen` is the `Screen` that will open once the current `ConfigScreen` is closed. This should typically be `MinecraftClient.getInstance().currentScreen`. 
 - The screen can be opened by using `MinecraftClient.getInstance().setScreen(yourConfigScreen)`.

### Display options for config screens:
 - The config screen can render two types of background:
   - List background (like the vanilla Video Settings screen) or untextured (like the vanilla Controls screen):
     - `boolean isListBackground()` returns `true` if the list background is rendered (default).
     - `boolean setListBackgroud(boolean textured)` sets whether  list background is rendered (this should only be changed to `false` if the page doesn't scroll, else it won't look good).
 - All config types have four parameters that can be set. These will affect their appearance on the config screen.
   - Displayed:
       - If this is `false`, the option will not be visible at all.
       - `boolean isDisplayed()` returns `true` if the option is visible.
       - `boolean setDisplayed(boolean displayed)` sets the visibility of the option.
   - Editable:
      - If this is `false`, the option will be visible but grayed out and unable to be interacted with.
      - `boolean isEditable()` returns `true` if the option is editable.
      - `boolean setEditable(boolean editable)` sets the editability of the option.
   - Full Width:
     - If this is `false`, the option will be displayed only on one half of the page, like many vanilla options. If it is `true` (default), the option will be centered and span a greater width.
     - `boolean isFullWidth()` returns `true` if the option is centered.
     - `boolean setFullWidth(boolean fullWidth)` sets the full width option.
   - Custom Width:
     - `int[] getWidth()` returns an array with index 0 being the width when the option spans both columns and index 1 being the width when it spans only one.
     - `boolean setCustomWidth(int widthFull, int widthHalf)` sets the width when the option spans both columns (`widthFull`) and when it spans one column (`widthHalf`).
 - `IntegerConfig`, `DoubleConfig` and `ListConfig` have the option to be displayed as a slider or a text field:
   - `boolean isSlider()` returns `true` if the option is a slider.
   - `boolean setSlider(boolean slider)` sets whether the option is a slider.

### Example
To create a screen from the config in [Creating a Config](CONFIG.md), and open it, when `yourKeybind` is pressed, you could modify the code like so:
```java
[...]
static ConfigScreen yourScreen;

public static void init() {
	[... yourPage.setPath("yourFolder"); ]

	yourScreen = new ConfigScreen(yourPage, MinecraftClient.getInstance().currentScreen);
	
	yourKeybind.onPressed(ThisClass::onKeybindPressed);

	[ SaveLoadManager.globalSaveConfig(yourPage); ...]
}

public static void onKeybindPressed(String name) {
	MinecraftClient.getInstance().setScreen(yourScreen);
}
```

