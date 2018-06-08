package com.example.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.FrameLayout;

import me.xmai.global.config.Constants;

/**
 * 设置定时拍照功能
 * @author huqiang
 *
 */
public class InitTimetoTakePic {

	private static InitTimetoTakePic mInstance;
	Activity mContext;
	static FrameLayout mSurfaceViewFrame;
	private static Camera mCamera;
    private static CameraPreview mPreview;
	private static takePicCallback mTakePicCallback;
    
	static String TAG = InitTimetoTakePic.class.getSimpleName();
	private InitTimetoTakePic(Activity context)
	{
		this.mContext = context;
	}
	public synchronized static InitTimetoTakePic getInstance(Activity context,takePicCallback tpicCallback)
	{
		if(mInstance ==null)
		{
			mInstance = new InitTimetoTakePic(context);
			
		}
		mTakePicCallback=tpicCallback;
		return mInstance;
	}
	public void initView(FrameLayout surfaceViewFrame)
	{
		mSurfaceViewFrame = surfaceViewFrame;
	}
	/**
	 * 启动定时拍照并上传功能
	 */
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			try {
				switch(msg.what)
				{
					case 1:
						Log.v(TAG, "开始拍照");
						initCarema();
						break;
					case 2:
						mCamera.autoFocus(new AutoFocusCallback() {

							@Override
							public void onAutoFocus(boolean success, Camera camera) {
								// 从Camera捕获图片
								Log.v(TAG, "自动聚焦111"+success);
								mCamera.takePicture(null, null, mPicture);
//		            	mHandler.sendEmptyMessageDelayed(1, 5*1000);
							}
						});
						break;
				}
			} catch (Exception e) {

			}
		}
	};
	public void start()
	{
		mHandler.sendEmptyMessageDelayed(1, 3*1000); //7s 后开始启动相机
	}
	private void initCarema() {
		Log.v(TAG, "initCarema");
		if(mCamera==null)
		{
			Log.v(TAG, "camera=null");
			mCamera = getCameraInstance();
			mPreview = new CameraPreview(mContext, mCamera);
			mSurfaceViewFrame.removeAllViews();
			mSurfaceViewFrame.addView(mPreview);
		}
		Log.v(TAG, mCamera==null ?"mCamera is null":"mCamera is not null");
		mCamera.startPreview();
        mHandler.sendEmptyMessageDelayed(2, 3*1000); //3s后拍照
	}
	/** 检测设备是否存在Camera硬件 */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // 存在
            return true;
        } else {
            // 不存在
            return false;
        }
    }

    /** 打开一个Camera */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); 
            c.setDisplayOrientation(90);
            Camera.Parameters mParameters = c.getParameters();
            //可以用得到当前所支持的照片大小，然后
            List<Size> ms = mParameters.getSupportedPictureSizes();
            mParameters.setPictureSize(ms.get(0).width, ms.get(0).height);  //默认最大拍照取最大清晰度的照片
            c.setParameters(mParameters);
        } catch (Exception e) {
            Log.d(TAG, "打开Camera失败失败");
        }
        return c; 
    }

    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 获取Jpeg图片，并保存在sd卡上
        	String path = Constants.MODEL.CACHEPATH.CACHE;
        	File dirF = new File(path);
        	if(!dirF.exists())
        	{
        		dirF.mkdirs();
        	}
        	File pictureFile = new File(path + System.currentTimeMillis()+ ".jpg");
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Log.d(TAG, "保存图成功"+pictureFile.getAbsolutePath());
                mTakePicCallback.takePicSuccess(pictureFile.getAbsolutePath());
            } catch (Exception e) {
                Log.d(TAG, "保存图片失败");
                mTakePicCallback.takePicFailed(e.toString());
                e.printStackTrace();
            }
            releaseCarema();
        }
    };
    public void releaseCarema(){
    	if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
    }

    public interface takePicCallback{
    	void takePicSuccess(String path);
    	void takePicFailed(String msg);
	}
}
