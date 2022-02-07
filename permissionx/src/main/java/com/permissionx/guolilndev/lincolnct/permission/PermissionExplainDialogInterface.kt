package com.permissionx.guolilndev.lincolnct.permission

import android.content.Context
import com.permissionx.guolilndev.lincolnct.dialog.PermissionDialogInterface

interface PermissionExplainDialogInterface : PermissionDialogInterface {
    /**
     * set this dialog config with content, positive button text , negative button text and attach params passed by outside
     */
    fun setDialogConfig(config: PermissionDialogConfig)
}

class PermissionDialogConfig(
    val context: Context,
    val permissions: List<String>
) : PermissionExplainDialogConfig() {
    private var _permissionTips: List<String>? = null

    var permissionTips
        get() = _permissionTips ?: emptyList()
        set(value) {
            _permissionTips = value
        }

    override fun clone(): PermissionDialogConfig {
        val newConfig = PermissionDialogConfig(context, permissions)
        newConfig.applyConfig(this)
        newConfig._permissionTips = this._permissionTips?.toList()
        return newConfig
    }
}