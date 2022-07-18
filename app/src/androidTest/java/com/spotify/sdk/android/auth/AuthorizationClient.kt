package com.spotify.sdk.android.auth

import android.app.Activity

class AuthorizationClient {

    companion object {
        @JvmStatic
        fun openLoginActivity(activity: Activity, requestCode: Int, request: AuthorizationRequest) =
            Unit
    }
}
