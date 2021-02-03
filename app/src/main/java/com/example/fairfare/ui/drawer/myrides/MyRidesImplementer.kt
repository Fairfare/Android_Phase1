package com.example.fairfare.ui.drawer.myrides

import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MyRidesImplementer(private val view: IMyRidesView) : IMyRidesPresenter {

    override fun getRide(token: String?,cnt:Int?,currentLat: String?,currentLong: String?) {


        view.showWait()
        val call = ApiClient.client.getMyRides(
            "Bearer $token",cnt.toString(),currentLat,currentLong)
        call!!.enqueue(object : Callback<GetRideResponsePOJO?> {
            override fun onResponse(
                call: Call<GetRideResponsePOJO?>,
                response: Response<GetRideResponsePOJO?>
            ) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        view.removeWait()
                        view.getRidesSuccess(response.body())
                    }
                } else if (response.code() == 422) {
                    view.removeWait()
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        view.validationError(pojo)
                    } catch (exception: IOException) {
                    }

                }
            }

            override fun onFailure(
                call: Call<GetRideResponsePOJO?>,
                t: Throwable
            ) {
                view.removeWait()
                view.onFailure(t.message)
            }
        })
    }


}