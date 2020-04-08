package com.shanya.serialport

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Window
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search.*

const val EXTRA_DEVICE_ADDRESS = "device_address"

class SearchActivity : AppCompatActivity() {

    private lateinit var mBtAdapter: BluetoothAdapter
    private lateinit var mNewDevicesArrayAdapter:ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setResult(Activity.RESULT_CANCELED)

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        val pairedDevicesArrayAdapter = ArrayAdapter<String>(this, R.layout.device_name)
        mNewDevicesArrayAdapter = ArrayAdapter(this, R.layout.device_name)

        // Find and set up the ListView for paired devices
        // Find and set up the ListView for paired devices
        val pairedListView =
            findViewById<ListView>(R.id.pairedDeviceRecyclerView)
        pairedListView.adapter = pairedDevicesArrayAdapter
        pairedListView.onItemClickListener = mDeviceClickListener

        // Find and set up the ListView for newly discovered devices
        // Find and set up the ListView for newly discovered devices
        val newDevicesListView =
            findViewById<ListView>(R.id.unpairedDeviceRecyclerView)
        newDevicesListView.adapter = mNewDevicesArrayAdapter
        newDevicesListView.onItemClickListener = mDeviceClickListener

        // Register for broadcasts when a device is discovered
        // Register for broadcasts when a device is discovered
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(mReceiver, filter)

        // Register for broadcasts when discovery has finished
        // Register for broadcasts when discovery has finished
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(mReceiver, filter)

        // Get the local Bluetooth adapter
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter()

        // Get a set of currently paired devices
        // Get a set of currently paired devices
        val pairedDevices = mBtAdapter.bondedDevices

        // If there are paired devices, add each one to the ArrayAdapter
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {
                pairedDevicesArrayAdapter.add(device.name + "\n" + device.address)
            }
        } else {
            val noDevices = "无已配对设备"
            pairedDevicesArrayAdapter.add(noDevices)
        }

        searchButton.setOnClickListener {
            doDiscovery()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Make sure we're not doing discovery anymore
        mBtAdapter.cancelDiscovery()
        // Unregister broadcast listeners
        unregisterReceiver(mReceiver)
    }

    private fun doDiscovery() {
        // Indicate scanning in the title
        setTitle("正在扫描")
        // Turn on sub-title for new devices
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering) {
            mBtAdapter.cancelDiscovery()
        }
        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,R.anim.slide_to_right)
    }

    private val mDeviceClickListener =
        OnItemClickListener { av, v, arg2, arg3 ->
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery()
            // Get the device MAC address, which is the last 17 chars in the View
            val info = (v as TextView).text.toString()
            val address = info.substring(info.length - 17)
            // Create the result Intent and include the MAC address
            val intent = Intent()
            intent.putExtra(
                EXTRA_DEVICE_ADDRESS,
                address
            )
            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND == action) { // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(
                    BluetoothDevice.EXTRA_DEVICE
                )
                // If it's already paired, skip it, because it's been listed already
                if (device != null && device.bondState != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.name + "\n" + device.address)
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                setTitle("选择一个设备去连接")
                if (mNewDevicesArrayAdapter.count == 0) {
                    val noDevices = "未发现设备"
                    mNewDevicesArrayAdapter.add(noDevices)
                }
            }
        }
    }

}
