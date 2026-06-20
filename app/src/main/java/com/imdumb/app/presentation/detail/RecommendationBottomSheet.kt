package com.imdumb.app.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imdumb.app.R
import com.imdumb.app.databinding.BottomSheetRecommendationBinding
import com.imdumb.app.presentation.common.HtmlFormatter

class RecommendationBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetRecommendationBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = requireArguments().getString(ARG_TITLE).orEmpty()
        val summaryHtml = requireArguments().getString(ARG_SUMMARY_HTML).orEmpty()

        binding.sheetTitle.text = getString(R.string.recommend_sheet_title, title)
        binding.movieDetail.text = HtmlFormatter.fromHtml(summaryHtml)
        binding.commentInput.doAfterTextChanged {
            if (!it.isNullOrBlank()) binding.commentLayout.error = null
        }
        binding.confirmButton.setOnClickListener {
            val comment = binding.commentInput.text?.toString()?.trim().orEmpty()
            if (comment.isBlank()) {
                binding.commentLayout.error = getString(R.string.comment_required)
                return@setOnClickListener
            }

            binding.commentLayout.error = null
            parentFragmentManager.setFragmentResult(
                RESULT_KEY,
                bundleOf(RESULT_COMMENT to comment)
            )
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheetDialog = dialog as? BottomSheetDialog ?: return
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return

        bottomSheet.layoutParams = bottomSheet.layoutParams.apply {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "RecommendationBottomSheet"
        const val RESULT_KEY = "recommendation_result"
        const val RESULT_COMMENT = "recommendation_comment"

        private const val ARG_TITLE = "arg_title"
        private const val ARG_SUMMARY_HTML = "arg_summary_html"

        fun newInstance(title: String, summaryHtml: String) = RecommendationBottomSheet().apply {
            arguments = bundleOf(
                ARG_TITLE to title,
                ARG_SUMMARY_HTML to summaryHtml
            )
        }
    }
}
