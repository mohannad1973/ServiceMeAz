package com.gropse.serviceme.activities.user

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.activities.both.SubscriptionPlanActivity
import com.gropse.serviceme.fragment.user.ActivitiesFragment
import com.gropse.serviceme.fragment.user.CurrentServiceUserFragment
import com.gropse.serviceme.fragment.user.HomeUserFragment
import com.gropse.serviceme.fragment.user.ProfileUserFragment
import com.gropse.serviceme.pojo.HomeResult
import com.gropse.serviceme.pojo.PlanResult
import com.gropse.serviceme.utils.toast
import kotlinx.android.synthetic.main.activity_home_user.*


class HomeUserActivity : BaseActivity() {
    private var isEditEnabled = false
    private var backPressed = 0L
    private var bean: HomeResult? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_user)
        setUpToolbar(R.string.home, false)

        changeFragment(0)
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    changeFragment(0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_activity -> {
                    changeFragment(1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_current -> {
                    changeFragment(2)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    changeFragment(3)
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

    fun setPlan(plan: HomeResult?) {
        bean = plan
    }

    private fun changeFragment(position: Int) {
        var newFragment: Fragment? = null
        when (position) {
            0 -> newFragment = HomeUserFragment.newInstance()
            1 -> newFragment = ActivitiesFragment.newInstance()
            2 -> newFragment = CurrentServiceUserFragment.newInstance()
            3 -> newFragment = ProfileUserFragment.newInstance()
        }
        invalidateOptionsMenu()
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, newFragment).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        currentFragment.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val result = super.onPrepareOptionsMenu(menu)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment is ProfileUserFragment) {
            menu.findItem(R.id.action_edit).isVisible = !isEditEnabled
            menu.findItem(R.id.action_save).isVisible = isEditEnabled
        } else {
            menu.findItem(R.id.action_edit).isVisible = false
            menu.findItem(R.id.action_save).isVisible = false
        }
        menu.findItem(R.id.action_plan).isVisible = currentFragment is HomeUserFragment
        return result
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        when (id) {
            R.id.action_plan -> {
                val intent = Intent(this, SubscriptionPlanActivity::class.java)
                if (bean?.plan?.planDuration != 0L)
                    intent.putExtra(PlanResult::class.java.name, bean?.plan)
                startActivityForResult(intent, 111)
            }
            R.id.action_edit -> {
                editEnable(true)
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (currentFragment is ProfileUserFragment) {
                    currentFragment.enableEdit(isEditEnabled)
                }
                return true
            }
            R.id.action_save -> {
//                editEnable(false)
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (currentFragment is ProfileUserFragment) {
                    currentFragment.editProfileUser()
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
