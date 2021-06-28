package com.sunzk.colortest

import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.sunzk.colortest.activity.ModeSelectActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
        
        window.decorView
            .postDelayed({ goToTestActivity() }, 2000)
    }

    private fun goToTestActivity() {
        val intent = Intent(this, ModeSelectActivity::class.java)
        startActivity(intent)
        finish()
    }
}