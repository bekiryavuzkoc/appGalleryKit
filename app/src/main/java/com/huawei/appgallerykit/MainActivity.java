package com.huawei.appgallerykit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.jos.AppUpdateClient;
import com.huawei.hms.jos.JosApps;
import com.huawei.updatesdk.service.appmgr.bean.ApkUpgradeInfo;
import com.huawei.updatesdk.service.otaupdate.CheckUpdateCallBack;
import com.huawei.updatesdk.service.otaupdate.UpdateKey;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUpdate();
    }


    private void checkUpdate(){
        AppUpdateClient client = JosApps.getAppUpdateClient(this);
        client.checkAppUpdate(this, new UpdateCallBack(this));
    }

    private static class UpdateCallBack implements CheckUpdateCallBack {
        private final WeakReference<MainActivity> weakMainActivity;
        private UpdateCallBack(MainActivity mainActivity) {
            this.weakMainActivity = new WeakReference<>(mainActivity);
        }


        public void onUpdateInfo(Intent intent) {
            if (intent != null) {
                MainActivity mainActivity = null;
                if (weakMainActivity.get() != null){
                    mainActivity = weakMainActivity.get();
                }
                int status = intent.getIntExtra(UpdateKey.STATUS, 100);
                int rtnCode = intent.getIntExtra(UpdateKey.FAIL_CODE, 200);
                String rtnMessage = intent.getStringExtra(UpdateKey.FAIL_REASON);
                Serializable info = intent.getSerializableExtra(UpdateKey.INFO);

                if (info instanceof ApkUpgradeInfo && mainActivity != null ) {

                    AppUpdateClient client = JosApps.getAppUpdateClient(mainActivity);
                    //Force Update option is selected as false.
                    client.showUpdateDialog(mainActivity, (ApkUpgradeInfo) info, false);
                    Log.i("AppGalleryKit", "checkUpdatePop success");
                }
                if(mainActivity != null) {
                    //status --> 3: constant value NO_UPGRADE_INFO, indicating that no update is available.
                    Log.i("AppGalleryKit","onUpdateInfo status: " + status + ", rtnCode: "
                            + rtnCode + ", rtnMessage: " + rtnMessage);
                }
            }
        }

        @Override
        public void onMarketInstallInfo(Intent intent) {
            //onMarketInstallInfo
        }

        @Override
        public void onMarketStoreError(int i) {
            //onMarketStoreError
        }

        @Override
        public void onUpdateStoreError(int i) {
            //onUpdateStoreError
        }
    }
}