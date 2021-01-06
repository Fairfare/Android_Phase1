package com.example.fairfare.ui.drawer.myrides

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.fairfare.R
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.example.fairfare.ui.drawer.myrides.ridedetails.MyRideDetailsActivity
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.ui.ridedetails.RideDetailsActivity
import com.example.fairfare.ui.ridereview.RideReviewActivity
import com.example.fairfare.ui.service.GPSTracker
import com.example.fairfare.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.example.fairfare.utils.ProjectUtilities
import java.io.IOException
import java.util.*

class MyRides : Fragment(), IMyRidesView, MyTripsAdapter.IClickListener {

    private var iMyRidesPresenter: IMyRidesPresenter? = null

    var myTripsAdapter: MyTripsAdapter? = null
    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null
    var CurrentpageCount: Int = 1
    var totalPageCount: Int = 1

    var loading = true

    var mLayoutManager: LinearLayoutManager? = null

    var getCurrentCity: String? = null
    private var myRideList: List<GetRideResponsePOJO.DataItem> = ArrayList()


    @JvmField
    @BindView(R.id.recycler_view_myRides)
    var recycler_view_myRides: RecyclerView? = null


    @JvmField
    @BindView(R.id.ivImg)
    var ivImg: ImageView? = null

    @JvmField
    @BindView(R.id.tvEmptyTxt)
    var tvEmptyTxt: TextView? = null

    @JvmField
    @BindView(R.id.rlEmpty)
    var rlEmpty: RelativeLayout? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_rides, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)


        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        initView()


        iMyRidesPresenter = MyRidesImplementer(this)
        iMyRidesPresenter!!.getRide(token, CurrentpageCount)



        mLayoutManager = LinearLayoutManager(activity)
        recycler_view_myRides!!.setLayoutManager(mLayoutManager)


        recycler_view_myRides!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                loading = true


                if (dy > 0) { //check for scroll down
                    if (loading) {
                        CurrentpageCount = CurrentpageCount + 1
                        loading = false

                        Log.d("onScrolled",dx.toString())

                    }


                }
            }
        })


        return rootView
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initView() {
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_home)
        toolbar.title = "My Rides"


        val gps = GPSTracker(activity!!)
        if (gps.canGetLocation()) {

            val geocoder = Geocoder(activity!!, Locale.getDefault())
            try {
                val addresses =
                    geocoder.getFromLocation(
                        gps!!.latitude!!.toDouble(),
                        gps!!.longitude!!.toDouble(),
                        1
                    )
                if (addresses != null) {
                    val returnedAddress = addresses[0]
                    val strReturnedAddress =
                        StringBuilder()
                    for (j in 0..returnedAddress.maxAddressLineIndex) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(j))
                    }
                    getCurrentCity = returnedAddress.subAdminArea

                }
            } catch (e: IOException) {
            }

        } else {
            gps.showSettingsAlert()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_home_lang, menu!!)
        super.onCreateOptionsMenu(menu!!, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                preferencesManager!!.setStringValue(
                    Constants.SHARED_PREFERENCE_PICKUP_AITPORT,
                    "LOCALITY"
                )
                sharedpreferences!!.edit().clear().commit()
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }


    override fun onResume() {
        super.onResume()
    }

    override fun schduleRideSuccess(scheduleRideResponsePOJO: ScheduleRideResponsePOJO?) {
    }

    override fun validationError(validationResponse: ValidationResponse?) {
        Toast.makeText(
            activity,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun getRidesSuccess(getRideResponsePOJO: GetRideResponsePOJO?) {
        myRideList = getRideResponsePOJO!!.data!!
        CurrentpageCount = getRideResponsePOJO.currentPage
        totalPageCount = getRideResponsePOJO.lastPage

        if (myRideList.size > 0) {
            myTripsAdapter = MyTripsAdapter(activity, myRideList, getCurrentCity)
            recycler_view_myRides!!.layoutManager = LinearLayoutManager(activity)
            recycler_view_myRides!!.adapter = myTripsAdapter
            myTripsAdapter!!.setClickListener(this@MyRides)
            myTripsAdapter!!.notifyDataSetChanged()
        } else {
            rlEmpty!!.visibility = View.VISIBLE
            ivImg!!.setBackgroundResource(R.drawable.empty_ride)
            tvEmptyTxt!!.text = "You have not taken any Rides yet!"

        }


    }

    override fun showWait() {
        ProjectUtilities.showProgressDialog(activity)
    }

    override fun removeWait() {
        ProjectUtilities.dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        Toast.makeText(activity, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    override fun startRide(
        id: Int,
        airport: String?,
        vahicalRadeCardID: Int,
        sLat: String?,
        sLong: String?,
        dLat: String?,
        dLong: String?
    ) {

        val intent = Intent(activity, RideDetailsActivity::class.java)
        intent.putExtra("MyRides_vehicle_rate_card_id", vahicalRadeCardID.toString())
        intent.putExtra("MyRides_airport_ratr_card_id", airport)
        intent.putExtra("MyRides_RideID", id.toString())
        intent.putExtra("MyRidessLat", sLat)
        intent.putExtra("MyRidessLong", sLong)
        intent.putExtra("MyRidesdLat", dLat)
        intent.putExtra("MyRidesdLong", dLong)
        startActivity(intent)


    }

    override fun rateRide(id: Int) {

        val intent = Intent(activity, RideReviewActivity::class.java)
        intent.putExtra("MyRides_id", id.toString())
        intent.putExtra("MyRides", "DrawerMyRides")
        startActivity(intent)

    }

    override fun rideDetails(id: Int) {
        val intent = Intent(activity, MyRideDetailsActivity::class.java)
        intent.putExtra("Id",id.toString())
        startActivity(intent)
    }

}