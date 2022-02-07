package com.permissionx.guolilndev.lincolnct.request

import com.permissionx.guolilndev.lincolnct.dialog.PermissionDialogInterface

/**
 * 作用域相关接口，用于处理权限相关对话框接口
 */
interface IScope {
    /**
     * 显示当前需要处理的对话框对象
     */
    fun showHandlePermissionDialog(dialog: PermissionDialogInterface)
}