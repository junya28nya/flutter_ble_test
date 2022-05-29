package com.example.flutter_test03

import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.methodchannel/interop"
    private val METHOD_PLATFORM_test01 = "test01"

    private lateinit var channel: MethodChannel
    private var num = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Flutter", "MainActivity.onCreate() called.")
        print("MainActivity.onCreate() called.")
        super.onCreate(savedInstanceState)
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
                result.success(text)
            }
            }
        super.configureFlutterEngine(flutterEngine)
        }
}
