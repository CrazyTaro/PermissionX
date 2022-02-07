package com.permissionx.app

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.permissionx.guolilndev.lincolnct.permission.PermissionDialogRequestGlobalConfig
import com.permissionx.guolilndev.lincolnct.permission.PermissionDialogType
import com.permissionx.guolilndev.lincolnct.permission.PermissionRequestBuilder

class MainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val makeRequestBtn = view.findViewById<Button>(R.id.makeRequestBtn)
        makeRequestBtn.setOnClickListener {
            PermissionRequestBuilder.newInstance(this)
                .requestPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_CALENDAR,
                )
                .setPermissionGroupExplainTips(Manifest.permission.CAMERA, "相机")
                .setPermissionGroupExplainTips(Manifest.permission.ACCESS_FINE_LOCATION, "位置信息")
                .setPermissionGroupExplainTips(Manifest.permission.RECORD_AUDIO, "位置信息")
                .setRequestReason("请求以上权限是必须的")
                .setDeniedTips("这些权限都是必须的，请授权使用")
                .setForwardSettingTips("以上权限都是需要的，请到设置中开启授权")
                .apply {
                    getPermissionDialogConfig()
                        .setExplainDialogPositiveText(PermissionDialogType.EXPLAIN_REQUEST_REASON, "皮卡丘")
                        .setShowPermissionGroupExplainTipsEnabled(true)
                        .setShowPermissionGroupExplainTipsEnabled(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, true)
                        .setExplainDialogDelegate { type, builer, config, denied ->
                            when (type) {
                                PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS -> {
                                    config.permissionTips = listOf("哈哈", "222")
                                    config.positiveText = "请前往系统设置页面"
                                    config.negativeText = "我就不给权限"
                                    if (denied.contains(Manifest.permission.READ_CALENDAR)) {
                                        config.message = config.message.plus("\n日历是必须的，如果不给不知道行程")
                                    }
                                    true
                                }
                                else -> false
                            }
                        }
                }
                .request { granted, grantedList, deniedList ->
                    if (granted) {
                        Toast.makeText(activity, "All permissions are granted", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(activity, "The following permissions are denied：$deniedList", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }

        view.findViewById<Button>(R.id.makeDefaultRequestBtn)
            .setOnClickListener {
                PermissionRequestBuilder.newInstance(this)
                    .requestPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_CALENDAR,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_CALENDAR,
                    )
                    .request { granted, grantedList, deniedList ->
                        if (granted) {
                            Toast.makeText(activity, "All permissions are granted", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(activity, "The following permissions are denied：$deniedList", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }

        PermissionRequestBuilder.init(
            PermissionDialogRequestGlobalConfig().apply {
                setExplainDialogMessageText(PermissionDialogType.EXPLAIN_REQUEST_REASON, "这是默认的请求文本")
                setExplainDialogPositiveText(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, "这是默认的确认文本")
                setExplainDialogNegativeText(PermissionDialogType.EXPLAIN_DENIED_TIPS, "这是默认的取消文本")
                setShowPermissionGroupExplainTipsEnabled(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, true)
                setExplainDialogMessageText(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, "默认跳转设置时是列出权限分组的，其它的没有")
            }
        )
    }
}