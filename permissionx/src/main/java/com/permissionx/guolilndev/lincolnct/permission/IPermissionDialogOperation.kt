package com.permissionx.guolilndev.lincolnct.permission

import android.content.Context
import android.content.pm.PackageManager

/**
 * 权限对话框的配置操作
 */
interface IPermissionConfigDialogOperation {
    //region 快捷的参数配置
    /**
     * 设置指定类型对话框的确认文本
     */
    fun setExplainDialogPositiveText(type: PermissionDialogType, text: String): IPermissionConfigDialogOperation {
        getExplainDialogConfig(type)?.positiveText = text
        return this
    }
    /**
     * 设置指定类型对话框的取消文本
     */
    fun setExplainDialogNegativeText(type: PermissionDialogType, text: String): IPermissionConfigDialogOperation {
        getExplainDialogConfig(type)?.negativeText = text
        return this
    }
    /**
     * 设置指定类型对话框的消息内容
     */
    fun setExplainDialogMessageText(type: PermissionDialogType, text: String): IPermissionConfigDialogOperation {
        getExplainDialogConfig(type)?.message = text
        return this
    }
    //endregion

    //region 全局配置，对所有对话框类型都生效的方法
    /**
     * 设置默认的对话框的接口，等同于 [setExplainDialog] 并配置所有对话框类型均使用此接口
     */
    fun setExplainDialog(dialog: PermissionExplainDialogInterface): IPermissionConfigDialogOperation {
        PermissionDialogType.values()
            .forEach { setExplainDialog(it, dialog) }
        return this
    }
    /**
     * 设置全局的对话框类型是否都需要显示出权限分组的提示信息，详见 [setShowPermissionGroupExplainTipsEnabled]
     */
    fun setShowPermissionGroupExplainTipsEnabled(show: Boolean): IPermissionConfigDialogOperation {
        PermissionDialogType.values()
            .forEach { setShowPermissionGroupExplainTipsEnabled(it, show) }
        return this
    }
    //endregion

    //region 权限接口代理处理，完全自主地处理对话框参数
    /**
     * 设置权限对话框代理对象
     */
    fun setExplainDialogDelegate(delegate: IPermissionDialogDelegate): IPermissionConfigDialogOperation
    /**
     * 获取权限对话框代理对象
     */
    fun getExplainDialogDelegate(): IPermissionDialogDelegate?
    //endregion
    /**
     * 设置权限提示对话框的接口实现，需要指定对话框类型
     * @param type [PermissionDialogType]，包含三种对话框
     * - [PermissionDialogType.EXPLAIN_REQUEST_REASON]
     * - [PermissionDialogType.EXPLAIN_DENIED_TIPS]
     * - [PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS]
     */
    fun setExplainDialog(type: PermissionDialogType, dialog: PermissionExplainDialogInterface): IPermissionConfigDialogOperation
    /**
     * 设置授权权限的分组，用于优化显示多个权限的分类；
     * - 在配置权限后，将尝试根据该权限去查找对应的权限组，如果能查找到，则更新该权限组的说明；
     * - 如果无法找到该权限对应的权限组（有可能该权限不存在权限组中），则会配置该权限对应的说明，即将该权限视为单独一个权限组进行配置
     *
     * @param permission 某个具体的权限，注意这里是具体的某个权限，而不是权限组本身
     * @param tips 该权限所在的项目组的说明
     */
    fun setPermissionGroupExplainTips(permission: String, tips: String): IPermissionConfigDialogOperation
    /**
     * 指定对话框类型，设置是否显示权限分组的提示信息。如果需要显示，则会在该对话框的内容下再另外显示出当前的权限分组及其提示信息，如
     *
     * ```
     * 以下权限是应用运行所必须的，请授权
     * ● 日历
     * ```
     *
     * 如果提示信息是相同的文本内容，将会被过滤只显示一条信息
     *
     * @param type 权限对话框类型
     * @param show 是否需要显示权限分组提示信息
     */
    fun setShowPermissionGroupExplainTipsEnabled(type: PermissionDialogType, show: Boolean): IPermissionConfigDialogOperation
    /**
     * 设置指定对话框类型的对话框配置信息
     * @param type [PermissionDialogType]
     * @param dialogConfig [IPermissionDialogConfig] 权限对话框配置
     */
    fun setExplainDialogConfig(type: PermissionDialogType, dialogConfig: IPermissionDialogConfig): IPermissionConfigDialogOperation

