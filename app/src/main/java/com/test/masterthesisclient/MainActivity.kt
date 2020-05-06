package com.test.masterthesisclient

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.test.masterthesisclient.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController
    private lateinit var dataBinding : ActivityMainBinding
    private lateinit var drawewToggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(navController.graph,dataBinding.drawerLayout)
        findViewById<Toolbar>(R.id.my_toolbar)
            .setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        setupActionBarWithNavController(navController,appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        dataBinding.navView.setNavigationItemSelectedListener {

            when(it?.itemId)
            {
                R.id.host ->
                {
                    navController.navigate(R.id.action_baseFragment_to_setHostBottomSheet)
                    return@setNavigationItemSelectedListener true
                }

                R.id.actions ->
                {

                }

                R.id.logout ->
                {

                }
            }

            return@setNavigationItemSelectedListener false
        }


    }

    override fun onResume() {
        super.onResume()




//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white);
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                dataBinding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return true
    }

}
