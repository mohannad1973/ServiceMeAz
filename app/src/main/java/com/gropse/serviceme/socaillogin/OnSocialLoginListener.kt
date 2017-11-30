package com.gropse.serviceme.socaillogin

interface OnSocialLoginListener {

    fun onSocialLogin(bean: SocialBean)

    fun onSocialError(message: String)

}
