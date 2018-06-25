package me.example.wxchat.app;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;
import com.lzy.okserver.OkDownload;
import com.snappydb.DB;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;
import com.wx.common.support.utils.DataUtils;
import com.wx.common.support.utils.SafeHandler;
import com.wx.common.support.utils.ThreadManager;
import com.xmaihh.phone.app.activity.MainActivity;
import com.xmaihh.phone.support.hardware.DeviceUtils;
import com.xmaihh.voice.AudioRecorder;
import com.xmaihh.voice.RecordButton;
import com.xmaihh.voice.utils.MediaHelper;
import com.xmaihh.voice.utils.VoiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.example.wxchat.R;
import me.example.wxchat.support.model.WeChatMessage;
import me.example.wxchat.support.model.WxchatMessageBean;
import me.example.wxchat.support.persistence.DbUtils;
import me.example.wxchat.support.persistence.MessageDatabase;
import me.example.wxchat.support.ui.Injection;
import me.example.wxchat.support.ui.MsgViewModel;
import me.example.wxchat.support.ui.ViewModelFactory;
import me.example.wxchat.support.view.PopClickEvent;
import me.example.wxchat.support.view.PopOptionUtil;
import me.example.wxchat.support.view.TextMagnifyLoadingDialog;
import me.example.wxchat.support.view.WxchatAdapter;
import me.xmai.global.config.Constants;

