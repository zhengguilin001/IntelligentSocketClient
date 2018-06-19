package com.ctyon.watch.ui.activity.contact;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ctyon.watch.R;
import com.ctyon.watch.model.ContactEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.net.Uri;
//add by shipeixian for adjust ui begin
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ImageView;
//add by shipeixian for adjust ui end
/**
 * Created by zx
 * On 2017/9/12
 */

public class ContactActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "ContactActivity";
    private TextView tvName;
    private TextView tvNumber;
    private PopupWindow popupWindow;
    private ContactEntity contact;
    private static final int REQUEST_CODE = 1001;
    private static final int REQUEST_CODE2 = 1002;
    private boolean isKinsfolk;
    private String[] menu;
    private Intent intent;
    private String name;
    private String number;

    //add by shipeixian for adjust ui begin
    //private Button goBackButton;
    //private RelativeLayout dialZone;
    private ImageView headIcon;
    //add by shipeixian for adjust ui end

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initView();
        tvNumber.requestFocus();
        copyDataBase("address.db");
        setData();
    }

    private void initView() {
        tvName = (TextView)findViewById(R.id.tvName);
        tvNumber = (TextView)findViewById(R.id.tvNumber);
        //ImageButton mback = (ImageButton)findViewById(R.id.ibBack);
        //ImageButton mmore = (ImageButton)findViewById(R.id.ibMore);
        //Button mCall = (Button)findViewById(R.id.btnCall);
        //Button btnLeft = (Button) findViewById(R.id.btn_left);
        //btnLeft.setOnClickListener(this);
        //mCall.setOnClickListener(this);
        //mmore.setOnClickListener(this);
        //mback.setOnClickListener(this);
       
        //add by shipeixian for adjust ui begin
        headIcon = (ImageView)findViewById(R.id.headIcon);
        tvNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.parse("tel:"+tvNumber.getText().toString().trim()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        //add by shipeixian for adjust ui end
    }

    //add by shipeixian for headIcon with contact name begin
    private void setContactHeadIcon(String contactName, ImageView imageView) {
        if (contactName.contains("爸")) {
            imageView.setBackgroundResource(R.drawable.address_book_father_big);
            return;
        }
        if (contactName.contains("妈")) {
            imageView.setBackgroundResource(R.drawable.address_book_mom_big);
            return;
        }
        if (contactName.contains("爷")) {
            imageView.setBackgroundResource(R.drawable.address_book_grandfather_big);
            return;
        }
        if (contactName.contains("奶")) {
            imageView.setBackgroundResource(R.drawable.address_book_grandmother_big);
            return;
        }
        if (contactName.contains("公")) {
            imageView.setBackgroundResource(R.drawable.address_book_grandpa_big);
            return;
        }
        if (contactName.contains("婆")) {
            imageView.setBackgroundResource(R.drawable.address_book_grandma_big);
            return;
        }
        if (contactName.contains("老师")) {
            imageView.setBackgroundResource(R.drawable.address_book_teacher_big);
            return;
        }
        if (contactName.contains("哥") || contactName.contains("弟")) {
            imageView.setBackgroundResource(R.drawable.address_book_brother_big);
            return;
        }
        if (contactName.contains("姐") || contactName.contains("妹")) {
            imageView.setBackgroundResource(R.drawable.address_book_sister_big);
            return;
        }
        imageView.setBackgroundResource(R.drawable.address_book_custom_big);
    }
    //add by shipeixian for headIcon with contact name end

    /***
     * 拷贝号码归属地数据库到data/data/package name/files/ 目录
     * @param dbName
     */
    private void copyDataBase(String dbName) {
        File desFile = new File(getFilesDir(), dbName);//要拷贝的目标地址
        if (desFile.exists()) {//如果已经存在,就不进行拷贝工作直接返回
            Log.d("print", "数据库" + dbName + "已经存在");
            return;
        }
        //否则就进行数据库拷贝
        FileOutputStream out = null;
        InputStream in = null;

        try {
            in = getAssets().open(dbName);
            out = new FileOutputStream(desFile);

            int len;
            byte[] buffer = new byte[1024];

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void setBackgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (popupWindow != null){
            popupWindow.dismiss();
        }
    }

    private void setData() {
        intent = getIntent();
        contact = intent.getParcelableExtra("contact");
        isKinsfolk = intent.getBooleanExtra("isKinsfolk", false);
        if (contact == null) {
            //isFromCallMainActivity = intent.getBooleanExtra("isFromCallMainActivity",false);
            name = intent.getStringExtra("name");
            number = intent.getStringExtra("number");
            /*if (isFromCallMainActivity){
                id = ContactsManager.getInstance(this).queryId(name,number);
            }else {
                id = intent.getLongExtra("id",-1);
            }*/
            if (name == null) {
                tvName.setText(getString(R.string.unknownContact));
            } else {
                tvName.setText(name);
            }
            setTvNumber(number);
        } else {
            name = contact.getName();
            tvName.setText(name);
             number = contact.getNumber();
            setTvNumber(number);
        }
        setContactHeadIcon(tvName.getText().toString(), headIcon);
    }

    /***
     * 根据号码设置号码和归属地
     * @param number 号码
     * @return 号码和归属地
     */
    private String setTvNumber(String number) {
        //String location = getContactLocation(number);
        String location = "";
        if (location.equals("")){
            tvNumber.setText(number);
        }else {
            tvNumber.setText(number+"-"+location);
        }
        return location;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
                if(isKinsfolk){
                    intent.putExtra("editContact",contact);
                    setResult(REQUEST_CODE2,intent);
                }
                finish();
                break;
            /*case R.id.ibMore:
                //showPopupWindow();
                break;*/
            /*case R.id.btnCall:
                //String number = parserNumber(tvNumber.getText().toString().trim());
                Log.e(TAG,"Calling:"+number);
                if (PhoneNumberUtils.isEmergencyNumber(number)){
                    Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.parse("tel:"+number));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +number));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this,getString(R.string.openCallPermission), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startActivity(intent);
                }
                break;*/
            /*case R.id.btn_left:
                Intent intent = new Intent(ContactActivity.this, NewMessageActivity.class);
                intent.putExtra("number",number);
                intent.putExtra("name",name);
                startActivity(intent);
                break;*/

        }
    }

    /*private void showPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popup_menu, null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setContentView(contentView);
        ListView popList = (ListView)contentView.findViewById(R.id.lv_pop_menu);
        menu = getResources().getStringArray(R.array.popMenu2);
        if(isKinsfolk){
            menu = getResources().getStringArray(R.array.kinsfolk_popMenu);
        }
        if (tvName.getText().equals(getString(R.string.unknownContact))){
            menu[1] = getString(R.string.savingContact);
        }
        PopWindowAdapter adapter = new PopWindowAdapter(this,menu);
        popList.setAdapter(adapter);
        setBackgroundAlpha(0.5f);
        popupWindow.showAtLocation(getLayoutInflater().inflate(R.layout.activity_contact, null), Gravity.BOTTOM, 0, 0);
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
                        Intent intent2 = new Intent(ContactActivity.this,CallDetailsActivity.class);
                        intent2.putExtra("number",parserNumber(tvNumber.getText().toString().trim()));
                        startActivity(intent2);
                        break;
                    case 1:
                        if (menu[1].equals(getString(R.string.savingContact))){
                            Intent intent = new Intent(ContactActivity.this,NewContactActivity.class);
                            intent.putExtra("number",parserNumber(tvNumber.getText().toString().trim()));
                            startActivityForResult(intent,REQUEST_CODE);
                        }else if (contact == null){
                            Intent intent = new Intent(ContactActivity.this,EditContactActivity.class);
                            intent.putExtra("name",name);
                            intent.putExtra("number",number);
                            intent.putExtra("isFromCallLog",true);
//                            ContactEntity contact2 = ContactsManager.getInstance(ContactActivity.this).getContact(name,number);
//                            if (contact2 != null){
//                                intent.putExtra("contact2",contact2);
//                            }
                            startActivityForResult(intent,REQUEST_CODE);
                        }else{
                            Intent intent = new Intent(ContactActivity.this,EditContactActivity.class);
                            intent.putExtra("contact",contact);
                            intent.putExtra("isKinsfolk",isKinsfolk);
                            startActivityForResult(intent,REQUEST_CODE);
                        }
                        break;
                    case 2:
                        Intent msgIntent = new Intent(ContactActivity.this, NewMessageActivity.class);
                        msgIntent.putExtra("content",tvName.getText().toString()+":"+number);
                        startActivity(msgIntent);
                        break;
                    case 3:
                        popupWindow.dismiss();
                        if(isKinsfolk){
                            intent.putExtra("delName",tvName.getText().toString());
                            setResult(REQUEST_CODE2,intent);
                            finish();
                        }
                        break;
                }
                popupWindow.dismiss();
            }
        });
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && resultCode == 1002){
            Log.e("TAG","onActivityResult执行");
            String name = data.getStringExtra("name");
            String number = data.getStringExtra("number");
            Log.e("TAG","number:"+number);
            tvName.setText(name);
            setTvNumber(number);
            if (contact != null){
                contact.setNumber(number);
                contact.setName(name);
            }
        }else if(resultCode == 1004){
            finish();
        }else if(resultCode == 1005){
            boolean isDel = data.getBooleanExtra("isDel", false);
            ContactEntity entity = data.getParcelableExtra("editContact");
            if(isDel){
                intent.putExtra("delName",tvName.getText().toString());
                setResult(REQUEST_CODE2,intent);
                finish();
            }else{
                intent.putExtra("editContact",entity);
                setResult(REQUEST_CODE2,intent);
                finish();
            }
        }else if (resultCode == 1006){
            finish();
        }
    }
    private static final String PATH = "data/data/com.ctyon.ctyonlauncher/files/address.db";

    /***
     * 查找数据库
     * @param number 电话号码
     * @return 号码归属地
     */
    private String getContactLocation(String number){
        if (number != null){
            String location = "";
            //获取数据库对象
            SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
                    SQLiteDatabase.OPEN_READONLY);
            //正则表达式匹配
            if (number.matches("^1[3-8]\\d{9}$")) {//匹配器11位手机号
                Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)",
                        new String[]{number.substring(0, 7)});//截取前七个
                if (cursor.moveToNext())
                    location = cursor.getString(0);
                cursor.close();
            }else if (number.matches("^\\d+$")){//匹配数字
                switch (number.length()){
                    case 3:
                        location = "";//三位数就是报警电话
                        break;
                    case 4:
                        location = "";
                        break;
                    case 5:
                        location = "";
                        break;
                    case 7:
                    case 8:
                        location = "";
                        break;
                    default:
                        if (number.startsWith("0")&&number.length()>10){//有可能是长途电话
                            //有些区号是4位，有些区号是3位（包括0）
                            // 先查询4位区号
                            Cursor cursor = database.rawQuery("select location from data2 where area =?",
                                    new String[]{number.substring(1,4)});
                            if (cursor.moveToNext())
                                location = cursor.getString(0);
                            else {
                                cursor.close();
                                //查询3位区号
                                cursor = database.rawQuery("select location from data2 where area =?",
                                        new String[]{number.substring(1,3)});
                                if (cursor.moveToNext())
                                    location = cursor.getString(0);
                                cursor.close();
                            }
                        }
                        break;
                }
            }
            database.close();
            return location;
        }
        return "";
    }

    /***
     * 格式化tvNumber的字符串
     * @param number
     * @return
     */
    private String parserNumber(String number){
        if (number.contains("-")){
            String[] result = number.split("-");
            return result[0];
        }else {
            return number;
        }

    }

    @Override
    public void onBackPressed() {
        if(isKinsfolk){
            intent.putExtra("editContact",contact);
            setResult(REQUEST_CODE2,intent);
        }
        super.onBackPressed();
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_CALL:
                if(number != null)
                    Log.e(TAG,"Calling:"+number);
                    PhoneUtils.makeCall(ContactActivity.this,number);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}
