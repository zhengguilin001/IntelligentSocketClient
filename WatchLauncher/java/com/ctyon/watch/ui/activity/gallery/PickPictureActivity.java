package com.ctyon.watch.ui.activity.gallery;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ctyon.watch.R;
import com.ctyon.watch.ui.activity.BaseActivity;
import com.mylhyl.pickpicture.PickPictureCallback;
import com.mylhyl.pickpicture.PickPictureHelper;
import com.mylhyl.pickpicture.PictureTotal;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
//add by shipeixian for adjust ui begin
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
//add by shipeixian for adjust ui end

/**
 * 手机图片列表中的详细
 * Created by hupei on 2016/7/14.
 */
public class PickPictureActivity extends BaseActivity {

    public static final String EXTRA_PICTURE_PATH = "picture_path";

    private ProgressDialog mProgressDialog;
    private TextView          tv;
    private PickPictureHelper pickPictureHelper;

    List<PictureTotal> list;
    ViewPager mVpContainer;
    private List<String> mList;//此相册下所有图片的路径集合

    //add by shipeixian for adjust ui begin
    private ListView photoListView;
    private PhotoAdapter photoAdapter;
    private Button goBackButton;
    private RelativeLayout titleBar;
    //add by shipeixian for adjust ui end

    private ViewPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_picture);
        mVpContainer = (ViewPager) findViewById(R.id.vp_container);
        tv = (TextView) findViewById(R.id.tv);
        //add by shipeixian for adjust ui begin
        titleBar = (RelativeLayout) findViewById(R.id.titleBar);
        goBackButton = (Button) findViewById(R.id.backButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                PickPictureActivity.this.finish();
            }
        });
        photoListView = (ListView) findViewById(R.id.photoListView);
        photoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mVpContainer.setCurrentItem(i, false);
                titleBar.setVisibility(View.GONE);
                photoListView.setVisibility(View.GONE);
                mVpContainer.setVisibility(View.VISIBLE);
            }
        });

        photoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showNormalDialog(i);
                return true;
            }
        });
        //add by shipeixian for adjust ui end
    }

    @Override
    protected void setContentView() {

    }

    @Override
    protected void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mList = getImagePathFromSD();
                if(mList != null && mList.size() > 0) {
                    // 进行ui操作
                    tv.setVisibility(View.GONE);
                    mVpContainer.setVisibility(View.GONE);

                    mAdapter = new ViewPagerAdapter(mList, PickPictureActivity.this);
                    mVpContainer.setAdapter(mAdapter);

                    mAdapter.setOnViewPagerClickListener(new ViewPagerAdapter.OnViewPagerLongClickListener() {
                        @Override
                        public void onLongClickListener(View view, int position) {
                            Log.d("fei",mList.get(position));
                            showNormalDialog(position);
                        }
                    });
                    photoAdapter = new PhotoAdapter(PickPictureActivity.this, mList);
                    photoListView.setAdapter(photoAdapter);
                    photoListView.setVisibility(View.VISIBLE);
                    photoAdapter.notifyDataSetChanged();
                }
            }
        }).start();
    }

    @Override
    protected void initComponentEvent() {

    }

    @Override
    protected void initComponentView() {

    }

    private void showNormalDialog(final int position){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);

        normalDialog.setTitle("提示");
        normalDialog.setMessage("是否删除图片");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DeleteImage(mList.get(position));
                    }
                });
                     normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }


    private void DeleteImage(String imgPath) {
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=?",
                new String[] { imgPath }, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = this.getContentResolver().delete(uri, null, null);
            result = count == 1;
        } else {
            File file = new File(imgPath);
            result = file.delete();
        }
        if (result) {
            mList.remove(imgPath);
            //add by shipeixian for update ui when delete photo begin
            mAdapter = null;
            mAdapter = new ViewPagerAdapter(mList, PickPictureActivity.this);
            mVpContainer.setAdapter(mAdapter);
            mAdapter.setOnViewPagerClickListener(new ViewPagerAdapter.OnViewPagerLongClickListener() {
                @Override
                public void onLongClickListener(View view, int position) {
                    showNormalDialog(position);
                }
            });
            //add by shipeixian for update ui when delete photo end
            mAdapter.notifyDataSetChanged();
            photoAdapter.notifyDataSetChanged();
           // Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_LONG).show();

            if (mList.size()<=0){
                tv.setText("暂无数据");
                tv.setVisibility(View.VISIBLE);
                mVpContainer.setVisibility(View.GONE);
                titleBar.setVisibility(View.VISIBLE);
            }
        }
    }
    private Handler mHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    // 进行ui操作
                    tv.setVisibility(View.GONE);
                    mVpContainer.setVisibility(View.GONE);

                    list= (List<PictureTotal>) msg.obj;
                    //mList = pickPictureHelper.getChildPathList(0);
                    mAdapter = new ViewPagerAdapter(mList, PickPictureActivity.this);
                    mVpContainer.setAdapter(mAdapter);

                    mAdapter.setOnViewPagerClickListener(new ViewPagerAdapter.OnViewPagerLongClickListener() {
                        @Override
                        public void onLongClickListener(View view, int position) {
                            Log.d("fei",mList.get(position));
                            showNormalDialog(position);
                        }
                    });
                    //add by shipeixian for adjust ui begin
                    photoAdapter = new PhotoAdapter(PickPictureActivity.this, mList);
                    photoListView.setAdapter(photoAdapter);
                    photoListView.setVisibility(View.VISIBLE);
                    photoAdapter.notifyDataSetChanged();
                    //add by shipeixian for adjust ui end
                    break;
                case 2:
                    // 进行ui操作
                    mVpContainer.setVisibility(View.GONE);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("暂无数据");
                    break;
                default:
                    mVpContainer.setVisibility(View.GONE);
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("暂无数据");
                    break;
            }
        }
    };


    @Override
    protected void onResume() {
        getPicture();
        super.onResume();
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
     */
    private void getPicture() {
        Log.d("fei","getPicture");
        pickPictureHelper = PickPictureHelper.readPicture(this, new PickPictureCallback() {
            @Override
            public void onStart() {
                //显示进度条
                mProgressDialog = ProgressDialog.show(PickPictureActivity.this, null, "正在加载");
            }

            @Override
            public void onSuccess(List<PictureTotal> list) {
                mProgressDialog.dismiss();
                Message msg = new Message();
                msg.what=1;
                msg.obj=list;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onError() {
                mProgressDialog.dismiss();
                Message msg = new Message();
                msg.what=2;
                mHandler.sendMessage(msg);

            }
        });
    }

    //add by shipeixian for adjust ui begin
    @Override
    public void onBackPressed() {
        if (mVpContainer.getVisibility() == View.VISIBLE) {
            mVpContainer.setVisibility(View.GONE);
            titleBar.setVisibility(View.VISIBLE);
            if(mList.size() > 0) {
            	photoListView.setVisibility(View.VISIBLE);
                photoAdapter.notifyDataSetChanged();
            }
        } else {
            finish();   
        }
    }
    //add by shipeixian for adjust ui end

    //add by shipeixian for cancel animation begin
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    //add by shipeixian for cancel animation end


    /**
     * 从sd卡获取图片资源
     * @return
     */
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
