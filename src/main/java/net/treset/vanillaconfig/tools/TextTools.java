package net.treset.vanillaconfig.tools;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Language;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.text.DecimalFormat;

public class TextTools {

    public static String translateOrDefault(String text) {
        String out = text;

        Language lang = Language.getInstance();
        if(lang == null) return out;

        out = lang.get(text);

        return out;
    }

    public static String booleanToString(boolean bool) {
        if(bool) return translateOrDefault("vanillaconfig.boolean.true");
        return translateOrDefault("vanillaconfig.boolean.false");
    }

    public static String intToString(int i) {
        String s = Integer.toString(i);
        return asDisplayIntString(s);
    }
    public static String asIntString(String i) {
        if(i.equals("")) i = "0";
        else if(i.equals("-") || i.equals("0-")) i = "-0";
        return i;
    }
    public static String asDisplayIntString(String i) {
        if(i.startsWith("0") && i.length() > 1 && !i.startsWith("0.")) i = i.substring(1);
        else if(i.startsWith("-0") && i.length() > 2 && !i.startsWith("-0.")) i = "-" + i.substring(2);
        if(i.contains(".")) i = roundString(i, 0);
        return i;
    }
    public static int stringToInt(String i) {
        try {
            return Integer.parseInt(asIntString(i));
        } catch(NumberFormatException e) {
            return 0;
        }
    }

    public static String roundString(String s, int decPlaces) {
        if(!s.contains(".")) return s;
        boolean startsMinus = s.startsWith("-");
        double d = stringToDouble(s);
        int i = (int)Math.rint(d * Math.pow(10, decPlaces));
        d = i / Math.pow(10, decPlaces);
        s = doubleToString(d);
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
        if(d.equals("")) d = "0";
        else if(d.equals("-") || d.equals("0-")) d = "-0";
        return d;
    }
    public static String asDisplayDoubleString(String d) {
        if(d.startsWith("0") && d.length() > 1 && !d.startsWith("0.")) d = d.substring(1);
        else if(d.startsWith("-0") && d.length() > 2 && !d.startsWith("-0.")) d = "-" + d.substring(2);
        else if(d.startsWith(".")) d = "0" + d;
        else if(d.startsWith("-.")) d = "-0" + d.substring(1);
        return d;
    }
    public static double stringToDouble(String d) {
        try {
            return  Double.parseDouble(TextTools.asDoubleString(d));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int getKeycodeFromScancode(int scancode) {
        int i = 32;
        while(i < 1000) {
            if(GLFW.glfwGetKeyScancode(i) == scancode) return i;
            i++;
        }
        return 0;
    }
    static ImmutableList<Integer> kpKeys = ImmutableList.of(
            GLFW.GLFW_KEY_KP_0,
            GLFW.GLFW_KEY_KP_1,
            GLFW.GLFW_KEY_KP_2,
            GLFW.GLFW_KEY_KP_3,
            GLFW.GLFW_KEY_KP_4,
            GLFW.GLFW_KEY_KP_5,
            GLFW.GLFW_KEY_KP_6,
            GLFW.GLFW_KEY_KP_7,
            GLFW.GLFW_KEY_KP_8,
            GLFW.GLFW_KEY_KP_9,
            GLFW.GLFW_KEY_KP_DIVIDE,
            GLFW.GLFW_KEY_KP_MULTIPLY,
            GLFW.GLFW_KEY_KP_SUBTRACT,
            GLFW.GLFW_KEY_KP_ADD,
            GLFW.GLFW_KEY_KP_DECIMAL
    );
    @Nullable
    public static String getKeyFromScancode(int scancode, boolean allowNonText) {
        String str = GLFW.glfwGetKeyName(-1, scancode);

        int key = getKeycodeFromScancode(scancode);
        if(allowNonText && (str == null || str.equals("") || str.equals("\t") || kpKeys.contains(key)))
            str = translateOrDefault(InputUtil.fromKeyCode(key, -1).toString());

        if(str != null) str = str.substring(0, 1).toUpperCase() + (str.length() > 1 ? str.substring(1) : "");

        return str;
    }
    public static String scancodesAsDisplayKeys(int[] sc) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < sc.length; i++) {
            String key = TextTools.getKeyFromScancode(sc[i], true);
            if(key != null) {
                str.append(key);
                if(i != sc.length - 1) str.append(" + ");
            }
        }
        return str.toString();
    }
    public static String appendKeyToDisplayKeys(String key, String displayKeys) {
        if(displayKeys.length() == 0) return key;
        return displayKeys + " + " + key;
    }

    public static String[][] stringArrayArrayFromStringArray(String[] array) {
        String[][] newArr = new String[array.length][1];
        for(int i = 0; i < array.length; i++) {
            newArr[i][0] = array[i];
        }
        return newArr;
    }

    public static int getKeyCodeFromKey(String key) {
        for (int i = 0; i < 97; i++) {
            if (key.equalsIgnoreCase(GLFW.glfwGetKeyName(-1, i)))
                return i;
        }
        return -1;
    }
}
