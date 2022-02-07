package com.permissionx.app

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.permissionx.guolilndev.lincolnct.permission.AbsPermissionExplainDialog
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
                //配置全局的dialog样式，通过接口的方式，允许实际的dialog是使用任何的方式实现
                //默认的 PermissionDialogRequestGlobalConfig 已经有实现的 dialog
                setExplainDialog(PermissionDialogType.EXPLAIN_REQUEST_REASON, object :
                    AbsPermissionExplainDialog() {
                    override fun showDialog(): Boolean {
                        val dialogInterface = this
                        dialog = AlertDialog.Builder(config.context)
                            .apply {
                                if (config.title.isNotEmpty()) {
                                    setTitle(config.title)
                                }
                                setMessage(generateDisplayMessage(config))
                                setCancelable(cancelable)
                                if (config.positiveText.isNotEmpty()) {
                                    setPositiveButton(config.positiveText) { dialog, which ->
                                        positiveCallback?.onPositiveAction(dialogInterface)
                                    }
                                }
                                if (config.negativeText.isNotEmpty()) {
                                    setNegativeButton(config.negativeText) { dialog, which ->
                                        negativeCallback?.onNegativeAction(dialogInterface)
                                    }
                                }
                            }
                            .create()
                        if (dismissCallback != null) {
                            dialog.setOnDismissListener {
                                dismissCallback?.onDismissAction(dialogInterface)
                            }
                        }
                        dialog.show()
                        return true
                    }
                })
                //设置默认的对话框类型的文本内容/确认文本/取消文本
                setExplainDialogMessageText(PermissionDialogType.EXPLAIN_REQUEST_REASON, "这是默认的请求文本")
                setExplainDialogPositiveText(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, "这是默认的确认文本")
                setExplainDialogNegativeText(PermissionDialogType.EXPLAIN_DENIED_TIPS, "这是默认的取消文本")
                setExplainDialogMessageText(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, "默认跳转设置时是列出权限分组的，其它的没有")
                //设置默认的对话框类型是否显示出权限分组的提示信息
                setShowPermissionGroupExplainTipsEnabled(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, true)
            }
        )
    }
}