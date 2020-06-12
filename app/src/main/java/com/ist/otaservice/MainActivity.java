package com.ist.otaservice;

        import android.Manifest;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.widget.Toast;

        import com.ist.httplib.LitepalManager;
        import com.ist.httplib.net.HttpTaskManager;
        import com.ist.otaservice.fragment.BackHandleInterface;
        import com.ist.otaservice.fragment.BaseFragment;
        import com.ist.otaservice.fragment.DownloadFragment;
        import com.ist.otaservice.fragment.MainFragment;
        import com.ist.otaservice.utils.CusFragmentManager;
        import com.ist.otaservice.utils.IstUtil;

public class MainActivity extends AppCompatActivity implements BackHandleInterface {
    private static final String TAG = "MainActivity";

    private BaseFragment mBaseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        checkPermission();
        /**
         * 建议参考 811 代码OtaUpgrade 或者参考 https://github.com/LillteZheng/ZDownLoader 重写一个
         */
        if (intent != null && intent.getBooleanExtra("service", false)) {
            CusFragmentManager.getInstance().config(getSupportFragmentManager(), R.id.content)
                    .addOrShowFragment(DownloadFragment.newInstance("restart"));
        } else {
            CusFragmentManager.getInstance().config(getSupportFragmentManager(), R.id.content)
                    .addOrShowFragment(MainFragment.newInstance());
        }
        initData();


    }


    @Override
    public void onSelectedFragment(BaseFragment backHandleFragment) {
        mBaseFragment = backHandleFragment;
    }

    private void initData() {

        if (IstUtil.isNetworkPositive()) {
            if (CustomerConfig.USE_DB) {
                boolean istExsit = LitepalManager.getInstance().isDbExsits();
                Log.d(TAG, "zyc-> initData isDbExsits: " + istExsit);
                if (istExsit) {
                    CusFragmentManager.getInstance().replaceFragment(DownloadFragment.newInstance("restart"), CusFragmentManager.RIGHT);
                }
            } else {
                if (HttpTaskManager.isDowning) {
                    CusFragmentManager.getInstance().replaceFragment(DownloadFragment.newInstance("restart"), CusFragmentManager.RIGHT);
                }
            }
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        if (mBaseFragment == null || !mBaseFragment.onPressBack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void checkPermission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false;
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            Log.i("Keven","isGranted == "+isGranted);
            if (!isGranted) {
                this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                .ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.REBOOT,
                                Manifest.permission.DELETE_CACHE_FILES
                        },
                        102);
            }
        }

    }
}
