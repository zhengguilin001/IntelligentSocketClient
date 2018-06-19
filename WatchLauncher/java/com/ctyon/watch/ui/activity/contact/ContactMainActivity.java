package com.ctyon.watch.ui.activity.contact;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.ctyon.watch.R;
import com.ctyon.watch.model.ContactEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//add by shipeixian for delete contact begin
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.ArrayList;
//add by shipeixian for delete contact end

public class ContactMainActivity extends Activity implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private ImageButton ibLeft;
    private ImageButton ibRight;
    private Button btnLeft;
    private Button btnRight;
    private ListView lvContacts;
    private PopupWindow popupWindow;
    private List<ContactEntity> contacts;
    private ContactsManager contactsManager;
    private ContactsAdapter contactsAdapter;
    private LinearLayout llNoContact;
    private ExecutorService singleThreadExecutor;
    private boolean isFirstIn = true;
    //当前选中的联系人
    private int curPosition = -1;
    private boolean isFromInCallUI;
    private MySimReceiver simReceiver;

    //add by shipeixian for adjust ui begin
    private Button goBackButton;
    //add by shipeixian for adjust ui end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_main);
        initView();
        //requestPermissions();
        contactsManager = ContactsManager.getInstance(this);
        singleThreadExecutor = Executors.newSingleThreadExecutor();
        //ProgressDialogUtil.showProgressDialog(ContactMainActivity.this,getString(R.string.loading)+"...");
        //queryAllContacts();
        isFromInCallUI = getIntent().getBooleanExtra("isFromInCallUI", false);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                /*case 1:
                    //ProgressDialogUtil.dismissProgressDialog();
                    if (contacts.isEmpty()){
                        lvContacts.setVisibility(View.GONE);
                        llNoContact.setVisibility(View.VISIBLE);
                    }else {
                        contactsAdapter = new ContactsAdapter(ContactMainActivity.this, contacts);
                        lvContacts.setAdapter(contactsAdapter);
                    }
                    break;*/
                /*case 2:
                    //发送广播给主界面刷新
                    Intent intent = new Intent("android.intent.action.APP_UNINSTALL");
                    intent.putExtra("DeleteAll",true);
                    sendBroadcast(intent);

                    ProgressDialogUtil.dismissProgressDialog();
                    queryAllContacts();
                    break;*/
                case 3:
                    //ProgressDialogUtil.dismissProgressDialog();
                    if (contacts.isEmpty()){
                        lvContacts.setVisibility(View.GONE);
                        llNoContact.setVisibility(View.VISIBLE);
                    }else {
                        lvContacts.setVisibility(View.VISIBLE);
                        llNoContact.setVisibility(View.GONE);
                        if (contactsAdapter == null){
                            contactsAdapter = new ContactsAdapter(ContactMainActivity.this, contacts);
                            lvContacts.setAdapter(contactsAdapter);
                        }else {
                            contactsAdapter.updateListView(contacts);
                        }
                    }
                    break;
            }
        }
    };

    private void queryAllContacts(){
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                contacts = contactsManager.queryAllContacts();
                Log.e("ContactMainActivity",contacts.toString());
                handler.sendEmptyMessage(3);
            }
        });
    }
    private void deleteAllContacts(){
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                contactsManager.deleteAllContacts();
                handler.sendEmptyMessage(2);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG","onResume"+ "isFirstIn:"+ isFirstIn);
        if (simReceiver == null){
            registerSimReceiver();
        }
        /*if (!isFirstIn){
            //ProgressDialogUtil.showProgressDialog(ContactMainActivity.this,getString(R.string.loading)+"...");
            singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    contacts = contactsManager.queryAllContacts();
                    handler.sendEmptyMessage(3);
                }
            });
        }
        isFirstIn = false;*/
        queryAllContacts();
        if (popupWindow != null){
            popupWindow.dismiss();
        }
    }

    private void registerSimReceiver() {
        simReceiver = new MySimReceiver();
        IntentFilter filter = new IntentFilter("com.android.ctyon.sim");
        registerReceiver(simReceiver,filter);
    }

    private void initView() {
        ibLeft = (ImageButton)findViewById(R.id.ib_left);
        //ibRight = (ImageButton)findViewById(R.id.ib_right);
        //btnLeft = (Button)findViewById(R.id.btn_left);
        //btnRight = (Button)findViewById(R.id.btn_right);
        lvContacts = (ListView)findViewById(R.id.lv_contacts);
        lvContacts.setOnItemClickListener(this);
        lvContacts.setOnItemSelectedListener(this);
        llNoContact = (LinearLayout) findViewById(R.id.llNoContact);
        ibLeft.setOnClickListener(this);
        //ibRight.setOnClickListener(this);
        //btnLeft.setOnClickListener(this);
        //btnRight.setOnClickListener(this);
        //add by shipeixian for delete contact begin
        lvContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactMainActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否删除该联系人");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final List<ContactEntity> deleteItem = new ArrayList<ContactEntity>();
                        deleteItem.add(contacts.get(position));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                contactsManager.deleteMultipleContacts(deleteItem);
                            }
                        }).start();
                        contacts.remove(contacts.get(position));
                        handler.sendEmptyMessageDelayed(3, 50);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
        //add by shipeixian for delete contact end

        //add by shipeixian for adjust ui begin
        goBackButton = (Button) findViewById(R.id.backButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ContactMainActivity.this.finish();
            }
        });
        //add by shipeixian for adjust ui end
    }

    public void setBackgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_left:
                if (isFromInCallUI){
                    Intent dialIntent =  new Intent(Intent.ACTION_CALL_BUTTON);//跳转到拨号界面
                    startActivity(dialIntent);
                    finish();
                }else {
                    finish();
                }
                break;
            /*case R.id.ib_right:
                //showPopupWindow();
                break;*/
            /*case R.id.btn_left:
                if (contacts != null){
                    if (contacts.size() == 0){
                        Toast.makeText(this, getString(R.string.no_contact), Toast.LENGTH_SHORT).show();
                    }else {

                        startActivity(new Intent(this,SearchContactActivity.class));
                    }
                }
                break;
            case R.id.btn_right:
                startActivity(new Intent(this,NewContactActivity.class));
                break;*/
        }
    }

    /*private void showPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popup_menu, null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setContentView(contentView);
        ListView popList = (ListView)contentView.findViewById(R.id.lv_pop_menu);
        String[] menu = getResources().getStringArray(R.array.popMenu);
        PopWindowAdapter adapter = new PopWindowAdapter(this,menu);
        popList.setAdapter(adapter);
        setBackgroundAlpha(0.5f);
        popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_main, null), Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1f);
            }
        });
        popList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 0:
                        if (contacts.size() == 0){
                            Toast.makeText(ContactMainActivity.this, getString(R.string.no_contact), Toast.LENGTH_SHORT).show();
                        }else {

                            startActivity(new Intent(ContactMainActivity.this,SearchContactActivity.class));
                        }
                        break;
                    case 1:
                        startActivity(new Intent(ContactMainActivity.this,NewContactActivity.class));
                        break;
                    case 2:
                        if (contacts.size() != 0){
                            startActivity(new Intent(ContactMainActivity.this,DeleteContactActivity.class));
                        }
                        break;
                    case 3:
                        if (contacts.size() != 0){
                            popupWindow.dismiss();
                            showPopupWindow2();
                        }
                        break;
                }
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
    }*/

    /*private void showPopupWindow2() {
        {
            View contentView = LayoutInflater.from(this).inflate(R.layout.popup_delete, null);
            TextView tvTitle =  (TextView) contentView.findViewById(R.id.tvTitle);
            tvTitle.setText(getString(R.string.deleteAllContactOrNot));
            final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setContentView(contentView);
            setBackgroundAlpha(0.5f);
            popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_edit_contact, null), Gravity.BOTTOM, 0, 0);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    setBackgroundAlpha(1f);
                }
            });
            Button btnDelete = (Button)contentView.findViewById(R.id.btnDelete);
            Button btnCancel = (Button)contentView.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProgressDialogUtil.showProgressDialog(ContactMainActivity.this,getString(R.string.deleting)+"...");
                    deleteAllContacts();
                    popupWindow.dismiss();
                }
            });
        }
    }*/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,ContactActivity.class);
        intent.putExtra("contact",contacts.get(position));
        startActivity(intent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        curPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        curPosition = -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ProgressDialogUtil.dismissProgressDialog();
        if (simReceiver != null){
            unregisterReceiver(simReceiver);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFromInCallUI){
            Intent dialIntent =  new Intent(Intent.ACTION_CALL_BUTTON);//跳转到拨号界面
            startActivity(dialIntent);
            finish();
        }
    }
    class MySimReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.android.ctyon.sim")){
                Log.e("ContactMainActivity","收到com.android.ctyon.sim广播");
                singleThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        contacts = contactsManager.queryAllContacts();
                        handler.sendEmptyMessage(3);
                    }
                });
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
