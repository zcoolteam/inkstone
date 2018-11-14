package com.zcool.inkstone.lang;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zcool.inkstone.R;
import com.zcool.inkstone.ext.backstack.dialog.ViewDialog;
import com.zcool.inkstone.util.ViewUtil;

public class DialogHelper {

    private DialogHelper() {
    }

    public static ViewDialog createPermissionConfirmDialog(Activity activity, ViewGroup parentView,
                                                           String title, String msg,
                                                           boolean cancelable, ViewDialog.OnHideListener hideListener,
                                                           View.OnClickListener okListener) {
        ViewDialog viewDialog = new ViewDialog.Builder(activity)
                .setContentView(R.layout.inkstone_dialog_helper_permission_confirm)
                .setParentView(parentView)
                .setCancelable(cancelable)
                .setOnHideListener(hideListener)
                .defaultAnimator()
                .create();

        TextView titleView = viewDialog.findViewById(R.id.title);
        if (titleView != null) {
            titleView.setText(title);
        }
        TextView messageView = viewDialog.findViewById(R.id.message);
        if (messageView != null) {
            messageView.setText(msg);
        }

        ViewUtil.onClick(viewDialog.getContentView(), null);
        ViewUtil.onClick(viewDialog.findViewById(R.id.action_ok), v -> {
            viewDialog.hide(false);
            if (okListener != null) {
                okListener.onClick(v);
            }
        });
        ViewUtil.onClick(viewDialog.findViewById(R.id.action_cancel), v -> viewDialog.hide(true));
        return viewDialog;
    }

}
