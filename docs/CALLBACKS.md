# Callbacks
   
###  General things
 - Most things that can be interacted with from outside the code have Callback methods for certain events.
 - These methods are called `boolean on[Event]([method])`.
 - The method always returns `void` and always parses a `String` with the name of the object that triggered the method as the last argument.  Methods can parse special arguments before the name.

### `onChange` Callbacks
 - Every config type, except `PageConfig`, has a `onChange` callback method.
 - These are triggered after the value changes, but not if the value is updated and stays the same.
 - These methods parse the previous value as a special argument. The new value can be read directly from the config option when the method is triggered.
 - `ListConfig` parses the previous Index as well as the previous value as special arguments.

### Other Callbacks
 - `PageConfig` has five callbacks:
	 - `onClick` is triggered after the config page is clicked on, and before it is opened.  It parses no special arguments.
	 - `onLoad` is triggered after the config has been loaded. It parses a `boolean` that indicates success as a special argument.
	 - `onSave` is triggered after the config has been saved. It parses a `boolean` that indicates success as a special argument.
	 - `onLoadPerWorld` is triggered after the config has been loaded per world. It parses a `boolean` that indicates success and a `String` with the world ID as special arguments.
	 - `onSavePerWorld` is triggered after the config has been saved per world. It parses a `boolean` that indicates success and a `String` with the world ID as special arguments.
 - `ConfigScreen` has two callbacks:
	 - `onOpen` is triggered after the config screen has been opened.
	 - `onClose` is triggered before the config screen is closed.

### Intended usage
 - Create a method that takes the correct arguments for a specific type of callback.
	 - In this method, run different actions based on the names you receive.
 - Apply this method to all callbacks that you want to have.
 - Example:
	```java
	BooleanConfig yourBoolean;
	BooleanConfig yourOtherBoolean;

	void init() {
		[...]
		yourBoolean.onChange(ThisClass::onBooleanConfigChanged);
		yourOtherBoolean.onChange(ThisClass::onBooleanConfigChanged);
	}

	void onBooleanConfigChanged(boolean prevBoolean, String name) {
		switch(name) {
			case yourBoolean.getKey() -> doSomething(yourBoolean.getBoolean());
			case yourOtherBoolean.getKey()⠀-> doSomethingElse(yourOtherBoolean.getBoolean());
		}
	}
	```
