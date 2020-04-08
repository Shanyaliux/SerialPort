package com.shanya.serialport

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_communication.*
import kotlinx.android.synthetic.main.fragment_communication.view.*

/**
 * A simple [Fragment] subclass.
 */
class CommunicationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_communication, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val infoViewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
        val handler = MyHandler(requireContext(),infoViewModel)
        val infoAdapter = InfoAdapter(requireActivity())
        infoRecyclerView.apply {
            adapter = infoAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }

        infoViewModel.allInfo.observe(requireActivity(), Observer {
            infoAdapter.setInfo(it)
            infoRecyclerView.scrollToPosition(it.size - 1)
        })


        buttonSend.setOnClickListener {
            SendThread(handler,editTextSend.text.toString()).start()
        }

    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

}

class InfoAdapter internal constructor(context: Context): RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var infos = emptyList<Info>()

    inner class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveLayout = itemView.findViewById(R.id.receiveLayout) as LinearLayout
        val sendLayout = itemView.findViewById(R.id.sendLayout) as LinearLayout

        val receiveType = itemView.findViewById(R.id.textViewReceiveType) as TextView
        val receiveTextView = itemView.findViewById(R.id.textViewReceice) as TextView
        val sendType = itemView.findViewById(R.id.textViewSendType) as TextView
        val sendTextView = itemView.findViewById(R.id.textViewSend) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val itemView = inflater.inflate(R.layout.info_cell,parent,false)
        return InfoViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return infos.size
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val current = infos[position]
        if (current.type == MSG_RECE_TYPE){
            holder.receiveLayout.visibility = View.VISIBLE
            holder.sendLayout.visibility = View.GONE
            holder.receiveTextView.text = current.content
        }else if(current.type == MSG_SEND_TYPE){
            holder.receiveLayout.visibility = View.GONE
            holder.sendLayout.visibility = View.VISIBLE
            holder.sendTextView.text = current.content
        }
    }

    internal fun setInfo(infos: List<Info>){
        this.infos = infos
        notifyDataSetChanged()
    }
}
