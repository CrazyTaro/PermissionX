package com.permissionx.guolilndev.lincolnct.permission

import android.app.AlertDialog


class PermissionExplainDialog : AbsPermissionExplainDialog() {
    override fun showDialog(): Boolean {
        dialog = AlertDialog.Builder(config.context)
            .apply {
                if (config.title.isNotEmpty()) {
                    setTitle(config.title)
                }
                setMessage(generateDisplayMessage(config))
                setCancelable(cancelable)
                if (config.positiveText.isNotEmpty()) {
                    setPositiveButton(config.positiveText) { dialog, which ->
                        positiveCallback?.onPositiveAction(this@PermissionExplainDialog)
                    }
                }
                if (config.negativeText.isNotEmpty()) {
                    setNegativeButton(config.negativeText) { dialog, which ->
                        negativeCallback?.onNegativeAction(this@PermissionExplainDialog)
                    }
                }
            }
            .create()
        if (dismissCallback != null) {
            dialog.setOnDismissListener {
                dismissCallback?.onDismissAction(this)
            }
        }
        dialog.show()
        return true
    }
}

open class PermissionExplainDialogConfig(
    override var title: String = "",
    override var message: String = "",
    override var positiveText: String = "确定",
    override var negativeText: String = "取消",
    override var attach: Any? = null,
) : IPermissionDialogConfig {
    override fun clone(): PermissionExplainDialogConfig {
        return PermissionExplainDialogConfig().apply {
            applyConfig(this)
        }
    }
}