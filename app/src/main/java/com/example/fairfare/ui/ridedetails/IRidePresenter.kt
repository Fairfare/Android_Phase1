package com.example.fairfare.ui.ridedetails

import java.util.ArrayList

interface IRidePresenter {

    fun startRide(

        token: String?,
        id: String?,
        vehicle_rate_card_id: String?,
        luggage_quantity: String?,
        schedule_date: String?,
        origin_place_id: String?,
        destination_place_id: String?,
        overview_polyline: String?,
        distance: String?,
        duration: String?,
        city_id: String?,
        airport_rate_card_id: String?,
        driver_name: String?,
        vehicle_no: String?,
        badge_no: String?,
        start_meter_reading: String?,
        sLat: String?,
        sLong: String?,
        dLat: String?,
        dLong: String?,
        imageList: ArrayList<ImageModel>?

    )
}