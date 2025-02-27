package com.permissionx.guolilndev.lincolnct.permission

open class PermissionDialogRequestGlobalConfig : IPermissionConfigDialogOperation {
    private val _permissionGroupTips = mutableMapOf<String, String>()
    private var _permissionDialogDelegate: IPermissionDialogDelegate? = null
    private var _explainDialogs = mutableMapOf(
        PermissionDialogType.EXPLAIN_REQUEST_REASON to DialogHolder(PermissionExplainDialog(), false, PermissionExplainDialogConfig()),
        PermissionDialogType.EXPLAIN_DENIED_TIPS to DialogHolder(PermissionExplainDialog(), false, PermissionExplainDialogConfig(positiveText = "去授权")),
        PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS to DialogHolder(PermissionExplainDialog(), false, PermissionExplainDialogConfig(positiveText = "去设置"))
    )

    internal val permissionGroupTips: Map<String, String>
        get() = _permissionGroupTips

    override fun setPermissionGroupExplainTips(permission: String, tips: String): IPermissionConfigDialogOperation {
        this._permissionGroupTips[PermissionRequestBuilder.getPermissionGroupOrReturnSelf(permission)] = tips
        return this
    }

    override fun setShowPermissionGroupExplainTipsEnabled(type: PermissionDialogType, show: Boolean): IPermissionConfigDialogOperation {
        this._explainDialogs[type]?.showGroupTips = show
        return this
    }

    override fun setExplainDialogConfig(type: PermissionDialogType, dialogConfig: IPermissionDialogConfig): IPermissionConfigDialogOperation {
        this._explainDialogs[type]?.dialogConfig = dialogConfig
        return this
    }

    override fun setExplainDialog(type: PermissionDialogType, dialog: PermissionExplainDialogInterface): IPermissionConfigDialogOperation {
        this._explainDialogs[type]?.dialog = dialog
        return this
    }

    override fun setExplainDialogDelegate(delegate: IPermissionDialogDelegate): IPermissionConfigDialogOperation {
        _permissionDialogDelegate = delegate
        return this
    }

    override fun getExplainDialogDelegate(): IPermissionDialogDelegate? {
        return _permissionDialogDelegate
    }

    override fun getPermissionExplainDialog(type: PermissionDialogType): PermissionExplainDialogInterface? {
        return this._explainDialogs[type]?.dialog
    }

    override fun isShowPermissionGroupExplainTipsEnabled(type: PermissionDialogType): Boolean {
        return this._explainDialogs[type]?.showGroupTips ?: false
    }

    override fun getExplainDialogConfig(type: PermissionDialogType): IPermissionDialogConfig? {
        return this._explainDialogs[type]?.dialogConfig
    }

    override fun getPermissionGroupExplainTips(permission: String): String? {
        return this._permissionGroupTips[permission]
    }

    override fun applyConfig(config: IPermissionConfigDialogOperation): IPermissionConfigDialogOperation {
        if (config is PermissionDialogRequestGlobalConfig) {
            this._permissionGroupTips.putAll(config._permissionGroupTips)
            config._explainDialogs.forEach { entry ->
                //这里是需要复制出来的，否则的话 dialogHolder 的参数配置会使用到默认的配置，因为对象引用一致
                this._explainDialogs[entry.key] = entry.value.clone()
            }
        }
        return this
    }
}

internal class DialogHolder(
    var dialog: PermissionExplainDialogInterface,
    var showGroupTips: Boolean = false,
    var dialogConfig: IPermissionDialogConfig
) : Cloneable {
    public override fun clone(): DialogHolder {
        return DialogHolder(dialog, showGroupTips, dialogConfig.clone())
    }
}