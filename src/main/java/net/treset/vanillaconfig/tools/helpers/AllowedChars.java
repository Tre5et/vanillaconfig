package net.treset.vanillaconfig.tools.helpers;

public enum AllowedChars {
    NONE(new String[]{}, false),
    ALL(new String[]{"*"}, false),
    NUMBERS(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-"}, true),
    DECIMAL_NUMBERS(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-", "."}, true);

    final String[] chars;
    final boolean ignoreShift;
    AllowedChars(String[] chars, boolean ignoreShift) {
        this.chars = chars;
        this.ignoreShift = ignoreShift;
    }

    public String[] getChars() { return this.chars; }
    public boolean ignoreShift() { return this.ignoreShift; }
}
