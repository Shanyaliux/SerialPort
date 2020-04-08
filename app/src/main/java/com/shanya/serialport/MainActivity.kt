package com.shanya.serialport

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.shanya.serialport.update.DownloadUtil
import com.shanya.serialport.update.VersionInfo
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.*

const val REQUEST_PERMISSION = 1
const val REQUEST_CONNECT_DEVICE = 2
const val NOT_CONNECT = 3
const val MY_UUID = "00001101-0000-1000-8000-00805F9B34FB" //SPP服务UUID号
private lateinit var mBluetoothDevice:BluetoothDevice
private lateinit var mBluetoothSocket:BluetoothSocket
private lateinit var mBluetoothAdapter:BluetoothAdapter
private lateinit var inputStream: InputStream
private var outputStream: OutputStream ?= null

class MainActivity : AppCompatActivity() {

    private lateinit var infoViewModel: InfoViewModel
    private lateinit var handler: MyHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        infoViewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
        handler = MyHandler(this,infoViewModel)

        //ViewPager
        viewPager.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount() = 3

            override fun createFragment(position: Int)=
                when (position) {
                    0 -> CommunicationFragment()
                    1 -> ControlFragment()
                    else -> CarFragment()
                }
        }

        TabLayoutMediator(tabLayout,viewPager){tab, position ->
            when(position){
                0 -> tab.text = ""
                1 -> tab.text = ""
                else -> tab.text = ""
            }
        }.attach()

        //获取蓝牙适配器实例
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuConnect -> {
                startActivityForResult(Intent(this,SearchActivity::class.java),
                    REQUEST_CONNECT_DEVICE)
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_from_right2)
            }
            R.id.menuCheckUpdate -> {
                Toast.makeText(application,"update",Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }



    private fun connectDevice(data: Intent?){
        val address = data?.extras?.getString(EXTRA_DEVICE_ADDRESS)
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address)

        try {
            mBluetoothSocket = mBluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(
                MY_UUID))
            mBluetoothSocket.connect()
            Toast.makeText(this, "连接" + mBluetoothDevice.name + "成功！", Toast.LENGTH_SHORT)
                .show()
        }catch (e:IOException){
            Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show()
        }

        inputStream = mBluetoothSocket.inputStream
        outputStream = mBluetoothSocket.outputStream

        infoViewModel.ReceiveThread(handler, inputStream).start()

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CONNECT_DEVICE -> {
                if (resultCode == Activity.RESULT_OK){
                    connectDevice(data)
                }
            }
        }
    }
}

class MyHandler(private val context: Context, private val infoViewModel: InfoViewModel): Handler(){
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when(msg.what){
            MSG_RECE_TYPE -> {
                infoViewModel.insert(Info(0, MSG_RECE_TYPE,msg.obj.toString()))
            }
            MSG_SEND_TYPE -> {
                infoViewModel.insert(Info(0, MSG_SEND_TYPE,msg.obj.toString()))
            }
            NOT_CONNECT -> {
                Toast.makeText(context,"未连接蓝牙",Toast.LENGTH_SHORT).show()
            }

        }
    }
}

class SendThread(private val handler: MyHandler,private val info: String): Thread(){
    override fun run() {
        super.run()
        if (outputStream == null){
            val message = Message.obtain()
            message.what = NOT_CONNECT
            handler.sendMessage(message)
        }else{
            val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
            bufferedWriter.write(info)
            bufferedWriter.flush()
            val message = Message.obtain()
            message.what = MSG_SEND_TYPE
            message.obj = info
            handler.sendMessage(message)
        }

    }
}
