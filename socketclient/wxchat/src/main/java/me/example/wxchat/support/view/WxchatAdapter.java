package me.example.wxchat.support.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;
import me.example.wxchat.R;
import me.example.wxchat.support.model.WxchatMessageBean;
import me.example.wxchat.support.widget.BubbleImageView;

/**
 * Created by Administrator on 2018/3/16.
 */

public class WxchatAdapter extends RecyclerView.Adapter<WxchatAdapter.WxchatHolder> {
    public interface WxchatAdapterDelegate{
        void wechatItemLongClickAction(WxchatMessageBean wmb,int postion);
        void wechatItemClickAction(WxchatMessageBean wmb,int postion);
        void wechatItemRetryButtonClickAction(WxchatMessageBean wmb,int postion);
    }

    private WxchatAdapterDelegate delegate;

    public void setDelegate(WxchatAdapterDelegate delegate) {
        this.delegate = delegate;
    }

    private static final int VIEWTPYE_DEF = 0x9999;
    private static final int VIEWTPYE_HANTU_TEXT = 0x01;
    private static final int VIEWTPYE_HANTU_VOICE = 0x02;
    private static final int VIEWTPYE_HANTU_IMAGE = 0x03;
    private static final int VIEWTPYE_USER_TEXT = 0x04;
    private static final int VIEWTPYE_USER_VOICE = 0x05;
    private static final int VIEWTPYE_USER_IMAGE = 0x06;
    //音频的长度  屏幕比例
    private static final float VOICE_MIN_LENTH = 0.2f;
    private static final float VOICE_MAX_LENTH = 0.53f;
    //图片的宽 高
    private static final float IMAGE_MAX_HEIGHTORWIDTH = 0.3f;

    private int screenWidth = 0;
    private int screenHeight = 0;

    private List<WxchatMessageBean> list;
    private Context context;

    public WxchatAdapter(List<WxchatMessageBean> list, Context context) {
        this.list = list;
        this.context = context;
        screenHeight = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();
        screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
    }

    public void refreshList(List<WxchatMessageBean> l) {
        list.clear();
        list.addAll(l);
        notifyDataSetChanged();
    }

