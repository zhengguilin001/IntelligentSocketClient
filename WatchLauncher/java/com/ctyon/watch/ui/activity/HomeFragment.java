package com.ctyon.watch.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ctyon.watch.R;
import com.ctyon.watch.listener.OnTouchListener;
import com.ctyon.watch.model.CommonConstants;
import com.ctyon.watch.ui.activity.calculator.CalculatorActivity;
import com.ctyon.watch.ui.activity.calllogs.CalllogActivity;
import com.ctyon.watch.ui.activity.contact.ContactMainActivity;
import com.ctyon.watch.ui.activity.gallery.PickPictureActivity;
import com.ctyon.watch.utils.LogUtils;
import com.ctyon.watch.utils.ResourceUtil;
import com.ctyon.watch.utils.WarnUtils;

/**
 * Created by Administrator on 2018/1/25.
 */

public class HomeFragment extends Fragment{

    private static FragmentManager fManager;
    private static HomeFragment bFragment;
    public static final int FLING_MIN_DISTANCE = 20;
    private static OnTouchListener mOnTouchListener;
    private boolean isClick;

    public static HomeFragment newInstance(FragmentManager manager, String tag,OnTouchListener listener){
        fManager = manager;
        Bundle args = new Bundle();
        args.putString(CommonConstants.FRAGMENT_TITLE_TAG, tag);
        bFragment = new HomeFragment();
        bFragment.setArguments(args);
        mOnTouchListener = listener;
        return bFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_base, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final String name = arguments.getString(CommonConstants.FRAGMENT_TITLE_TAG, "");
        ImageView imgView = (ImageView)view.findViewById(R.id.base_iv);
        imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(),ResourceUtil.getPrefixMipmap(view.getContext(),name)));

        final GestureDetector detector = new GestureDetector(view.getContext(), new GestureListener());
        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("isClick"+isClick);
                if (!isClick)
                    return;
                Intent intent = new Intent();
                switch (name){
                    case "apps":
                        //WarnUtils.toast(v.getContext(),name);
                        intent.setClass(getActivity(),QrcodeActivity.class);
                        break;
                    case "alarm":
                        intent.setClass(getActivity(),AlarmListActivity.class);
                        break;
                    case "calculator":

                        intent.setClass(getActivity(),CalculatorActivity.class);

                        break;
                    case "camera":
                        intent.setClass(getActivity(),CameraActivity.class);
                        break;
                    case "settings":
                        //  WarnUtils.toast(v.getContext(),name);
                        Intent intents = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intents);

                        break;
                    case "weather":
                        try {
                            intent = new Intent();
                            intent.setAction("android.intent.action.CTYON_WEATHER");
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "gallery":
                        intent.setClass(getActivity(),PickPictureActivity.class);
                        break;
                    case "dialer":
                        intent.setClass(getActivity(),DialerActivity.class);
                        break;
                    case "call_records":
                        intent.setClass(getActivity(),CalllogActivity.class);
                        break;
                    case "phone_book":
                        intent.setClass(getActivity(), ContactMainActivity.class);
                        break;
                    case "we_chat":
                        try {
                            intent = new Intent();
                            intent.setAction("android.intent.action.CTYON_WXCHAT");
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        //WarnUtils.toast(v.getContext(),name);
                        break;
                }
                try{
                    getActivity().startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private class GestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            LogUtils.e("onDown-----" + getActionName(e.getAction()));
            isClick = false;
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            LogUtils.e("onShowPress-----" + getActionName(e.getAction()));
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            LogUtils.e("onSingleTapUp-----" + getActionName(e.getAction()));
            isClick = true;
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtils.e("onScroll-----"
                    + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
                    + e2.getX() + "," + e2.getY() + ")");
            isClick = false;
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            LogUtils.e("onLongPress-----" + getActionName(e.getAction()));
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.i(getClass().getName(),
                    "onFling-----" + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
                            + e2.getX() + "," + e2.getY() + ")");

            float detly_x = e1.getX() - e2.getX();
            float detly_y = e1.getY() - e2.getY();

            if (Math.abs(detly_x) > Math.abs(detly_y)) {// 水平方向滑动
                int offsetPosition = 0;
                if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE) {
                    offsetPosition = 1;
                    LogUtils.e("onFling----- 向左滑动");
                } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE) {
                    offsetPosition = -1;
                    LogUtils.e("onFling----- 向右滑动");
                }
                mOnTouchListener.onVerticalFling(offsetPosition);
            } else {// 垂直方向滑动
                int offsetPosition = 0;
                if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE) {
                    offsetPosition = 1;
                    LogUtils.e("onFling----- 向上滑动");
                } else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE) {
                    offsetPosition = -1;
                    LogUtils.e("onFling----- 向下滑动");
                }
                mOnTouchListener.onVerticalFling(offsetPosition);
            }
            isClick = false;
            return false;
        }

        private String getActionName(int action) {
            String name = "";
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    name = "ACTION_DOWN";
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    name = "ACTION_MOVE";
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    name = "ACTION_UP";
                    break;
                }
                default:
                    break;
            }
            return name;
        }
    }

}
