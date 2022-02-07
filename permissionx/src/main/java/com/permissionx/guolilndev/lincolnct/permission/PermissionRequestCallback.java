package com.permissionx.guolilndev.lincolnct.permission;

import java.util.List;

public interface PermissionRequestCallback {
    void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList);
}
