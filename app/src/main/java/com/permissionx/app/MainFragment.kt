package com.permissionx.app

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
//                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .setPermissionGroupExplainTips(Manifest.permission.CAMERA, "相机权限(系统默认是称为相机)")
                .setRequestReason("请求以上权限是必须的，除相机是自定义权限组说明外，其它为系统默认说明")
                //如果没有权限拒绝时的提示信息，权限没有被完全禁止掉不再询问时，将直接回调申请结果，不会跳转系统设置
                .setDeniedTips("")
                .setForwardSettingTips("以上权限都是需要的，请到设置中开启授权")
                //配置本次申请所有弹窗的自定义样式
                //.setExplainDialog(null)
                //配置本次申请所有弹窗是否显示权限分组提示信息
                //.setShowPermissionGroupExplainTipsEnabled(false)
                //获取对话框更多的配置，可以精确配置到某个对话框类型的参数
                .beginDialogConfigTransaction()
                //单独修改指定对话框类型的确定/取消/内容文本等，是否显示权限分组提示信息
                .setExplainDialogPositiveText(PermissionDialogType.EXPLAIN_REQUEST_REASON, "皮卡丘")
                .setShowPermissionGroupExplainTipsEnabled(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, true)
                //单独处理弹窗显示内容
                .setExplainDialogDelegate { type, builer, config, denied ->
                    when (type) {
                        //只处理跳转系统权限的文本
                        PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS -> {
                            //处理权限组的提示，这里配置的话，将会完全替换掉权限分组的描述
                            //如果需要原始的分组描述，可以使用以下方法，生成默认的权限分组描述
                            //builer.getPermissionDialogConfig().generateDefaultPermissionGroupExplainTips(requireContext(),denied)
                            config.permissionTips = listOf("哈哈", "222")
                            //修改弹窗按钮文本
                            config.positiveText = "请前往系统设置页面"
                            config.negativeText = "我就不给权限"
                            //修改文本内容，默认message中为上述配置的提示信息
                            if (denied.contains(Manifest.permission.READ_CALENDAR)) {
                                config.message = config.message.plus("\n日历是必须的，如果不给不知道行程")
                            }
                            true
                        }
                        else -> false
                    }
                }
                .endDialogConfigTransaction()
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
                setExplainDialog(CustomDialog())
                //设置全局所有的默认的对话框类型是否显示出权限分组的提示信息
                setShowPermissionGroupExplainTipsEnabled(true)
                //如果需要指定对话框类型时，则需要配置指定的对话框类型及其相应的参数信息
                //setExplainDialog(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS,CustomDialog())
                //setShowPermissionGroupExplainTipsEnabled(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS,false)
                //设置默认的对话框类型的文本内容/确认文本/取消文本
                setExplainDialogMessageText(PermissionDialogType.EXPLAIN_REQUEST_REASON, "这是默认的请求文本")
                setExplainDialogMessageText(PermissionDialogType.EXPLAIN_DENIED_TIPS, "这是默认的拒绝提示文本")
                setExplainDialogPositiveText(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, "这是默认的确认文本")
                setExplainDialogNegativeText(PermissionDialogType.EXPLAIN_DENIED_TIPS, "这是默认的取消文本")
                setExplainDialogMessageText(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, "默认跳转设置时是列出权限分组的，其它的没有")
            }
        )
    }

    class CustomDialog : AbsPermissionExplainDialog() {
        override fun showDialog(): Boolean {
            val dialogInterface = this
            dialog = AlertDialog.Builder(config.context)
                .apply {
                    val content = LayoutInflater.from(config.context)
                        .inflate(R.layout.custom_dialog_layout, null, false)
                    setView(content)
                    val tvMessage = content.findViewById<TextView>(R.id.messageText)
                    val btnNegative = content.findViewById<Button>(R.id.negativeBtn)
                    val btnPositive = content.findViewById<Button>(R.id.positiveBtn)

                    tvMessage.text = generateDisplayMessage(config)
                    btnNegative.text = config.negativeText
                    btnPositive.text = config.positiveText
                    btnNegative.setOnClickListener { negativeCallback?.onNegativeAction(dialogInterface) }
                    btnPositive.setOnClickListener { positiveCallback?.onPositiveAction(dialogInterface) }

                    setCancelable(cancelable)
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
    }
}