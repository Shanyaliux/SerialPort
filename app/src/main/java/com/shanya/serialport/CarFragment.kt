package com.shanya.serialport

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.button_info_dialog.view.*
import kotlinx.android.synthetic.main.fragment_car.*

/**
 * A simple [Fragment] subclass.
 */
class CarFragment : Fragment() {

    private lateinit var handler: MyHandler
    private lateinit var sharedPreferencesDataSend: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_car, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val infoViewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
        handler = MyHandler(requireContext(),infoViewModel)
        sharedPreferencesDataSend = requireActivity().getSharedPreferences(CONTROL_BUTTON_DATA,
            Context.MODE_PRIVATE)

        val buttonListener = ButtonListener()

        val sharedPreferences = requireActivity().getSharedPreferences(CONTROL_BUTTON_NAME,Context.MODE_PRIVATE)

        buttonF1.text = sharedPreferences.getString(R.id.buttonF1.toString(),"")
        buttonF2.text = sharedPreferences.getString(R.id.buttonF2.toString(),"")
        buttonF3.text = sharedPreferences.getString(R.id.buttonF3.toString(),"")
        buttonF4.text = sharedPreferences.getString(R.id.buttonF4.toString(),"")
        buttonF5.text = sharedPreferences.getString(R.id.buttonF5.toString(),"")
        buttonF6.text = sharedPreferences.getString(R.id.buttonF6.toString(),"")
        buttonF7.text = sharedPreferences.getString(R.id.buttonF7.toString(),"")
        buttonF8.text = sharedPreferences.getString(R.id.buttonF8.toString(),"")
        buttonLl.text = sharedPreferences.getString(R.id.buttonLl.toString(),"")
        buttonLt.text = sharedPreferences.getString(R.id.buttonLt.toString(),"")
        buttonLr.text = sharedPreferences.getString(R.id.buttonLr.toString(),"")
        buttonLb.text = sharedPreferences.getString(R.id.buttonLb.toString(),"")
        buttonRl.text = sharedPreferences.getString(R.id.buttonRl.toString(),"")
        buttonRt.text = sharedPreferences.getString(R.id.buttonRt.toString(),"")
        buttonRr.text = sharedPreferences.getString(R.id.buttonRr.toString(),"")
        buttonRb.text = sharedPreferences.getString(R.id.buttonRb.toString(),"")

        buttonF1.setOnClickListener(buttonListener)
        buttonF2.setOnClickListener(buttonListener)
        buttonF3.setOnClickListener(buttonListener)
        buttonF4.setOnClickListener(buttonListener)
        buttonF5.setOnClickListener(buttonListener)
        buttonF6.setOnClickListener(buttonListener)
        buttonF7.setOnClickListener(buttonListener)
        buttonF8.setOnClickListener(buttonListener)
        buttonLl.setOnClickListener(buttonListener)
        buttonLt.setOnClickListener(buttonListener)
        buttonLr.setOnClickListener(buttonListener)
        buttonLb.setOnClickListener(buttonListener)
        buttonRl.setOnClickListener(buttonListener)
        buttonRt.setOnClickListener(buttonListener)
        buttonRr.setOnClickListener(buttonListener)
        buttonRb.setOnClickListener(buttonListener)

    }

    private fun createDialog(buttonId:Int){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val sharedPreferencesName = requireActivity().getSharedPreferences(CONTROL_BUTTON_NAME,Context.MODE_PRIVATE)
        val editorName = sharedPreferencesName.edit()
        val sharedPreferencesData = requireActivity().getSharedPreferences(CONTROL_BUTTON_DATA,Context.MODE_PRIVATE)
        val editorData = sharedPreferencesData.edit()
        val linearLayout: LinearLayout = inflater.inflate(R.layout.button_info_dialog,null) as LinearLayout
        linearLayout.editTextButtonName.text = Editable.Factory.getInstance().newEditable(sharedPreferencesName.getString(buttonId.toString(),""))
        linearLayout.editTextButtonData.text = Editable.Factory.getInstance().newEditable(sharedPreferencesData.getString(buttonId.toString(),""))
        builder.setView(linearLayout)
            .setPositiveButton("Yes"){_,_ ->
                (requireActivity().findViewById<Button>(buttonId)).text = linearLayout.editTextButtonName.text

                editorName.putString(buttonId.toString(),linearLayout.editTextButtonName.text.toString())
                editorName.apply()

                editorData.putString(buttonId.toString(),linearLayout.editTextButtonData.text.toString())
                editorData.apply()
            }
            .setNegativeButton("No"){_,_ ->

            }
        builder.create().show()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    inner class ButtonListener: View.OnClickListener{
        override fun onClick(v: View?) {
            if (switchCar.isChecked){
                when(v?.id){
                    R.id.buttonF1 -> { createDialog(R.id.buttonF1) }
                    R.id.buttonF2 -> { createDialog(R.id.buttonF2) }
                    R.id.buttonF3 -> { createDialog(R.id.buttonF3) }
                    R.id.buttonF4 -> { createDialog(R.id.buttonF4) }
                    R.id.buttonF5 -> { createDialog(R.id.buttonF5) }
                    R.id.buttonF6 -> { createDialog(R.id.buttonF6) }
                    R.id.buttonF7 -> { createDialog(R.id.buttonF7) }
                    R.id.buttonF8 -> { createDialog(R.id.buttonF8) }
                    R.id.buttonLl -> { createDialog(R.id.buttonLl) }
                    R.id.buttonLt -> { createDialog(R.id.buttonLt) }
                    R.id.buttonLr -> { createDialog(R.id.buttonLr) }
                    R.id.buttonLb -> { createDialog(R.id.buttonLb) }
                    R.id.buttonRl -> { createDialog(R.id.buttonRl) }
                    R.id.buttonRt -> { createDialog(R.id.buttonRt) }
                    R.id.buttonRr -> { createDialog(R.id.buttonRr) }
                    R.id.buttonRb -> { createDialog(R.id.buttonRb) }
                }
            }else{
                when(v?.id){
                    R.id.buttonF1 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonF1.toString(),"").toString()).start() }
                    R.id.buttonF2 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonF2.toString(),"").toString()).start() }
                    R.id.buttonF3 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonF3.toString(),"").toString()).start() }
                    R.id.buttonF4 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonF4.toString(),"").toString()).start() }
                    R.id.buttonF5 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonF5.toString(),"").toString()).start() }
                    R.id.buttonF6 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonF6.toString(),"").toString()).start() }
                    R.id.buttonF7 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonF7.toString(),"").toString()).start() }
                    R.id.buttonF8 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonF8.toString(),"").toString()).start() }
                    R.id.buttonLl -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonLl.toString(),"").toString()).start() }
                    R.id.buttonLt -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonLt.toString(),"").toString()).start() }
                    R.id.buttonLr -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonLr.toString(),"").toString()).start() }
                    R.id.buttonLb -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonLb.toString(),"").toString()).start() }
                    R.id.buttonRl -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonRl.toString(),"").toString()).start() }
                    R.id.buttonRt -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonRt.toString(),"").toString()).start() }
                    R.id.buttonRr -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonRr.toString(),"").toString()).start() }
                    R.id.buttonRb -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.buttonRb.toString(),"").toString()).start() }
                }
            }

        }
    }
}
