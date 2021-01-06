package com.example.fairfare.ui.drawer.mydisput

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fairfare.R
import com.example.fairfare.ui.drawer.mydisput.pojo.GetDisputResponsePOJO
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MyDisPutesAdapter(var context: FragmentActivity?, private val MyDisputsListList: List<GetDisputResponsePOJO.DataItem>) : RecyclerView.Adapter<MyDisPutesAdapter.MyViewHolder>() { var streetAddress: String? = null
    var deststreetAddress: String? = null
    private var iDisputClick: IDisputClickListener? = null


    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MyDisPutesAdapter.MyViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.templetes_my_disputes, viewGroup, false)
        return MyViewHolder(v)

    }

    override fun getItemCount(): Int {
        return MyDisputsListList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tv_status!!.text = "₹ "+MyDisputsListList[position].actualMeterCharges
        holder.tvDisputNo!!.text = "ID: "+MyDisputsListList[position].disputeNo
        Glide.with(context!!)
            .load(MyDisputsListList[position].vehicleImageUrl)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
            ).into(holder.iv_vehical!!)



        val formatviewRide = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")


        val formatRide = SimpleDateFormat("dd MMM, hh:mm a")
        val formaredDate = formatRide.format(formatviewRide.parse((MyDisputsListList[position].dateTime).toString()))
        val strformaredDate = formaredDate.replace("am", "AM").replace("pm", "PM")



        holder.tv_dateandTime!!.text = strformaredDate
        holder.tv_vahicalName!!.text =
            MyDisputsListList[position].vehicleName + " " + MyDisputsListList[position].vehicleNo


        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses =
                geocoder.getFromLocation(
                    (MyDisputsListList[position].originPlaceLat)!!.toDouble(),
                    (MyDisputsListList[position].originPlaceLong)!!.toDouble(), 1
                )
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress =
                    StringBuilder()
                for (j in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j))
                }
                streetAddress = strReturnedAddress.toString()
            }
        } catch (e: IOException) {
        }


        val geocoderDestination = Geocoder(context, Locale.getDefault())
        try {
            val addresses =
                geocoderDestination.getFromLocation(
                    (MyDisputsListList[position].destinationPlaceLat)!!.toDouble(),
                    (MyDisputsListList[position].destinationPlaceLong)!!.toDouble(), 1
                )
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress =
                    StringBuilder()
                for (j in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(j))
                }
                deststreetAddress = strReturnedAddress.toString()
            }
        } catch (e: IOException) {
        }


        holder.tv_myCurrentLocation!!.text = streetAddress
        holder.destnationAddress!!.text = deststreetAddress


    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        @JvmField
        @BindView(R.id.tv_dateandTime)
        var tv_dateandTime: TextView? = null

        @JvmField
        @BindView(R.id.tv_vahicalName)
        var tv_vahicalName: TextView? = null

        @JvmField
        @BindView(R.id.tv_myCurrentLocation)
        var tv_myCurrentLocation: TextView? = null

        @JvmField
        @BindView(R.id.destnationAddress)
        var destnationAddress: TextView? = null

         @JvmField
        @BindView(R.id.tv_status)
        var tv_status: TextView? = null

         @JvmField
        @BindView(R.id.tvDisputNo)
        var tvDisputNo: TextView? = null

        @JvmField
        @BindView(R.id.tv_fileCompllaint)
        var tv_fileCompllaint: TextView? = null

        @JvmField
        @BindView(R.id.tv_deleteDisput)
        var tv_deleteDisput: TextView? = null

        @JvmField
        @BindView(R.id.rlVehicalDetails)
        var rlVehicalDetails: LinearLayout? = null


        @JvmField
        @BindView(R.id.iv_vehical)
        var iv_vehical: ImageView? = null

        init {
            ButterKnife.bind(this, itemView)
            tv_fileCompllaint!!.setOnClickListener(this)
            tv_deleteDisput!!.setOnClickListener(this)
            rlVehicalDetails!!.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v!!.id == R.id.tv_fileCompllaint) {
                iDisputClick!!.fileComplaintClick(MyDisputsListList[adapterPosition].id)
            } else if (v!!.id == R.id.tv_deleteDisput) {
                iDisputClick!!.deleteDisputClick(MyDisputsListList[adapterPosition].id)
            }else{
                iDisputClick!!.detailDisputClick(MyDisputsListList[adapterPosition].id)
            }

        }
    }


    fun setClickListener(iDisputClick: IDisputClickListener?) {
        this.iDisputClick = iDisputClick
    }

    interface IDisputClickListener {
        fun fileComplaintClick(id: Int)
        fun deleteDisputClick(id: Int)
        fun detailDisputClick(id: Int)
    }

}



