package net.treset.vanillaconfig.tools;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class TextTools {

    public static String translateOrDefault(String text) {
        String out = text;

        Language lang = Language.getInstance();

        out = lang.getOrDefault(text);

        return out;
    }

    public static void narrate(Component text) {
        if(text == null || text.getString().isEmpty() || !Minecraft.getInstance().getNarrator().isActive()) return;
        Minecraft.getInstance().getNarrator().saySystemNow(text);
    }

    public static void narrate(String text, Object... args) {
        narrate(Component.translatable(text, args));
    }

    public static void narrateLiteral(String text) {
        narrate(Component.literal(text));
    }

    public static String booleanToString(boolean bool) {
        if(bool) return translateOrDefault("vanillaconfig.boolean.true");
        return translateOrDefault("vanillaconfig.boolean.false");
    }

    public static String intToString(int i) {
        String s = Integer.toString(i);
        return asDisplayIntString(s);
    }

    public static String getDecimalSymbol() {
         return String.valueOf(((DecimalFormat)DecimalFormat.getInstance()).getDecimalFormatSymbols().getDecimalSeparator());
    }

    public static String asIntString(String i) {
        if(i.isEmpty()) i = "0";
        else if(i.equals("-") || i.equals("0-")) i = "-0";
        return i;
    }
    public static String asDisplayIntString(String i) {
        if(i.startsWith("0") && i.length() > 1 && !i.startsWith("0" + getDecimalSymbol())) i = i.substring(1);
        else if(i.startsWith("-0") && i.length() > 2 && !i.startsWith("-0" + getDecimalSymbol())) i = "-" + i.substring(2);
        if(i.contains(getDecimalSymbol())) i = roundString(i, 0);
        return i;
    }
    public static int stringToInt(String i) {
        try {
            return NumberFormat.getInstance().parse(asIntString(i)).intValue();
        } catch(NumberFormatException | ParseException e) {
            return 0;
        }
    }

    public static String roundString(String s, int decPlaces) {
        if(!s.contains(getDecimalSymbol())) return s;
        boolean startsMinus = s.startsWith("-");
        s += "00";
        s = s.substring(0, s.indexOf(getDecimalSymbol()) + 2);
        if(startsMinus && !s.startsWith("-")) s = "-" + s;
        return s;
    }

    public static String doubleToString(double d) {
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(100);
        String s = df.format(d);
        return asDisplayDoubleString(s);
    }
    public static String asDoubleString(String d) {
        return asIntString(d);
    }
    public static String asDisplayDoubleString(String d) {
        if(d.startsWith("0") && d.length() > 1 && !d.startsWith("0" + getDecimalSymbol())) d = d.substring(1);
        else if(d.startsWith("-0") && d.length() > 2 && !d.startsWith("-0" + getDecimalSymbol())) d = "-" + d.substring(2);
        else if(d.startsWith(getDecimalSymbol())) d = "0" + d;
        else if(d.startsWith("-" + getDecimalSymbol())) d = "-0" + d.substring(1);
        return d;
    }
    public static double stringToDouble(String d) {
        try {
            return NumberFormat.getInstance().parse(asDoubleString(d)).doubleValue();
        } catch (NumberFormatException | ParseException e) {
            return 0;
        }
    }

    public static String keysAsDisplay(int[] keys) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            String key = InputConstants.Type.KEYBOARD.getOrCreate(keys[i]).getDisplayName().getString();
            str.append(key);
            if(i != keys.length - 1) str.append(" + ");
        }
        return str.toString();
    }
    public static String appendKeyToDisplayKeys(String key, String displayKeys) {
        if(displayKeys.isEmpty()) return key;
        return displayKeys + " + " + key;
    }

    public static String[][] stringArrayArrayFromStringArray(String[] array) {
        String[][] newArr = new String[array.length][1];
        for(int i = 0; i < array.length; i++) {
            newArr[i][0] = array[i];
        }
        return newArr;
    }
}