    @Override
    public WxchatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WxchatHolder wechatHolder = null;
        switch (viewType){
            case VIEWTPYE_HANTU_TEXT:
                wechatHolder = new TextHantuChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wechatcell_text_hantu, parent, false));
                break;
            case VIEWTPYE_HANTU_VOICE:
                wechatHolder = new VoiceHantuChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wechatcell_voice_hantu, parent, false));
                break;
            case VIEWTPYE_HANTU_IMAGE:
                wechatHolder = new ImageHantuChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wechatcell_image_hantu, parent, false));
                break;
            case VIEWTPYE_USER_TEXT:
                wechatHolder = new TextUserChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wechatcell_text_user, parent, false));
                break;
            case VIEWTPYE_USER_VOICE:
                wechatHolder = new VoiceUserChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wechatcell_voice_user, parent, false));
                break;
            case VIEWTPYE_USER_IMAGE:
                wechatHolder = new ImageUserChatHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wechatcell_image_user, parent, false));
                break;
            case VIEWTPYE_DEF:
                wechatHolder = new WxchatHolder(parent);
                break;
            default:break;
        }
        return wechatHolder;
    }

    @Override
    public void onBindViewHolder(WxchatHolder holder, int position) {
        final WxchatMessageBean wechatMessageBean = list.get(position);
        if(holder instanceof TextHantuChatHolder){
            TextHantuChatHolder newHolder = (TextHantuChatHolder)holder;
            newHolder.cellHantuTextTimeRelativelayout.setVisibility(wechatMessageBean.isShowMessageTime()?View.VISIBLE:View.GONE);
            newHolder.cellHantuTextTimeTextview.setText(wechatMessageBean.getMessageTime());
            newHolder.cellHantuTextContextTextview.setText(wechatMessageBean.getMessageText());

            newHolder.cellHantuTextContextRelativelayout.setOnClickListener(view ->{
                if(delegate!=null){
                    delegate.wechatItemClickAction(wechatMessageBean,position);
                }}
            );
            newHolder.cellHantuTextContextRelativelayout.setOnLongClickListener(view->
            {
                if(delegate!=null){
                    delegate.wechatItemLongClickAction(wechatMessageBean,position);
                }
                return true;
            });
        }else if(holder instanceof VoiceHantuChatHolder){
            final VoiceHantuChatHolder newHolder = (VoiceHantuChatHolder) holder;
            newHolder.cellHantuVoiceTimeRelativelayout.setVisibility(wechatMessageBean.isShowMessageTime()?View.VISIBLE:View.GONE);
            newHolder.cellHantuVoiceTimeTextview.setText(wechatMessageBean.getMessageTime());
            Integer duringTime=wechatMessageBean.getDuringTime();

            double voiceLength=(VOICE_MAX_LENTH-VOICE_MIN_LENTH)*screenWidth*duringTime/60.0+VOICE_MIN_LENTH*screenWidth;
            ViewGroup.LayoutParams currentParams=newHolder.cellHantuVoiceLevelRelativelayout.getLayoutParams();
            currentParams.width=(int)voiceLength;

            newHolder.cellHantuVoiceLevelRelativelayout.setLayoutParams(currentParams);
            newHolder.cellHantuVoiceDuringtimeTextview.setText(""+duringTime+" ''");

            newHolder.cellHantuVoiceAniotionImageView.setImageResource(R.drawable.play_voice_hantu);
            final AnimationDrawable animationDrawable = (AnimationDrawable) newHolder.cellHantuVoiceAniotionImageView.getDrawable();
            newHolder.cellHantuVoiceLevelRelativelayout.setOnClickListener(view -> {

                if (animationDrawable.isRunning()){
                    animationDrawable.stop();
                }
                animationDrawable.start();

                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        //execute the task
                        animationDrawable.stop();
                    }
                }, wechatMessageBean.getDuringTime()*1000);



                if (delegate!=null){
                    delegate.wechatItemClickAction(wechatMessageBean,position);
                }


            });
            newHolder.cellHantuVoiceLevelRelativelayout.setOnLongClickListener(view -> {

                if (delegate!=null){
                    delegate.wechatItemLongClickAction(wechatMessageBean,position);
                }

                return true;
            });
        }else if(holder instanceof ImageHantuChatHolder){
            ImageHantuChatHolder newHolder=(ImageHantuChatHolder)holder;

            if (wechatMessageBean.isShowMessageTime()){
                newHolder.cellHantuImageTimeRelativelayout.setVisibility(View.VISIBLE);
            }else {
                newHolder.cellHantuImageTimeRelativelayout.setVisibility(View.GONE);
            }



            newHolder.cellHantuImageTimeTextview.setText(wechatMessageBean.getMessageTime());
            Bitmap bitmap=wechatMessageBean.getImageBitmap();
            setImageLayout(newHolder.cellHantuImagePictureImageview,wechatMessageBean.getImageUrl());

            newHolder.cellHantuImageRelativelayout.setOnClickListener(view -> {
                if (delegate!=null){
                    delegate.wechatItemClickAction(wechatMessageBean,position);
                }
            });
            newHolder.cellHantuImageRelativelayout.setOnLongClickListener(view -> {
                if (delegate!=null){
                    delegate.wechatItemLongClickAction(wechatMessageBean,position);
                }
                return true;
            });
        }else if(holder instanceof TextUserChatHolder){
            TextUserChatHolder newHolder=(TextUserChatHolder)holder;

            newHolder.cellUserTextTimeRelativelayout.setVisibility(wechatMessageBean.isShowMessageTime()?View.VISIBLE:View.GONE);
            newHolder.cellUserTextTimeTextview.setText(wechatMessageBean.getMessageTime());
            newHolder.cellUserTextContextTextview.setText(wechatMessageBean.getMessageText());
            if (wechatMessageBean.getSentStatus()== WxchatMessageBean.MessageSendStatus.Sended){
                newHolder.cellUserTextResendButton.setVisibility(View.GONE);
                newHolder.cellUserTextLoadingProgressbar.setVisibility(View.GONE);
                newHolder.cellUserTextReadstatusTextview.setVisibility(View.VISIBLE);

                if (wechatMessageBean.getReadStatus()== WxchatMessageBean.MessageReadStatus.Read){

                    ColorStateList color = context
                            .getResources()
                            .getColorStateList(R.color.color_bababa);
                    newHolder.cellUserTextReadstatusTextview.setText("已读");
                    newHolder.cellUserTextReadstatusTextview.setTextColor(color);
                }else  if (wechatMessageBean.getReadStatus()== WxchatMessageBean.MessageReadStatus.UnRead){
                    newHolder.cellUserTextReadstatusTextview.setText("未读");
                    ColorStateList color = context
                            .getResources()
                            .getColorStateList(R.color.color_c00000);
                    newHolder.cellUserTextReadstatusTextview.setTextColor(color);
                }
            }else if (wechatMessageBean.getSentStatus()== WxchatMessageBean.MessageSendStatus.Sending){

                newHolder.cellUserTextResendButton.setVisibility(View.GONE);
                newHolder.cellUserTextLoadingProgressbar.setVisibility(View.VISIBLE);
                newHolder.cellUserTextReadstatusTextview.setVisibility(View.GONE);

            }else if (wechatMessageBean.getSentStatus()== WxchatMessageBean.MessageSendStatus.UnSended){
                newHolder.cellUserTextResendButton.setVisibility(View.VISIBLE);
                newHolder.cellUserTextLoadingProgressbar.setVisibility(View.GONE);
                newHolder.cellUserTextReadstatusTextview.setVisibility(View.GONE);
                newHolder.cellUserTextResendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (delegate!=null){
                            delegate.wechatItemRetryButtonClickAction(wechatMessageBean,position);
                        }
                    }
                });
            }



            newHolder.cellUserTextBgviewRelativelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.e("hebiao","========99999====cellUserTextBgviewRelativelayout=============");

                    if (delegate!=null){
                        delegate.wechatItemClickAction(wechatMessageBean,position);
                    }
                }
            });
            newHolder.cellUserTextBgviewRelativelayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

