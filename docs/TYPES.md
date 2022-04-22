# Config Types

### General things (this applies to all config types)
 - Constructor:
	 - Config options can be initialized using:
	 `new [ConfigType]([special parameters], String name, ?String[]/String description)`.
	 - The `special parameters` are specified for each ConfigType.
	 - The `name` can either be a literal name or a translation key. The option will be saved with the literal provided name and displayed with the translated name.
	 - The `description` is optional. Each String in the Array represents a line. If the description only has one line, it can be specified as a String outside an array. The Strings can either be a literal name or a translation key. It will be displayed when hovering over the option.
 - Methods that start with `set`, `add`, `remove`, `reset` or `on` always return `true` if the operation succeeded and `false` if it failed.
 - Methods:
	 - `String getKey()` returns the name untranslated, as specified in the constructor.
	 - `String getName()` returns the translated name.
	 - `boolean hasDesc()` returns `true` if the option has a description.
	 - `String[] getDesc()` returns the untranslated description.
	 - `boolean setDesc(String[]/String description)` sets the description.
	 - `boolean isNonexistentAllowed()` returns `true` if loading should succeed, even though no option with this name was found. The option will take its default value in that case. The default is `true`.
	 - `boolean allowNonexistent(boolean allow)` sets the value, allowing the option to be nonexistent when loading.
	 - `boolean resetValue()` resets the value to the default value.
	 - More methods are explained in [Option Migration](https://github.com/Tre5et/vanillaconfig/blob/1.18/docs/MIGRATE.md), [Config Screen](https://github.com/Tre5et/vanillaconfig/blob/1.18/docs/SCREEN.md) and [Callbacks](https://github.com/Tre5et/vanillaconfig/blob/1.18/docs/CALLBACKS.md).

### PageConfig
 - Special constructor parameters:
	 - `?BaseConfig[] options`: config options in a page.
 - Methods:
	 - `BaseConfig[] getOptions()` returns all options in the page.
	 - `boolean setOptions(BaseConfig[] options)` overrides the current options with the specified ones.
	 - `boolean addOption(BaseConfig option)` adds a single, specified option.
	 - `boolean removeOption(String name)` removes the option with the specified name.

### BooleanConfig
 - Special constructor parameters:
	 - `boolean defaultValue` the default boolean value.
	 - `?String[]/String descTrue` the description to be displayed when the option is true.
	 - `?String[]/String descFalse` the description to be displayed when the option is false.
 - Methods:
	 - `boolean getBoolean()` returns the current boolean value.
	 - `boolean setBoolean(boolean value)` sets the option to the specified value.
	 - `boolean getDefaultBoolean()` returns the default boolean value.
	 - `String[] getDescTrue()` returns the description to be displayed if the option is true.
	 - `boolean setDescTrue(String[]/String description)` sets the description to be displayed when the option is true.
	 - `String[] getDescFalse()` returns the description to be displayed if the option is false.
	 - `boolean setDescFalse(String[]/String description)` sets the description to be displayed when the option is false.

### IntegerConfig
- Special constructor parameters:
	- `int defaultValue` the default integer value.
	- `int minValue` the minimum value that is accepted.
	- `int maxValue` the maximum value that is accepted.
- Methods:
	- `int getInteger()` returns the current integer value.
	- `boolean setInteger(int value)` sets the option to the specified value.
	- `int getDefaultInteger()` returns the default value.
	- `int getMinInteger()` returns the minimum accepted value.
	- `boolean setMinInteger(int minValue)` sets the minimum accepted value to the specified value.
	- `int getMaxInteger()` returns the maximum accepted value.
	- `boolean setMaxInteger(int maxValue)` sets the maximum accepted value to the specified value.
	- `boolean isIntValid(int value)` returns `true` if the specified value would be accepted.

### DoubleConfig
- Special constructor parameters:
	- `double defaultValue` the default double value.
	- `double minValue` the minimum value that is accepted.
	- `double maxValue` the maximum value that is accepted.
- Methods:
	- `double getDouble()` returns the current double value.
	- `boolean setDouble(double value)` sets the option to the specified value.
	- `double getDefaultDouble()` returns the default value.
	- `double getMinDouble()` returns the minimum accepted value.
	- `boolean setMinDouble(double minValue)` sets the minimum accepted value to the specified value.
	- `double getMaxDouble()` returns the maximum accepted value.
	- `boolean setMaxDouble(double maxValue)` sets the maximum accepted value to the specified value.
	- `boolean isDoubleValid(double value)` returns `true` if the specified value would be accepted.

### StringConfig
- Special constructor parameters:
	- `double defaultValue` the default string value.
	- `int minLength` the minimum string length that is accepted.
	- `int maxLength` the maximum string length that is accepted.
- Methods:
	- `double getString()` returns the current string value.
	- `boolean setString(String value)` sets the option to the specified value.
	- `String getDefaultString()` returns the default value.
	- `String getMinLength()` returns the minimum accepted value.
	- `boolean setMinLength(int minLength)` sets the minimum accepted value to the specified value.
	- `String getMaxLength()` returns the maximum accepted value.
	- `boolean setMaxLength(int maxLength)` sets the maximum accepted value to the specified value.
	- `boolean isStringValid(String value)` returns `true` if the specified value would be accepted.

### ListConfig
- Special constructor parameters:
	- `String[] options` the options to select from. These can be either literal names or translation keys.
	- `int defaultOptionIndex / String defaultOption` the default option index of the list / the default option (if `defaultOption` isn't in the option list, default will be index `0`).
	- `?String[][] descriptions` descriptions in the order of the list. If the current list index has no description associated, the description at index `0` will be used.
- Methods:
	- `int getOptionIndex()` returns the current selected index.
	- `boolean setOptionIndex(int index)` sets the option index to the specified index.
	- `String getOption(?int index)` returns the option at the specified index or if omitted the current selected option.
	- `boolean setOption(String option)` sets the current active option. The specified option must be in the option list.
	- `int getDefaultOptionIndex()` returns the default index.
	- `String getDefaultOption()` returns the default option.
	- `String[] getOptions()` returns the option array.
	- `boolean setOptions(String[] options)` overrides the current option array. Use with care, as smaller arrays may cause the current option index to be out of bounds. This will cause `getOption()` to return an empty value.
	- `public String[][] getDescs()` returns a complete array of descriptions.
	- `boolean setDescs(String[][] descriptions)` sets the descriptions.
	- `boolean increment(boolean forward)` increments the option index forward or backward wrapping at the end of the list.

### KeybindConfig
- Special constructor parameters:
	- `int[] defaultKeys` default scancodes of keys required to be pressed to activate the keybind.
	- `int minAmount` the minimum accepted amount of keys.
	- `int maxAmount` the maximum accepted amount of keys.
- 	Methods:
	- `int[] getKeys()` returns an array of all key scancodes required to be pressed to activate the keybind.
	- `boolean setKeys(int[] keys)` sets the required key scancodes.
	- `int[] getDefaultKeys()` returns the default array of key scancodes.
	- `Keybind getKeybind()` returns the internally used keybind.
	- `int getMinAmount()` returns the minimum accepted amount of keys.
	- `boolean setMinAmount(int minAmount)` sets the minimum accepted amount of keys.
	- `int getMaxAmount()` returns the maximum accepted amount of keys.
	- `boolean setMaxAmount(int maxAmount)` sets the maximum accepted amount of keys.
	- `KeybindContext getContext()` returns the activation context of the keybind. This is either `IN_GAME` (default), where the keybind will only activate if the player is in a world, like vanilla keybinds, or `ANYWHERE`, where the keybind will activate no matter if the player is in menus or in a world (use this with great care!).
	- `setContext(KeybindContext context)` sets the activation context of the keybind.
	- `void resolve()` fires the event as if the keybind was pressed.
- Worth mentioning here: Keybind Callback:
	- `boolean onPressed(Consumer<String> method)` sets the method to be run when the key is pressed. More details in [Callbacks](https://github.com/Tre5et/vanillaconfig/blob/1.18/docs/CALLBACKS.md).
