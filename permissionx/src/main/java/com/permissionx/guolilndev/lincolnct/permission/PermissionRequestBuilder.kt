package com.permissionx.guolilndev.lincolnct.permission

import android.graphics.Color
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.permissionx.guolilndev.lincolnct.PermissionMediator
import com.permissionx.guolilndev.lincolnct.PermissionX
import com.permissionx.guolilndev.lincolnct.dialog.permissionMapOnR
import com.permissionx.guolilndev.lincolnct.request.IScope

class PermissionRequestBuilder private constructor() : IPermissionRequestBuilder {
    private lateinit var permissionMediator: PermissionMediator

    private val localConfig = PermissionDialogRequestGlobalConfig().applyConfig(globalConfig)
    private val permissionHolder = mutableListOf<String>()
    private var lightColorTint: Int? = Color.WHITE
    private var darkColorTint: Int? = Color.WHITE

    override fun getPermissionDialogConfig(): IPermissionConfigDialogOperation {
        return localConfig
    }

    fun requestPermissionSinceApi22(vararg permissions: String): PermissionRequestBuilder {
        return requestPermissionSinceApiAny(22, *permissions)
    }

    fun requestPermissionSinceApi23(vararg permissions: String): PermissionRequestBuilder {
        return requestPermissionSinceApiAny(23, *permissions)
    }

    fun requestPermissionSinceApi28(vararg permissions: String): PermissionRequestBuilder {
        return requestPermissionSinceApiAny(28, *permissions)
    }

    fun requestPermissionSinceApi29(vararg permissions: String): PermissionRequestBuilder {
        return requestPermissionSinceApiAny(29, *permissions)
    }

    fun requestPermissionSinceApiAny(sdkVersionInt: Int, vararg permissions: String): PermissionRequestBuilder {
        if (Build.VERSION.SDK_INT >= sdkVersionInt) {
            permissionHolder.addAll(permissions)
        }
        return this
    }

    /**
     * 需要请求的权限
     */
    fun requestPermissions(vararg permissions: String): PermissionRequestBuilder {
        permissionHolder.addAll(permissions)
        return this
    }

    /**
     * 设置主题色
     */
    fun setThemeColor(lightColor: Int, darkColor: Int): PermissionRequestBuilder {
        lightColorTint = lightColor
        darkColorTint = darkColor
        return this
    }

    /**
     * 设置主题色跟随当前的上下文 context
     */
    fun setThemeColorFlowContext(): PermissionRequestBuilder {
        lightColorTint = null
        darkColorTint = null
        return this
    }

    /**
     * 设置请求权限的原因，该信息会在请求前弹出提示
     */
    fun setRequestReason(reason: String): PermissionRequestBuilder {
        localConfig.getExplainDialogConfig(PermissionDialogType.EXPLAIN_REQUEST_REASON)?.message = reason
        return this
    }

    /**
     * 设置拒绝时的说明，该信息会在请求被拒绝时提示
     */
    fun setDeniedTips(tips: String): PermissionRequestBuilder {
        localConfig.getExplainDialogConfig(PermissionDialogType.EXPLAIN_DENIED_TIPS)?.message = tips
        return this
    }

    /**
     * 设置跳转权限设置页面的提示信息
     */
    fun setForwardSettingTips(tips: String): PermissionRequestBuilder {
        localConfig.getExplainDialogConfig(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS)?.message = tips
        return this
    }

    /**
     * 设置请求时需要解释说明的弹窗，推荐使用 [getPermissionDialogConfig] 获取配置接口配置本次显示的 dialog 配置
     */
    fun setExplainDialog(dialog: PermissionExplainDialogInterface): PermissionRequestBuilder {
        this.localConfig.setExplainDialog(dialog)
        return this
    }

    /**
     * 设置请求时需要解释说明的弹窗，推荐使用 [getPermissionDialogConfig] 获取配置接口配置本次显示的 dialog 配置
     */
    fun setExplainDialog(type: PermissionDialogType, dialog: PermissionExplainDialogInterface): PermissionRequestBuilder {
        this.localConfig.setExplainDialog(type, dialog)
        return this
    }

    /**
     * 设置权限组的说明提示，推荐使用 [getPermissionDialogConfig] 获取配置接口配置本次显示的 dialog 配置
     */
    fun setPermissionGroupExplainTips(permission: String, tips: String): PermissionRequestBuilder {
        this.localConfig.setPermissionGroupExplainTips(permission, tips)
        return this
    }

