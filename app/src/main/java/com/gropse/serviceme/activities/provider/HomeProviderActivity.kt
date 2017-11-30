package com.gropse.serviceme.activities.provider

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem

import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.fragment.BaseFragment
import com.gropse.serviceme.fragment.provider.CurrentServiceProviderFragment
import com.gropse.serviceme.fragment.provider.ProfileProviderFragment
import com.gropse.serviceme.fragment.provider.HomeProviderFragment
import com.gropse.serviceme.utils.toast
import kotlinx.android.synthetic.main.activity_home_provider.*


class HomeProviderActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var isEditEnabled = false
    private var backPressed = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_provider)
        mActivity = this
        setUpToolbar(R.string.home, false)

        changeFragment(0)
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    changeFragment(0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_current -> {
                    changeFragment(1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    changeFragment(2)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

    }

    fun editEnable(isEnable: Boolean) {
        isEditEnabled = isEnable
        invalidateOptionsMenu()
    }

    companion object {
        private val currentPage = 0
    }

    private fun changeFragment(position: Int) {
        var newFragment: Fragment? = null
        when (position) {
            0 -> {
                newFragment = HomeProviderFragment.newInstance()
                (newFragment as BaseFragment).loadScreenData()
            }
            1 -> {
                newFragment = CurrentServiceProviderFragment.newInstance()
            }
            2 -> {
                newFragment = ProfileProviderFragment.newInstance()
            }
        }
        invalidateOptionsMenu()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, newFragment).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        currentFragment.onActivityResult(requestCode, resultCode, data)
//        if (currentFragment is SellerProfileFragment) {
//            (currentFragment as SellerProfileFragment).editProfile()
//        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val result = super.onPrepareOptionsMenu(menu)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment is ProfileProviderFragment) {
            menu.findItem(R.id.action_edit).isVisible = !isEditEnabled
            menu.findItem(R.id.action_save).isVisible = isEditEnabled
        } else {
            menu.findItem(R.id.action_edit).isVisible = false
            menu.findItem(R.id.action_save).isVisible = false
        }
        return result
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        when (id) {
            R.id.action_edit -> {
                editEnable(true)
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (currentFragment is ProfileProviderFragment) {
                    currentFragment.enableEdit(isEditEnabled)
                }
                return true
            }
            R.id.action_save -> {
//                editEnable(false)
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (currentFragment is ProfileProviderFragment) {
                    currentFragment.editProfileProvider()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            backPressed = System.currentTimeMillis()
           toast(R.string.press_once_again_to_exit)
        }
    }
}


