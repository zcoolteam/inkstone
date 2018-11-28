package com.zcool.inkstone.lang;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FormValidator {

    private FormValidator() {
    }

    public static void bind(final InputView[] inputViews, final SubmitView[] submitViews) {
        checkFormSubmitStatus(inputViews, submitViews);

        if (inputViews != null) {
            for (InputView inputView : inputViews) {
                if (inputView == null) {
                    continue;
                }

                inputView.setOnContentChangedListener(
                        new InputView.OnContentChangedListener() {
                            @Override
                            public void onContentChanged(InputView view) {
                                checkFormSubmitStatus(inputViews, submitViews);
                            }
                        });
            }
        }
    }

    private static void checkFormSubmitStatus(InputView[] inputViews, SubmitView[] submitViews) {
        boolean enable = true;
        if (inputViews != null) {
            for (InputView inputView : inputViews) {
                if (inputView == null) {
                    continue;
                }

                if (!inputView.isContentEnable()) {
                    enable = false;
                    break;
                }
            }
        }

        if (submitViews != null) {
            for (SubmitView view : submitViews) {
                if (view != null) {
                    view.setSubmitEnable(enable);
                }
            }
        }
    }

    public interface InputView {

        interface OnContentChangedListener {
            void onContentChanged(InputView view);
        }

        boolean isContentEnable();

        void setOnContentChangedListener(OnContentChangedListener listener);
    }

    public static class InputViewFactory {

        private InputViewFactory() {
        }

        @Nullable
        public static InputView create(@Nullable CheckBox checkBox) {
            if (checkBox == null) {
                return null;
            }
            return new CheckBoxInputView(checkBox);
        }

        @Nullable
        public static InputView create(@Nullable TextView textView) {
            if (textView == null) {
                return null;
            }
            return new TextViewInputView(textView);
        }

        public abstract static class BaseInputView implements InputView {

            protected OnContentChangedListener mOutListener;

            @Override
            public void setOnContentChangedListener(OnContentChangedListener listener) {
                mOutListener = listener;
            }
        }

        public static class TextViewInputView extends BaseInputView {

            @NonNull
            private final TextView mTextView;

            public TextViewInputView(@NonNull TextView textView) {
                mTextView = textView;
                mTextView.addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(
                                    CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(
                                    CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (mOutListener != null) {
                                    mOutListener.onContentChanged(TextViewInputView.this);
                                }
                            }
                        });
            }

            @Override
            public boolean isContentEnable() {
                return mTextView.getText().length() > 0;
            }
        }

        public static class CheckBoxInputView extends BaseInputView {

            @NonNull
            private final CheckBox mCheckBox;

            public CheckBoxInputView(@NonNull CheckBox checkBox) {
                mCheckBox = checkBox;
                mCheckBox.setOnCheckedChangeListener(
                        new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(
                                    CompoundButton buttonView, boolean isChecked) {
                                if (mOutListener != null) {
                                    mOutListener.onContentChanged(CheckBoxInputView.this);
                                }
                            }
                        });
            }

            @Override
            public boolean isContentEnable() {
                return mCheckBox.isChecked();
            }
        }
    }

    public interface SubmitView {

        void setSubmitEnable(boolean enable);

    }

    public static class SubmitViewFactory {

        private SubmitViewFactory() {
        }

        @Nullable
        public static SubmitView create(@Nullable View view) {
            if (view == null) {
                return null;
            }
            return new SimpleSubmitView(view);
        }

        public static class SimpleSubmitView implements SubmitView {

            @NonNull
            private final View mView;

            public SimpleSubmitView(@NonNull View view) {
                mView = view;
            }

            @Override
            public void setSubmitEnable(boolean enable) {
                if (mView.isEnabled() != enable) {
                    mView.setEnabled(enable);
                }
            }

        }
    }
}
