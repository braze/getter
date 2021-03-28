package com.example.getter.flip

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.getter.R
import com.example.getter.flip.api.FlipRetriever
import kotlinx.android.synthetic.main.activity_flip.*
import kotlinx.coroutines.*


class FlipActivity : AppCompatActivity() {

    private lateinit var mTextView: TextView
    private lateinit var mFlipImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch to AppTheme for displaying the activity
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip)

        setSupportActionBar(toolbar)
        mTextView = findViewById(R.id.flip_tv)
        mFlipImageView = findViewById(R.id.flip_iv)
        mTextView.text = getString(R.string.flip_progress_bar_msg)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (isNetworkConnected()) {
            retrieve()
        } else {
            AlertDialog.Builder(this).setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setPositiveButton(R.string.ok) { _, _ -> }
                .setIcon(R.drawable.ic_report_problem).show()
        }

        // flip button functionality
        flip_btn.setOnClickListener {
            mTextView.setText(getString(R.string.flip_progress_bar_msg))
            if (isNetworkConnected()) {
                retrieve()
            } else {
                AlertDialog.Builder(this).setTitle("No Internet Connection")
                    .setMessage("Please check your internet connection and try again")
                    .setPositiveButton(R.string.ok) { _, _ -> }
                    .setIcon(R.drawable.ic_report_problem).show()
            }
        }

        // toolBar back button implementation
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun retrieve() {
        // Create a Coroutine scope using a job to be able to cancel when needed
        val flipActivityJob = Job()

        // Handle exceptions if any
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            AlertDialog.Builder(this).setTitle("Error")
                .setMessage(exception.message)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setIcon(android.R.drawable.ic_dialog_alert).show()
        }

        // the Coroutine runs using the Main (UI) dispatcher
        val coroutineScope = CoroutineScope(flipActivityJob + Dispatchers.Main)
        coroutineScope.launch(errorHandler) {
            val result = FlipRetriever().getFlip()
            // set text
            mTextView.text = result.answer?.toUpperCase()

            //set image to ImageView
            Glide.with(mFlipImageView.context).load(result.image).into(mFlipImageView)
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


}