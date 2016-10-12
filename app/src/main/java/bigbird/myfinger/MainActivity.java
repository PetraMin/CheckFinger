package bigbird.myfinger;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    private FingerprintManager fingerprintManager;
    private FingerprintManagerCompat fingerprintManagerCompat;//v4包
    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        fingerprintManagerCompat = FingerprintManagerCompat.from(this);
    }

    public void checkFinger(View v) {
        if (canCheckFinger()) {
            Toast.makeText(MainActivity.this, "请进行指纹识别", Toast.LENGTH_SHORT).show();
            startCheckFinger();
        }
    }

    private boolean canCheckFinger() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!fingerprintManagerCompat.isHardwareDetected()) {
            Toast.makeText(this, "没有指纹识别模块", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!keyguardManager.isKeyguardSecure()) {
            Toast.makeText(this, "没有开启锁屏密码", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!fingerprintManagerCompat.hasEnrolledFingerprints()) {
            Toast.makeText(this, "没有录入指纹", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void startCheckFinger() {
        fingerprintManagerCompat.authenticate(null, 0, null, new MyCallBack(), null);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        fingerprintManager.authenticate(null, mCancellationSignal, 0, mSelfCancelled, null);
    }

    CancellationSignal mCancellationSignal = new CancellationSignal();

//    FingerprintManager.AuthenticationCallback mSelfCancelled = new FingerprintManager.AuthenticationCallback() {
//        @Override
//        public void onAuthenticationError(int errorCode, CharSequence errString) {
//            //但多次指纹密码验证错误后，进入此方法；并且，不能短时间内调用指纹验证
//            Toast.makeText(MainActivity.this, errString, Toast.LENGTH_SHORT).show();
//
//        }
//
//        @Override
//        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
//
//            Toast.makeText(MainActivity.this, helpString, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//
//            Toast.makeText(MainActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onAuthenticationFailed() {
//            Toast.makeText(MainActivity.this, "指纹识别失败", Toast.LENGTH_SHORT).show();
//        }
//    };

    public class MyCallBack extends FingerprintManagerCompat.AuthenticationCallback {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            super.onAuthenticationError(errMsgId, errString);
            Toast.makeText(MainActivity.this, "指纹识别失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Toast.makeText(MainActivity.this, "指纹识别失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            super.onAuthenticationHelp(helpMsgId, helpString);
            Toast.makeText(MainActivity.this, helpString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Toast.makeText(MainActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void jumpFingerSetting(View v){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有指纹识别权限", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
        if (intent != null){
            startActivity(intent);
        }
    }
}
