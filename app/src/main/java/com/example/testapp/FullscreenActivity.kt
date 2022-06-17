package com.example.testapp

import android.R
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityFullscreenBinding
import com.example.testapp.*


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {
    private val mHideHandler = Handler(Looper.myLooper()!!)
    private var mContentView: View? = null
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView!!.windowInsetsController!!.hide(
                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
            )
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }
    private var mControlsView: View? = null
    private val mShowPart2Runnable = Runnable { // Delayed display of UI elements
        val actionBar = supportActionBar
        actionBar?.show()
        mControlsView!!.visibility = View.VISIBLE
    }
    private var mVisible = false
    private val mHideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {}
        }
        false
    }
    private var binding: ActivityFullscreenBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        mVisible = true
        mControlsView = binding!!.fullscreenContentControls
        mContentView = binding!!.fullscreenContent

        // Set up the user interaction to manually show or hide the system UI.
        (mContentView as TextView).setOnClickListener(View.OnClickListener { toggle() })

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding!!.button.setOnTouchListener(mDelayHideTouchListener)

        rangierer()
    }

    private fun rangierer(){
        loadaufträge()
    }

    private fun loadaufträge(){
        val connection = mssqlconnection()
        var content:String? = null
        var contentlist:List<String>? = null
        var targetlinearlayout:LinearLayout? = null
        var sourcetextview:TextView? = null

        content = connection.selectORupdate("SELECT ID FROM T_ResourceParkingLog WHERE CreatedOn > DATEADD(day, -1, GETDATE())","ID")

        if( content != null) {
            Log.e("PASS RETURNSQL", "#VALUE RETURNED")
            //TODO: try
            var sb = StringBuffer(content)
            sb.delete((sb.length)-5,sb.length)

            contentlist = content.split("|||||")    //splitting resultset from sql-return

            for (line in contentlist){
                Log.e("Zeile",line);
                //TODO:inflater-fun
               /* targetlinearlayout = layoutInflater.inflate(R.layout.activity_fullscreen, null) as LinearLayout
                sourcetextview = layoutInflater.inflate(R.layout.auftraege, null) as TextView
                //TODO: set attributes for sourcetextview

                targetlinearlayout.addView(sourcetextview)
                setContentView(targetlinearlayout)*/
            }
        }
        contentlist = null
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        val actionBar = supportActionBar
        actionBar?.hide()
        mControlsView!!.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView!!.windowInsetsController!!.show(
                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
            )
        } else {
            mContentView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}