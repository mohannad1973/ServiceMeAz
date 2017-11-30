package com.gropse.serviceme.activities.both

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.gropse.serviceme.R
import com.gropse.serviceme.utils.Prefs
import kotlinx.android.synthetic.main.activity_service_me_intro.*

class ServiceMeIntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_me_intro)

        btnContinue.setOnClickListener {
            Prefs(this).isFirstTimeLaunch = false
            startActivity(Intent(this, OptionActivity::class.java))
            finish()
        }
    }
}
