package com.imdumb.app.presentation.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.imdumb.app.IMDUMBApplication
import com.imdumb.app.R
import com.imdumb.app.databinding.ActivityDetailBinding
import com.imdumb.app.domain.model.Movie
import com.imdumb.app.domain.model.MovieDetail
import com.imdumb.app.presentation.common.HtmlFormatter
import com.imdumb.app.presentation.detail.adapter.ActorAdapter
import com.imdumb.app.presentation.detail.adapter.ImagePagerAdapter
import javax.inject.Inject

class DetailActivity :
    AppCompatActivity(),
    DetailView {

    @Inject
    lateinit var presenter: DetailPresenter

    private lateinit var binding: ActivityDetailBinding
    private lateinit var movie: Movie
    private lateinit var imageAdapter: ImagePagerAdapter
    private val actorAdapter = ActorAdapter()
    private var presenterAttached = false

    private val pageCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updatePageIndicator(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movie = readMovie(intent) ?: run {
            finish()
            return
        }

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as IMDUMBApplication).appComponent.inject(this)
        presenter.attach(this)
        presenterAttached = true

        configureViews()
        bindBasicMovie()
        presenter.load(movie)
    }

    private fun configureViews() = with(binding) {
        toolbar.title = getString(R.string.detail_title)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.navigationContentDescription = getString(R.string.back)
        toolbar.setNavigationOnClickListener { finish() }

        imageAdapter = ImagePagerAdapter(movie.title)
        imagePager.adapter = imageAdapter
        imagePager.offscreenPageLimit = 1
        imagePager.setPageTransformer(
            MarginPageTransformer(resources.getDimensionPixelSize(R.dimen.space_s))
        )
        imagePager.registerOnPageChangeCallback(pageCallback)

        actorsRecycler.apply {
            layoutManager = LinearLayoutManager(
                this@DetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = actorAdapter
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }

        supportFragmentManager.setFragmentResultListener(
            RecommendationBottomSheet.RESULT_KEY,
            this@DetailActivity
        ) { _, result ->
            val comment = result.getString(RecommendationBottomSheet.RESULT_COMMENT).orEmpty()
            presenter.recommend(movie, comment)
        }

        recommendButton.setOnClickListener {
            val fragmentManager = supportFragmentManager
            if (fragmentManager.findFragmentByTag(RecommendationBottomSheet.TAG) != null) {
                return@setOnClickListener
            }
            RecommendationBottomSheet.newInstance(
                title = movie.title,
                summaryHtml = movie.summaryHtml
            ).show(fragmentManager, RecommendationBottomSheet.TAG)
        }
    }

    private fun bindBasicMovie() = with(binding) {
        movieTitle.text = movie.title
        movieRating.text = movie.rating?.let {
            getString(R.string.rating_format, it)
        } ?: getString(R.string.rating_unavailable)
        movieSummary.text = HtmlFormatter.fromHtml(movie.summaryHtml)
        val initialImages = movie.posterUrl?.let(::listOf).orEmpty().ifEmpty { listOf("") }
        imageAdapter.submitList(initialImages) {
            updatePageIndicator(imagePager.currentItem)
        }
    }

    override fun showLoading(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showDetail(detail: MovieDetail) = with(binding) {
        val images = detail.imageUrls.ifEmpty { listOf("") }
        imageAdapter.submitList(images) {
            imagePager.setCurrentItem(0, false)
            updatePageIndicator(0)
        }

        actorAdapter.submitList(detail.actors)
        actorsEmpty.visibility = if (detail.actors.isEmpty()) View.VISIBLE else View.GONE
        actorsRecycler.visibility = if (detail.actors.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun setRecommendationEnabled(enabled: Boolean) {
        binding.recommendButton.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    override fun showRecommendationSuccess() {
        Snackbar.make(
            binding.root,
            R.string.recommendation_success,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun updatePageIndicator(position: Int) {
        if (!::binding.isInitialized || !::imageAdapter.isInitialized) return
        val count = imageAdapter.itemCount
        binding.pageIndicator.text = if (count == 0) {
            getString(R.string.page_format, 0, 0)
        } else {
            getString(R.string.page_format, position + 1, count)
        }
    }

    override fun onDestroy() {
        if (::binding.isInitialized) {
            if (::imageAdapter.isInitialized) {
                binding.imagePager.unregisterOnPageChangeCallback(pageCallback)
                binding.imagePager.adapter = null
            }
            binding.actorsRecycler.adapter = null
        }
        if (presenterAttached) {
            presenter.detach()
            presenterAttached = false
        }
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_ID = "extra_movie_id"
        private const val EXTRA_TITLE = "extra_movie_title"
        private const val EXTRA_RATING = "extra_movie_rating"
        private const val EXTRA_SUMMARY = "extra_movie_summary"
        private const val EXTRA_POSTER = "extra_movie_poster"
        private const val EXTRA_GENRES = "extra_movie_genres"

        fun newIntent(context: Context, movie: Movie): Intent = Intent(context, DetailActivity::class.java).apply {
            putExtra(EXTRA_ID, movie.id)
            putExtra(EXTRA_TITLE, movie.title)
            putExtra(EXTRA_RATING, movie.rating ?: Double.NaN)
            putExtra(EXTRA_SUMMARY, movie.summaryHtml)
            putExtra(EXTRA_POSTER, movie.posterUrl)
            putStringArrayListExtra(EXTRA_GENRES, ArrayList(movie.genres))
        }

        private fun readMovie(intent: Intent): Movie? {
            val id = intent.getLongExtra(EXTRA_ID, -1L)
            val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
            if (id < 0L || title.isBlank()) return null

            val ratingValue = intent.getDoubleExtra(EXTRA_RATING, Double.NaN)
            return Movie(
                id = id,
                title = title,
                rating = ratingValue.takeUnless { it.isNaN() },
                summaryHtml = intent.getStringExtra(EXTRA_SUMMARY).orEmpty(),
                posterUrl = intent.getStringExtra(EXTRA_POSTER),
                genres = intent.getStringArrayListExtra(EXTRA_GENRES).orEmpty()
            )
        }
    }
}
