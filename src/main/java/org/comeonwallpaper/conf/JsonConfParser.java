package org.comeonwallpaper.conf;

import com.google.gson.Gson;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;

public class JsonConfParser {
    private final Gson gson = new Gson();
    public Config parse(@NonNull File file) throws IOException {
        // TODO
        return null;
    }
}
