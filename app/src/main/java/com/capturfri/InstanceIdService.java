package com.capturfri;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class InstanceIdService extends FirebaseInstanceIdService {

    private Manager manager;
    @Override
    public void onTokenRefresh() {
        manager = Manager.getInstance();
        manager.setInstanceid(FirebaseInstanceId.getInstance().getToken());
    }
}
