package srs.otptextbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * @author Sony Raj on 22/05/17.
 */

public class OtpTextBox extends LinearLayout implements View.OnKeyListener, TextWatcher {

    public OtpTextBox(Context context) {
        this(context, null);
    }

    public OtpTextBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OtpTextBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.OtpTextBox);
        TypedValue typedValue = new TypedValue();
        TypedArray accentColor = getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        setOrientation(HORIZONTAL);
        try {
            int numChildren = typedArray.getInt(R.styleable.OtpTextBox_numChildren, 1);
            LayoutInflater inflater = LayoutInflater.from(context);
            for (int i = 0; i < numChildren; i++) {
                EditText child = (EditText)
                        inflater.inflate(R.layout.otp_text_box, this, false);

                Drawable drawable = child.getBackground();
                drawable.setColorFilter(accentColor.getColor(0, 0), PorterDuff.Mode.SRC_ATOP);

                child.setBackground(drawable);

                child.setTag(i);
                child.setOnKeyListener(this);
                child.addTextChangedListener(this);
                addView(child);
            }

        } catch (Exception e) {
            //ignore for now
        } finally {
            typedArray.recycle();
            accentColor.recycle();
        }

    }


    public void setError(String error) {
        for (int i = getChildCount() - 1; i <= 0; i--) {
            AppCompatEditText lastChild = (AppCompatEditText) getChildAt(i);
            if (lastChild == null) continue;
            lastChild.setError(error);
            break;
        }
    }

    public void setText(String otp) throws IllegalArgumentException {
        int childCount = getChildCount();
        otp = otp.trim();
        if (childCount < otp.length())
            throw new IllegalArgumentException("OTP length must not be greater than " + childCount);
        for (int i = 0; i < otp.length(); i++) {
            ((AppCompatEditText) getChildAt(i)).setText(otp.charAt(i));
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_UP) return false;
        final AppCompatEditText current = (AppCompatEditText) v;
        final AppCompatEditText target;
        int tag = (int) current.getTag();
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (current.getText().length() >= 1) current.setText("");
            if (tag > 0) {
                target = (AppCompatEditText) findViewWithTag(tag - 1);
                if (target != null) target.requestFocus();
            }
            return true;
        }


        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        AppCompatEditText current = (AppCompatEditText) findFocus();
        if (current == null) return;
        if (TextUtils.isEmpty(current.getText())) return;
        int tag = (int) current.getTag();
        int childCount = getChildCount();
        if (tag == childCount - 1) return;
        AppCompatEditText target = (AppCompatEditText) findViewWithTag(tag + 1);
        if (target == null) return;
        target.requestFocus();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public String getOtp() {
        int childCount = getChildCount();
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < childCount; i++) {
            AppCompatEditText child = (AppCompatEditText) getChildAt(i);
            if (child == null) continue;
            sb.append(child.getText().toString());
        }
        return (sb.toString());
    }


}
