package com.permissionx.guolilndev.lincolnct.dialog

import androidx.fragment.app.FragmentManager

/**
 * permission dialog interface, works like a dialog. this interface can be implemented by dialog or dialog fragment,
 * permissionX will try to show this dialog interface as a dialog by calling method "showDialog()";
 * while show as a dialog fail the method "showDialogFragment()" will be called so that this dialog interface can be shown normally.
 *
 * all the setXXX methods will call first before trying to show the dialog.so maybe you can't use views directly in any setMethods, cause
 * usually views will be created on dialog's 'onCreated' method.
 *
 * for now,this dialog interface only considers would be implemented by dialog or dialog fragment.
 */
interface PermissionDialogInterface {

    /**
     * set the positive callback called when dialog executes positive action
     */
    fun setPositiveAction(callback: PermissionPositiveCallback)

    /**
     * set the negative callback called when dialog executes negative action
     */
    fun setNegativeAction(callback: PermissionNegativeCallback)

    /**
     * set the dismiss callback called when dialog dismisses
     */
    fun setDismissListener(callback: PermissionDismissCallback)

    /**
     * set this dialog whether can be cancelable or not.
     */
    fun setDialogCancelable(cancelable: Boolean)

    /**
     * set this dialog whether can be cancelable or not.
     */
    fun setDialogCanceledOnTouchOutside(cancelable: Boolean)

    /**
     * try to show a dialog, return true if the operation supported otherwise return false. remember this method will be called after all setMethod
     */
    fun showDialog(): Boolean

    /**
     * try to show a dialog fragment, return true if the operation supported otherwise return false. remember this method will be called after all setMethod
     */
    fun showDialogFragment(fm: FragmentManager, tag: String): Boolean

    fun dismissDialog()

    /**
     * Provide permissions to request. These permissions should be the ones that shows on your rationale dialog.
     * @return Permissions list to request.
     */
    fun getPermissionsToRequest(): List<String>
}