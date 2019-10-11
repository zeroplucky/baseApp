package com.mindaxx.clgk.widget;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mindaxx.clgk.R;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 仿IOS对话框
 * Created by cai.jia on 2017/10/20 0020.
 */

public class UniversalDialog extends DialogFragment {

    private static final String PARAMS = "params";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_single_button)
    TextView tvSingleButton;
    @BindView(R.id.tv_positive_button)
    TextView tvPositive;
    @BindView(R.id.tv_negative_button)
    TextView tvNegative;
    @BindView(R.id.lly_double_action)
    LinearLayout llyDoubleAction;

    private Builder params;

    private static UniversalDialog newInstance(Builder builder) {
        UniversalDialog dialog = new UniversalDialog();
        Bundle args = new Bundle();
        args.putSerializable(PARAMS, builder);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Dialog_full);
        Bundle args = getArguments();
        if (args != null) {
            params = (Builder) args.getSerializable(PARAMS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_dialog_universal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setText(tvTitle, params.title);
        setTextSize(tvTitle, params.titleTextSize);
        setTextColor(tvTitle, params.titleTextColor);

        setText(tvContent, params.content);
        setTextSize(tvContent, params.contentTextSize);
        setTextColor(tvContent, params.contentTextColor);
        setTextViewGravity(tvContent, params.contentGravity);

        setText(tvSingleButton, params.positive);
        setTextSize(tvSingleButton, params.positiveTextSize);
        setTextColor(tvSingleButton, params.positiveTextColor);

        setText(tvPositive, params.positive);
        setTextSize(tvPositive, params.positiveTextSize);
        setTextColor(tvPositive, params.positiveTextColor);

        setText(tvNegative, params.negative);
        setTextSize(tvNegative, params.negativeTextSize);
        setTextColor(tvNegative, params.negativeTextColor);

        llyDoubleAction.setVisibility(params.isSingleButton ? View.GONE : View.VISIBLE);
        tvSingleButton.setVisibility(params.isSingleButton ? View.VISIBLE : View.GONE);
        tvTitle.setVisibility(params.hasTitle || !TextUtils.isEmpty(params.title) ? View.VISIBLE : View.GONE);
    }

    private void setText(TextView textView, CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
    }

    private void setTextSize(TextView textView, int textSize) {
        if (textSize > 0) {
            textView.setTextSize(textSize);
        }
    }

    private void setTextColor(TextView textView, int textColor) {
        if (textColor > 0) {
            textView.setTextColor(textColor);
        }
    }

    private void setTextViewGravity(TextView textView, int gravity) {
        textView.setGravity(gravity);
    }

    @OnClick({R.id.tv_single_button, R.id.tv_positive_button, R.id.tv_negative_button})
    public void onViewClicked(View view) {
        int action = -1;
        switch (view.getId()) {
            case R.id.tv_single_button:
            case R.id.tv_positive_button:
                action = ACTION_POSITIVE;
                break;

            case R.id.tv_negative_button:
                action = ACTION_NEGATIVE;
                break;
        }

        if (actionClickListener != null && action != -1) {
            actionClickListener.onDialogActionClick(action);
        }
    }

    public static class Builder implements Serializable {

        private CharSequence title;
        private int titleTextSize;
        private int titleTextColor;

        private CharSequence content;
        private int contentTextSize;
        private int contentTextColor;

        private CharSequence negative;
        private int negativeTextSize;
        private int negativeTextColor;

        private CharSequence positive;
        private int positiveTextSize;
        private int positiveTextColor;

        private boolean isSingleButton;
        private boolean hasTitle;
        private int contentGravity = Gravity.CENTER;

        public Builder title(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder titleTextSize(int titleTextSize) {
            this.titleTextSize = titleTextSize;
            return this;
        }

        public Builder titleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public Builder content(CharSequence content) {
            this.content = content;
            return this;
        }

        public Builder contentTextSize(int contentTextSize) {
            this.contentTextSize = contentTextSize;
            return this;
        }

        public Builder contentTextColor(int contentTextColor) {
            this.contentTextColor = contentTextColor;
            return this;
        }

        public Builder negative(CharSequence negative) {
            this.negative = negative;
            return this;
        }

        public Builder negativeTextSize(int negativeTextSize) {
            this.negativeTextSize = negativeTextSize;
            return this;
        }

        public Builder negativeTextColor(int negativeTextColor) {
            this.negativeTextColor = negativeTextColor;
            return this;
        }

        public Builder positive(CharSequence positive) {
            this.positive = positive;
            return this;
        }

        public Builder contentGravity(int gravity) {
            contentGravity = gravity;
            return this;
        }

        public Builder positiveTextSize(int positiveTextSize) {
            this.positiveTextSize = positiveTextSize;
            return this;
        }

        public Builder positiveTextColor(int positiveTextColor) {
            this.positiveTextColor = positiveTextColor;
            return this;
        }

        public Builder isSingleButton(boolean isSingleButton) {
            this.isSingleButton = isSingleButton;
            return this;
        }

        public Builder hasTitle(boolean hasTitle) {
            this.hasTitle = hasTitle;
            return this;
        }

        public UniversalDialog build() {
            return UniversalDialog.newInstance(this);
        }
    }

    public static final int ACTION_POSITIVE = 1;
    public static final int ACTION_NEGATIVE = 2;

    public interface OnDialogActionClickListener {

        void onDialogActionClick(int action);
    }

    private OnDialogActionClickListener actionClickListener;

    public void setOnDialogActionClickListener(OnDialogActionClickListener listener) {
        this.actionClickListener = listener;
    }

    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
