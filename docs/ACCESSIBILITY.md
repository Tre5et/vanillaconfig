# Accessibility

### Automatic Accessibility
- The config screen supports tab navigation by default
- All option types are able to be used entirely using keyboard input
- The tab behaviour mimics the vanilla one
- All option types come with narrations in both English and German
- The narrations mimic vanilla narrations with some improvements to make them easier to understand

### Custom Narrations
- All narrations are customizable by providing a `String` supplier Function to the `set[NarrationType]Narration(Supplier<String>)` method of an option type
- All option types have two NarrationTypes:
  - `Select`: is played whenever the option is hovered over with the mouse or selected using tab navigation
  - `Activate`: is played whenever the option is clicked on or is pressed enter on
- All typeable option types (`Double`, `Integer`, `Keybind`, `String`) have three additional option types:
  - `Change`: Is played whenever the text-field content is changed
  - `Save`: Is played whenever the value is saved (the editing of the field is confirmed by pressing Enter or clicking somewhere else)
  - `Reset`: Is played whenever the value is reset (the editing is cancelled by pressing Escape or Right Click)
- All option types that are able to be sliders (`Double`, `Integer`, `List`) have one additional option type:
  - `ChangeSlider`: Is played whenever the slider value is changed

### Intended usage
- After initializing an option, provide a narration function
- Beware: you need to take care of translating translation keys yourself here. To do so, you can use the `TextTools.translateOrDefault(String)` method provided by VanillaConfig
- Example:
```java
static BooleanConfig yourBoolean;
static BooleanConfig yourOtherBoolean;

static void init() {
    [...]
    yourBoolean.setSelectCallback(
        () -> String.format("Wow, you selected my awesome boolean with the value %s.", yourBoolean.getBoolean());  
    );
    yourOtherBoolean.setActivateCallback(
        () -> {
            boolean yourOtherBooleanName = yourOtherBoolean.getName();
            return String.format(
                    TextTools.translateOrDefault("your_mod.narration.other_boolean.activate"), 
                    yourOtherBooleanName, 
                    yourOtherBoolean.getBoolean()
            );
        }
    );
}
```
```json
en_us.json:

{
  [...]
  "your_mod.narration.other_boolean.activate" : "This is my awesome boolean with the name %s that you just changed to %s.",
  [...]
}
```