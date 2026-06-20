package com.imdumb.app.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.imdumb.app.IMDUMBApplication
import com.imdumb.app.databinding.ActivitySplashBinding
import com.imdumb.app.presentation.home.HomeActivity
import javax.inject.Inject

class SplashActivity :
    AppCompatActivity(),
    SplashView {

    @Inject
    lateinit var presenter: SplashPresenter

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as IMDUMBApplication).appComponent.inject(this)
        presenter.attach(this)
        presenter.start()
    }

    override fun showLoading(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showStatus(message: String) {
        binding.splashStatus.text = message
    }

    override fun openHome(homeTitle: String) {
        if (isFinishing || isDestroyed) return
        startActivity(
            Intent(this, HomeActivity::class.java)
                .putExtra(HomeActivity.EXTRA_HOME_TITLE, homeTitle)
        )
        finish()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }
}
