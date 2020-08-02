package org.comeonwallpaper.windows;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

import java.util.HashMap;

public class WinUserLibLoader {
    private static WinUserLib lib;
    public static synchronized WinUserLib loadInstance() {
        if (lib == null) {
            lib = Native.load("user32",
                    WinUserLib.class,
                    new HashMap<String, Object>() {
                        {
                            put(WinUserLib.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
                            put(WinUserLib.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
                        }
                    }
            );
        }
        return lib;
    }
}
