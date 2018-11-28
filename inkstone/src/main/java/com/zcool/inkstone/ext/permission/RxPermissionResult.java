package com.zcool.inkstone.ext.permission;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;

public class RxPermissionResult {

    @NonNull
    public final Set<String> permissions = new HashSet<>();

    @NonNull
    public final Set<String> grantedPermissions = new HashSet<>();

    @NonNull
    public final Set<String> defaultDinedPermissions = new HashSet<>();

    @NonNull
    public final Set<String> opsDinedPermissions = new HashSet<>();

    @NonNull
    public final Set<String> needShowRationalePermissions = new HashSet<>();

    public RxPermissionResult(String[] permissions) {
        Collections.addAll(this.permissions, permissions);
    }

    public RxPermissionResult(Collection<String> permissions) {
        this.permissions.addAll(permissions);
    }

    public void addGrantedPermission(String permission) {
        this.grantedPermissions.add(permission);
    }

    public void addDefaultDeniedPermission(String permission) {
        this.defaultDinedPermissions.add(permission);
    }

    public void addOpsDeniedPermission(String permission) {
        this.opsDinedPermissions.add(permission);
    }

    public void addNeedShowRationalePermission(String permission) {
        this.needShowRationalePermissions.add(permission);
    }

    public boolean isAllGranted() {
        return this.grantedPermissions.size() == this.permissions.size();
    }

    public boolean hasAnyRationalePermission() {
        return !this.needShowRationalePermissions.isEmpty();
    }

    public RxPermissionResult create() {
        return new RxPermissionResult(this.permissions);
    }

    public String[] getOriginalPermissions() {
        return this.permissions.toArray(new String[this.permissions.size()]);
    }

}
