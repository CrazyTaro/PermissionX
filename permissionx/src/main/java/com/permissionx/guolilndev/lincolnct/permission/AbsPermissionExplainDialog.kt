package com.permissionx.guolilndev.lincolnct.permission

import android.app.Dialog
import android.support.v4.app.FragmentManager
import com.permissionx.guolilndev.lincolnct.dialog.PermissionDismissCallback
import com.permissionx.guolilndev.lincolnct.dialog.PermissionNegativeCallback
import com.permissionx.guolilndev.lincolnct.dialog.PermissionPositiveCallback

abstract class AbsPermissionExplainDialog :
    PermissionExplainDialogInterface {
    protected lateinit var config: PermissionDialogConfig

    protected var cancelable: Boolean = false
    protected var cancelableOutSide: Boolean = false
    protected var dismissCallback: PermissionDismissCallback? = null
    protected var negativeCallback: PermissionNegativeCallback? = null
    protected var positiveCallback: PermissionPositiveCallback? = null
    protected lateinit var dialog: Dialog

    override fun setDialogConfig(config: PermissionDialogConfig) {
        this.config = config
    }

    override fun dismissDialog() {
        dialog.dismiss()
    }

    override fun getPermissionsToRequest(): List<String> {
        return config.permissions
    }

    override fun setDialogCancelable(cancelable: Boolean) {
        this.cancelable = cancelable
    }

    override fun setDialogCanceledOnTouchOutside(cancelable: Boolean) {
        this.cancelableOutSide = cancelable
    }

    override fun setDismissListener(callback: PermissionDismissCallback) {
        this.dismissCallback = callback
    }

    override fun setNegativeAction(callback: PermissionNegativeCallback) {
        this.negativeCallback = callback
    }

    override fun setPositiveAction(callback: PermissionPositiveCallback) {
        this.positiveCallback = callback
    }

    override fun showDialogFragment(fm: FragmentManager, tag: String): Boolean {
        return false
    }

    protected fun generateDisplayMessage(config: PermissionDialogConfig): String {
        return StringBuilder(256)
            .apply {
                append(config.message)
                if (config.message.isNotEmpty()) {
                    append('\n')
                }
                config.permissionTips
                    .forEachIndexed { index, s ->
                        append("● ")
                        append(s)
                        if (config.permissionTips.lastIndex != index) {
                            append('\n')
                        }
                    }
            }
            .toString()
    }
}