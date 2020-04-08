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
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.button_info_dialog.*
import kotlinx.android.synthetic.main.button_info_dialog.view.*
import kotlinx.android.synthetic.main.fragment_control.*

/**
 * A simple [Fragment] subclass.
 */

const val CONTROL_BUTTON_NAME = "control_button_name"
const val CONTROL_BUTTON_DATA = "control_button_data"

class ControlFragment : Fragment() {

    private lateinit var handler: MyHandler
    private lateinit var sharedPreferencesDataSend: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_control, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val infoViewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
        handler = MyHandler(requireContext(),infoViewModel)
        sharedPreferencesDataSend = requireActivity().getSharedPreferences(CONTROL_BUTTON_DATA,Context.MODE_PRIVATE)
        val buttonListener = ButtonListener()

        val sharedPreferences = requireActivity().getSharedPreferences(CONTROL_BUTTON_NAME,Context.MODE_PRIVATE)
        button1.text = sharedPreferences.getString(R.id.button1.toString(),"")
        button2.text = sharedPreferences.getString(R.id.button2.toString(),"")
        button3.text = sharedPreferences.getString(R.id.button3.toString(),"")
        button4.text = sharedPreferences.getString(R.id.button4.toString(),"")
        button5.text = sharedPreferences.getString(R.id.button5.toString(),"")
        button6.text = sharedPreferences.getString(R.id.button6.toString(),"")
        button7.text = sharedPreferences.getString(R.id.button7.toString(),"")
        button8.text = sharedPreferences.getString(R.id.button8.toString(),"")
        button9.text = sharedPreferences.getString(R.id.button9.toString(),"")
        button10.text = sharedPreferences.getString(R.id.button10.toString(),"")
        button11.text = sharedPreferences.getString(R.id.button11.toString(),"")
        button12.text = sharedPreferences.getString(R.id.button12.toString(),"")
        button13.text = sharedPreferences.getString(R.id.button13.toString(),"")
        button14.text = sharedPreferences.getString(R.id.button14.toString(),"")
        button15.text = sharedPreferences.getString(R.id.button15.toString(),"")

        button1.setOnClickListener(buttonListener)
        button2.setOnClickListener(buttonListener)
        button3.setOnClickListener(buttonListener)
        button4.setOnClickListener(buttonListener)
        button5.setOnClickListener(buttonListener)
        button6.setOnClickListener(buttonListener)
        button7.setOnClickListener(buttonListener)
        button8.setOnClickListener(buttonListener)
        button9.setOnClickListener(buttonListener)
        button10.setOnClickListener(buttonListener)
        button11.setOnClickListener(buttonListener)
        button12.setOnClickListener(buttonListener)
        button13.setOnClickListener(buttonListener)
        button14.setOnClickListener(buttonListener)
        button15.setOnClickListener(buttonListener)


    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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

    inner class ButtonListener: View.OnClickListener{
        override fun onClick(v: View?) {
            if (switchControl.isChecked){
                when(v?.id){
                    R.id.button1 -> { createDialog(R.id.button1) }
                    R.id.button2 -> { createDialog(R.id.button2) }
                    R.id.button3 -> { createDialog(R.id.button3) }
                    R.id.button4 -> { createDialog(R.id.button4) }
                    R.id.button5 -> { createDialog(R.id.button5) }
                    R.id.button6 -> { createDialog(R.id.button6) }
                    R.id.button7 -> { createDialog(R.id.button7) }
                    R.id.button8 -> { createDialog(R.id.button8) }
                    R.id.button9 -> { createDialog(R.id.button9) }
                    R.id.button10 -> { createDialog(R.id.button10) }
                    R.id.button11 -> { createDialog(R.id.button11) }
                    R.id.button12 -> { createDialog(R.id.button12) }
                    R.id.button13 -> { createDialog(R.id.button13) }
                    R.id.button14 -> { createDialog(R.id.button14) }
                    R.id.button15 -> { createDialog(R.id.button15) }
                }
            }else{
                when(v?.id){
                    R.id.button1 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button1.toString(),"").toString()).start() }
                    R.id.button2 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button2.toString(),"").toString()).start() }
                    R.id.button3 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button3.toString(),"").toString()).start() }
                    R.id.button4 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button4.toString(),"").toString()).start() }
                    R.id.button5 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button5.toString(),"").toString()).start() }
                    R.id.button6 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button6.toString(),"").toString()).start() }
                    R.id.button7 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button7.toString(),"").toString()).start() }
                    R.id.button8 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button8.toString(),"").toString()).start() }
                    R.id.button9 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button9.toString(),"").toString()).start() }
                    R.id.button10 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button10.toString(),"").toString()).start() }
                    R.id.button11 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button11.toString(),"").toString()).start() }
                    R.id.button12 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button12.toString(),"").toString()).start() }
                    R.id.button13 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button13.toString(),"").toString()).start() }
                    R.id.button14 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button14.toString(),"").toString()).start() }
                    R.id.button15 -> { SendThread(handler,
                        sharedPreferencesDataSend.getString(R.id.button15.toString(),"").toString()).start() }
                }
            }
        }
    }
}


