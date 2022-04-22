# Config Screen

### Creating a screen
 - A screen can be created using the constructor `new ConfigScreen(PageConfig config, Screen parentScreen)`.
 - `config` is the `PageConfig` that contains the options that should be displayed.
 - `parentScreen` is the `Screen` that will open once the current `ConfigScreen` is closed. This should typically be `MinecraftClient.getInstance().currentScreen`. 
 - The screen can be opened by using `MinecraftClient.getInstance().setScreen(yourConfigScreen)`.

### Display options for config screens:
 - All config types have two parameters that can be set. These will affect their appearance on the config screen.
 - Displayed:
	 - If this is `false`, the option will not be visible at all.
	 - `boolean isDisplayed()` returns `true` if the option is visible.
	 - `boolean setDisplayed(boolean displayed)` sets the visibility of the option.
  - Editable:
	 - If this is `false`, the option will be visible but grayed out and unable to be interacted with.
	 - `boolean isEditable()` returns `true` if the option is editable.
	 - `boolean setEditable(boolean editable)` sets the editability of the option.

### Example
To create a screen from the config in [Creating a Config](https://github.com/Tre5et/vanillaconfig/blob/v1.0.1/docs/CONFIG.md), and open it, when `yourKeybind` is pressed, you could modify the code like so:
```java
[...]
static ConfigScreen yourScreen;

public static void init() {
	[... yourPage.setPath("yourFolder"); ]

	yourScreen = new ConfigScreen(yourPage, MinecraftClient.getInstance().currentScreen);
	
	yourKeybind.onPressed(ThisClass::onKeybindPressed);

	[ yourPage.load(); ...]
}

public static void onKeybindPressed(String name) {
	MinecraftClient.getInstance().setScreen(yourScreen);
}
```

