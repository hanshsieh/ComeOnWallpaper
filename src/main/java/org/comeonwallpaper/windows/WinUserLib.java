package org.comeonwallpaper.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;

public interface WinUserLib extends User32 {
    WinUserLib INSTANCE = Native.load(
            "user32",
            WinUserLib.class,
            W32APIOptions.UNICODE_OPTIONS);
    // Sets the wallpaper
    // From https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-systemparametersinfow
    long SPI_SETDESKWALLPAPER = 0x0014;
    // Writes the new system-wide parameter setting to the user profile.
    // See https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-systemparametersinfow
    long SPIF_UPDATEINIFILE = 0x01;
    // Broadcasts the WM_SETTINGCHANGE message after updating the user profile.
    // See https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-systemparametersinfow
    long SPIF_SENDWININICHANGE = 0x02;

    /**
     * @see "https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-systemparametersinfow"
     * @param uiAction The type of the action to take.
     * @param uiParam The parameter of the action.
     * @param pvParam Another parameter of the action.
     * @param fWinIni If a system parameter is being set, specifies whether the user profile is to be updated,
     *                and if so, whether the WM_SETTINGCHANGE message is to be broadcast to all top-level windows
     *                to notify them of the change.
     * @return If the function succeeds, true is returned.
     */
    boolean SystemParametersInfo(
            long uiAction,
            int uiParam,
            String pvParam,
            long fWinIni);
}
