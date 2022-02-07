/*
 * Copyright (C) guolin, PermissionX Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.permissionx.guolilndev.lincolnct.dialog

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.permissionx.guolilndev.lincolnct.R

/**
 * Default rationale dialog to show if developers did not implement their own custom rationale dialog.
 *
 * @author guolin
 * @since 2020/8/27
 */
class DefaultDialog(
    context: Context,
    private val permissions: List<String>,
    private val message: String,
    private val positiveText: String,
    private val negativeText: String?,
    private val lightColor: Int,
    private val darkColor: Int
) : RationaleDialog(context, R.style.PermissionXDefaultDialog) {

    val messageText by lazy { findViewById<TextView>(R.id.messageText) }
    val positiveBtn by lazy { findViewById<Button>(R.id.positiveBtn) }
    val negativeBtn by lazy { findViewById<Button>(R.id.negativeBtn) }
    val negativeLayout by lazy { findViewById<ViewGroup>(R.id.negativeLayout) }
    val permissionsLayout by lazy { findViewById<ViewGroup>(R.id.permissionsLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permissionx_default_dialog_layout)
        setupText()
        buildPermissionsLayout()
        setupWindow()
    }

    /**
     * Provide the positive button instance to continue requesting.
     * @return Positive button instance to continue requesting.
     */
    override fun getPositiveButton(): View {
        return positiveBtn
    }

    /**
     * Provide the negative button instance to abort requesting.
     * This is alternative. If negativeText is null we just return null, means all these permissions are necessary.
     * @return Negative button instance to abort requesting. Or null if all these permissions are necessary.
     */
    override fun getNegativeButton(): View? {
        return negativeText?.let {
            return negativeBtn
        }
    }

    /**
     * Provide the permissions to request again.
     * @return Permissions to request again.
     */
    override fun getPermissionsToRequest(): List<String> {
        return permissions
    }

    /**
     * Check if the permission layout if empty.
     * It is possible if all the permissions passed in are invalid permission such as a string named
     * "hello world". We won't add these into permission layout.
     */
    internal fun isPermissionLayoutEmpty(): Boolean {
        return permissionsLayout.childCount == 0
    }

    /**
     * Setup text and text color on the dialog.
     */
    private fun setupText() {
        messageText.text = message
        positiveBtn.text = positiveText
        if (negativeText != null) {
            negativeLayout.visibility = View.VISIBLE
            negativeBtn.text = negativeText
        } else {
            negativeLayout.visibility = View.GONE
        }
        if (isDarkTheme()) {
            if (darkColor != -1) {
                positiveBtn.setTextColor(darkColor)
                negativeBtn.setTextColor(darkColor)
            }
        } else {
            if (lightColor != -1) {
                positiveBtn.setTextColor(lightColor)
                negativeBtn.setTextColor(lightColor)
            }
        }
    }

    /**
     * Add every permission that need to explain the request reason to the dialog.
     * But we only need to add the permission group. So if there're two permissions belong to one group, only one item will be added to the dialog.
     */
    private fun buildPermissionsLayout() {
        val tempSet = HashSet<String>()
        val currentVersion = Build.VERSION.SDK_INT
        for (permission in permissions) {
            val permissionGroup = when (currentVersion) {
                Build.VERSION_CODES.Q -> permissionMapOnQ[permission]
                Build.VERSION_CODES.R -> permissionMapOnR[permission]
                else -> {
                    try {
                        val permissionInfo = context.packageManager.getPermissionInfo(permission, 0)
                        permissionInfo.group
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        null
                    }
                }
            }
            if ((permission in allSpecialPermissions && !tempSet.contains(permission))
                || (permissionGroup != null && !tempSet.contains(permissionGroup))) {
                val itemBinding = layoutInflater.inflate(R.layout.permissionx_permission_item, permissionsLayout, false)
                val permissionText = itemBinding.findViewById<TextView>(R.id.permissionText)
                val permissionIcon = itemBinding.findViewById<ImageView>(R.id.permissionIcon)
                when (permission) {
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                        permissionText.text = context.getString(R.string.permissionx_access_background_location)
                        permissionIcon.setImageResource(R.drawable.permissionx_ic_location)
                    }
                    Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                        permissionText.text = context.getString(R.string.permissionx_system_alert_window)
                        permissionIcon.setImageResource(R.drawable.permissionx_ic_alert)
                    }
                    Manifest.permission.WRITE_SETTINGS -> {
                        permissionText.text = context.getString(R.string.permissionx_write_settings)
                        permissionIcon.setImageResource(R.drawable.permissionx_ic_setting)
                    }
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
                        permissionText.text = context.getString(R.string.permissionx_manage_external_storage)
                        permissionIcon.setImageResource(R.drawable.permissionx_ic_storage)
                    }
                    else -> {
                        permissionText.text = context.getString(context.packageManager.getPermissionGroupInfo(permissionGroup!!, 0).labelRes)
                        permissionIcon.setImageResource(context.packageManager.getPermissionGroupInfo(permissionGroup, 0).icon)
                    }
                }
                if (isDarkTheme()) {
                    if (darkColor != -1) {
                        permissionIcon.setColorFilter(darkColor, PorterDuff.Mode.SRC_ATOP)
                    }
                } else {
                    if (lightColor != -1) {
                        permissionIcon.setColorFilter(lightColor, PorterDuff.Mode.SRC_ATOP)
                    }
                }
                permissionsLayout.addView(itemBinding)
                tempSet.add(permissionGroup ?: permission)
            }
        }
    }

    /**
     * Setup dialog window to show. Control the different window size in portrait and landscape mode.
     */
    private fun setupWindow() {
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        if (width < height) {
            // now we are in portrait
            window?.let {
                val param = it.attributes
                it.setGravity(Gravity.CENTER)
                param.width = (width * 0.86).toInt()
                it.attributes = param
            }
        } else {
            // now we are in landscape
            window?.let {
                val param = it.attributes
                it.setGravity(Gravity.CENTER)
                param.width = (width * 0.6).toInt()
                it.attributes = param
            }
        }
    }

    /**
     * Currently we are in dark theme or not.
     */
    private fun isDarkTheme(): Boolean {
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

}