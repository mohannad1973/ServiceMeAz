package com.gropse.serviceme.socaillogin

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import com.gropse.serviceme.R
import com.gropse.serviceme.utils.AppConstants

import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.User
import twitter4j.auth.RequestToken

import com.gropse.serviceme.utils.isNetworkAvailable

class TwitterManager(private val activity: Activity?, private val consumerKey: String, private val consumerSecret: String, private val listener: OnSocialLoginListener) {
    private val twitter: Twitter by lazy {
        val t = TwitterFactory().instance
        t.setOAuthConsumer(consumerKey, consumerSecret)
        t
    }
//    private val progress: ProgressDialog by lazy {
//        val p = ProgressDialog(activity)
//        p.setMessage(activity?.getString(R.string.message_loading))
//        p.setProgressStyle(ProgressDialog.STYLE_SPINNER)
//        p.isIndeterminate = true
//        p
//    }

    //    private var listener: OnSocialLoginListener? = null
    private var oauth_url: String? = null
    private var oauth_verifier: String? = null
    private var auth_dialog: Dialog? = null
    private var web: WebView? = null
    private var requestToken: RequestToken? = null
    //    private var progress: ProgressDialog? = null
    private val callbackUrl = "http://www.gropse.com"

    init {

    }

    fun doLogin() {
        if (activity?.isNetworkAvailable() == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) TokenGetTwitter(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR) else TokenGetTwitter(this).execute()
        } else {
            listener.onSocialError("Please check your internet connection.")
        }

    }

    fun doLogout() {
        android.webkit.CookieManager.getInstance().removeAllCookie()
    }

    //	public ArrayList<ContactBean> getTwitterFollower() {
    //		return twitterFollowersList;
    //	}
    //
    //	public void sendDirectMessageToFollower(ArrayList<Long> followersId) {
    //		new SendDirectMsg().execute(followersId);
    //	}
    //
    //	private class SendDirectMsg extends AsyncTask<ArrayList<Long>, Boolean, Integer> {
    //
    //		@Override
    //		protected Integer doInBackground(ArrayList<Long>... followersId) {
    //			for (long id : followersId[0]) {
    //				try {
    //					twitter.sendDirectMessage(id, userName + " invited you to join Pavi.");
    //					Log.e("" + id, userName + " invited you to join Pavi.");
    //				} catch (TwitterException e) {
    //					e.printStackTrace();
    //					listener.onTwitterError(e.getMessage());
    //				}
    //			}
    //			return null;
    //		}
    //
    //	}
    companion object {
        private class TokenGetTwitter(private val twitterManager: TwitterManager) : AsyncTask<String, String, String>() {

            override fun onPreExecute() {
                super.onPreExecute()
//                twitterManager.progress.show()
            }

            override fun doInBackground(vararg args: String): String? {
                if (twitterManager.requestToken == null) {
                    twitterManager.requestToken = twitterManager.twitter.getOAuthRequestToken(twitterManager.callbackUrl)
                    twitterManager.oauth_url = twitterManager.requestToken?.authorizationURL
                }
                return twitterManager.oauth_url
            }

            override fun onPostExecute(oauth_url: String?) {
//                twitterManager.progress.dismiss()
                if (oauth_url != null) {
                    Log.e("URL", oauth_url)
                    twitterManager.auth_dialog = Dialog(twitterManager.activity)
                    twitterManager.auth_dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    twitterManager.auth_dialog?.setContentView(R.layout.twitter_auth_dialog)
                    twitterManager.web = twitterManager.auth_dialog?.findViewById(R.id.web_view)

                    twitterManager.web?.loadUrl(oauth_url)
                    twitterManager.web?.webViewClient = object : WebViewClient() {
                        internal var authComplete = false

                       override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            if (twitterManager.auth_dialog?.isShowing == false) {
                                twitterManager.web?.stopLoading()
                            } else {
                                super.onPageStarted(view, url, favicon)
                            }
                        }

                        override fun onPageFinished(view: WebView, url: String) {
                            super.onPageFinished(view, url)
                            if (url.startsWith(twitterManager.callbackUrl) && authComplete == false) {
                                authComplete = true
                                Log.e("Url", url)
                                val uri = Uri.parse(url)
                                twitterManager.oauth_verifier = uri.getQueryParameter("oauth_verifier")
                                twitterManager.web?.stopLoading()
                                twitterManager.auth_dialog?.dismiss()
                                AccessTokenGet(twitterManager).execute()
                            } else if (url.contains("denied")) {
                                twitterManager.web?.stopLoading()
                                twitterManager.auth_dialog?.dismiss()
                                twitterManager.listener.onSocialError("Sorry !, Permission Denied")
                            }
                        }
                    }
//                    twitterManager.web!!.webViewClient = object : WebViewClient() {
//                        internal var authComplete = false
//
//                        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
//                            if (twitterManager.auth_dialog?.isShowing == true) {
//                                super.onPageStarted(view, url, favicon)
//                            } else {
//                                twitterManager.web?.stopLoading()
//                            }
//                        }
//
//                        override fun onPageFinished(view: WebView, url: String) {
//                            super.onPageFinished(view, url)
//                            if (url.startsWith(twitterManager.callbackUrl) && authComplete == false) {
//                                authComplete = true
//                                Log.e("Url", url)
//                                val uri = Uri.parse(url)
//                                twitterManager.oauth_verifier = uri.getQueryParameter("oauth_verifier")
//                                twitterManager.web?.stopLoading()
//                                twitterManager.auth_dialog?.dismiss()
//                                AccessTokenGet(twitterManager).execute()
//                            } else if (url.contains("denied")) {
//                                twitterManager.web?.stopLoading()
//                                twitterManager.auth_dialog?.dismiss()
//                                twitterManager.listener.onSocialError("Sorry !, Permission Denied")
//                            }
//                        }
//                    }
                    twitterManager.auth_dialog?.show()
                    twitterManager.auth_dialog?.setCancelable(true)
                } else {
                    twitterManager.listener.onSocialError("Sorry !, Network Error or Invalid Credentials")
                }
            }
        }

        private class AccessTokenGet(private val twitterManager: TwitterManager) : AsyncTask<String, String, Boolean>() {
            internal var user: User? = null

            override fun onPreExecute() {
                super.onPreExecute()
//                twitterManager.progress.show()
            }

            override fun doInBackground(vararg args: String): Boolean? {
                try {
                    val accessToken = twitterManager.twitter.getOAuthAccessToken(twitterManager.requestToken, twitterManager.oauth_verifier)
                    user = twitterManager.twitter.showUser(accessToken.userId)

                } catch (e: TwitterException) {
                    e.printStackTrace()
                    twitterManager.listener.onSocialError(e.message ?: "")
                    return false
                }

                return true
            }

            override fun onPostExecute(response: Boolean?) {
//                twitterManager.progress.dismiss()
                if (response != null && user != null) {
                    val bean = SocialBean()
                    bean.loginType = AppConstants.LOGIN_TYPE_TWITTER
                    bean.id = user?.id.toString()
                    bean.fullName = user?.name ?: ""
                    bean.email = ""
                    bean.password = ""
                    bean.image = user?.biggerProfileImageURL ?: ""
                    twitterManager.listener.onSocialLogin(bean)
                } else {
                    twitterManager.listener.onSocialError("Something goes wrong, please try again.")
                }
            }
        }
    }
}
