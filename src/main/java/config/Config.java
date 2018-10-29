package config;

import org.ini4j.Ini;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static final String FILENAME = "visualizer.ini";

    public static final Map<String, Boolean> EXTRAS = new HashMap<String, Boolean>();
    public static final Map<String, Integer> VALUES = new HashMap<String, Integer>();
    public static final Map<String, String> FILES = new HashMap<String, String>();

    public static void load(String...args) throws IOException {

        String filename = FILENAME;
        Ini ini = new Ini(new FileReader(filename));

        for(String key : ini.get("files").keySet())
            FILES.put(key, String.valueOf(ini.get("files").fetch(key)));

        for(String key : ini.get("experimental").keySet())
            VALUES.put(key, Integer.valueOf(ini.get("experimental").fetch(key)));

        for(String key : ini.get("extras").keySet())
            EXTRAS.put(key, Boolean.valueOf(ini.get("extras").fetch(key)));

    }

}
