package com.ctyon.watch.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.ctyon.watch.R;
import com.ctyon.watch.listener.OnTouchListener;
import com.ctyon.watch.ui.adapter.FragPagerAdapter;
import com.ctyon.watch.ui.view.ScrollViewPager;
import com.ctyon.watch.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


//add by shipeixian begin
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.os.Bundle;
import android.content.Context;
//add by shipeixian end

//add by shipeixian for reback lockscreen view begin
import android.app.Instrumentation;
import android.os.Handler;
import android.os.Message;
//add by shipeixian for reback lockscreen view end

//add by shipeixian for reject call in begin
import java.lang.reflect.Method;
import android.util.Log;
import android.provider.Settings;
import com.ctyon.watch.ui.activity.contact.ContactsManager;
//add by shipeixian for reject call in end

public class MainActivity extends BaseActivity {

    private ScrollViewPager mPager;
    private FragPagerAdapter mAdapter;
    private List<Fragment> mList = new ArrayList<>();
    private FragmentManager fManager;
    private int mPosition = 0;

    //add by shipeixian for avoid powerkey conflict begin
    private boolean  isBackKeyPress = false;
    //add by shipeixian for avoid powerkey conflict end

    //add by shipeixian for set speakerphone on begin
    private AudioManager audioManager;
    private int currVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelListner listener = new TelListner() ;
        telManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

    }

    private class TelListner extends PhoneStateListener {  
        boolean comingPhone=false;  
        @Override  
        public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                        case TelephonyManager.CALL_STATE_IDLE:/* 无任何状态 */
                                if(this.comingPhone){
                                        this.comingPhone=false;
                                   }
                                break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:/* 接起电话 */
                                this.comingPhone=true;
                                break;
                        case TelephonyManager.CALL_STATE_RINGING:/* 电话进来 */
                                //add by shipeixian for reject call in begin
                                Log.d("---SHIPEIXIAN---", "电话进来了");
				try {
				    //0:关闭  1：打开
				    int isOpen = Settings.Global.getInt(getContentResolver(), "socket_client_callstrategy");
				    if (isOpen == 1) {
                                        ContactsManager contactsManager = ContactsManager.getInstance(MainActivity.this);
                                        if(contactsManager.isContact(incomingNumber) || contactsManager.queryAllContacts() == null || contactsManager.queryAllContacts().size() == 0) {
                                               this.comingPhone=true;
                                        } else {
                                               try{
                                                    Log.d("---SHIPEIXIAN---", "拒绝陌生人来电，挂断电话 begin");
						    TelephonyManager telMag = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						    Class<TelephonyManager> c = TelephonyManager.class;
						    // 再去反射TelephonyManager里面的私有方法 getITelephony 得到 ITelephony对象
						    Method mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
						    //允许访问私有方法
						    mthEndCall.setAccessible(true);
						    final Object obj = mthEndCall.invoke(telMag, (Object[]) null);
						    // 再通过ITelephony对象去反射里面的endCall方法，挂断电话
						    Method mt = obj.getClass().getMethod("endCall");
						    //允许访问私有方法
						    mt.setAccessible(true);
						    mt.invoke(obj);
						    Log.d("---SHIPEIXIAN---", "拒绝陌生人来电，挂断电话 end");
						} catch (Exception e){
						    e.printStackTrace();
						}
                                        }
				    } else {
                                        this.comingPhone=true;
                                    }
				} catch (Exception e) {

				}
				//add by shipeixian for reject call in end
                                break;
                    }
        }  
    }  
    
    //add by shipeixian for set speakerphone on end

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void loadData() {
        fManager = getSupportFragmentManager();
        OnTouchListener listener = new OnTouchListener() {
            @Override
            public void onVerticalFling(int offsetPosition) {
                LogUtils.e("纵向"+offsetPosition+"---"+mPosition);
                mPager.setVertical(false);
                mPager.setCurrentItem(mPosition + offsetPosition);
            }

            @Override
            public void onHorizontalFling(int offsetPosition) {
                LogUtils.e("横向"+offsetPosition+"---");
                mPager.setVertical(false);
            }
        };
        mList.add(HomeFragment.newInstance(fManager,"dialer",listener));
        mList.add(HomeFragment.newInstance(fManager,"phone_book",listener));
        mList.add(HomeFragment.newInstance(fManager,"call_records",listener));
        mList.add(HomeFragment.newInstance(fManager,"camera",listener));
        mList.add(HomeFragment.newInstance(fManager,"weather",listener));
        mList.add(HomeFragment.newInstance(fManager,"we_chat",listener));
        mList.add(HomeFragment.newInstance(fManager,"gallery",listener));
        mList.add(HomeFragment.newInstance(fManager,"calculator",listener));
        mList.add(HomeFragment.newInstance(fManager,"alarm",listener));
        mList.add(HomeFragment.newInstance(fManager,"settings",listener));
        mList.add(HomeFragment.newInstance(fManager, "apps",listener));
    }

    @Override
    protected void initComponentEvent() {
        mAdapter = new FragPagerAdapter(fManager,mList,this);
        mPager.setAdapter(mAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                //position = position % mList.size();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(mList.size() > 1) {
            int i = ((Short.MAX_VALUE / 2) / mList.size()) * mList.size();
            mPager.setCurrentItem(i, false);
        }
    }

    @Override
    protected void initComponentView() {

        mPager = (ScrollViewPager)findViewById(R.id.main_pager);
        //设置滑动动画
        mPager.setNoScroll(true);
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }

    //add by shipeixian for reback lockscreen view begin
    private void sendKeyEvent(final int keyCode) {
        final Instrumentation instrumentation = new Instrumentation();  
        new Thread(new Runnable() {

            @Override
            public void run () {
                instrumentation.sendKeyDownUpSync(keyCode);
            }
        }).start();
    }
   
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 3:
                    sendKeyEvent(26);
                    isBackKeyPress = false;
                    break;
            }
        }
    };
    //add by shipeixian for reback lockscreen view end

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        sendBroadcast(new Intent("KEYGUARD_ACTION_BACKPRESS"));
        return super.onKeyDown(keyCode, event);
    }
}
