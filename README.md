## [TakePhoto](https://github.com/crazycodeboy/TakePhoto) 简介  
`TakePhoto`是一款用于在Android设备上获取照片（拍照或从相册、文件中选择）、裁剪图片、压缩图片的开源工具库，目前最新版本[2.0.4](https://github.com/crazycodeboy/TakePhoto/)。  
2.0以下版本及API说明，详见[TakePhoto1.0+](https://github.com/crazycodeboy/TakePhoto/blob/master/README%20-V1.0+.md)。  

**V2.0**    

- 支持通过相机拍照获取图片
- 支持从相册选择图片
- 支持从文件选择图片  
- 支持对图片进行压缩
- 支持对图片进行裁剪
- 支持对裁剪及压缩参数个性化配置  
- 提供自带裁剪工具(可选)  
- 支持智能选取及裁剪异常处理
- 支持因拍照Activity被回收后的自动恢复   

GitHub地址： [https://github.com/crazycodeboy/TakePhoto](https://github.com/crazycodeboy/TakePhoto)

## 预览图  

运行效果图：    

![运行效果图](https://raw.githubusercontent.com/crazycodeboy/TakePhoto/master/Screenshots/%E9%A2%84%E8%A7%88%E5%9B%BE.jpg)


## 如何使用   

### 使用TakePhoto有以下两种方式：
**方式一：通过继承的方式**  
1. 继承`TakePhotoActivity`、`TakePhotoFragmentActivity`、`TakePhotoFragment`三者之一。  
2. 通过`getTakePhoto()`获取`TakePhoto`实例进行相关操作。  
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
 TakePhoto onEnableCompress(CompressConfig config,boolean showCompressDialog);
```

**Usage:**  

```java
getTakePhoto().onEnableCompress(compressConfig,true).onPickFromGalleryWithCrop(imageUri,cropOptions);
```  
如果你启用了图片压缩，`TakePhoto`会使用`CompressImage`对图片进行压缩处理，`CompressImage`目前支持对图片的尺寸以及图片的质量进行压缩。默认情况下，`CompressImage`开启了尺寸与质量双重压缩。  

#### 对指定图片进行压缩  
另外，你也可以对指定图片进行压缩：    
**Usage:**  

```java
new CompressImageImpl(compressConfig).compress(picturePath, new CompressImage.CompressListener() {
    @Override
    public void onCompressSuccess(String imgPath) {//图片压缩成功

    }
    @Override
    public void onCompressFailed(String imagePath,String msg) {//图片压缩失败

    }
});
```

#### CompressConfig  
`CompressConfig`是用于图片压缩的配置类，你可以通过`CompressConfig.Builder`对图片压缩后的尺寸以及质量进行相关设置。如果你想改变压缩的方式可以通过`CompressConfig.Builder`进行相关设置。     
**Usage:**   

```java
CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(50*1024).setMaxPixel(800).create();
getTakePhoto().onEnableCompress(compressConfig,true).onPickFromGallery();
```


## 兼容性

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

## 在项目中使用    
为方便大家使用，现已将TakePhoto V2.0.4发布到JCenter(如果你对如何将项目发布到JCenter感兴趣可以参考：《[教你轻松将Android library 发布到JCenter](http://blog.csdn.net/fengyuzhengfan/article/details/51407009))》  
Gradle:  

```groovy
    compile 'com.jph.takephoto:takephoto_library:2.0.4'
```

Maven:  

```groovy
<dependency>
  <groupId>com.jph.takephoto</groupId>
  <artifactId>takephoto_library</artifactId>
  <version>2.0.4</version>
  <type>pom</type>
</dependency>
```  

## 最后  
如果你对[TakePhoto](https://github.com/crazycodeboy/TakePhoto)有更好的建议或想改造它，欢迎大家[Fork and Pull requests](https://github.com/crazycodeboy/TakePhoto)。  
