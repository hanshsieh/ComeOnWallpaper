package org.comeonwallpaper.monitor;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.comeonwallpaper.windows.WinUserLib;

import java.util.ArrayList;
import java.util.List;

public class MonitorService {
    private final WinUserLib userLib = WinUserLib.INSTANCE;

    public int getMonitorCount() throws RuntimeException {
        // https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getsystemmetrics
        int num = userLib.GetSystemMetrics(WinUser.SM_CMONITORS);
        if (num == 0) {
            throw new RuntimeException("Failed to get the number of monitors");
        }
        return num;
    }

    @NonNull
    public List<Monitor> getMonitors() throws RuntimeException {
        List<Monitor> monitors = new ArrayList<>();
        User32.INSTANCE.EnumDisplayMonitors(null, null, (hMonitor, hdc, rect, lparam) -> {
            monitors.add(createMonitor(hMonitor));
            return 1;
        }, new WinDef.LPARAM(0));
        return monitors;
    }

    @NonNull
    private Monitor createMonitor(WinUser.@NonNull HMONITOR hMonitor) {
        WinUser.MONITORINFOEX info = new WinUser.MONITORINFOEX();
        User32.INSTANCE.GetMonitorInfo(hMonitor, info);
        return new Monitor.Builder()
            .setDisplayArea(convertRectangle(info.rcMonitor))
            .setWorkingArea(convertRectangle(info.rcWork))
            .build();
    }

    @NonNull
    private Rectangle convertRectangle(WinDef.@NonNull RECT rect) {
        return new Rectangle(
                rect.left,
                rect.top,
                rect.right - rect.left,
                rect.bottom - rect.top);
    }
}
