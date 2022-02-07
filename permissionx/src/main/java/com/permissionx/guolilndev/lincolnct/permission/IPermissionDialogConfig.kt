package com.permissionx.guolilndev.lincolnct.permission

/**
 * 权限对话框配置参数
 */
interface IPermissionDialogConfig : Cloneable {
    /**
     * 对话框的标题
     */
    var title: String
    /**
     * 对话框内容
     */
    var message: String
    /**
     * 确定文本
     */
    var positiveText: String
    /**
     * 取消文本
     */
    var negativeText: String
    /**
     * 对话框相关参数
     */
    var attach: Any?

    open fun applyConfig(config: IPermissionDialogConfig?) {
        config?.let { it ->
            this.title = it.title.takeIf { it.isNotEmpty() } ?: this.title
            this.message = it.message.takeIf { it.isNotEmpty() } ?: this.message
            this.positiveText = it.positiveText.takeIf { it.isNotEmpty() } ?: this.positiveText
            this.negativeText = it.negativeText.takeIf { it.isNotEmpty() } ?: this.negativeText
            this.attach = it.attach.takeIf { it != null } ?: this.attach
        }
    }

    public override fun clone(): IPermissionDialogConfig
}