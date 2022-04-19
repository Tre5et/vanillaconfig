package net.treset.vanillaconfig.tools;

import com.google.gson.*;
import net.treset.vanillaconfig.VanillaConfigMod;
import net.treset.vanillaconfig.config.version.ConfigVersion;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileTools {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean writeJsonToFile(JsonObject obj, File file)
    {
        File fileTmp = new File(file.getParentFile(), file.getName() + ".tmp"); //create temporary storage file

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileTmp), StandardCharsets.UTF_8)) { //open writer
            writer.write(GSON.toJson(obj)); //write json to file
            writer.close();

            if (file.exists() && file.isFile() && !file.delete()) return false; //delete old file if exists

            return fileTmp.renameTo(file); //commit temporary file
        }
        catch (Exception e) {
            e.printStackTrace();
            VanillaConfigMod.LOGGER.info("Failed to write JSON data to file '{}'", fileTmp.getAbsolutePath());
        }
        return false;
    }

    public static JsonObject readJsonFile(File file) {
        if (file.exists() && file.isFile() && file.canRead()) { //file exists and can be read

            JsonElement elm;
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) { //open reader
                elm = JsonParser.parseReader(reader); //read file to json element
            } catch (Exception e) {
                e.printStackTrace();
                VanillaConfigMod.LOGGER.error("Failed to parse the JSON file '{}'", file.getAbsolutePath());
                return null;
            }

            return elm.getAsJsonObject(); //return json object
        }
        return null;
    }

    public static JsonElement findJsonElementFromPath(JsonObject obj, String path) {
        String[] paths = path.split("/");
        JsonElement e = new JsonObject();
        for (int i = 0; i < paths.length; i++) {
            if(paths[i].isEmpty()) continue;
            e = obj.get(paths[i]);
            if(e == null) return null;
            if(!e.isJsonObject()) {
                if(i == paths.length - 1) continue;
                return null;
            }
            obj = e.getAsJsonObject();
        }
        return e;
    }

    public static File getConfigFile(String name, String path) {
        String finalPath = "./config/" + assemblePathString(name, path);
        return new File(finalPath);
    }
    public static File getConfigFile(String path) {
        String finalPath = "./config/" + path;
        return new File(finalPath);
    }
    public static String assemblePathString(String name, String path) {
        String finalPath = "";
        if(!path.equals("")) finalPath += path + ((path.endsWith("/"))? "" : "/");
        finalPath += name + (name.endsWith(".json")? "" : ".json");
        return finalPath;
    }
    public static boolean removeFile(File file) {
        if(!fileExists(file)) return false;
        return file.delete();
    }

    public static boolean writeVersion(String name, ConfigVersion version) {
        if(!version.isValid() || !version.isDefinite()) return false;

        File config = getConfigFile("vanillaconfig.json");
        JsonObject obj = new JsonObject();
        if(fileExists(config)) {
            obj = readJsonFile(config);
            if(obj == null) return false;
        }
        JsonPrimitive primitive = obj.getAsJsonPrimitive(name);
        if(primitive != null) {
            obj.remove("name");
        }

        obj.addProperty(name, version.getAsString());

        return writeJsonToFile(obj, getConfigFile("vanillaconfig.json"));
    }
    public static ConfigVersion readVersion(String name) {
        File config = getConfigFile("vanillaconfig.json");
        if(!fileExists(config)) return new ConfigVersion("0.0.0");
        JsonObject obj = readJsonFile(config);
        if(obj == null) return new ConfigVersion("0.0.0");
        JsonPrimitive primitive = obj.getAsJsonPrimitive(name);
        if(primitive == null || !primitive.isString()) return new ConfigVersion("0.0.0");
        return new ConfigVersion(primitive.getAsString());
    }

    public static boolean fileExists(File file) {
        return file != null && file.isFile();
    }

    public static boolean createFile(File file) {
        File parent = file.getParentFile();
        if(parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
        return true;
    }
}
