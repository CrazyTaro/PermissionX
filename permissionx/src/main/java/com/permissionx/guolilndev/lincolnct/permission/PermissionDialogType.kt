package com.permissionx.guolilndev.lincolnct.permission

/**
 * 权限提示对话框类型
 */
enum class PermissionDialogType {
    /**
     * 请求权限原因的提示对话框
     */
    EXPLAIN_REQUEST_REASON,
    /**
     * 权限被拒绝后的提示对话框
     */
    EXPLAIN_DENIED_TIPS,
    /**
     * 权限被禁止后跳转权限配置的对话框
     */
    EXPLAIN_FORWARD_SETTING_TIPS
}