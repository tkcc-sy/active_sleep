package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class PermissionRequestModel extends RealmObject {
    public boolean hasRequestPermission;

    public boolean isHasRequestPermission() {
        return hasRequestPermission;
    }

    public void setHasRequestPermission(boolean hasRequestPermission) {
        this.hasRequestPermission = hasRequestPermission;
    }
}
