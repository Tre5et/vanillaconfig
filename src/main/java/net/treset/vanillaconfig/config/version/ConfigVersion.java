package net.treset.vanillaconfig.config.version;

public class ConfigVersion {
    Integer a, b, c = null;
    boolean valid = false;

    public ConfigVersion(Integer a, Integer b, Integer c) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.valid = true;
    }
    public ConfigVersion(String version) {
        if(setFromString(version)) this.valid = true;
    }

    public boolean matches(ConfigVersion version) {
        if(!this.isDefinite()) return false;
        return (version.a == null || this.a.equals(version.a)) &&
                (version.b == null || this.b.equals(version.b)) &&
                (version.c == null || this.c.equals(version.c));
    }
    public boolean biggerThan(ConfigVersion version) {
        if(!this.isDefinite()) return false;

        if(version.a == null) return false;
        if(this.a > version.a) return true;
        if(version.b == null) return false;
        if(this.b > version.b) return true;
        return version.c != null && this.c > version.c;
    }
    public boolean biggerOrEqualTo(ConfigVersion version) {
        return this.matches(version) || this.biggerThan(version);
    }
    public boolean smallerThan(ConfigVersion version) {
        if(!this.isDefinite()) return false;

        if(version.a == null) return false;
        if(this.a < version.a) return true;
        if(version.b == null) return false;
        if(this.b < version.b) return true;
        return version.c != null && this.c < version.c;
    }
    public boolean smallerOrEqualTo(ConfigVersion version) {
        return this.matches(version) || this.smallerThan(version);
    }

    public boolean isValid() { return this.valid; }
    public boolean isDefinite() {
        return this.isValid() && this.a != null && this.b != null && this.c != null;
    }

    public boolean setFromString(String version) {
        Integer[] ints = getFromString(version);
        if(ints == null) return false;
        this.a = ints[0];
        this.b = ints[1];
        this.c = ints[2];
        return true;
    }

    public String getAsString() {
        String str = "";
        if(!this.isValid()) return str;
        if(this.a == null) str += "x";
        else str += this.a.toString();
        str += ".";
        if(this.b == null) str += "x";
        else str += this.b.toString();
        str += ".";
        if(this.c == null) str += "x";
        else str += this.c.toString();
        return str;
    }

    public static Integer[] getFromString(String version) {
        Integer[] ints = new Integer[3];
        String[] versions = version.split("\\.");
        if(versions.length != 3) return null;
        for (int i = 0; i < 3; i++) {
            if(versions[i].equals("x")) continue;
            Integer num;
            try {
                num = Integer.parseInt(versions[i]);
            } catch (NumberFormatException e) {
                return null;
            }
            ints[i] = num;
        }
        return ints;
    }
}
