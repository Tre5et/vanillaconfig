package net.treset.vanillaconfig.tools.helpers;

public enum AllowedChars {
    ALL(new String[]{"*"}),
    NUMBERS(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-"}),
    DECIMAL_NUMBERS(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-", "."});

    final String[] chars;
    AllowedChars(String[] chars) {
        this.chars = chars;
    }

    public String[] getChars() { return this.chars; }
}
