package com.imdumb.app.presentation.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import com.imdumb.app.IMDUMBApplication
import com.imdumb.app.R
import com.imdumb.app.databinding.ActivityHomeBinding
import com.imdumb.app.domain.model.Movie
import com.imdumb.app.domain.model.MovieCategory
import com.imdumb.app.presentation.detail.DetailActivity
import com.imdumb.app.presentation.home.adapter.CategoryAdapter
import javax.inject.Inject

class HomeActivity :
    AppCompatActivity(),
    HomeView {

    @Inject
    lateinit var presenter: HomePresenter

    private lateinit var binding: ActivityHomeBinding
    private val categoryAdapter = CategoryAdapter(::openMovieDetail)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as IMDUMBApplication).appComponent.inject(this)
        presenter.attach(this)

        configureViews()
        presenter.loadCategories()
    }

    private fun configureViews() = with(binding) {
        toolbar.title = intent.getStringExtra(EXTRA_HOME_TITLE)
            .orEmpty()
            .ifBlank { getString(R.string.categories_title) }
        toolbar.subtitle = getString(R.string.environment_label)

        categoriesRecycler.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = categoryAdapter
            setHasFixedSize(true)
            (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        }

        swipeRefresh.setColorSchemeResources(R.color.primary, R.color.secondary)
        swipeRefresh.setOnRefreshListener(presenter::loadCategories)
        retryButton.setOnClickListener { presenter.loadCategories() }
    }

    override fun showLoading(show: Boolean) = with(binding) {
        val hasContent = categoryAdapter.currentList.isNotEmpty()
        progress.visibility = if (show && !hasContent) View.VISIBLE else View.GONE
        if (!show) swipeRefresh.isRefreshing = false
        if (show && !hasContent) stateContainer.visibility = View.GONE
    }

    override fun showCategories(categories: List<MovieCategory>) = with(binding) {
        stateContainer.visibility = View.GONE
        categoriesRecycler.visibility = View.VISIBLE
        categoryAdapter.submitList(categories)
    }

    override fun showError(message: String) = with(binding) {
        val hasContent = categoryAdapter.currentList.isNotEmpty()
        categoriesRecycler.visibility = if (hasContent) View.VISIBLE else View.GONE
        if (hasContent) {
            Snackbar.make(root, message, Snackbar.LENGTH_LONG).show()
        } else {
            stateMessage.text = message
            stateContainer.visibility = View.VISIBLE
            retryButton.visibility = View.VISIBLE
        }
    }

    override fun showEmpty() = with(binding) {
        categoryAdapter.submitList(emptyList())
        categoriesRecycler.visibility = View.GONE
        stateMessage.setText(R.string.empty_movies)
        stateContainer.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
    }

    private fun openMovieDetail(movie: Movie) {
        startActivity(DetailActivity.newIntent(this, movie))
    }

    override fun onDestroy() {
        binding.categoriesRecycler.adapter = null
        presenter.detach()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_HOME_TITLE = "extra_home_title"
    }
}
