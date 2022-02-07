# PermissionX

更新自郭霖的 permissionx ，实现一些项目上需要的配置及优化 UI 的适配和处理方式。 原项目请查看：
[中文文档](https://blog.csdn.net/sinyu890807/category_10108528.html)

更新后的项目相比于原项目，会让项目在自定义和个性化方式更加自由。

[![](https://jitpack.io/v/CrazyTaro/PermissionX.svg)](https://jitpack.io/#CrazyTaro/PermissionX)

## Quick Setup

```groovy
repositories {
    google()
    mavenCentral()
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.CrazyTaro:PermissionX:Tag'
}
```

## Basic Usage

首先请在 manifest 的文件中声明需要授权的权限。

```xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.permissionx.app">

    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.CALL_PHONE" />

</manifest>
```

接下来，你就可以在代码中进行动态申请权限了。

```kotlin
PermissionRequestBuilder.newInstance(this)
    .requestPermissions(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE
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
```

以上是最简单的权限请求处理方式。通过创建 PermissionRequestBuilder 来构建需要申请的权限，在创建时，允许权限传入 activity 或者是 fragment
作为权限初始化的对象。

## Request Dialog tips

相比于原来的 permissionx，要不需要自定义 dialog 对象，通过实现库中提供的 RationaleDialogFragment 或 RationaleDialog
来解决个性化弹窗的问题；要不只能使用默认的 dialog 样式，可能不满足自己的需要。
并且在使用申请权限前的原因提示、权限拒绝后的解释说明，以及跳转设置页面时的提示信息等都需要自行额外配置和调用接口，这里提供了默认的实现和快捷配置方式。

```kotlin
PermissionRequestBuilder.newInstance(this)
    .requestPermissions(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE
    )
    .setRequestReason("设置权限请求的原因，在权限请求前提示")
    .setDeniedTips("设置权限被拒绝时的解释原因，在权限被拒绝后提示")
    .setForwardSettingTips("设置跳转权限设置页面的说明，在权限被禁止后又不能提示时显示")
    .request()
```

提示信息（对话框）分三种类型，详见`PermissionDialogType`：

- EXPLAIN_REQUEST_REASON，请求权限的原因，在申请前提示
- EXPLAIN_DENIED_TIPS，权限被拒绝后的提示，在申请权限被拒绝后提示
- EXPLAIN_FORWARD_SETTING_TIPS，跳转设置页面的提示

**注意权限被拒绝后，是否有拒绝提示的弹窗取决于 fragment.shouldShowRequestPermissionRationale()
系统方法，该方法大致意思为：在权限被用户拒绝后，是否允许向用户解释权限申请的原因，以便用户更好地理解和去授权权限。该操作由系统决定。**

当申请的权限中任一权限被禁止了不再询问时，将会显示跳转设置页面的提示信息。

## Custom Dialog Style

在自身项目不使用任何默认的 dialog 或者是 dialogFragment，或者是不方便去实现/继承 RationaleDialog
相关的类时（如使用了第三方库），此库提供了通过接口的方式直接对对话框样式进行定制的功能。

**在 init 方法中配置了全局的对话框样式后，默认情况下都会使用该对话框样式**。

```kotlin
PermissionRequestBuilder.init(
    PermissionDialogRequestGlobalConfig().apply {
        //配置全局的dialog样式，通过接口的方式，允许实际的dialog是使用任何的方式实现
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
```

自定义 dialog 的样式只要实现了接口 PermissionExplainDialogInterface 即可，推荐实现 AbsPermissionExplainDialog
类，可以快速完成接口的实现，因为部分参数仅是用于存储记录数据而已。 AbsPermissionExplainDialog 默认是通过 dialog 实现的，只要在 `showDialog()`
方法中创建相应的 dialog 对象（**继承自系统的 dialog**），并为该抽象类中的 dialog 字段赋值即可；**否则需要自行实现`showDialog()`
与`dismissDialog()`两个方法**

## More Usage

### Permission Group Tips

类似于原 permissionx 项目中可以对权限进行分组说明，以便用户更好地理解，当前项目对该功能进行了扩展。

默认情况下基于 30 版本的 API 权限及其分组进行了定义，但是后续有可能会有其它新的权限或者是原来的权限分组信息不满足要求，可以对分组信息进行调整或修改。

```kotlin
//直接对权限进行配置分组信息
PermissionDialogRequestGlobalConfig()
    .setPermissionGroupExplainTips(Manifest.permission.READ_CALENDAR, "系统日历权限")
    .setPermissionGroupExplainTips(Manifest.permission.WRITE_CALENDAR, "系统日历权限")
    //api 23 以后就会有分组信息，对分组信息进行设置可以修改分组的说明
    .setPermissionGroupExplainTips(Manifest.permission_group.CONTACTS, "系统联系人")
```

设置权限分组的提示信息可以对原来分组的信息进行修改或调整。注意这个功能的使用必须跟另一个配置配合使用。
因为权限分组提示信息一般是在请求原因或拒绝权限时用来提示用户当前需要申请的权限属于哪些分组，简要提示用户。但是并不是所有地方都需要这样的提示，所以可以在需要使用时配置上**显示权限分组信息**

```kotlin
PermissionDialogRequestGlobalConfig()
    //为不同的对话框配置是否显示权限分组的提示信息
    .setShowPermissionGroupExplainTipsEnabled(PermissionDialogType.EXPLAIN_FORWARD_SETTING_TIPS, true)
```

## Dark Theme

不对原项目的主题配置做变更，自定义对话框需要自行实现暗黑主题的配置

## License

```
Copyright (C) guolin, PermissionX Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
