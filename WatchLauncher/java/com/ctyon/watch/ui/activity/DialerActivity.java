package com.ctyon.watch.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ctyon.watch.R;
//add by shipeixian for finish when press back key begin
import android.view.KeyEvent;
//add by shipeixian for finish when press back key end


/**
 * Created by zx
 * On 2018/1/27
 */

public class DialerActivity extends Activity implements View.OnClickListener, View.OnLongClickListener,View.OnTouchListener{
    private static final String TAG = "MainActivity";
    private EditText editText;
    /**
     * 1
     */
    private Button Button1;
    /**
     * 2
     */
    private Button Button2;
    /**
     * 3
     */
    private Button Button3;
    /**
     * 4
     */
    private Button Button4;
    /**
     * 5
     */
    private Button Button5;
    /**
     * 6
     */
    private Button Button6;
    /**
     * 7
     */
    private Button Button7;
    /**
     * 8*
     */
    private Button Button8;
    /**
     * 9#
     */
    private Button Button9;
    private Button ButtonCall;
    /**
     * 0+
     */
    private Button Button0;
    private Button ButtonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        initView();

    }

    private void initView() {
        editText = (EditText) findViewById(R.id.EditText);
        editText.setOnTouchListener(this);
        Button1 = (Button) findViewById(R.id.Button1);
        Button1.setOnClickListener(this);
        Button2 = (Button) findViewById(R.id.Button2);
        Button2.setOnClickListener(this);
        Button3 = (Button) findViewById(R.id.Button3);
        Button3.setOnClickListener(this);
        Button4 = (Button) findViewById(R.id.Button4);
        Button4.setOnClickListener(this);
        Button5 = (Button) findViewById(R.id.Button5);
        Button5.setOnClickListener(this);
        Button6 = (Button) findViewById(R.id.Button6);
        Button6.setOnClickListener(this);
        Button7 = (Button) findViewById(R.id.Button7);
        Button7.setOnClickListener(this);
        Button8 = (Button) findViewById(R.id.Button8);
        Button8.setOnClickListener(this);
        Button8.setOnLongClickListener(this);
        Button9 = (Button) findViewById(R.id.Button9);
        Button9.setOnClickListener(this);
        Button9.setOnLongClickListener(this);
        ButtonCall = (Button) findViewById(R.id.ButtonCall);
        ButtonCall.setOnClickListener(this);
        Button0 = (Button) findViewById(R.id.Button0);
        Button0.setOnClickListener(this);
        Button0.setOnLongClickListener(this);
        ButtonDelete = (Button) findViewById(R.id.ButtonDelete);
        ButtonDelete.setOnClickListener(this);
        //add by shipeixian for call hardtest begin
        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if ("*#4400".equals(charSequence.toString())) {
                    startActivity(new Intent().setClassName("com.ztemt.test.basic", "com.ztemt.test.basic.TestListActivity"));
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {

            }
        });
        //add by shipeixian for call hardtest end
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Button1:
                inputNumber("1");
                break;
            case R.id.Button2:
                inputNumber("2");
                break;
            case R.id.Button3:
                inputNumber("3");
                break;
            case R.id.Button4:
                inputNumber("4");
                break;
            case R.id.Button5:
                inputNumber("5");
                break;
            case R.id.Button6:
                inputNumber("6");
                break;
            case R.id.Button7:
                inputNumber("7");
                break;
            case R.id.Button8:
                inputNumber("8");
                break;
            case R.id.Button9:
                inputNumber("9");
                break;
            case R.id.ButtonCall:
                String number = editText.getText().toString();
                if (!number.isEmpty()){
                    call(number);
                }
                break;
            case R.id.Button0:
                inputNumber("0");
                break;
            case R.id.ButtonDelete:
//                int  keyCode = KeyEvent.KEYCODE_DEL;
//                KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
//                KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
//                editText.onKeyDown(keyCode, keyEventDown);
//                editText.onKeyUp(keyCode, keyEventUp);
                deleteNumber();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int inType = editText.getInputType();
        editText.setInputType(InputType.TYPE_NULL);
        editText.onTouchEvent(event);
        editText.setInputType(inType);
        int len = editText.getText().toString().length();
        editText.setSelection(len);
        return true;
    }

    /***
     * 输入号码
     * @param number
     */
    private void inputNumber(String number){
        int index = editText.getSelectionStart();
        Editable editable = editText.getText();
        editable.insert(index,number);
    }

    /***
     * 删除号码
     */
    private void deleteNumber(){
        int index = editText.getSelectionStart();
        Editable editable = editText.getText();
        if(index > 0){
            editable.delete(index-1, index);
        }
    }

    /***
     * 拨打电话
     * @param number 电话号码
     */
    private void call(String number) {
        if (PhoneNumberUtils.isEmergencyNumber(number)) {
            Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.parse("tel:"+number));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,getString(R.string.openCallPermission),Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.Button8:
                inputNumber("*");
                break;
            case R.id.Button9:
                inputNumber("#");
                break;
            case R.id.Button0:
                inputNumber("+");
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //add by shipeixian for finish when press back key begin  
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    //add by shipeixian for finish when press back key end

    //add by shipeixian for cancel animation begin
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    //add by shipeixian for cancel animation end
}
