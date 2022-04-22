
# Setup
- Start your mod project like normal
 - In your `gradle.properties` file, add a line:
	 -  `vanillaconfig_version = [version]`. Replace `[version]` with the API version you would like to use. The latest version is `1.0.1`.
 - In your `build.gradle` file, add:
	 - In the `repositories` section: 
	 `maven { url 'https://raw.githubusercontent.com/Tre5et/maven/main/' }`
	 - In the `dependencies` section: 
	 `modImplementation "net.treset:vanillaconfig:${project.vanillaconfig_version}"`
 - Load the Gradle changes in your IDE (in IntelliJ IDEA, click the elephant symbol in the top right of the file window).
 
#### You can now move on to [Creating a Config](https://github.com/Tre5et/vanillaconfig/blob/v1.0.1/docs/CONFIG.md).

