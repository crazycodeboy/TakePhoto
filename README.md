## [TakePhoto](https://github.com/crazycodeboy/TakePhoto) 简介

[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](https://github.com/crazycodeboy/TakePhoto/pulls)
[![Download](https://api.bintray.com/packages/crazycodeboy/maven/TakePhoto/images/download.svg) ](https://bintray.com/crazycodeboy/maven/TakePhoto/_latestVersion)
[![GitHub release](https://img.shields.io/github/release/crazycodeboy/TakePhoto.svg?maxAge=2592000?style=flat-square)](https://github.com/crazycodeboy/TakePhoto/releases)
[![License Apache2.0](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat)](https://raw.githubusercontent.com/crazycodeboy/TakePhoto/master/LICENSE)



`TakePhoto`是一款用于在Android设备上获取照片（拍照或从相册、文件中选择）、裁剪图片、压缩图片的开源工具库，目前最新版本[4.1.0](https://github.com/crazycodeboy/TakePhoto/)。
3.0以下版本及API说明，详见[TakePhoto2.0+](https://github.com/crazycodeboy/TakePhoto/blob/master/README.2+.md)。  

>TakePhoto交流平台：QQ群：556387607（群1，未满）

**V4.0**

- 支持通过相机拍照获取图片
- 支持从相册选择图片
- 支持从文件选择图片  
- 支持批量图片选取
- 支持图片压缩以及批量图片压缩
- 支持图片裁切以及批量图片裁切
- 支持照片旋转角度自动纠正
- 支持自动权限管理(无需关心SD卡及摄像头权限等问题)
- 支持对裁剪及压缩参数个性化配置  
- 提供自带裁剪工具(可选)  
- 支持智能选取及裁剪异常处理
- 支持因拍照Activity被回收后的自动恢复   
- 支持Android8.1
- +支持多种压缩工具
- +支持多种图片选择工具

GitHub地址： [https://github.com/crazycodeboy/TakePhoto](https://github.com/crazycodeboy/TakePhoto)
## 目录

- [安装说明](#安装说明)
- [演示](#演示)
- [使用说明](#使用说明)
- [自定义UI](#自定义ui)
- [API](#api)
- [兼容性](#兼容性)
- [贡献](#贡献)
- [更新说明](#更新说明)
- [最后](#混淆)

## 安装说明  
**Gradle:**  

```groovy
    compile 'com.jph.takephoto:takephoto_library:4.1.0'
```

**Maven:**  

```groovy
<dependency>
  <groupId>com.jph.takephoto</groupId>
  <artifactId>takephoto_library</artifactId>
  <version>4.1.0</version>
  <type>pom</type>
</dependency>
```  


## 演示 

运行效果图：    
![预览图](https://raw.githubusercontent.com/crazycodeboy/TakePhoto/master/Screenshots/takephoto_preview.png)
![运行效果图](https://raw.githubusercontent.com/crazycodeboy/TakePhoto/master/Screenshots/%E9%A2%84%E8%A7%88%E5%9B%BE.jpg)


## 使用说明   

### 使用TakePhoto有以下两种方式：
**方式一：通过继承的方式**  
1. 继承`TakePhotoActivity`、`TakePhotoFragmentActivity`、`TakePhotoFragment`三者之一。  
2. 通过`getTakePhoto()`获取`TakePhoto`实例进行相关操作。  
3. 重写以下方法获取结果        

```java
 void takeSuccess(TResult result);
 void takeFail(TResult result,String msg);
 void takeCancel();
```  
此方式使用简单，满足的大部分的使用需求，具体使用详见[simple](https://github.com/crazycodeboy/TakePhoto/blob/master/simple/src/main/java/com/jph/simple/SimpleActivity.java)。如果通过继承的方式无法满足实际项目的使用，可以通过下面介绍的方式。  

**方式二：通过组装的方式**  

可参照：[TakePhotoActivity](https://github.com/crazycodeboy/TakePhoto/blob/master/takephoto_library/src/main/java/com/jph/takephoto/app/TakePhotoActivity.java)，以下为主要步骤：  

1.实现`TakePhoto.TakeResultListener,InvokeListener`接口。

2.在 `onCreate`,`onActivityResult`,`onSaveInstanceState`方法中调用TakePhoto对用的方法。  

3.重写`onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)`，添加如下代码。

```java
  @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionManager.handlePermissionsResult(this,type,invokeParam,this);
    }
```    

4.重写`TPermissionType invoke(InvokeParam invokeParam)`方法，添加如下代码：  

```java
 @Override
    public TPermissionType invoke(InvokeParam invokeParam) {
        TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(TPermissionType.WAIT.equals(type)){
            this.invokeParam=invokeParam;
        }
        return type;
    }
```

5.添加如下代码获取TakePhoto实例：  

```java
   /**
     *  获取TakePhoto实例
     * @return
     */
    public TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        return takePhoto;
    }    
```

## 自定义UI

TakePhoto不仅支持对相关参数的自定义，也支持对UI的自定义，下面就像大家介绍如何自定义TakePhoto的相册与裁剪工具的UI。

### 自定义相册
如果TakePhoto自带相册的UI不符合你应用的主题的话，你可以对它进行自定义。方法如下：   

#### 自定义Toolbar 

在“res/layout”目录中创建一个名为“toolbar.xml”的布局文件，内容如下：   

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.Toolbar xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    app:theme="@style/CustomToolbarTheme"
    android:background="#ffa352">
</android.support.v7.widget.Toolbar>
```

在“toolbar.xml”文件中你可以指定TakePhoto自带相册的主题以及Toolbar的背景色。

#### 自定义状态栏

在“res/values”目录中创建一个名为“colors.xml”的资源文件，内容如下： 

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="multiple_image_select_primaryDark">#212121</color>
</resources>
```

通过上述方式便可以自定义状态栏的颜色。

#### 自定义提示文字

在“res/values”目录的“string.xml”文件冲添加如下代码：

```xml
<resources>    
    <string name="album_view">选择图片</string>
    <string name="image_view">单击选择</string>
    <string name="add">确定</string>
    <string name="selected">已选</string>
    <string name="limit_exceeded">最多能选 %d 张</string>
</resources>
```

重写上述代码，便可以自定义TakePhoto自带相册的提示文字。

### 自定义裁切工具

在“res/layout”目录中创建一个名为“crop__activity_crop.xml”与“crop__layout_done_cancel.xml”的布局文件，内容如下：  

**crop__activity_crop.xml**  

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <com.soundcloud.android.crop.CropImageView
        android:id="@+id/crop_image"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_alignParentTop="true"
        android:background="@drawable/crop__texture"
        android:layout_above="@+id/done_cancel_bar" />
    <include
        android:id="@+id/done_cancel_bar"
        android:layout_alignParentBottom="true"
        layout="@layout/crop__layout_done_cancel"
        android:layout_height="50dp"
        android:layout_width="match_parent" />
</RelativeLayout>
```

**crop__layout_done_cancel.xml**  

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    style="@style/Crop.DoneCancelBar">
    <FrameLayout
        android:id="@+id/btn_cancel"
        style="@style/Crop.ActionButton">
        <TextView style="@style/Crop.ActionButtonText.Cancel" />
    </FrameLayout>
    <FrameLayout
        android:id="@+id/btn_done"
        style="@style/Crop.ActionButton">
        <TextView style="@style/Crop.ActionButtonText.Done" />
    </FrameLayout>
</LinearLayout>
```

重写上述代码，便可以自定义TakePhoto裁切工具的UI。

## API

### 获取图片
TakePhoto提供拍照，从相册选择，从文件中选择三种方式获取图片。    

#### API:

```java
/**
 * 从文件中获取图片（不裁剪）
 */
void onPickFromDocuments();
/**
 * 从相册中获取图片（不裁剪）
 */
void onPickFromGallery();
/**
 * 从相机获取图片(不裁剪)
 * @param outPutUri 图片保存的路径
 */
void onPickFromCapture(Uri outPutUri);
/**
 * 图片多选
 * @param limit 最多选择图片张数的限制
 **/
void onPickMultiple(int limit);
```
以上三种方式均提供对应的裁剪API，详见：[裁剪图片](https://github.com/crazycodeboy/TakePhoto#裁剪图片)。    
**注：**  
由于不同Android Rom厂商对系统有不同程度的定制，有可能导致某种选择图片的方式不支持，所以为了提高`TakePhoto`的兼容性，当某种选的图片的方式不支持时，`TakePhoto`会自动切换成使用另一种选择图片的方式进行图片选择。      

### 裁剪图片  

#### API  
`TakePhoto`支持对图片进行裁剪，无论是拍照的照片,还是从相册、文件中选择的图片。你只需要调用`TakePhoto`的相应方法即可：  

```java
/**
 * 从相机获取图片并裁剪
 * @param outPutUri 图片裁剪之后保存的路径
 * @param options 裁剪配置             
 */
void onPickFromCaptureWithCrop(Uri outPutUri, CropOptions options);
/**
 * 从相册中获取图片并裁剪
 * @param outPutUri 图片裁剪之后保存的路径
 * @param options 裁剪配置
 */
void onPickFromGalleryWithCrop(Uri outPutUri, CropOptions options);
/**
 * 从文件中获取图片并裁剪
 * @param outPutUri 图片裁剪之后保存的路径
 * @param options 裁剪配置
 */
void onPickFromDocumentsWithCrop(Uri outPutUri, CropOptions options);
/**
 * 图片多选，并裁切
 * @param limit 最多选择图片张数的限制
 * @param options  裁剪配置
 * */
void onPickMultipleWithCrop(int limit, CropOptions options);
```   
#### 对指定图片进行裁剪     
另外，TakePhoto也支持你对指定图片进行裁剪：     

```java
/**
 * 裁剪图片
 * @param imageUri 要裁剪的图片
 * @param outPutUri 图片裁剪之后保存的路径
 * @param options 裁剪配置
 */
void onCrop(Uri imageUri, Uri outPutUri, CropOptions options)throws TException;
/**
 * 裁剪多张图片
 * @param multipleCrop 要裁切的图片的路径以及输出路径
 * @param options 裁剪配置
 */
void onCrop(MultipleCrop multipleCrop, CropOptions options)throws TException;
```

#### CropOptions
`CropOptions`是用于裁剪的配置类，通过它你可以对图片的裁剪比例，最大输出大小，以及是否使用`TakePhoto`自带的裁剪工具进行裁剪等，进行个性化配置。    

**Usage:**  

```java
 CropOptions cropOptions=new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(true).create();  
 getTakePhoto().onPickFromDocumentsWithCrop(imageUri,cropOptions);  
 //或  
 getTakePhoto().onCrop(imageUri,outPutUri,cropOptions);  

```

**注：**  
由于不同Android Rom厂商对系统有不同程度的定制，有可能系统中没有自带或第三方的裁剪工具，所以为了提高`TakePhoto`的兼容性，当系统中没有自带或第三方裁剪工具时，`TakePhoto`会自动切换到使用`TakePhoto`自带的裁剪工具进行裁剪。  

>另外TakePhoto4.0+支持指定使用TakePhoto自带相册,如：`takePhoto.setTakePhotoOptions(new TakePhotoOptions.Builder().setWithOwnGallery(true).create());`
详情可参考:[Demo](https://github.com/crazycodeboy/TakePhoto/blob/master/simple/src/main/java/com/jph/simple/CustomHelper.java)

### 压缩图片
你可以选择是否对图片进行压缩处理，你只需要告诉它你是否要启用压缩功能以及`CompressConfig`即可。  

#### API
```java
 /**
  * 启用图片压缩
  * @param config 压缩图片配置
  * @param showCompressDialog 压缩时是否显示进度对话框
  * @return
  */
 void onEnableCompress(CompressConfig config,boolean showCompressDialog);
```

**Usage:**  

```java
TakePhoto takePhoto=getTakePhoto();
takePhoto.onEnableCompress(compressConfig,true);
takePhoto.onPickFromGallery();
```  
如果你启用了图片压缩，`TakePhoto`会使用`CompressImage`对图片进行压缩处理，`CompressImage`目前支持对图片的尺寸以及图片的质量进行压缩。默认情况下，`CompressImage`开启了尺寸与质量双重压缩。  

#### 对指定图片进行压缩  
另外，你也可以对指定图片进行压缩：    
**Usage:**  

```java
new CompressImageImpl(compressConfig,result.getImages(), new CompressImage.CompressListener() {
    @Override
    public void onCompressSuccess(ArrayList<TImage> images) {
        //图片压缩成功
    }
    @Override
    public void onCompressFailed(ArrayList<TImage> images, String msg) {
        //图片压缩失败
    }
}).compress();
```

#### CompressConfig  
`CompressConfig`是用于图片压缩的配置类，你可以通过`CompressConfig.Builder`对图片压缩后的尺寸以及质量进行相关设置。如果你想改变压缩的方式可以通过`CompressConfig.Builder`进行相关设置。     
**Usage:**   

```java
CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(50*1024).setMaxPixel(800).create();
```
#### 指定压缩工具

#### 使用TakePhoto压缩工具进行压缩： 

```
CompressConfig config=new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width>=height? width:height)
                    .create();
takePhoto.onEnableCompress(config,showProgressBar);
```

#### 使用Luban进行压缩： 
```
LubanOptions option=new LubanOptions.Builder()
                    .setGear(Luban.CUSTOM_GEAR)
                    .setMaxHeight(height)
                    .setMaxWidth(width)
                    .setMaxSize(maxSize)
                    .create();
CompressConfig config=CompressConfig.ofLuban(option);
takePhoto.onEnableCompress(config,showProgressBar);
```

>详情可参考Demo:[CustomHelper.java](https://github.com/crazycodeboy/TakePhoto/blob/master/simple/src/main/java/com/jph/simple/CustomHelper.java)


## 兼容性

### Android6.0
由于Android6.0新增了"运行时权限控制(Runtime Permissions)"，为了应对这一改变，TakePhoto加入和自动权限管理，当TakePhoto检测到需要权限时，TakePhoto会自动申请权限，所以小伙伴们不用担心权限的使用问题。

### Android7.0  

在Android N中，Android 框架执行了 StrictMode，应用间共享文件和以前也有所区别。为了适配Android7.0的改变，同时也为了方便大家使用TakePhoto，TakePhoto会自动根据手机的Android版本自行适配，小伙伴们依旧可以向TakePhoto传递`Uri imageUri = Uri.fromFile(file);`类型的Uri而不用担心兼容性问题。

### TakePhoto在深度兼容性方面的测试    
![兼容性测试报告](https://raw.githubusercontent.com/crazycodeboy/TakePhoto/master/Screenshots/%E5%85%BC%E5%AE%B9%E6%80%A7%E6%B5%8B%E8%AF%95.jpg)

### 获取更高的兼容性    
`TakePhot`o是基于Android官方标准API编写的，适配了目前市场上主流的Rom。如果你在使用过程中发现了适配问题，可以[提交Issues](https://github.com/crazycodeboy/TakePhoto/issues)。   
1. 为适配部分手机拍照时会回收`Activity`，`TakePhoto`在`onSaveInstanceState`与 `onCreate`做了相应的恢复处理。  
2. 为适配部分手机拍照或从相册选择图片时屏幕方向会发生转变,从而导致拍照失败的问题，可以在AndroidManifest.xml中对使用了`TakePhoto`的`Activity`添加android:configChanges="orientation|keyboardHidden|screenSize"配置。  
eg:  

```
<activity
    android:name=".MainActivity"
    android:screenOrientation="portrait"
    android:configChanges="orientation|keyboardHidden|screenSize"
    android:label="@string/app_name" >
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

## 贡献  
如果你在使用TakePhoto中遇到任何问题可以提[Issues](https://github.com/crazycodeboy/TakePhoto/issues)出来。另外欢迎大家为TakePhoto贡献智慧，欢迎大家[Fork and Pull requests](https://github.com/crazycodeboy/TakePhoto)。     

## 更新说明

v4.1.0(2018/4/2)
-----------------

1. Upgrade glide to 4.6.1；
2. Upgrade  buildToolsVersion & targetSdkVersion ；
3. rename package name ;

v4.0.3(2017/1/18)
-----------------
**Bugfixes**

1. Fixed bug and add new features([`62a6725`](https://github.com/crazycodeboy/TakePhoto/commit/62a6725a99118ec0ce0f4cf1cd76b2ba70e21745))-@[Yanqilong](https://github.com/Yanqilong)
2. fix 鲁班压缩出现路径重复([`a0a64a59`](https://github.com/crazycodeboy/TakePhoto/commit/a0a64a59762fa8554eb46b6ec544f70a5d46f551))-@[namezhouyu](https://github.com/namezhouyu)


v4.0.2(2016/11/28)
------------------
1. 压缩成功后返回原图路径(originalPath), 以便用户可以自行处理原图。
2. 压缩成功后压缩路径path改为compressPath。
3. 压缩成功后返回图片来源类型，现在分CAMERA, OTHER两种。
4. 用户可以配置CompressConfig.enableReserveRaw(boolean)方法，ture保留原图，false删除原图，当且仅当类型为CAMERA此配置才有效
5. 纠正拍照旋转角度功能改为可选

## 最后

### 关于代码混淆
如果你的项目中启用了代码混淆，可在混淆规则文件(如：proguard-rules.pro)中添加如下代码：   

```
-keep class com.jph.takephoto.** { *; }
-dontwarn com.jph.takephoto.**

-keep class com.darsh.multipleimageselect.** { *; }
-dontwarn com.darsh.multipleimageselect.**

-keep class com.soundcloud.android.crop.** { *; }
-dontwarn com.soundcloud.android.crop.**

```
