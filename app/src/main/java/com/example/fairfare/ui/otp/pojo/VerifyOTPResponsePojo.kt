package com.example.fairfare.ui.otp.pojo

import com.google.gson.annotations.SerializedName

class VerifyOTPResponsePojo {
    @SerializedName("message")
    var message: String? = null

    @SerializedName("user")
    var user: User? = null

    @SerializedName("token")
    var token: String? = null

    override fun toString(): String {
        return "VResponse{" +
                "message = '" + message + '\'' +
                ",user = '" + user + '\'' +
                ",token = '" + token + '\'' +
                "}"
    }

    inner class User {
        @SerializedName("phone_no")
        var phoneNo: String? = null

        @SerializedName("gender")
        var gender: String? = null

        @SerializedName("country_phone_code")
        var countryPhoneCode: String? = null

        @SerializedName("name")
        var name: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("email")
        var email: String? = null

        override fun toString(): String {
            return "User{" +
                    "phone_no = '" + phoneNo + '\'' +
                    ",gender = '" + gender + '\'' +
                    ",country_phone_code = '" + countryPhoneCode + '\'' +
                    ",name = '" + name + '\'' +
                    ",id = '" + id + '\'' +
                    ",email = '" + email + '\'' +
                    "}"
        }
    }
}