//                    Log.e("hebiao","=========000000==========tivelayout.setOnLongClickListener======");

                    if (delegate!=null){
                        delegate.wechatItemLongClickAction(wechatMessageBean,position);
                    }

                    return true;
                }
            });

        }else if (holder instanceof VoiceUserChatHolder){
            final VoiceUserChatHolder newHolder=(VoiceUserChatHolder)holder;


            if (wechatMessageBean.isShowMessageTime()){
                newHolder.cellUserVoiceTimeRelativelayout.setVisibility(View.VISIBLE);
            }else {
                newHolder.cellUserVoiceTimeRelativelayout.setVisibility(View.GONE);
            }

            newHolder.cellUserVoiceTimeTextview.setText(wechatMessageBean.getMessageTime());
            Integer duringTime=wechatMessageBean.getDuringTime();

            double voiceLength=(VOICE_MAX_LENTH-VOICE_MIN_LENTH)*screenWidth*duringTime/60.0+VOICE_MIN_LENTH*screenWidth;

            ViewGroup.LayoutParams currentParams=newHolder.cellUserVoiceLevelviewRelativelayout.getLayoutParams();
            currentParams.width=(int)voiceLength;


            newHolder.cellUserVoiceLevelviewRelativelayout.setLayoutParams(currentParams);
            newHolder.cellUserVoiceDuringtimeTextview.setText(""+duringTime+" ''");

            if (wechatMessageBean.getSentStatus()== WxchatMessageBean.MessageSendStatus.Sended){
//                newHolder.cellUserVoiceReadstatusTextview.setVisibility(View.VISIBLE); //todo 2018-03-27 14:53
                newHolder.cellUserVoiceResendButton.setVisibility(View.GONE);
                newHolder.cellUserVoiceLoadingProgressbar.setVisibility(View.GONE);
                if (wechatMessageBean.getReadStatus()== WxchatMessageBean.MessageReadStatus.Read){

                    ColorStateList color = context
                            .getResources()
                            .getColorStateList(R.color.color_bababa);
//                    newHolder.cellUserVoiceReadstatusTextview.setText("已读");　//todo 2018-03-27 14:53
                    newHolder.cellUserVoiceReadstatusTextview.setTextColor(color);
                }else  if (wechatMessageBean.getReadStatus()== WxchatMessageBean.MessageReadStatus.UnRead){
//                    newHolder.cellUserVoiceReadstatusTextview.setText("未读"); //todo 2018-03-27 14:53
                    ColorStateList color = context
                            .getResources()
                            .getColorStateList(R.color.color_c00000);
                    newHolder.cellUserVoiceReadstatusTextview.setTextColor(color);
                }
            }else if (wechatMessageBean.getSentStatus()== WxchatMessageBean.MessageSendStatus.Sending){
                newHolder.cellUserVoiceReadstatusTextview.setVisibility(View.GONE);
                newHolder.cellUserVoiceResendButton.setVisibility(View.GONE);
                newHolder.cellUserVoiceLoadingProgressbar.setVisibility(View.VISIBLE);
            }else if (wechatMessageBean.getSentStatus()== WxchatMessageBean.MessageSendStatus.UnSended){
                newHolder.cellUserVoiceReadstatusTextview.setVisibility(View.GONE);
                newHolder.cellUserVoiceResendButton.setVisibility(View.VISIBLE);
                newHolder.cellUserVoiceLoadingProgressbar.setVisibility(View.GONE);


                newHolder.cellUserVoiceResendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (delegate!=null){
                            delegate.wechatItemRetryButtonClickAction(wechatMessageBean,position);
                        }
                    }
                });
            }


            newHolder.cellUserVoiceAniotionImageView.setImageResource(R.drawable.play_voice_user);
            final  AnimationDrawable animationDrawable = (AnimationDrawable) newHolder.cellUserVoiceAniotionImageView.getDrawable();
            newHolder.cellUserVoiceLevelviewRelativelayout.setOnClickListener(view -> {
                if (animationDrawable.isRunning()){
                    animationDrawable.stop();
                }
                animationDrawable.start();

                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        //execute the task
                        animationDrawable.stop();
                    }
                }, wechatMessageBean.getDuringTime()*1000);

                if (delegate!=null){
                    delegate.wechatItemClickAction(wechatMessageBean,position);
                }
            });
            newHolder.cellUserVoiceLevelviewRelativelayout.setOnLongClickListener(view -> {
                if (delegate!=null){
                    delegate.wechatItemLongClickAction(wechatMessageBean,position);
                }

                return true;
            });



        }else if (holder instanceof ImageUserChatHolder){
            ImageUserChatHolder newHolder=(ImageUserChatHolder)holder;


            if (wechatMessageBean.isShowMessageTime()){
                newHolder.cellUserImageTimeRelativelayout.setVisibility(View.VISIBLE);
            }else {
                newHolder.cellUserImageTimeRelativelayout.setVisibility(View.GONE);
            }


            newHolder.cellUserImageTimeTextview.setText(wechatMessageBean.getMessageTime());
            setImageLayout(newHolder.cellUserImagePictureImageview,wechatMessageBean.getImageUrl());

            if (wechatMessageBean.getSentStatus()== WxchatMessageBean.MessageSendStatus.Sended){
                newHolder.cellUserImageReadstatusTextview.setVisibility(View.VISIBLE);
                if (wechatMessageBean.getReadStatus()== WxchatMessageBean.MessageReadStatus.Read){

                    ColorStateList color = context
                            .getResources()
                            .getColorStateList(R.color.color_bababa);
                    newHolder.cellUserImageReadstatusTextview.setText("已读");
                    newHolder.cellUserImageReadstatusTextview.setTextColor(color);
                }else  if (wechatMessageBean.getReadStatus()== WxchatMessageBean.MessageReadStatus.UnRead){
                    newHolder.cellUserImageReadstatusTextview.setText("未读");
                    ColorStateList color = context
                            .getResources()
                            .getColorStateList(R.color.color_c00000);
                    newHolder.cellUserImageReadstatusTextview.setTextColor(color);
                }
            }else if (wechatMessageBean.getSentStatus()== WxchatMessageBean.MessageSendStatus.Sending){
                newHolder.cellUserImageLoadingProgressbar.setVisibility(View.VISIBLE);
            }else if (wechatMessageBean.getSentStatus()== WxchatMessageBean.MessageSendStatus.UnSended){
                newHolder.cellUserImageResendButton.setVisibility(View.VISIBLE);

                newHolder.cellUserImageResendButton.setOnClickListener(view -> {
                    if (delegate!=null){
                        delegate.wechatItemRetryButtonClickAction(wechatMessageBean,position);
                    }
                });

            }


            newHolder.cellUserImageRelativelayout.setOnClickListener(view -> {
                if (delegate!=null){
                    delegate.wechatItemClickAction(wechatMessageBean,position);
                }
            });
            newHolder.cellUserImageRelativelayout.setOnLongClickListener(view -> {
                if (delegate!=null){
                    delegate.wechatItemLongClickAction(wechatMessageBean,position);
                }
                return true;
            });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position){
        WxchatMessageBean wechatMessageBean = list.get(position);
        if (wechatMessageBean.getSenderType() == WxchatMessageBean.MessageSenderType.Parents) {
            if (wechatMessageBean.getMessageType() == WxchatMessageBean.MessageType.Text) {
                return VIEWTPYE_HANTU_TEXT;
            } else if (wechatMessageBean.getMessageType() == WxchatMessageBean.MessageType.Voice) {
                return VIEWTPYE_HANTU_VOICE;
            } else if (wechatMessageBean.getMessageType() == WxchatMessageBean.MessageType.Image) {
                return VIEWTPYE_HANTU_IMAGE;
            }
        } else if (wechatMessageBean.getSenderType() == WxchatMessageBean.MessageSenderType.User) {
            if (wechatMessageBean.getMessageType() == WxchatMessageBean.MessageType.Text) {
                return VIEWTPYE_USER_TEXT;
            } else if (wechatMessageBean.getMessageType() == WxchatMessageBean.MessageType.Voice) {
                return VIEWTPYE_USER_VOICE;
            } else if (wechatMessageBean.getMessageType() == WxchatMessageBean.MessageType.Image) {
                return VIEWTPYE_USER_IMAGE;
            }
        }
        return VIEWTPYE_DEF;
    }

    class WxchatHolder extends RecyclerView.ViewHolder {

        public WxchatHolder(View itemView) {
            super(itemView);
        }
    }
    final class TextHantuChatHolder extends WxchatHolder{
        RelativeLayout cellHantuTextContextRelativelayout;
        TextView cellHantuTextTimeTextview;
        RelativeLayout cellHantuTextTimeRelativelayout;
        ImageView cellHantuTextLogoImageview;
        TextView cellHantuTextContextTextview;

        public TextHantuChatHolder(View itemView) {
            super(itemView);
            cellHantuTextContextRelativelayout = itemView.findViewById(R.id.cell_hantu_text_context_relativelayout);
            cellHantuTextTimeTextview=itemView.findViewById(R.id.cell_hantu_text_time_textview);
            cellHantuTextTimeRelativelayout=itemView.findViewById(R.id.cell_hantu_text_time_relativelayout);
            cellHantuTextLogoImageview= itemView.findViewById(R.id.cell_hantu_text_logo_imageview);
            cellHantuTextContextTextview=itemView.findViewById(R.id.cell_hantu_text_context_textview);
        }
    }
    final class VoiceHantuChatHolder extends WxchatHolder {
        TextView cellHantuVoiceTimeTextview;
        RelativeLayout cellHantuVoiceTimeRelativelayout;
        ImageView cellHantuVoiceLogoImageview;
        RelativeLayout cellHantuVoiceLevelRelativelayout;
        ImageView cellHantuVoiceAniotionImageView;
        TextView cellHantuVoiceDuringtimeTextview;

        public VoiceHantuChatHolder(View itemView) {
            super(itemView);
            cellHantuVoiceTimeTextview=itemView.findViewById( R.id.cell_hantu_voice_time_textview);
            cellHantuVoiceTimeRelativelayout= itemView.findViewById(R.id.cell_hantu_voice_time_relativelayout);
            cellHantuVoiceLogoImageview=itemView.findViewById(R.id.cell_hantu_voice_logo_imageview);
            cellHantuVoiceLevelRelativelayout=itemView.findViewById(R.id.cell_hantu_voice_level_relativelayout);
            cellHantuVoiceAniotionImageView=itemView.findViewById( R.id.cell_hantu_voice_aniotion_imageview);
            cellHantuVoiceDuringtimeTextview=itemView.findViewById( R.id.cell_hantu_voice_duringtime_textview);
        }
    }
    final class ImageHantuChatHolder extends WxchatHolder {

        RelativeLayout cellHantuImageRelativelayout;
        TextView cellHantuImageTimeTextview;
        RelativeLayout cellHantuImageTimeRelativelayout;
        ImageView cellHantuImageLogoImageview;
        BubbleImageView cellHantuImagePictureImageview;

        public ImageHantuChatHolder(View itemView) {
            super(itemView);
            cellHantuImageRelativelayout=itemView.findViewById(R.id.cell_hantu_image_relativelayout);
            cellHantuImageTimeTextview =itemView.findViewById(R.id.cell_hantu_image_time_textview);
            cellHantuImageTimeRelativelayout=itemView.findViewById(R.id.cell_hantu_image_time_relativelayout);
            cellHantuImageLogoImageview=itemView.findViewById( R.id.cell_hantu_image_logo_imageview);
            cellHantuImagePictureImageview = itemView.findViewById(R.id.cell_hantu_image_picture_imageview);
        }
    }
    final class TextUserChatHolder extends WxchatHolder {
        TextView cellUserTextTimeTextview;
        RelativeLayout cellUserTextTimeRelativelayout;
        ImageView cellUserTextLogoImageview;
        TextView cellUserTextContextTextview;
        RelativeLayout cellUserTextBgviewRelativelayout;
        Button cellUserTextResendButton;
        ProgressBar cellUserTextLoadingProgressbar;
        TextView cellUserTextReadstatusTextview;

        public TextUserChatHolder(View itemView) {
            super(itemView);
            cellUserTextTimeTextview =itemView.findViewById(R.id.cell_user_text_time_textview);
            cellUserTextTimeRelativelayout=itemView.findViewById(R.id.cell_user_text_time_relativelayout);
            cellUserTextLogoImageview=itemView.findViewById(R.id.cell_user_text_logo_imageview);
            cellUserTextContextTextview=itemView.findViewById(R.id.cell_user_text_context_textview);
            cellUserTextBgviewRelativelayout=itemView.findViewById(R.id.cell_user_text_bgview_relativelayout);
            cellUserTextResendButton=itemView.findViewById(R.id.cell_user_text_resend_button);
            cellUserTextLoadingProgressbar=itemView.findViewById(R.id.cell_user_text_loading_progressbar);
            cellUserTextReadstatusTextview=itemView.findViewById(R.id.cell_user_text_readstatus_textview);
        }
    }
    final class VoiceUserChatHolder extends WxchatHolder {
        TextView cellUserVoiceTimeTextview;
        RelativeLayout cellUserVoiceTimeRelativelayout;
        ImageView cellUserVoiceLogoImageview;
        RelativeLayout cellUserVoiceLevelviewRelativelayout;
        ImageView cellUserVoiceAniotionImageView;
        TextView cellUserVoiceDuringtimeTextview;
        Button cellUserVoiceResendButton;
        ProgressBar cellUserVoiceLoadingProgressbar;
        TextView cellUserVoiceReadstatusTextview;

        public VoiceUserChatHolder(View itemView) {
            super(itemView);
            cellUserVoiceTimeTextview=itemView.findViewById(R.id.cell_user_voice_time_textview);
            cellUserVoiceTimeRelativelayout=itemView.findViewById(R.id.cell_user_voice_time_relativelayout);
            cellUserVoiceLogoImageview=itemView.findViewById(R.id.cell_user_voice_logo_imageview);
            cellUserVoiceLevelviewRelativelayout=itemView.findViewById(R.id.cell_user_voice_level_relativelayout);
            cellUserVoiceAniotionImageView=itemView.findViewById(R.id.cell_user_voice_aniotion_imageview);
            cellUserVoiceDuringtimeTextview=itemView.findViewById(R.id.cell_user_voice_duringtime_textview);
            cellUserVoiceResendButton=itemView.findViewById(R.id.cell_user_voice_resend_button);
            cellUserVoiceLoadingProgressbar=itemView.findViewById(R.id.cell_user_voice_loading_progressbar);
            cellUserVoiceReadstatusTextview=itemView.findViewById(R.id.cell_user_voice_readstatus_textview);
        }
    }
    final class ImageUserChatHolder extends WxchatHolder {

        RelativeLayout cellUserImageRelativelayout;

        TextView cellUserImageTimeTextview;
        RelativeLayout cellUserImageTimeRelativelayout;
        ImageView cellUserImageLogoImageview;
        BubbleImageView cellUserImagePictureImageview;
        Button cellUserImageResendButton;
        ProgressBar cellUserImageLoadingProgressbar;
        TextView cellUserImageReadstatusTextview;
        public ImageUserChatHolder(View itemView) {
            super(itemView);
             cellUserImageRelativelayout=itemView.findViewById(R.id.cell_user_image_relativelayout);
             cellUserImageTimeTextview=itemView.findViewById(R.id.cell_user_image_time_textview);
             cellUserImageTimeRelativelayout=itemView.findViewById(R.id.cell_user_image_time_relativelayout);
             cellUserImageLogoImageview=itemView.findViewById(R.id.cell_user_image_logo_imageview);
             cellUserImagePictureImageview=itemView.findViewById(R.id.cell_user_image_picture_imageview);
             cellUserImageResendButton=itemView.findViewById(R.id.cell_user_image_resend_button);
             cellUserImageLoadingProgressbar=itemView.findViewById(R.id.cell_user_image_loading_progressbar);
             cellUserImageReadstatusTextview=itemView.findViewById(R.id.cell_user_image_readstatus_textview);
        }
    }

    private void setImageLayout(final ImageView img,String url){
        Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                ViewGroup.LayoutParams layoutParams=img.getLayoutParams();

                int bw=resource.getWidth();
                int bh=resource.getHeight();

                float maxwh=screenHeight*IMAGE_MAX_HEIGHTORWIDTH;

                if (bw>=bh){
                    layoutParams.width=(int)maxwh;
                    layoutParams.height=(int)(bh*maxwh/bw);
                }else {
                    layoutParams.height=(int)maxwh;
                    layoutParams.width=(int)(bw*maxwh/bh);
                }
                img.setLayoutParams(layoutParams);
                img.setImageBitmap(resource);

            }
        });

    }

}
