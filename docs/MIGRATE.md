﻿# Option Migration

### In file migration
 - To migrate a config option from a different place in the same file
 - Migrating from the same page (renaming the option):
	 - before the config is loaded, run `yourOption.migrateFrom(String oldName)`
	 - `oldName` is the name of the JSON element that the option should be migrated from (the previous name of the option).
 - Migrating from a different page (moving the option to a new place):
	 - before the config is loaded, run `yourOption.migrateFrom(String oldPath)`
	 - `oldPath` is the absolute path within the JSON of the option to be migrated from.
	 - Example:
	 To migrate the in [Creating a Config](CONFIG.md) created file to this file:
		```json
		{
			"your.boolean": true,
			"A Integer": -5,
			"your.other.page": {
				"Cool Keybind": [35, 24],
			},
			"your.list": "list.option.1"
		}
				
		```
		While keeping the value of `your.list`, you could modify the code like so:
		```java
		[...]
		public static void init() {
			[... yourList = new ListConfig(
				new String[]{ "list.options.1", "list.options.2", "list.options.3"},
				0, "your.list", "Cool description ;)"
			); ]
			
			yourOtherPage = new PageConfig(new BaseConfig[]{ yourKeybind },
				"your.other.page", "This is a page Description!"
			);		

			yourPage.addOption(yourOtherPage);
			yourPage.addOption(yourList);

			yourList.migrateFrom("your.other.page/your.list");

			[ yourPage.load(); ...]
		}
		```

### Migrating from other files
 - To migrate a complete config from a different file
 - Before loading the config, run `yourPageConfig.migrateFileFrom(String oldPath)`
 - `oldPath` is the path of the file in the config directory to be migrated from, including the `.json` extension(!). If the file name stays the same, the file name and extension may be omitted from the end.
 - Example:
 To migrate a directory structure like this, created in  [Creating a Config](CONFIG.md) :
    ```
    config
      L yourFolder
        L your.config.json
    ```
     To a directory structure like this:
    ```
    config
      L yourFolder
      L yourNewFolder
        L your.new.config.json
    ```
    While moving the options from `your.config.json` to `your.new.config.json`, you could modify the code like so:
    ```java
    [...]
    public static void init() {
        yourPage = new PageConfig("your.new.config");
		
        [ yourBoolean = new BooleanConfig(true, "your.boolean", "your.boolean.description"); ...
        ... yourOtherPage.removeOption("your.boolean"); ]

        yourPage.setPath("yourNewFolder");
        yourPage.migrateFileFrom("yourFolder/your.config.json");

        [ SaveLoadManager.globalSaveConfig(yourPage); ...]
    }
    ```
 - For migrating per world configs `oldPath` is the old folder name plus a `.json` extension. When loading the correct file in the folder will automatically be picked. This only works correctly if the previous file naming logic is the same as the one implemented in `ClientTools.getWorldId()`!

#### To do this properly, check out [Version Management](VERSION.md).


