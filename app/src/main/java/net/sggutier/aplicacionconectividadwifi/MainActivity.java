package net.sggutier.aplicacionconectividadwifi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

// NOTA GENERAL: Desde Android 10 ya no es se puede activar/desactivar el wifi desde una app
// ver enlace: https://developer.android.com/reference/android/net/wifi/WifiManager#setWifiEnabled(boolean)
// OTRA NOT: Tampoco es posible ya comenzar un escaneo manualmente
// ver enlace https://developer.android.com/reference/android/net/wifi/WifiManager#startScan()
public class MainActivity extends AppCompatActivity {
    private static String TAG = "ActividadPrincipal";
    private static int REQUEST_PERMISSIONS_REQUEST_CODE = 7531;

    private FloatingActionButton mBotonPedirPermiso;
    private ListaRedesFragment mRedesFragment;
    private FragmentManager fm;
    private Fragment fragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);
        mBotonPedirPermiso = findViewById(R.id.boton_escanear_wifi);

        mBotonPedirPermiso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentaCargarFragmento();
            }
        });

        intentaCargarFragmento();
    }

    private void intentaCargarFragmento() {
        fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            if(!checkPermissions()) {
                requestPermissions();
            }
            else {
                fragment = new ListaRedesFragment();
                fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
            }
        }
    }

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Mostrando información acerca de los permisos y por qué.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Pidiendo permiso");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}
