package com.example.flutter_test03

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.methodchannel/interop"
    private val METHOD_PLATFORM_test01 = "test01"

    private lateinit var channel: MethodChannel
    private var num = 0;

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()
    val isBleScanPermissionGranted
        get() = hasPermission(Manifest.permission.BLUETOOTH_SCAN)
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("test01","scanCallback.onScanResult() called.")
            with(result.device) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("test01", "Bluetooth_Connect not granted.")
                    return
                }
                Log.i("test01", "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
            }
        }
    }

    fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }
    fun requestBlePermission(){
        if (isBleScanPermissionGranted) {
            return
        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT), ENABLE_BLUETOOTH_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("test01", "onRequestPermissionResult called.")
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    Log.d("test01", "onRequestPermissionResult called. Permission not granted.")
                    requestBlePermission()
                } else {
                    Log.d("test01", "onRequestPermissionResult called. Permission granted.")
                    startBleScan()
                }
            }
        }
    }
    fun startBleScan(){
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) {
        Log.d("test01", "startBleScan called.")

        if(!isBleScanPermissionGranted){
            requestBlePermission()
        }
        else {
            Log.d("test01", "start BLE Scan")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("test01", "Bluetooth_Scan not granted.")
                return
            }
            bleScanner.startScan(null,scanSettings,scanCallback)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Flutter", "MainActivity.onCreate() called.")
        print("MainActivity.onCreate() called.")
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        requestBlePermission()
    }




    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        Log.d("Flutter", "MainActivity.configureFlutterEngine() called.")
        print("MainActivity.configureFlutterEngine() called.")

        channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
        channel.setMethodCallHandler { methodCall: MethodCall, result: MethodChannel.Result ->
            if (methodCall.method == METHOD_PLATFORM_test01) {
                Log.d("Flutter", "MainActivity test01 called.")
                num = num + 1;
                var text : String = "Android : $num"
                startBleScan()


                channel.invokeMethod("callMe", listOf("a", "b"), object : MethodChannel.Result {
                    override fun success(result: Any?) {
                        Log.d("Android", "result = $result")
                    }
                    override fun error(errorCode: String?, errorMessage: String?, errorDetails: Any?) {
                        Log.d("Android", "$errorCode, $errorMessage, $errorDetails")
                    }
                    override fun notImplemented() {
                        Log.d("Android", "notImplemented")
                    }
                })


                result.success(text)
            }
            }
        super.configureFlutterEngine(flutterEngine)
        }
}