    /**
     * 获取当前权限的相应类型的对话框
     * @param type [PermissionDialogType]，权限对话框类型
     */
    fun getPermissionExplainDialog(type: PermissionDialogType): PermissionExplainDialogInterface?
    /**
     * 获取当前权限的相应类型的对话框，并确认该类型对话框存在，如不存在则抛出异常
     * @param type [PermissionDialogType]，权限对话框类型
     */
    fun getPermissionExplainDialogEnsureExist(type: PermissionDialogType): PermissionExplainDialogInterface {
        return getPermissionExplainDialog(type)!!
    }

    /**
     * 是否显示指定对话框类型的权限分组提示信息
     * @param type [PermissionDialogType]，权限对话框类型
     */
    fun isShowPermissionGroupExplainTipsEnabled(type: PermissionDialogType): Boolean
    /**
     * 获取权限对话框配置
     * @param type [PermissionDialogType]，权限对话框类型
     */
    fun getExplainDialogConfig(type: PermissionDialogType): IPermissionDialogConfig?
    /**
     * 获取权限对话框配置，并确认存在，如不存在则抛出异常
     */
    fun getExplainDialogConfigEnsureExist(type: PermissionDialogType): IPermissionDialogConfig {
        return getExplainDialogConfig(type)!!
    }

    /**
     * 获取指定的权限分组信息的提示
     */
    fun getPermissionGroupExplainTips(permission: String): String?
    /**
     * 返回当前的权限组说明，这里的参数来自默认的权限组定义，也可以通过 [setPermissionGroupExplainTips] 配置权限组的说明信息;
     */
    fun generateDefaultPermissionGroupExplainTips(context: Context, permissions: List<String>): List<String> {
        return permissions
            .mapNotNull {
                val group = PermissionRequestBuilder.getPermissionGroupOrReturnSelf(it)
                var explainTip = getPermissionGroupExplainTips(group)
                if (explainTip == null) {
                    try {
                        explainTip = context.packageManager.getPermissionGroupInfo(group, 0)
                            .loadLabel(context.packageManager)
                            .toString()
                    } catch (ignore: PackageManager.NameNotFoundException) {
                    }
                }
                explainTip
            }
            .distinct()
    }

    /**
     * 将当前的配置应用新的配置对象
     */
    fun applyConfig(config: IPermissionConfigDialogOperation): IPermissionConfigDialogOperation
}

/**
 * 权限请求构建对象的接口
 */
interface IPermissionRequestBuilder {
    /**
     * 获取权限对话框配置参数
     */
    fun beginDialogConfigTransaction(): IPermissionDialogTransaction
}

/**
 * 用于弹窗配置与权限请求类的转换
 */
interface IPermissionDialogTransaction : IPermissionConfigDialogOperation {
    override fun setExplainDialogPositiveText(type: PermissionDialogType, text: String): IPermissionDialogTransaction {
        super.setExplainDialogPositiveText(type, text)
        return this
    }

    override fun setExplainDialogNegativeText(type: PermissionDialogType, text: String): IPermissionDialogTransaction {
        super.setExplainDialogNegativeText(type, text)
        return this
    }

    override fun setExplainDialogMessageText(type: PermissionDialogType, text: String): IPermissionDialogTransaction {
        super.setExplainDialogMessageText(type, text)
        return this
    }

    override fun setExplainDialog(dialog: PermissionExplainDialogInterface): IPermissionDialogTransaction {
        super.setExplainDialog(dialog)
        return this
    }

    override fun setShowPermissionGroupExplainTipsEnabled(show: Boolean): IPermissionDialogTransaction {
        super.setShowPermissionGroupExplainTipsEnabled(show)
        return this
    }

    override fun setExplainDialogDelegate(delegate: IPermissionDialogDelegate): IPermissionDialogTransaction

    override fun setExplainDialog(type: PermissionDialogType, dialog: PermissionExplainDialogInterface): IPermissionDialogTransaction

    override fun setPermissionGroupExplainTips(permission: String, tips: String): IPermissionDialogTransaction

    override fun setShowPermissionGroupExplainTipsEnabled(type: PermissionDialogType, show: Boolean): IPermissionDialogTransaction

    override fun setExplainDialogConfig(type: PermissionDialogType, dialogConfig: IPermissionDialogConfig): IPermissionDialogTransaction

    override fun applyConfig(config: IPermissionConfigDialogOperation): IPermissionDialogTransaction

    fun endDialogConfigTransaction(): PermissionRequestBuilder
}