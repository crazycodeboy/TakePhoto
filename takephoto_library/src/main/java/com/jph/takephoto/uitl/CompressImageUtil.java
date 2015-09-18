package com.jph.takephoto.uitl;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 压缩照片2.0
 * @author JPH
 * @date 2015-08-26 下午1:44:26
 */
public class CompressImageUtil {
	//压缩文件的路径
	private String imgPath;
	//压缩结果监听器
	private CompressListener listener;
	/**
	 * 多线程压缩图片的质量
	 * @author JPH
	 * @param bitmap 内存中的图片
	 * @param imgPath 图片的保存路径
	 * @date 2014-12-5下午11:30:43
	 */
	private void compressImageByQuality(final Bitmap bitmap,final String imgPath){
		if(bitmap==null){
			sendMsg(false,"像素压缩失败");
			return;
		}
		new Thread(new Runnable() {//开启多线程进行压缩处理
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int options = 100;
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
				while (baos.toByteArray().length / 1024 >100) {//循环判断如果压缩后图片是否大于100KB,大于继续压缩         
					baos.reset();//重置baos即让下一次的写入覆盖之前的内容 
					options -= 10;//图片质量每次减少10
					if(options<0)options=0;//如果图片质量小于0，则将图片的质量压缩到最小值
					bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//将压缩后的图片保存到baos中
					if(options==0)break;//如果图片的质量已降到最低则，不再进行压缩
				}
//				if(bitmap!=null&&!bitmap.isRecycled()){
//					bitmap.recycle();//回收内存中的图片
//				}
				try {
					FileOutputStream fos = new FileOutputStream(new File(imgPath));//将压缩后的图片保存的本地上指定路径中
					fos.write(baos.toByteArray());
					fos.flush();
					fos.close();
					sendMsg(true, imgPath);
				} catch (Exception e) {
					sendMsg(false,"质量压缩失败");
					e.printStackTrace();
				}
			}
		}).start();
	}
	/**
	 * 按比例缩小图片的像素以达到压缩的目的
	 * @author JPH
	 * @param imgPath
	 * @return 
	 * @date 2014-12-5下午11:30:59
	 */
	private void compressImageByPixel(String imgPath) {
		Bitmap bitmap=null;
		if(imgPath==null){
			sendMsg(false,"要压缩的文件不存在");
			return;
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;//只读边,不读内容
		bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int width = newOpts.outWidth;
		int height = newOpts.outHeight;
		float maxSize =1200f;//默认1200px
		int be = 1;
		if (width > height && width > maxSize) {//缩放比,用高或者宽其中较大的一个数据进行计算
			be = (int) (newOpts.outWidth / maxSize);
			be++;
		} else if (width < height && height > maxSize) {
			be = (int) (newOpts.outHeight / maxSize);
			be++;
		}
		newOpts.inSampleSize =be;//设置采样率
		newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
		bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
		compressImageByQuality(bitmap,imgPath);//压缩好比例大小后再进行质量压缩  
	}
	/**
	 * 压缩文件并检查压缩是否完成
	 * @author JPH
	 * @date 2015-1-26 下午4:58:18
	 * @param imgPath
	 */
	public void compressImageByPixel(String imgPath,CompressListener listener) {
		this.imgPath=imgPath;
		this.listener=listener;
		File file=new File(imgPath);
		if (file==null||!file.exists()||!file.isFile()){//如果文件不存在，则不做任何处理
			sendMsg(false,"要压缩的文件不存在");
			return;
		}
		this.compressImageByPixel(imgPath);
	}

	/**
	 * 发送压缩结果的消息
	 * @param isSuccess 压缩是否成功
	 * @param obj
	 */
	private void sendMsg(boolean isSuccess,String obj){
		Message msg=new Message();
		msg.obj=obj;
		msg.what=isSuccess?1:0;
		mhHandler.sendMessage(msg);
	}
	/**
	 * 此handle的目的主要是为了将接口在主线程中触发
	 * ，为了安全起见把接口放到主线程触发
	 */
	Handler mhHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1){//压缩成功
				listener.onCompressSuccessed((String)msg.obj);
			}else if (msg.what==0) {//压缩失败
				listener.onCompressFailed((String) msg.obj);
			}
		}
	};

	/**
	 * 压缩结果监听器
	 */
	public interface CompressListener{
		/**
		 * 压缩成功
		 * @param imgPath 压缩图片的路径
		 */
		void onCompressSuccessed(String imgPath);

		/**
		 * 压缩失败
		 * @param msg 失败的原因
		 */
		void onCompressFailed(String msg);
	}
}
