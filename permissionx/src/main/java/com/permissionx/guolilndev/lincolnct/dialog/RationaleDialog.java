/*
 * Copyright (C)  guolin, PermissionX Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.permissionx.guolilndev.lincolnct.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Base Dialog class to inherits to display a rationale dialog and show user why you need the permissions that you asked.
 * Your dialog must have a positive button to proceed request and an optional negative button to cancel request. Override
 * {@link RationaleDialog#getPositiveButton()} and {@link RationaleDialog#getNegativeButton()} to implement that.
 *
 * @author guolin
 * @since 2020/7/6
 */
public abstract class RationaleDialog extends Dialog implements PermissionDialogInterface {
    private DismissListenerDelegate dismissListenerDelegate = null;

    public RationaleDialog(@NonNull Context context) {
        super(context);
    }

    public RationaleDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RationaleDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * Return the instance of positive button on the dialog. Your dialog must have a positive button to proceed request.
     *
     * @return The instance of positive button on the dialog.
     */
    abstract public @NonNull
    View getPositiveButton();

    /**
     * Return the instance of negative button on the dialog.
     * If the permissions that you request are mandatory, your dialog can have no negative button.
     * In this case, you can simply return null.
     *
     * @return The instance of positive button on the dialog, or null if your dialog has no negative button.
     */
    abstract public @Nullable
    View getNegativeButton();

    @Override
    public void setPositiveAction(@NotNull PermissionPositiveCallback callback) {
        getPositiveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPositiveAction(RationaleDialog.this);
            }
        });
    }

    @Override
    public void setNegativeAction(@NotNull PermissionNegativeCallback callback) {
        getNegativeButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onNegativeAction(RationaleDialog.this);
            }
        });
    }

    @Override
    public void setDismissListener(@NotNull PermissionDismissCallback callback) {
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                callback.onDismissAction(RationaleDialog.this);
            }
        });
    }

    @Override
    public boolean showDialog() {
        show();
        return true;
    }

    @Override
    public boolean showDialogFragment(@NotNull FragmentManager fm, @NotNull String tag) {
        //not support, just ignore
        return false;
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }

    @Override
    public void setDialogCancelable(boolean cancelable) {
        setCancelable(cancelable);
    }

    @Override
    public void setDialogCanceledOnTouchOutside(boolean cancelable) {
        setCanceledOnTouchOutside(cancelable);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        if (listener != null) {
            if (dismissListenerDelegate == null) {
                dismissListenerDelegate = new DismissListenerDelegate();
                super.setOnDismissListener(dismissListenerDelegate);
            }
            dismissListenerDelegate.addDismissListener(listener);
        }
    }

    private static class DismissListenerDelegate implements OnDismissListener {
        List<OnDismissListener> listeners;

        void addDismissListener(@NonNull OnDismissListener listener) {
            if (listeners == null) {
                listeners = new ArrayList<>(5);
            }
            listeners.add(listener);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (listeners != null) {
                for (OnDismissListener item : listeners) {
                    item.onDismiss(dialog);
                }
                listeners.clear();
            }
        }
    }
}