public class WxchatActivity extends AppCompatActivity implements WxchatAdapter.WxchatAdapterDelegate
,SafeHandler.HandlerContainer, RecordButton.QuickRecordListener {

    //录音太短提示区域
    private LinearLayout oneSecondRecordZone;

    private RecyclerView recyclerview;
    private SafeHandler<WxchatActivity> mHandler;
    private PopOptionUtil mPop;

    List<WxchatMessageBean> mList = null;
    WxchatAdapter adapter = null;
    WxchatMessageBean wmb=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wxchat);

        mHandler = new SafeHandler<>(this);
        mPop = new PopOptionUtil(this);
        mPop.setOnPopClickEvent(new PopClickEvent() {
            @Override
            public void onPreClick() {
                Toast.makeText(WxchatActivity.this,"复制",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNextClick() {
                Toast.makeText(WxchatActivity.this,"删除",Toast.LENGTH_SHORT).show();
            }
        });
        mViewModelFactory = Injection.provideViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MsgViewModel.class);
        recyclerview =findViewById(R.id.recycleview);
        mList = new ArrayList<>();
//        allocList();
        adapter=new WxchatAdapter(mList,this);
        adapter.setDelegate(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(adapter);

        RecordButton v = findViewById(R.id.send_voice);
        v.setQuickRecordListener(this);
        v.setAudioRecord(new AudioRecorder());
        v.setRecordListener(filePath -> {
            try {
                Log.d("521", "录音文件路径"+filePath+"录音时长"+VoiceUtil.getAmrDuration(new File(filePath)));
                WeChatMessage msg = new WeChatMessage();
                msg.setMessageType(WxchatMessageBean.MessageType.Voice.ordinal());
                msg.setVoiceUrl(filePath);
                msg.setDuringTime(VoiceUtil.getAmrDuration(new File(filePath)));
                msg.setShowMeaageTime(true);
                msg.setMessageSenderType(WxchatMessageBean.MessageSenderType.User.ordinal());
                msg.setMessageTime(System.currentTimeMillis());
                msg.setMessageSendStatus(WxchatMessageBean.MessageSendStatus.Sending.ordinal());
//                DbUtils.insertMsg(this,msg);
                mHandler.obtainMessage(Constants.COMMON.MSG.MSG_SEND_VOICE,msg).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        oneSecondRecordZone = (LinearLayout) findViewById(R.id.oneSecondRecordTipZone);
    }


    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private ViewModelFactory mViewModelFactory;
    private MsgViewModel mViewModel;
    @Override
    protected void onStart() {
        super.onStart();
        mDisposable.add(mViewModel.getMessages().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(list->{
            List<WxchatMessageBean> ll = new ArrayList<>();
            for(WeChatMessage m:list){
                wmb=new WxchatMessageBean();
                    wmb.setMessageType(WxchatMessageBean.MessageType.valueOf(m.getMessageType()));
                    wmb.setMessageTime(DataUtils.longToString(m.getMessageTime(),"yyyy-MM-dd HH:mm"));
                    wmb.setMessageText(m.getMessageText());
                    wmb.setSenderType(WxchatMessageBean.MessageSenderType.valueOf(m.getMessageSenderType()));
                    wmb.setSentStatus(WxchatMessageBean.MessageSendStatus.valueOf(m.getMessageSendStatus()));
                    wmb.setShowMessageTime(m.isShowMeaageTime());
                    wmb.setReadStatus(WxchatMessageBean.MessageReadStatus.valueOf(m.getMessageReadStatus()));
                    wmb.setVoiceUrl(m.getVoiceUrl());
                    wmb.setDuringTime(m.getDuringTime());
                    wmb.setImageUrl(m.getImageUrl());
                    wmb.setLogoUrl(m.getLogoUrl());
                    wmb.setValue1(m.getValue1());
                    //add by shipeixian begin
                    wmb.setId(m.getId());
                    wmb.setSecid(m.getSecid());
                    wmb.setValue2(m.getValue2());
                    //add by shipeixian end
                ll.add(wmb);
            }
            Log.d("521", "onStart: mList"+ll.size());
            adapter.refreshList(ll);
            adapter.notifyDataSetChanged();
            recyclerview.scrollToPosition(adapter.getItemCount()-1);  //开启自动滑动到底部

            OverFlow(list);
        })
        );

    }

    @Override
    public void wechatItemLongClickAction(WxchatMessageBean wmb, int postion) {
    }
    @Override
    public void wechatItemClickAction(WxchatMessageBean wmb, int postion) {
        if(wmb.getMessageType()==WxchatMessageBean.MessageType.Voice){
            if(wmb.getVoiceUrl()!=null) {
                MediaHelper.playSound(wmb.getVoiceUrl(), mp -> mp.reset());
            }else{
                GetRequest<File> getRequest = OkGo.get(wmb.getValue1());
                getRequest.execute(new FileCallback() {
                    @Override
                    public void onSuccess(Response<File> response) {
                        MediaHelper.playSound(response.body().getAbsolutePath(), mp -> mp.reset());
                    }
                });
            }
            //add by shipeixian begin
            if (wmb.getReadStatus() == WxchatMessageBean.MessageReadStatus.UnRead) {
                WeChatMessage bean = new WeChatMessage();
                bean.setId(wmb.getId());
                bean.setMessageType(WxchatMessageBean.MessageType.Voice.ordinal());
                bean.setMessageSenderType(WxchatMessageBean.MessageSenderType.Parents.ordinal());
                bean.setMessageReadStatus(WxchatMessageBean.MessageReadStatus.Read.ordinal());
                bean.setSecid(wmb.getSecid());
                try {
                    bean.setMessageTime(DataUtils.stringToLong(wmb.getMessageTime(),"yyyy-MM-dd HH:mm"));
                } catch (Exception e) {

                }
                bean.setShowMeaageTime(true);
                bean.setValue1(wmb.getValue1());
                bean.setDuringTime(wmb.getDuringTime());
                bean.setValue2(wmb.getValue2());
                DbUtils.insertMsg(getApplicationContext(), bean);
            }
            //add by shipeixian end
        }else if(wmb.getMessageType()==WxchatMessageBean.MessageType.Text){

        }

    }

    @Override
    public void wechatItemRetryButtonClickAction(WxchatMessageBean wmb, int postion) {
    }

    @Override
    public void handleMessage(Message smg) {
        switch (smg.what){
            case Constants.COMMON.MSG.MSG_SEND_VOICE:
                if (smg.obj instanceof WeChatMessage){
                    WeChatMessage m = (WeChatMessage) smg.obj;
                    PostRequest<String> postRequest = OkGo.<String>post(Constants.COMMON.Url.sendVoice)
                            .params("imei", DeviceUtils.getIMEI(this))
                            //.params("imei", "C5B20180200030")
                            .params("token", Settings.Global.getString(getContentResolver(),
                                    Constants.MODEL.SETTINGS.GLOBAL_TOKEN))
                            .params("content",new File(m.getVoiceUrl()))
                            .converter(new StringConvert());
                    postRequest.execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            m.setMessageSendStatus(WxchatMessageBean.MessageSendStatus.Sended.ordinal());
                            DbUtils.insertMsg(getApplicationContext(),m);
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            m.setMessageSendStatus(WxchatMessageBean.MessageSendStatus.UnSended.ordinal());
                            DbUtils.insertMsg(getApplicationContext(),m);
                        }
                    });
                }
                break;
                default:
                    break;
        }
    }

    private void OverFlow(List<WeChatMessage> list) {
        if (list.size() > 1) {
            int id = list.get(list.size() - 1).getId();
            ThreadManager.getThreadPollProxy().execute(() -> {
                        MessageDatabase database = MessageDatabase.getInstance(this);
                        database.beginTransaction();
                        for (int i = id; i <= id + 5; i++) {  //超过5数据就删除
                            Log.d("521", "OverFlow: "+id);
//                            Injection.provideMsgDatasource(this)
//                                    .deleteMsgById(id, id);
                        }
                        database.setTransactionSuccessful();
                        database.endTransaction();
                    }
            );
        }
    }

    //add by shipeixian for cancel animation begin
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    //add by shipeixian for cancel animation end


    @Override
    public void onOneSecondRecordAudio() {
        oneSecondRecordZone.setVisibility(View.VISIBLE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                oneSecondRecordZone.setVisibility(View.GONE);
            }
        }, 500);
    }
}
