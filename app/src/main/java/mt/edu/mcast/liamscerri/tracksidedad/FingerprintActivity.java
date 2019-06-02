package mt.edu.mcast.liamscerri.tracksidedad;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.security.Key;

public class FingerprintActivity extends AppCompatActivity {

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if(fingerprintManager.isHardwareDetected()){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED){
                    if (keyguardManager.isKeyguardSecure()){
                        if (fingerprintManager.hasEnrolledFingerprints()){
                            FingerprintHandler fingerprintHandler = new FingerprintHandler(this);
                            fingerprintHandler.startAuth(fingerprintManager, null);
                        }
                        else Log.d("FingerprintActivity", "Fail 5");
                    }
                    else Log.d("FingerprintActivity", "Fail 4");
                }
                else Log.d("FingerprintActivity", "Fail 3");
            }
            else Log.d("FingerprintActivity", "Fail 2");
        }
        else Log.d("FingerprintActivity", "Fail 1");

    }

}
