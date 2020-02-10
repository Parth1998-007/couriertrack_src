package com.couriertrack.utils;

import android.graphics.Rect;
import android.text.method.TransformationMethod;
import android.view.View;

public class PassWordMask implements TransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence charSequence, View view) {
        return new PasswordCharSequence(charSequence);
    }

    @Override
    public void onFocusChanged(View view, CharSequence charSequence, boolean b, int i, Rect rect) {

    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;
        public PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }
        public char charAt(int index) {
            return '*'; // This is the important part
        }
        public int length() {
            return mSource.length(); // Return default
        }
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }

}
