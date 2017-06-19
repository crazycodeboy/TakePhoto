## [TakePhoto](https://github.com/crazycodeboy/TakePhoto) 简介    
`TakePhoto`是一款用于Android设备获取照片（拍照或从相册、文件中选择）、裁剪图片、压缩图片的开源工具库，目前最新版本**[2.0.0已发布，请使用最新版](https://github.com/crazycodeboy/TakePhoto)**。   

**V1.0+**  

- 支持以拍照的方式获取照片 
- 支持从相册选择照片  
- 支持对照片进行裁切
- 支持对照片进行压缩
- 支持对裁切及压缩参数自定义  
- 支持因拍照Activity被回收后的自动恢复   

GitHub地址： [https://github.com/crazycodeboy/TakePhoto](https://github.com/crazycodeboy/TakePhoto)
## 如何使用   
### 使用TakePhoto有以下两种方式：
**方式一：通过继承的方式**  
1. 继承TakePhotoActivity、TakePhotoFragmentActivity、TakePhotoFragment三者之一。  
2. 通过`getTakePhoto()`获取TakePhoto实例进行相关操作。  
3. 重写以下方法获取结果        
```java
void takeSuccess(String imagePath);  
void takeFail(String msg);
void takeCancel();
```  
此方式使用简单，满足的大部分的使用需求，具体使用详见simple。如果通过继承的方式无法满足实际项目的使用，可以通过下面介绍的方式。  

**方式二：通过组装的方式**  
1. 获取TakePhoto实例`TakePhoto takePhoto=new TakePhotoImpl(getActivity(),this);`  
2. 在 `onCreate`,`onActivityResult`,`onSaveInstanceState`方法中调用TakePhoto对用的方法。  
3. 调用TakePhoto实例进行相关操作。  
4. 在`TakeResultListener`相关方法中获取结果。      
### 关于压缩照片 
你可以选择是否对照片进行压缩处理。  
```java
 /**
  * 启用照片压缩
  * @param config 压缩照片配置
  * @param showCompressDialog 压缩时是否显示进度对话框
  * @return 
  */
 TakePhoto onEnableCompress(CompressConfig config,boolean showCompressDialog);
```
eg：  
`getTakePhoto().onEnableCompress(new CompressConfig.Builder().setMaxSize(50*1024).setMaxPixel(800).create(),true).onPicSelectCrop(imageUri);`  
如果你启用了照片压缩，TakePhoto会使用`CompressImage`对照片进行压缩处理，CompressImage目前支持对照片的尺寸以及照片的质量进行压缩。默认情况下，CompressImage开启了尺寸与质量双重压缩，
你可以通过CompressConfig.Builder对照片压缩后的尺寸以及质量进行相关设置。如果你想改变压缩的方式可以通过CompressConfig.Builder进行相关设置。  
## 关于兼容性问题  
TakePhoto是基于Android官方标准API编写的，适配了目前市场上主流的Rom。如果你在使用过程中发现了适配问题，可以提交Issues。   
1. 为适配部分手机拍照时会回收Activity,TakePhoto在`onSaveInstanceState`与 `onCreate`做了相应的恢复处理。  
2. 为适配部分手机拍照或从相册选择照片时屏幕方向会发生转变,从而导致拍照失败的问题，可以在AndroidManifest.xml中对使用了TakePhoto的Activity添加android:configChanges="orientation|keyboardHidden|screenSize"配置。  
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

## 在项目中使用    
为方便大家使用，现已将TakePhoto发布到JCenter(如果你对如何将项目发布到JCenter感兴趣可以参考：《[教你轻松将Android library 发布到JCenter](http://blog.csdn.net/fengyuzhengfan/article/details/51407009))》  
Gradle:  
```groovy 
    compile 'com.jph.takephoto:takephoto_library:1.0.1'
```

Maven:  
```groovy 
<dependency>
  <groupId>com.jph.takephoto</groupId>
  <artifactId>takephoto_library</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```  
## 最后  
如果你对[TakePhoto](https://github.com/crazycodeboy/TakePhoto)有更好的建议或想改造它，欢迎大家Fork and Pull requests。  
