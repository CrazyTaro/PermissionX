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

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

/**
 * Base DialogFragment class to inherits to display a rationale dialog and show user why you need the permissions that you asked.
 * Your DialogFragment must have a positive button to proceed request and an optional negative button to cancel request. Override
 * {@link RationaleDialogFragment#getPositiveButton()} and {@link RationaleDialogFragment#getNegativeButton()} to implement that.
 *
 * @author guolin
 * @since 2020/9/1
 */
public abstract class RationaleDialogFragment extends DialogFragment implements PermissionDialogInterface {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            dismiss();
        }
    }

    /**
     * Return the instance of positive button on the DialogFragment. Your DialogFragment must have a positive button to proceed request.
     *
     * @return The instance of positive button on the DialogFragment.
     */
    abstract public @NonNull
    View getPositiveButton();

    /**
     * Return the instance of negative button on the DialogFragment.
     * If the permissions that you request are mandatory, your DialogFragment can have no negative button.
     * In this case, you can simply return null.
     *
     * @return The instance of positive button on the DialogFragment, or null if your DialogFragment has no negative button.
     */
    abstract public @Nullable
    View getNegativeButton();

    @Override
    public void setPositiveAction(@NotNull PermissionPositiveCallback callback) {
        getPositiveButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPositiveAction(RationaleDialogFragment.this);
            }
        });
    }

    @Override
    public void setNegativeAction(@NotNull PermissionNegativeCallback callback) {
        View view = getNegativeButton();
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onNegativeAction(RationaleDialogFragment.this);
                }
            });
        }
    }

    @Override
    public void setDismissListener(@NotNull PermissionDismissCallback callback) {
        //not support, just ignore
    }

    @Override
    public boolean showDialog() {
        //not support, just ignore
        return false;
    }

    @Override
    public boolean showDialogFragment(@NotNull FragmentManager fm, @NotNull String tag) {
        showNow(fm, tag);
        return true;
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
        //dialog fragment not support so do nothing
    }
}