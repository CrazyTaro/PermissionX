package com.permissionx.app

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.permissionx.guolilndev.lincolnct.dialog.RationaleDialog

@TargetApi(30)
class CustomDialog(context: Context, private val message: String, private val permissions: List<String>) :
    RationaleDialog(context, R.style.CustomDialog) {

    val messageText by lazy { findViewById<TextView>(R.id.messageText) }
    val positiveBtn by lazy { findViewById<Button>(R.id.positiveBtn) }
    val negativeBtn by lazy { findViewById<Button>(R.id.negativeBtn) }
    val permissionsLayout by lazy { findViewById<ViewGroup>(R.id.permissionsLayout) }

    private val permissionMap = mapOf(
        Manifest.permission.READ_CALENDAR to Manifest.permission_group.CALENDAR,
        Manifest.permission.WRITE_CALENDAR to Manifest.permission_group.CALENDAR,
        Manifest.permission.READ_CALL_LOG to Manifest.permission_group.CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG to Manifest.permission_group.CALL_LOG,
        "android.permission.PROCESS_OUTGOING_CALLS" to Manifest.permission_group.CALL_LOG,
        Manifest.permission.CAMERA to Manifest.permission_group.CAMERA,
        Manifest.permission.READ_CONTACTS to Manifest.permission_group.CONTACTS,
        Manifest.permission.WRITE_CONTACTS to Manifest.permission_group.CONTACTS,
        Manifest.permission.GET_ACCOUNTS to Manifest.permission_group.CONTACTS,
        Manifest.permission.ACCESS_FINE_LOCATION to Manifest.permission_group.LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION to Manifest.permission_group.LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION to Manifest.permission_group.LOCATION,
        Manifest.permission.RECORD_AUDIO to Manifest.permission_group.MICROPHONE,
        Manifest.permission.READ_PHONE_STATE to Manifest.permission_group.PHONE,
        Manifest.permission.READ_PHONE_NUMBERS to Manifest.permission_group.PHONE,
        Manifest.permission.CALL_PHONE to Manifest.permission_group.PHONE,
        Manifest.permission.ANSWER_PHONE_CALLS to Manifest.permission_group.PHONE,
        Manifest.permission.ADD_VOICEMAIL to Manifest.permission_group.PHONE,
        Manifest.permission.USE_SIP to Manifest.permission_group.PHONE,
        Manifest.permission.ACCEPT_HANDOVER to Manifest.permission_group.PHONE,
        Manifest.permission.BODY_SENSORS to Manifest.permission_group.SENSORS,
        Manifest.permission.ACTIVITY_RECOGNITION to Manifest.permission_group.ACTIVITY_RECOGNITION,
        Manifest.permission.SEND_SMS to Manifest.permission_group.SMS,
        Manifest.permission.RECEIVE_SMS to Manifest.permission_group.SMS,
        Manifest.permission.READ_SMS to Manifest.permission_group.SMS,
        Manifest.permission.RECEIVE_WAP_PUSH to Manifest.permission_group.SMS,
        Manifest.permission.RECEIVE_MMS to Manifest.permission_group.SMS,
        Manifest.permission.READ_EXTERNAL_STORAGE to Manifest.permission_group.STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE to Manifest.permission_group.STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION to Manifest.permission_group.STORAGE
    )

    private val groupSet = HashSet<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_layout)
        messageText.text = message
        buildPermissionsLayout()
        window?.let {
            val param = it.attributes
            val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()
            val height = param.height
            it.setLayout(width, height)
        }
    }

    override fun getNegativeButton(): View {
        return negativeBtn
    }

    override fun getPositiveButton(): View {
        return positiveBtn
    }

    override fun getPermissionsToRequest(): List<String> {
        return permissions
    }

    private fun buildPermissionsLayout() {
        for (permission in permissions) {
            val permissionGroup = permissionMap[permission]
            if (permissionGroup != null && !groupSet.contains(permissionGroup)) {
                val itemBinding = layoutInflater.inflate(R.layout.permissions_item, permissionsLayout, false) as TextView
                itemBinding.text = context?.let {
                    it.packageManager.getPermissionGroupInfo(permissionGroup, 0)
                        .loadLabel(it.packageManager)
                }
                permissionsLayout.addView(itemBinding)
                groupSet.add(permissionGroup)
            }
        }
    }

}