package io.noties.markwon.app.sample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.BundleCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import io.noties.markwon.app.R
import io.noties.markwon.app.databinding.FragmentSampleBinding
import io.noties.markwon.app.sample.Sample
import io.noties.markwon.app.utils.active
import io.noties.markwon.app.utils.safeDrawing

class SampleFragment : Fragment() {

    private var isCodeSelected = false

    private var _mBinding: FragmentSampleBinding? = null
    private val mBinding get() = _mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mBinding = FragmentSampleBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCodeSelected = savedInstanceState?.getBoolean(KEY_CODE_SELECTED) ?: false

        initAppBar(view)
        initTabBar(view)

        ViewCompat.setOnApplyWindowInsetsListener(view) {v,insets->
            val safeDrawing = insets.safeDrawing(false)
            mBinding.appBar.updatePaddingRelative(
                top = safeDrawing.top,
                start = safeDrawing.left,
                end = safeDrawing.right
            )
            mBinding.tabBar.updatePaddingRelative(
                bottom = safeDrawing.bottom,
                start = safeDrawing.left,
                end = safeDrawing.right
            )
            insets
        }
    }

    private fun initAppBar(view: View) {
        val appBar: View = view.findViewById(R.id.app_bar)
        val icon: View = appBar.findViewById(R.id.app_bar_icon)
        val title: TextView = appBar.findViewById(R.id.app_bar_title)

        icon.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        title.text = sample.title
    }

    private fun initTabBar(view: View) {
        val tabBar: View = view.findViewById(R.id.tab_bar)
        val preview: View = tabBar.findViewById(R.id.tab_bar_preview)
        val code: View = tabBar.findViewById(R.id.tab_bar_code)

        preview.setOnClickListener {
            if (!it.active) {
                it.active = true
                code.active = false
                showPreview()
            }
        }

        code.setOnClickListener {
            if (!it.active) {
                it.active = true
                preview.active = false
                showCode()
            }
        }

        // maybe check state (in case of restoration)

        // initial values
        preview.active = !isCodeSelected
        code.active = isCodeSelected

        if (isCodeSelected) {
            showCode()
        } else {
            showPreview()
        }
    }

    private fun showPreview() {
        isCodeSelected = false
        showFragment(TAG_PREVIEW, TAG_CODE) { SamplePreviewFragment.init(sample) }
    }

    private fun showCode() {
        isCodeSelected = true
        showFragment(TAG_CODE, TAG_PREVIEW) { SampleCodeFragment.init(sample) }
    }

    private fun showFragment(showTag: String, hideTag: String, provider: () -> Fragment) {
        val manager = childFragmentManager
        manager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

            val existing = manager.findFragmentByTag(showTag)
            if (existing != null) {
                show(existing)
            } else {
                add(mBinding.container.id, provider(), showTag)
            }

            manager.findFragmentByTag(hideTag)?.also {
                hide(it)
            }

            commitAllowingStateLoss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_CODE_SELECTED, isCodeSelected)
    }

    private val sample: Sample by lazy(LazyThreadSafetyMode.NONE) {
        BundleCompat.getParcelable(requireArguments(), ARG_SAMPLE, Sample::class.java)!!
    }

    companion object {
        private const val ARG_SAMPLE = "arg.Sample"
        private const val TAG_PREVIEW = "tag.Preview"
        private const val TAG_CODE = "tag.Code"
        private const val KEY_CODE_SELECTED = "key.Selected"

        fun init(sample: Sample): SampleFragment {
            val fragment = SampleFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_SAMPLE, sample)
            }
            return fragment
        }
    }
}