package ritwik.bluetooth.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ritwik.bluetooth.R;
import ritwik.bluetooth.adapters.PairedDeviceAdapter;
import ritwik.bluetooth.models.DeviceInformation;
import ritwik.bluetooth.utilities.AppConstants;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener,
                   PairedDeviceAdapter.DeviceListener {
    private BluetoothAdapter mBluetoothAdapter;
    private List<DeviceInformation> mDeviceList = new ArrayList <> ();
    private PairedDeviceAdapter mAdapter;

    private RecyclerView mRvDevicesList;
    private TextView mTvNoPairedDevices, mTvSearchDevice, mTvDeviceListHeader;
    private ProgressBar mProgressBar;

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver () {
        @Override public void onReceive ( Context context, Intent intent ) {
            String action = intent.getAction ();
            if ( BluetoothDevice.ACTION_FOUND.equals ( action ) ) {
                BluetoothDevice device = intent.getParcelableExtra ( BluetoothDevice.EXTRA_DEVICE );
                // Create a new device item.
                DeviceInformation deviceInformation = new DeviceInformation ();
                deviceInformation.setDeviceName ( device.getName () );
                android.util.Log.e ( "Device Name", device.getName () );
                deviceInformation.setDeviceMACAddress ( device.getAddress () );
                android.util.Log.e ( "Device Address", device.getAddress () );
                mDeviceList.add ( deviceInformation );
                // Add it to our adapter.
                mAdapter.updateDeviceList ( mDeviceList );
            }
        }
    };

    @Override public void onClick ( View view ) {
        switch ( view.getId () ) {
            case R.id.discover_devices :
                if ( getString ( R.string.discover_devices ).equals ( mTvSearchDevice.getText ().toString () ) )
                    registerBluetoothBroadcast ();
                else
                    unregisterBluetoothBroadcast ();
                break;
        }
    }

    @Override protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        initializeViews ();
        requestPermissions ();
    }

    @Override public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
        switch ( requestCode ) {
            case AppConstants.MY_BLUETOOTH_PERMISSIONS :
                if ( grantResults.length > 0 && grantResults[0] == AppConstants.GRANTED )
                    initializeBluetoothAdapter ();
                else
                    ActivityCompat.requestPermissions (
                            this,
                            new String [] { AppConstants.BLUETOOTH, AppConstants.BLUETOOTH_ADMIN,
                           AppConstants.COARSE_LOCATION, AppConstants.FINE_LOCATION },
                            AppConstants.MY_BLUETOOTH_PERMISSIONS
                    );
                break;
        }
    }

    @Override protected void onActivityResult ( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult ( requestCode, resultCode, data );
        if ( requestCode == AppConstants.BLUETOOTH_REQUEST ) {
            if ( resultCode == RESULT_OK )
                initializeBluetoothAdapter ();
        }

    }

    /**
     * Initializes all the views.
     */
    private void initializeViews () {
        mTvNoPairedDevices = (TextView) findViewById ( R.id.text_no_paired_devices );
        mTvSearchDevice = (TextView) findViewById ( R.id.text_search_device );
        mTvDeviceListHeader = (TextView) findViewById ( R.id.text_device_list_header );
        mProgressBar = (ProgressBar) findViewById ( R.id.discovery_progress );
        mRvDevicesList = (RecyclerView) findViewById ( R.id.paired_device_recycler_view );
        RelativeLayout rlDiscoverDevices = (RelativeLayout) findViewById ( R.id.discover_devices
        );
        // Set Layout Manager for Recycler View.
        mRvDevicesList.setLayoutManager ( new LinearLayoutManager ( MainActivity.this ) );
        // Set on-Click Listener for Discover Device Layout.
        rlDiscoverDevices.setOnClickListener ( MainActivity.this );
        // Set Adapter for Recycler View.
        mAdapter = new PairedDeviceAdapter ( MainActivity.this, MainActivity.this, mDeviceList );
    }

    /**
     * Requests User to grant permission for Bluetooth.
     */
    private void requestPermissions () {
        // Check whether device's android version is greater than Marshmallow or not.
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            android.util.Log.e ( "Android Version", ">= Lollipop" );
            // Check whether BLUETOOTH permission is granted or not.
            boolean hasBluetoothPermission =
                    ContextCompat.checkSelfPermission ( getApplicationContext (), AppConstants.BLUETOOTH ) == AppConstants.GRANTED;
            // Check whether BLUETOOTH_ADMIN permission is granted or not.
            boolean hasBluetoothAdminPermission =
                    ContextCompat.checkSelfPermission ( getApplicationContext (), AppConstants.BLUETOOTH_ADMIN ) == AppConstants.GRANTED;

            boolean hasCoarseLocationPermission =
                    ContextCompat.checkSelfPermission ( getApplicationContext (), AppConstants
                            .COARSE_LOCATION ) == AppConstants.GRANTED;

            boolean hasFineLocationPermission =
                    ContextCompat.checkSelfPermission ( getApplicationContext (), AppConstants
                            .FINE_LOCATION ) == AppConstants.GRANTED;
            // Check whether both permissions are granted or not
            boolean hasRequiredPermissions = hasBluetoothPermission && hasBluetoothAdminPermission && hasCoarseLocationPermission && hasFineLocationPermission;
            if ( ! hasRequiredPermissions ) {
                android.util.Log.e ( "Permission", "Denied" );
                if (
                        ActivityCompat.shouldShowRequestPermissionRationale ( this, AppConstants.BLUETOOTH )
                                &&
                        ActivityCompat.shouldShowRequestPermissionRationale ( this, AppConstants.BLUETOOTH_ADMIN )
                                &&
                        ActivityCompat.shouldShowRequestPermissionRationale ( this, AppConstants.COARSE_LOCATION )
                                &&
                        ActivityCompat.shouldShowRequestPermissionRationale ( this, AppConstants.FINE_LOCATION )
                ) {
                    android.util.Log.e ( "Permission", "Bluetooth" );
                }
                else
                    ActivityCompat.requestPermissions (
                            this,
                            new String [] { AppConstants.BLUETOOTH, AppConstants.BLUETOOTH_ADMIN,
                                    AppConstants.COARSE_LOCATION, AppConstants.FINE_LOCATION },
                            AppConstants.MY_BLUETOOTH_PERMISSIONS
                    );
            }
            else {
                android.util.Log.e ( "Permission", "Granted" );
                ActivityCompat.requestPermissions (
                        this,
                        new String [] { AppConstants.BLUETOOTH, AppConstants.BLUETOOTH_ADMIN,
                                AppConstants.COARSE_LOCATION, AppConstants.FINE_LOCATION },
                        AppConstants.MY_BLUETOOTH_PERMISSIONS
                );
            }

        }
    }

    /**
     * Initializes Bluetooth Adapter.
     * Decides whether Device supports Bluetooth or not.
     */
    private void initializeBluetoothAdapter () {
        showToastMessage ( "Hello Bluetooth" );

        // Initialize Bluetooth Adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter ();

        // Check whether device supports Bluetooth or not.
        if ( mBluetoothAdapter == null ) {
            // Device does not support Bluetooth
            new AlertDialog.Builder ( this )
                    .setTitle ( "Not Compatible" )
                    .setMessage ( "Your Phone does not support bluetooth" )
                    .setPositiveButton ( "Exit", new DialogInterface.OnClickListener () {
                        public void onClick ( DialogInterface dialog, int which ) {
                            System.exit(0);
                        }
                    })
                    .setIcon ( android.R.drawable.ic_dialog_alert )
                    .show ();
        } else {
            // Checks whether Bluetooth is active on the device or not.
            if ( ! mBluetoothAdapter.isEnabled () ) requestUserToEnableBluetooth ();
            else continueApp ();
        }
    }

    /**
     * Ask User to enable Bluetooth.
     */
    private void requestUserToEnableBluetooth () {
        Intent bluetoothStartIntent = new Intent ( BluetoothAdapter.ACTION_REQUEST_ENABLE );
        startActivityForResult ( bluetoothStartIntent, AppConstants.BLUETOOTH_REQUEST );
    }

    public void continueApp () {
        gatherPairedDevices ();
    }

    /**
     * Gathers List of Bluetooth Paired Devices.
     */
    private void gatherPairedDevices () {
        // Gathers all the paired devices.
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices ();
        // Adds the List of Devices.
        List<DeviceInformation> deviceList = new ArrayList <> ();
        for ( BluetoothDevice device : pairedDevices ) {
            DeviceInformation deviceInformation = new DeviceInformation ();
            deviceInformation.setDeviceName ( device.getName () );
            deviceInformation.setDeviceMACAddress ( device.getAddress () );
            deviceList.add ( deviceInformation );
        }
        // Checks whether there are Bluetooth Paired Devices or not.
        if ( deviceList.size () != 0 ) {
            mDeviceList = deviceList;
            setPairedDevicesAdapter ();
        }
        else showNoPairedDevices ();
    }

    /**
     * Sets the Adapter for Recycler View that shows Paired Device.
     */
    private void setPairedDevicesAdapter () {
        android.util.Log.e ( "Device List", String.valueOf ( mDeviceList.size () ) );
        mAdapter.updateDeviceList ( mDeviceList );
    }

    /**
     * Show No Paired Device.
     */
    private void showNoPairedDevices () {
        mTvDeviceListHeader.setVisibility ( View.GONE );
        mRvDevicesList.setVisibility ( View.GONE );
        mTvNoPairedDevices.setVisibility ( View.VISIBLE );
    }

    /**
     * Hide No Paired Device.
     */
    private void hideNoPairedDevices () {
        mRvDevicesList.setVisibility ( View.VISIBLE );
        mTvNoPairedDevices.setVisibility ( View.GONE );
    }

    /**
     * Show Toast Message.
     * @param message Message to be displayed.
     */
    private void showToastMessage ( String message ) {
        Toast
                .makeText (
                        MainActivity.this,
                        message,
                        Toast.LENGTH_SHORT
                )
                .show ();
    }

    private void registerBluetoothBroadcast () {
        android.util.Log.e ( "Discovery", "Starts" );
        mTvSearchDevice.setText ( getString ( R.string.stop_search ) );
        mTvDeviceListHeader.setText ( getString ( R.string.search_device ) );
        mTvDeviceListHeader.setVisibility ( View.VISIBLE );
        mProgressBar.setVisibility ( View.VISIBLE );
        IntentFilter filter = new IntentFilter ( BluetoothDevice.ACTION_FOUND );
        registerReceiver ( mBluetoothReceiver, filter );
        mBluetoothAdapter.startDiscovery ();
    }

    private void unregisterBluetoothBroadcast () {
        android.util.Log.e ( "Discovery", "End" );
        mTvSearchDevice.setText ( getString ( R.string.discover_devices ) );
        mTvDeviceListHeader.setText ( getString ( R.string.paired_devices ) );
        mProgressBar.setVisibility ( View.GONE );
        unregisterReceiver ( mBluetoothReceiver );
        mBluetoothAdapter.cancelDiscovery ();
    }

    @Override
    public void onDeviceSelected ( DeviceInformation information ) {

    }
}