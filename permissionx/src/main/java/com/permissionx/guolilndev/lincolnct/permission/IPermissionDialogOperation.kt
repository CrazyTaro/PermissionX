package com.permissionx.guolilndev.lincolnct.permission

/**
 * 权限相关的处理操作
 */
interface IPermissionOperation {
    fun setExplainDialog(dialog: PermissionExplainDialogInterface): IPermissionOperation
    fun setExplainDialog(type: PermissionDialogType, dialog: PermissionExplainDialogInterface): IPermissionOperation
    /**
     * 设置授权权限的分组，用于优化显示多个权限的分类；
     * - 在配置权限后，将尝试根据该权限去查找对应的权限组，如果能查找到，则更新该权限组的说明；
     * - 如果无法找到该权限对应的权限组（有可能该权限不存在权限组中），则会配置该权限对应的说明，即将该权限视为单独一个权限组进行配置
     *
     * @param permission 某个具体的权限，注意这里是具体的某个权限，而不是权限组本身
     * @param tips 该权限所在的项目组的说明
     */
    fun setPermissionGroupExplainTips(permission: String, tips: String): IPermissionOperation
    fun setShowPermissionGroupExplainTipsEnabled(show: Boolean): IPermissionOperation
    fun setShowPermissionGroupExplainTipsEnabled(type: PermissionDialogType, show: Boolean): IPermissionOperation
    fun setExplainDialogConfig(type: PermissionDialogType, dialogConfig: IPermissionDialogConfig): IPermissionOperation
}

interface IPermissionConfigOperation : IPermissionOperation {
    fun getPermissionExplainDialog(type: PermissionDialogType): PermissionExplainDialogInterface?
    fun getPermissionExplainDialogEnsureExist(type: PermissionDialogType): PermissionExplainDialogInterface {
        return getPermissionExplainDialog(type)!!
    }

    fun isShowPermissionGroupExplainTipsEnabled(type: PermissionDialogType): Boolean

    fun getExplainDialogConfig(type: PermissionDialogType): IPermissionDialogConfig?
    fun getExplainDialogConfigEnsureExist(type: PermissionDialogType): IPermissionDialogConfig {
        return getExplainDialogConfig(type)!!
    }

    fun setExplainDialogPositiveText(type: PermissionDialogType, text: String): IPermissionConfigOperation {
        getExplainDialogConfig(type)?.positiveText = text
        return this
    }

    fun setExplainDialogNegativeText(type: PermissionDialogType, text: String): IPermissionConfigOperation {
        getExplainDialogConfig(type)?.negativeText = text
        return this
    }

    fun setExplainDialogMessageText(type: PermissionDialogType, text: String): IPermissionConfigOperation {
        getExplainDialogConfig(type)?.message = text
        return this
    }
}

interface IPermissionRequestBuilder {
    fun getPermissionDialogConfig(): IPermissionConfigOperation
}