    @JvmOverloads
    fun request(callback: PermissionRequestCallback? = null) {
        permissionMediator.permissions(permissionHolder)
            .apply {
                if (lightColorTint != null && darkColorTint != null) {
                    setDialogTintColor(lightColorTint!!, darkColorTint!!)
                }
                val requestReason = localConfig.getExplainDialogConfigEnsureExist(PermissionDialogType.EXPLAIN_REQUEST_REASON).message
                val deniedReason = localConfig.getExplainDialogConfigEnsureExist(PermissionDialogType.EXPLAIN_DENIED_TIPS).message
                val settingReason = localConfig.getExplainDialogConfigEnsureExist(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS).message
                //如果存在请求权限原因，则配置请求权限前的提示
                if (requestReason.isNotEmpty()) {
                    explainReasonBeforeRequest()
                    onExplainRequestReason { scope, deniedList, beforeRequest ->
                        if (beforeRequest) {
                            processExplainDialog(scope, deniedList, activity, PermissionDialogType.EXPLAIN_REQUEST_REASON)
                        } else if (!beforeRequest) {
                            if (deniedReason.isNotEmpty()) {
                                processExplainDialog(scope, deniedList, activity, PermissionDialogType.EXPLAIN_DENIED_TIPS)
                            } else {
                                //2022/2/10 Lincoln-如果是拒绝的情况下，又不需要显示出dialog的话，则标识dialog没有被调用
                                showDialogCalled = false
                            }
                        }
                    }
                } else if (deniedReason.isNotEmpty()) {
                    //如果存在拒绝权限的请求，则配置拒绝时的提示
                    onExplainRequestReason { scope, deniedList ->
                        processExplainDialog(scope, deniedList, activity, PermissionDialogType.EXPLAIN_DENIED_TIPS)
                    }
                }
                if (settingReason.isNotEmpty()) {
                    //如果存在跳转配置的请求，则配置跳转配置的提示
                    onForwardToSettings { scope, deniedList ->
                        if (settingReason.isNotEmpty()) {
                            processExplainDialog(scope, deniedList, activity, PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS)
                        }
                    }
                }
                if (callback != null) {
                    request { allGranted, grantedList, deniedList ->
                        callback.onResult(allGranted, grantedList, deniedList)
                    }
                } else {
                    request(null)
                }
            }
    }

    //处理权限对话框配置
    private fun processExplainDialog(scope: IScope, deniedList: List<String>, activity: FragmentActivity, type: PermissionDialogType) {
        getExplainDialogOrDefaultDialog(type).let {
            val delegate = localConfig.getExplainDialogDelegate()
            val config = PermissionDialogConfig(activity, permissionHolder)
            config.applyConfig(localConfig.getExplainDialogConfigEnsureExist(type))
            val processByUser = delegate
                ?.onExplainDialogConfig(type, this, config, deniedList)
                ?: false
            if (!processByUser) {
                if (localConfig.isShowPermissionGroupExplainTipsEnabled(type)) {
                    config.permissionTips = localConfig.generateDefaultPermissionGroupExplainTips(deniedList)
                }
            }
            it.setDialogConfig(config)
            scope.showHandlePermissionDialog(it)
        }
    }

    private fun getExplainDialogOrDefaultDialog(type: PermissionDialogType): PermissionExplainDialogInterface {
        return localConfig.getPermissionExplainDialog(type)
            ?: localConfig.getPermissionExplainDialogEnsureExist(PermissionDialogType.EXPLAIN_REQUEST_REASON)
    }

    companion object {
        private var globalConfig: IPermissionConfigDialogOperation = PermissionDialogRequestGlobalConfig()
        private val permissionGroup: Map<String, String> = permissionMapOnR

        @JvmStatic
        fun newInstance(fragment: Fragment): PermissionRequestBuilder {
            val builder = PermissionRequestBuilder()
            builder.permissionMediator = PermissionX.init(fragment)
            return builder
        }

        @JvmStatic
        fun newInstance(activity: FragmentActivity): PermissionRequestBuilder {
            val builder = PermissionRequestBuilder()
            builder.permissionMediator = PermissionX.init(activity)
            return builder
        }

        @JvmStatic
        fun init(globalConfig: IPermissionConfigDialogOperation) {
            Companion.globalConfig = globalConfig
        }

        internal fun getPermissionGroupOrReturnSelf(permission: String): String {
            return permissionGroup[permission] ?: permission
        }
    }
}

