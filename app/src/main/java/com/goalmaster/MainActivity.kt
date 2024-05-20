package com.goalmaster

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.goalmaster.review.ReviewAndPlanFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationsChannels()

        if (intent.action == "ACTION_VIEW_REVIEW") {
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, ReviewAndPlanFragment()).commit();
        }
    }

    private fun createNotificationsChannels() {
        ContextCompat.getSystemService(this, NotificationManager::class.java)
            ?.deleteNotificationChannel("gm_test")
        val channel = NotificationChannel(
            getString(R.string.reminders_notification_channel_id),
            getString(R.string.reminders_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        ContextCompat.getSystemService(this, NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}