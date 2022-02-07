package com.permissionx.guolilndev.lincolnct.permission;

import android.support.annotation.NonNull;

import java.util.List;

public interface IPermissionDialogDelegate {
    boolean onExplainDialogConfig(@NonNull PermissionDialogType type,
                                  @NonNull PermissionRequestBuilder builder,
                                  @NonNull PermissionDialogConfig config,
                                  @NonNull List<String> deniedPermissions);
}
