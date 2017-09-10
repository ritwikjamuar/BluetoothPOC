package ritwik.bluetooth.utilities;

import android.Manifest;
import android.content.pm.PackageManager;

public class AppConstants {
    public static final int MY_BLUETOOTH_PERMISSIONS = 101;
    public static final String BLUETOOTH = Manifest.permission.BLUETOOTH;
    public static final String BLUETOOTH_ADMIN = Manifest.permission.BLUETOOTH_ADMIN;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final int GRANTED = PackageManager.PERMISSION_GRANTED;
    public static final int BLUETOOTH_REQUEST = 111;
}