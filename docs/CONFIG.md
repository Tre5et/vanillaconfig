# Creating a Config
### General things
 - Creating a Config can be done anywhere in your code, however it is recommended to do so in a Client entrypoints (defined in `fabric.mod.json`) as this will ensure that automatic loading and saving will properly take place.

### Config creation
 - The basis of every Config is a `PageConfig`. 
 - Create one by initializing a variable of type `PageConfig` and assign it with `new PageConfig(String name)`.
 - `name` is the name your Config will have. This will be the name it is saved under and the name that will be displayed at the top of the config screen. 
 - The name can either be a literal name or a translation key, the translation for which is specified in the standard Minecraft language files under `resources/assets/[modid]/lang`. 

### Adding config options
 - First, create your desired config options by  initializing a variable of one of the [Config Types](https://github.com/Tre5et/vanillaconfig/blob/1.18/docs/TYPES.md) and assigning it with the appropriate constructor.
 - Adding these options to your `PageConfig` can be done in three ways:
	 - Directly in the Constructor: 
		 - Create a new array of type `BaseConfig` and add all your desired options to it.
		 - Use the constructor `new PageConfig(BaseConfig[] yourOptions, String name)`.
	 - Adding an option array:
		 - Create a new array of type `BaseConfig` and add all your desired options to it.
		 - After creating your `PageConfig` as normal, run `yourPageConfig.setOptions(BaseConfig[] yourOptions)`.
	 - Adding individual options:
		 - After creating your `PageConfig` as normal, run for each option `yourPageConfig.addOption(BaseConfig yourOption)`.
 - Removing options from your `PageConfig`:
	 - Run `yourPageConfig.removeOption(String name)`.
	 - `name` is the name of the option you would like to remove.
	 - This function returns a boolean, indicating the success of the removal.

### Saving and Loading
 - You can save configs globally or per world:
     - Globally saved configs are saved as `[name].json`.
     - Per world saved configs are saved as `[name]/[worldId].json`.
 - Saving and loading can be done automatically and manually:
 - Automatic saving / loading:
     - Globally: After creating your config run `SaveLoadManager.globalSaveConfig(yourPageConfig)`.
     - Per World: After creating your config run `SaveLoadManager.worldSaveConfig(yourPageConfig)`.
     - Configs will now automatically be loaded and saved as required.
 - Manual saving / loading:
     - Globally: To load or save a config, run `yourPageConfig.load()` or `yourPageConfig.save()`.
     - Per world: To load or save a config, run `yourPageConfig.loadPerWorld()` or `yourPageConfig.savePerWorld()`. Both functions can take a `String worldId`.
 - To load and save a config in another location, run before loading the first time `yourPageConfig.setPath(String path)`.
     - `path` is the path that the config should be saved / loaded in within the config directory. It should not end with the file name.
 - To load and save with a different file name than the `name` of the `ConfigPage`, run before loading for the first time `yourConfigPage.setSaveName(String name)`.
     - `name` is the file name of the config file. This should NOT include the `.json` extension.
 - To migrate configs from different files, take a look at [Option Migration](https://github.com/Tre5et/vanillaconfig/blob/1.18/docs/MIGRATE.md).

### Example
```java
static PageConfig yourPage;
static BooleanConfig yourBoolean;
static IntegerConfig yourInteger;

static PageConfig yourOtherPage;
static KeybindConfig yourKeybind;
static ListConfig yourList;

public static void init() {
	yourPage = new PageConfig("your.config");
	yourBoolean = new BooleanConfig(true, "your.boolean", "your.boolean.description");
	yourInteger = new IntegerConfig(-5, -109, 10, "A Integer", 
			new String[]{ "your.integer.desc.line1", "your.integer.desc.line2"}
		);
	yourPage.setOptions(new BaseConfig[]{ yourBoolean, yourInteger });

	yourKeybind = new KeybindConfig(new int[0x23, 0x18] /*H + O*/, 0, 10, "Cool Keybind");
	yourList = new ListConfig(
			new String[]{ "list.options.1", "list.options.2", "list.options.3"},
			0, "your.list", "Cool description ;)"
		);
	yourOtherPage = new PageConfig(new BaseConfig[]{ yourKeybind, yourList, yourBoolean },
			"your.other.page", "This is a page Description!"
		);

	yourPage.addOption(yourOtherPage);
	yourOtherPage.removeOption("your.boolean");
    
	yourPage.setSaveName("yourFile")
	yourPage.setPath("yourFolder");
    
	SaveLoadManager.globalSaveConfig(yourPage);
}
```
Creates, loads and saves the file `config / yourFolder / yourFile.json`:
```json
{
	"your.boolean": true,
	"A Integer": -5,
	"your.other.page": {
		"Cool Keybind": [35, 24],
		"your.list": "list.option.1"
	}
}
		
```


