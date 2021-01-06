package com.example.fairfare.ui.Login.privacypolicy

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.example.fairfare.R
import com.example.fairfare.networking.ApiClient
import com.example.fairfare.ui.Login.pojo.ValidationResponse
import com.example.fairfare.ui.drawer.privacypolicy.ContentResponsePOJO
import com.example.fairfare.ui.home.HomeActivity
import com.example.fairfare.utils.Constants
import com.example.fairfare.utils.PreferencesManager
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PrivacyPolicy : Fragment() {

    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null
    @JvmField
    @BindView(R.id.webView)
    var webView: WebView? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_content_page, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        initView()
        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        callApi()

        return rootView
    }

    private fun callApi() {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog


        val call = ApiClient.client.pageContents( "Privacy-Policy")
        call!!.enqueue(object : Callback<ContentResponsePOJO?> {
            override fun onResponse(
                call: Call<ContentResponsePOJO?>,
                response: Response<ContentResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    if (response.body() != null) {
                        webView!!.settings.javaScriptEnabled = true
                        webView!!.loadDataWithBaseURL(
                            null,
                            response.body()!!.pageContent!!.content,
                            "text/html",
                            "utf-8",
                            null
                        )

                    }
                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(activity, pojo.message, Toast.LENGTH_LONG).show()


                    } catch (exception: IOException) {
                    }

                }
            }

            override fun onFailure(
                call: Call<ContentResponsePOJO?>, t: Throwable
            ) {
                progressDialog.dismiss()
                Toast.makeText(activity, t.stackTrace.toString(), Toast.LENGTH_LONG).show()
                Log.d("response", t.stackTrace.toString())
            }
        })
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



    private fun initView() {
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_login)
        toolbar.title = "Privacy Policy"
        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

    }


}