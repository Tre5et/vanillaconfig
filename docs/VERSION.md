# Version Management
 - VanillaConfig will automatically save a version with your config. If you don't specify it, it will be `x.x.x`.

### Loading versions (all of these methods are found in `PageConfig`):
 - `boolean loadVersion()` loads the config version into the `PageConfig`.
 - `boolean hasVersion()` returns `true` if the config has a valid version.
 - `ConfigVersion getVersion()` returns a `ConfigVersion` object with the loaded version.

### The `ConfigVersion` object:
 - Stores a version of the format `majorVersion.minorVersion.patch`, similar to Semantic Versioning.
 - The version numbers can be integer numbers or `x` for a blank version.
 - A new `ConfigVersion` can be created using `new ConfigVersion(String version)` or `new ConfigVersion(Integer majorVersion, Integer minorVersion, Integer patch)`
 - `version` is a version string of the format `majorVersion.minorVersion.patch`. If this is misformatted, the version will be `x.x.x`.
 - The integer numbers may be `null`, which is equivalent to `x` in the string.
 - `String getAsString()` returns the version as a string of the format `majorVersion.minorVersion.patch`.
 - `boolean isValid()` returns `false` if the supplied config version was misformatted.
 - `boolean isDefinite()` returns `true` if the version doesn't contain a blank version number.
 - Comparing versions:
	 - The versions these methods are run on must be definite, else all of them will return `false`. 
	 - `boolean matches(ConfigVersion version)` returns `true` if the `ConfigVersion` is the same as  or part of `version`.
		 - Examples: 
			```java
			 new ConfigVersion("1.2.3").matches(new ConfigVesion("1.2.3") = true
	 		 new ConfigVersion("1.2.3").matches(new ConfigVesion("1.x.3") = true
	 		 new ConfigVersion("1.2.3").matches(new ConfigVesion("1.2.4") = false
			 new ConfigVersion("1.2.3").matches(new ConfigVesion("2.x.x") = false
			```
    - `boolean biggerThan(ConfigVersion version)` returns `true` if the `ConfigVersion` is bigger but not the same as or part of `version`.
	    - Examples:
		    ```java
		    new ConfigVersion("1.2.3").biggerThan(new ConfigVesion("1.2.2") = true
		    new ConfigVersion("1.2.3").biggerThan(new ConfigVesion("0.x.1") = true
		    new ConfigVersion("1.2.3").biggerThan(new ConfigVesion("1.2.3") = false
		    new ConfigVersion("1.2.3").biggerThan(new ConfigVesion("1.2.x") = false
		    new ConfigVersion("1.2.3").biggerThan(new ConfigVesion("1.3.x") = false
		    ```
     - `boolean smallerThan(ConfigVersion version)` returns `true` if the `ConfigVersion` is smaller but not the same as or part of `version`.
	    - Examples:
		   ```java
		    new ConfigVersion("1.2.3").smallerThan(new ConfigVesion("1.2.4") = true
		    new ConfigVersion("1.2.3").smallerThan(new ConfigVesion("2.x.4") = true
		    new ConfigVersion("1.2.3").smallerThan(new ConfigVesion("1.2.3") = false
		    new ConfigVersion("1.2.3").smallerThan(new ConfigVesion("1.2.x") = false
		    new ConfigVersion("1.2.3").smallerThan(new ConfigVesion("1.1.x") = false
		    ```
     - `boolean biggerOrEqualTo(ConfigVersion version)` returns `true` if `ConfigVersion` is bigger than or the same as or part of `version`.
    - `boolean smallerOrEqualTo(ConfigVersion version)` returns `true` if `ConfigVersion` is smaller than or the same as or part of `version`.

### Setting the version for saving (in `PageConfig`):
 - `boolean setVersion(ConfigVersion version)` sets the version of the config. This will be saved the next time it is saved.
 - `version` should always be definite, as otherwise comparisons won't work.

### Intended usage:
 - Version comparison can be used for migration based on the last config version.
 - Load the version before loading the config. Be careful, if the name of the config changed since the last save, the config will load no version. If `PageConfig.hasVersion()` is `false`, you might want to check old names manually by using ` FileTools.readVersion(String oldName)`, which returns a `ConfigVersion`. To check whether this existed, use `ConfigVersion.isDefinite()`.
 - Based on the loaded version, different migration patterns can be applied to suit migrating from this particular version.

### Example:
To only apply the migration in [Option Migration](https://github.com/Tre5et/vanillaconfig/blob/v1.0.1/docs/MIGRATE.md) (In file migration) if the config's version is between (or equal to) versions `1.1.0` and `1.3.5` (which is the last `1.3.x` version), you could modify the code like so:
```java
[...]
public static void init() {
	[... yourList = new ListConfig(
		new String[]{ "list.options.1", "list.options.2", "list.options.3"},
		0, "your.list", "Cool description ;)"
	); ]

	yourPage.loadVersion();
	if(yourPage.getVersion().biggerThan(new ConfigVersion("1.0.x"))
			 && yourPage.getVersion().smallerOrEqualTo(new ConfigVersion("1.3.x"))) {
			 
		yourOtherPage = new PageConfig(new BaseConfig[]{ yourKeybind },
			"your.other.page", "This is a page Description!"
		);		

		yourPage.addOption(yourOtherPage);
		yourPage.addOption(yourList);

		yourList.migrateFrom("your.other.page/your.list");
	}

	yourPage.setVersion(new ConfigVersion("1.4.0");

	[ yourPage.load(); ...]
}
```
