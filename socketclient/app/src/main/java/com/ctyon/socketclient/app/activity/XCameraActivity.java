package com.ctyon.socketclient.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.ctyon.socketclient.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import me.xmai.global.config.Constants;

/**
 * 相机界面
 */
public class XCameraActivity extends Activity{


    private Camera mCamera;
    private SurfaceView mSurface;
    private boolean isCameraFinish = true;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1111) {
                if(isCameraFinish) {
                    if (mCamera != null) {
                        isCameraFinish = false;
                        mCamera.takePicture(null, new RawCallback(), new PictureTakeCallback());
                    }
                }
            }
            if (msg.what == 4444) {
                XCameraActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        setContentView(R.layout.activity_camera);
        initComponentView();
        initComponentEvent();

        handler.sendEmptyMessageDelayed(1111, 1500);
    }


    private void initComponentEvent() {


    }

    private void initComponentView() {

        mSurface = (SurfaceView)findViewById(R.id.camera_surface);

        SurfaceHolder holder = mSurface.getHolder();

        holder.setFixedSize(240, 240);
        holder.setKeepScreenOn(true);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new TakeCameraSurfaceCallback());
    }

    /**
     * 打开相机
     */
    private void openCamera(){
        try{
            mCamera = Camera.open();
            if (mCamera == null) {
                int cametacount = Camera.getNumberOfCameras();
                mCamera = Camera.open(cametacount - 1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 释放相机
     */
    private void releaseCamera(){
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 获取最佳尺寸
     * @param sizes
     * @param
     * @param
     * @return
     */
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

    /**
     * 通知更新
     * @param file
     */
    private void notifyUpdate(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        sendBroadcast(intent);
    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_take_pic:
                if(!isAnimating && isCameraFinish) {
                    if (mCamera != null) {
                        isCameraFinish = false;
                        mCamera.takePicture(null, new RawCallback(), new PictureTakeCallback());
                    }
                }
                break;
            default:
                break;
        }
    }*/

    /**
     * 将照片旋转
     * @param bm
     * @param degree
     * @return
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    private final class TakeCameraSurfaceCallback implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            openCamera();
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> supportedPreviewSizes = params.getSupportedPreviewSizes();
            Camera.Size bestSupportedSize = getBestSupportedSize(supportedPreviewSizes);
            params.setJpegQuality(80);//照片质量
            params.setPictureSize(1024, 768);//图片分辨率
            params.setPreviewFrameRate(5);//预览帧率
            params.setPreviewSize(bestSupportedSize.width,bestSupportedSize.height);
            mCamera.setDisplayOrientation(90);
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {

                        }
                    });
            /**
             * 开启预览
             */
            mCamera.startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mCamera.stopPreview();
            mCamera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            releaseCamera();
        }
    }

    private final class RawCallback implements Camera.PictureCallback{

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    }

    private final class PictureTakeCallback implements Camera.PictureCallback{

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap bitmap = rotateBitmapByDegree(bmp, 90);
            if(bitmap != null){
                mCamera.stopPreview();
                //mPicture.setVisibility(View.VISIBLE);
                mSurface.setVisibility(View.GONE);
                //mPicture.setImageBitmap(bitmap);
            }

            // 获取Jpeg图片，并保存在sd卡上
            String path = Constants.MODEL.CACHEPATH.CACHE;
            File dirF = new File(path);
            if(!dirF.exists())
            {
                dirF.mkdirs();
            }
            File file = new File(path + System.currentTimeMillis()+ ".jpg");

            /*String fileName= Environment.getExternalStorageDirectory().getPath()
                    + File.separator
                    +"Pictures"
                    +File.separator
                    +"Pic_"+System.currentTimeMillis()+".jpg";
            File file=new File(fileName);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdir();//创建文件夹
            }*/
            try {
                BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);//向缓冲区压缩图片
                bos.flush();
                bos.close();
                Log.i("shipeixian", "拍照成功，照片已保存！");
                Settings.Global.putString(getContentResolver(),
                        Constants.MODEL.SETTINGS.GLOBAL_PIC_PATH,file.getAbsolutePath());
                sendBroadcast(new Intent("com.ctyon.shawn.UPLOAD_PHOTO").putExtra("is_upload_success", true));
                //notifyUpdate(file);
                mSurface.setVisibility(View.VISIBLE);
                //mPicture.setVisibility(View.GONE);
                isCameraFinish = true;
                mCamera.startPreview();
                handler.sendEmptyMessageDelayed(4444, 1500);
            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(XCameraActivity.this, "拍照失败！", Toast.LENGTH_SHORT).show();
                Log.i("shipeixian", "拍照失败！");
                sendBroadcast(new Intent("com.ctyon.shawn.UPLOAD_PHOTO").putExtra("is_upload_success", false));
                handler.sendEmptyMessageDelayed(4444, 1500);
            }
        }
    }

    //add by shipeixian for cancel animation begin
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    //add by shipeixian for cancel animation end
}
