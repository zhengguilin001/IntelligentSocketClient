package me.example.wxchat.support.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.example.wxchat.R;

/**
 * Created by chensy160803 on 2017-03-02.
 * 点击文字放大
 */

public class TextMagnifyLoadingDialog extends Dialog {
    private TextView textView;// 显示文字
    private RelativeLayout layout;
    private String text;

    public TextMagnifyLoadingDialog(Context context, String text) {
        super(context, R.style.magnifyDialogStyle);
        this.text = text;
    }

    protected TextMagnifyLoadingDialog(Context context, boolean cancelable,
                                       OnCancelListener cancelListener, String text) {
        super(context, cancelable, cancelListener);
        this.text = text;
    }

    public TextMagnifyLoadingDialog(Context context, int theme, String text) {
        super(context, theme);
        this.text = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_magnify);
        textView = (TextView) findViewById(R.id.tv_dialog_magnify);
        layout = (RelativeLayout) findViewById(R.id.rl_dialog_magnify);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        textView.setText(text);
    }
}
