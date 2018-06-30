package com.ctyon.watch.ui.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.ctyon.watch.R;
import com.ctyon.watch.utils.ScreenUtils;
import com.ctyon.watch.utils.WarnUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 相机界面
 */
public class CameraActivity extends BaseActivity implements View.OnClickListener{


    private Camera mCamera;
    private int screenWidth;
    private int screenHeight;
    private ImageView mBack;
    private ImageView mTakePic;
    private ImageView mPicture;
    private SurfaceView mSurface;
    private boolean isAnimating = false;
    private boolean isCameraFinish = true;
    private AnimationDrawable frameAnimation;
    private MediaPlayer shootMP;
    private SoundPool mSoundPool;
    private int mRefocusSound;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_camera);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void initComponentEvent() {
        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = ScreenUtils.getScreenHeight(this);
        mBack.setOnClickListener(this);
        mTakePic.setOnClickListener(this);

        mSoundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
        mRefocusSound = mSoundPool.load(this, R.raw.shutter, 1);

        mTakePic.setImageResource(R.drawable.shutter_button_anim);
        mTakePic.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void initComponentView() {

        mSurface = (SurfaceView)findViewById(R.id.camera_surface);
        mPicture = (ImageView)findViewById(R.id.iv_full_pic);
        mBack = (ImageView)findViewById(R.id.iv_camera_back);
        mTakePic = (ImageView)findViewById(R.id.iv_take_pic);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_take_pic:
                if(!isAnimating && isCameraFinish) {
                    if (mCamera != null) {
                        isCameraFinish = false;
                        mCamera.takePicture(null, new RawCallback(), new PictureTakeCallback());
                    }
                    doShutterAnimation();

                    mSoundPool.play(mRefocusSound, 1.0f, 1.0f, 0, 0, 1.0f);
                }
                break;
            case R.id.iv_camera_back:
                finish();
                break;
            default:
                break;
        }
    }

    public void doShutterAnimation() {
        frameAnimation = (AnimationDrawable) mTakePic.getDrawable();
        frameAnimation.stop();
        frameAnimation.start();
    }

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

    public void shootSound()
    {
        AudioManager meng = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);

        if (volume != 0)
        {
            if (shootMP == null)
                shootMP = MediaPlayer.create(this, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (shootMP != null)
                shootMP.start();
        }
    }

    private final class PictureTakeCallback implements Camera.PictureCallback{

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap bitmap = rotateBitmapByDegree(bmp, 90);
            if(bitmap != null){
                mCamera.stopPreview();
                mPicture.setVisibility(View.VISIBLE);
                mSurface.setVisibility(View.GONE);
                mPicture.setImageBitmap(bitmap);
            }

            String photoCountName = "";
            int photoCount = 1;
            List<String> photoNames = getImagePathFromSD();
            if (photoNames != null && photoNames.size() > 0) {
                photoCount = photoNames.size()+1;
            }
            switch (String.valueOf(photoCount).length()){
                case 1:
                    photoCountName = "000"+photoCount+".jpg";
                    break;
                case 2:
                    photoCountName = "00"+photoCount+".jpg";
                    break;
                case 3:
                    photoCountName = "0"+photoCount+".jpg";
                    break;
                default:
                    photoCountName = photoCount+".jpg";
                    break;
            }
            String fileName= Environment.getExternalStorageDirectory().getPath()
                    + File.separator
                    +"Pictures"
                    +File.separator
                    +photoCountName;
            File file=new File(fileName);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdir();//创建文件夹
            }
            try {
                BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);//向缓冲区压缩图片
                bos.flush();
                bos.close();
                WarnUtils.toast(CameraActivity.this,"拍照成功，照片已保存！");
                notifyUpdate(file);
                mSurface.setVisibility(View.VISIBLE);
                mPicture.setVisibility(View.GONE);
                isCameraFinish = true;
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
                WarnUtils.toast(CameraActivity.this,"拍照失败！");
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

    private List<String> getImagePathFromSD() {
        // 图片列表
        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator +"Pictures";
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (checkIsImageFile(file.getPath())) {
                imagePathList.add(file.getPath());
            }
        }
        // 返回得到的图片列表
        return imagePathList;
    }
    /**
     * 检查扩展名，得到图片格式的文件
     * @param fName  文件名
     * @return
     */
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp") ) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }
}
