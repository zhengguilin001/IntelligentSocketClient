package com.ctyon.watch.ui.activity.calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ctyon.watch.R;
import com.ctyon.watch.ui.activity.BaseActivity;

public class CalculatorActivity extends BaseActivity {

    private String  expression = "";
    private boolean last_equal = false;//上一次的按键是否为等号

    protected EditText text1;//第一行，用来显示按过等号之后的完整表达式
    protected EditText text2;//第二行，用来显示表达式和结果
    protected static boolean isSimple = true;//当前是否是简易计算器

    private View board;
    private View board2;

    private int screen_width;
    private int screen_height;

    private LinearLayout display;
    private Button[]     buttons;
    private Button[]     buttons2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        Button change_btn = (Button)findViewById(R.id.change);
        change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //更换键盘
                if(isSimple == true){
                    //缩放动画效果
                    board.setVisibility(View.INVISIBLE);
                    board2.setVisibility(View.VISIBLE);
                    ScaleAnimation sa = new ScaleAnimation(1.2f,1f,1.2f,1f,
                            Animation.RELATIVE_TO_SELF,1f,
                            Animation.RELATIVE_TO_SELF,1f);
                    sa.setDuration(300);
                    board2.startAnimation(sa);
                    isSimple = false;
                }else{
                    ScaleAnimation sa = new ScaleAnimation(1f,1.25f,1f,1.2f,
                            Animation.RELATIVE_TO_SELF,1f,
                            Animation.RELATIVE_TO_SELF,1f);
                    sa.setDuration(300);
                    board2.startAnimation(sa);

                    board2.setVisibility(View.INVISIBLE);
                    board.setVisibility(View.VISIBLE);
                    isSimple = true;
                }
            }
        });

        text1 = (EditText)findViewById(R.id.text1);
        text2 = (EditText)findViewById(R.id.text2);


        //初始化计算器键盘
        buttons = new Button[18];
        buttons2 = new Button[29];
        initSimpleBoard(buttons);//初始化简易计算器键盘
        initScienceBoard(buttons2);//初始化科学计算器键盘
        board = (View)findViewById(R.id.board);
        board2 = (View)findViewById(R.id.board2);


        if(savedInstanceState != null){
            text1.setText(savedInstanceState.getString("text1"));
           text2.setText(savedInstanceState.getString("text2"));
            isSimple = savedInstanceState.getBoolean("isSimple");
            Log.v("TAG==>","OKKOKOKO");
        }
    }

    @Override
    protected void setContentView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void initComponentEvent() {

    }

    @Override
    protected void initComponentView() {

    }


    //活动被回收时，保存临时数据
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("text1", text1.getText().toString());
        outState.putString("text2", text2.getText().toString());
        outState.putBoolean("isSimple",isSimple);

    }


    //初始化简易计算器键盘
    private void initSimpleBoard(final Button[] buttons){
        buttons[0] = (Button)findViewById(R.id.zero);
        buttons[1] = (Button)findViewById(R.id.one);
        buttons[2] = (Button)findViewById(R.id.two);
        buttons[3] = (Button)findViewById(R.id.three);
        buttons[4] = (Button)findViewById(R.id.four);
        buttons[5] = (Button)findViewById(R.id.five);
        buttons[6] = (Button)findViewById(R.id.six);
        buttons[7] = (Button)findViewById(R.id.seven);
        buttons[8] = (Button)findViewById(R.id.eight);
        buttons[9] = (Button)findViewById(R.id.nine);

        buttons[10] = (Button)findViewById(R.id.empty);
        buttons[11] = (Button)findViewById(R.id.delete);
        buttons[12] = (Button)findViewById(R.id.divide);
        buttons[13] = (Button)findViewById(R.id.multiple);
        buttons[14] = (Button)findViewById(R.id.minus);
        buttons[15] = (Button)findViewById(R.id.plus);
        buttons[16] = (Button)findViewById(R.id.equal);
        buttons[17] = (Button)findViewById(R.id.dot);


        initCommonBtns(buttons);
    }


    //初始化科学计算器键盘
    private void initScienceBoard(final Button[] buttons){
        buttons[0] = (Button)findViewById(R.id.zero2);
        buttons[1] = (Button)findViewById(R.id.one2);
        buttons[2] = (Button)findViewById(R.id.two2);
        buttons[3] = (Button)findViewById(R.id.three2);
        buttons[4] = (Button)findViewById(R.id.four2);
        buttons[5] = (Button)findViewById(R.id.five2);
        buttons[6] = (Button)findViewById(R.id.six2);
        buttons[7] = (Button)findViewById(R.id.seven2);
        buttons[8] = (Button)findViewById(R.id.eight2);
        buttons[9] = (Button)findViewById(R.id.nine2);

        buttons[10] = (Button)findViewById(R.id.empty2);
        buttons[11] = (Button)findViewById(R.id.delete2);
        buttons[12] = (Button)findViewById(R.id.divide2);
        buttons[13] = (Button)findViewById(R.id.multiple2);
        buttons[14] = (Button)findViewById(R.id.minus2);
        buttons[15] = (Button)findViewById(R.id.plus2);
        buttons[16] = (Button)findViewById(R.id.equal2);
        buttons[17] = (Button)findViewById(R.id.dot2);

        initCommonBtns(buttons);


        //初始化剩余的11个按钮
        buttons[18] = (Button)findViewById(R.id.sin);
        buttons[19] = (Button)findViewById(R.id.cos);
        buttons[20] = (Button)findViewById(R.id.tan);
        buttons[21] = (Button)findViewById(R.id.ln);


        buttons[22] = (Button)findViewById(R.id.factorial);
        buttons[23] = (Button)findViewById(R.id.power);
        buttons[24] = (Button)findViewById(R.id.sqrt);
        buttons[25] = (Button)findViewById(R.id.pi);
        buttons[26] = (Button)findViewById(R.id.left_parentheses);
        buttons[27] = (Button)findViewById(R.id.right_parentheses);
        buttons[28] = (Button)findViewById(R.id.e);

        buttons[18].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("fei","11");
                expression += buttons[18].getText() + "(";
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        buttons[19].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                expression += buttons[19].getText() + "(";
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        buttons[20].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                expression += buttons[20].getText() + "(";
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        buttons[21].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                expression += buttons[21].getText() + "(";
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });

        buttons[22].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                expression += buttons[22].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        buttons[23].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                expression += buttons[23].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        buttons[24].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                expression += buttons[24].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        buttons[25].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                expression += buttons[25].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        buttons[26].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                expression += buttons[26].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        buttons[27].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                expression += buttons[27].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        buttons[28].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                expression += buttons[28].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
    }


    //初始化简易计算器，科学计算器相同的18个按钮
    private void initCommonBtns(final Button[] buttons){
        //添加监听事件
        //数字0～9
        for(int i = 0; i < 10; i++){
            final int m = i;
            buttons[i].setTextSize(22);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(last_equal){
                        expression = "";//这次按的数字，如果上次按了等号，则清空表达式
                        last_equal = false;
                    }
                    expression += buttons[m].getText();

                    text2.setText(expression);
                    text2.setSelection(expression.length());
                }
            });
            //add by shipeixian for Negative calculation begin
            if (i == 0) {
                buttons[0].setText("0 C");
                buttons[0].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        expression = "";
                        text2.setText("0");
                        text1.setText("");
                        return true;
                    }
                });
                buttons[0].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(last_equal){
                            expression = "";
                            last_equal = false;
                        }
                        expression += "0";

                        text2.setText(expression);
                        text2.setSelection(expression.length());
                    }
                });
            }
            if (i == 7) {
                buttons[7].setText("7 D");
                buttons[7].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(expression.length() < 1){
                             expression = "";
                             text2.setText("0");
                             text1.setText("");
                             return true;
                        }
                        expression = expression.substring(0,expression.length()-1);
                        text2.setText(expression);
                        text2.setSelection(expression.length());
                        last_equal = false;
                        return true;
                    }
                });
                buttons[7].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(last_equal){
                            expression = "";
                            last_equal = false;
                        }
                        expression += "7";

                        text2.setText(expression);
                        text2.setSelection(expression.length());
                    }
                });
            }
            if (i == 8) {
                buttons[8].setText("8 (");
                buttons[8].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(last_equal){
                            expression = "";
                            last_equal = false;
                        }
                        expression += "(";

                        text2.setText(expression);
                        text2.setSelection(expression.length());
                        return true;
                    }
                });
                buttons[8].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(last_equal){
                            expression = "";
                            last_equal = false;
                        }
                        expression += "8";

                        text2.setText(expression);
                        text2.setSelection(expression.length());
                    }
                });
            }
            if (i == 9) {
                buttons[9].setText("9 )");
                buttons[9].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(last_equal){
                            expression = "";
                            last_equal = false;
                        }
                        expression += ")";

                        text2.setText(expression);
                        text2.setSelection(expression.length());
                        return true;
                    }
                });
                buttons[9].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(last_equal){
                            expression = "";
                            last_equal = false;
                        }
                        expression += "9";

                        text2.setText(expression);
                        text2.setSelection(expression.length());
                    }
                });
            }
            //add by shipeixian for Negative calculation end
        }
        //empty
        buttons[10].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression = "";
                text2.setText("0");
                text1.setText(null);
                last_equal = false;
            }
        });
        //delete
        buttons[11].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expression.length() < 1){
                    return;
                }
                expression = expression.substring(0,expression.length()-1);
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        //divide
        buttons[12].setTextSize(22);
        buttons[12].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression += buttons[12].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        //multiple
        buttons[13].setTextSize(22);
        buttons[13].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression += buttons[13].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        //minus
        buttons[14].setTextSize(22);
        buttons[14].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression += buttons[14].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        //plus
        buttons[15].setTextSize(22);
        buttons[15].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression += buttons[15].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        //equal
        buttons[16].setTextSize(22);
        buttons[16].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(last_equal) return;//如果上次还是按的等号，那么什么也不做
                if(!expression.contains("+") && !expression.contains("-") && !expression.contains("*") && !expression.contains("/") && !expression.contains("(") && !expression.contains(")")) {
		    if (expression.indexOf(".") != expression.lastIndexOf(".")) {
		        
		    } else if (expression.endsWith(".")) {
		        expression = expression.substring(0, expression.length() - 1);
                        text2.setText(expression);
                        return;
		    } else {
                        return;
                    }
                }

                //小小的动画效果
                AnimationSet animSet = new AnimationSet(true);
                TranslateAnimation ta = new TranslateAnimation(0,0,0,-100);
                ta.setDuration(80);
                AlphaAnimation aa = new AlphaAnimation(1f, 0f);
                aa.setDuration(75);
                animSet.addAnimation(ta);
                animSet.addAnimation(aa);
                text2.startAnimation(animSet);
                animSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //动画结束之后计算
                        text1.setText(expression + "=");
                        text1.setSelection(expression.length()+1);//在第一行显示计算表达式
                        try{
                            //add by shipeixian for Negative calculation begin
                            com.ctyon.watch.utils.Calculator calc = new com.ctyon.watch.utils.Calculator();
                            Double calcResult = calc.prepareParam(expression + "=");
                            expression = com.ctyon.watch.utils.CaculatorUtils.formatResult(String.format("%." + com.ctyon.watch.utils.CaculatorUtils.RESULT_DECIMAL_MAX_LENGTH + "f", calcResult));
                            //expression = Calculate.calculate(expression);
                            //add by shipeixian for Negative calculation end
                            text2.setText(expression);//在第二行显示计算结果
                        }catch(Exception exception){
                            text2.setText("表达式错误!");//在第二行显示计算结果
                            expression = "";
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });


                // 为下一次按计算器键盘做准备。
                // 如果下次按的是数字，那么清空第二行重新输入第一个数。
                // 如果是非数字，那就当这次的结果是输入的第一个数，直接参与运算。
                last_equal = true;

            }


        });
        buttons[17].setTextSize(22);
        buttons[17].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression += buttons[17].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
    }

    /*@Override
    public void onBackPressed() {
        if(expression.length() < 1){
            super.onBackPressed();
            return;
        }
        expression = expression.substring(0,expression.length()-1);
        text2.setText(expression);
        text2.setSelection(expression.length());
        last_equal = false;
   }*/

   //add by shipeixian for cancel animation begin
    @Override
    protected void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
    }
    //add by shipeixian for cancel animation end

}


