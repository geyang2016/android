package com.ruyiruyi.rylibrary.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruyiruyi.rylibrary.R;

public class CustomDialog extends BaseDialog {

    public CustomDialog(Context context) {
        super(context);
    }
    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onTouchOutside() {
        cancel();
    }

    public static class Builder {
        private String message;
        private View contentView;
        private String positiveButtonText;
        private String negativeButtonText;
        private String singleButtonText;
        private View.OnClickListener positiveButtonClickListener;
        private View.OnClickListener negativeButtonClickListener;
        private View.OnClickListener singleButtonClickListener;

        private View layout;
        private CustomDialog dialog;
        private EditText inputbox1;
        private EditText inputbox2;
        private EditText inputbox3;
        private EditText inputbox4;
        private EditText inputbox5;
        private EditText inputbox6;
        private EditText inputbox7;
        private LinearLayout boxesContainer;

        public Builder(Context context) {
            //这里传入自定义的style，直接影响此Dialog的显示效果。style具体实现见style.xml
            dialog = new CustomDialog(context, R.style.Dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.dialog_layout, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, View.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, View.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setSingleButton(String singleButtonText, View.OnClickListener listener) {
            this.singleButtonText = singleButtonText;
            this.singleButtonClickListener = listener;
            return this;
        }

        /**
         * 创建单按钮对话框
         * @return
         */
        public CustomDialog createSingleButtonDialog() {
            showSingleButton();
            layout.findViewById(R.id.singleButton).setOnClickListener(singleButtonClickListener);
            //如果传入的按钮文字为空，则使用默认的“返回”
            if (singleButtonText != null) {
                ((Button) layout.findViewById(R.id.singleButton)).setText(singleButtonText);
            } else {
                ((Button) layout.findViewById(R.id.singleButton)).setText("返回");
            }
            create();
            return dialog;
        }

        /**
         * 创建双按钮对话框
         * @return
         */
        public CustomDialog createTwoButtonDialog() {
            showTwoButton();
            layout.findViewById(R.id.positiveButton).setOnClickListener(positiveButtonClickListener);
            layout.findViewById(R.id.negativeButton).setOnClickListener(negativeButtonClickListener);
            //如果传入的按钮文字为空，则使用默认的“是”和“否”
            if (positiveButtonText != null) {
                ((TextView) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
            } else {
                ((TextView) layout.findViewById(R.id.positiveButton)).setText("是");
            }
            if (negativeButtonText != null) {
                ((TextView) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
            } else {
                ((TextView) layout.findViewById(R.id.negativeButton)).setText("否");
            }
            create();
            inputbox1 = ((EditText) layout.findViewById(R.id.et_car_license_inputbox1));
            inputbox2 = ((EditText) layout.findViewById(R.id.et_car_license_inputbox2));
            inputbox3 = ((EditText) layout.findViewById(R.id.et_car_license_inputbox3));
            inputbox4 = ((EditText) layout.findViewById(R.id.et_car_license_inputbox4));
            inputbox5 = ((EditText) layout.findViewById(R.id.et_car_license_inputbox5));
            inputbox6 = ((EditText) layout.findViewById(R.id.et_car_license_inputbox6));
            inputbox7 = ((EditText) layout.findViewById(R.id.et_car_license_inputbox7));

            //输入车牌完成后的intent过滤器
            return dialog;
        }

        /**
         * 单按钮对话框和双按钮对话框的公共部分在这里设置
         */
        private void create() {
            if (message != null) {      //设置提示内容
               // ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {       //如果使用Builder的setContentview()方法传入了布局，则使用传入的布局
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            dialog.setContentView(layout);
            dialog.setCancelable(true);     //用户可以点击手机Back键取消对话框显示
            dialog.setCanceledOnTouchOutside(false);        //用户不能通过点击对话框之外的地方取消对话框显示
        }

        /**
         * 显示双按钮布局，隐藏单按钮
         */
        private void showTwoButton() {
            layout.findViewById(R.id.singleButtonLayout).setVisibility(View.GONE);
            layout.findViewById(R.id.twoButtonLayout).setVisibility(View.VISIBLE);
        }

        /**
         * 显示单按钮布局，隐藏双按钮
         */
        private void showSingleButton() {
            layout.findViewById(R.id.singleButtonLayout).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.twoButtonLayout).setVisibility(View.GONE);
        }

    }
}