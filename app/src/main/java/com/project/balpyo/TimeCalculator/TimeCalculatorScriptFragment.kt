package com.project.balpyo.TimeCalculator

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.balpyo.Home.ViewModel.StorageViewModel
import com.project.balpyo.MainActivity
import com.project.balpyo.R
import com.project.balpyo.Utils.MyApplication
import com.project.balpyo.databinding.FragmentTimeCalculatorScriptBinding
import kotlin.properties.Delegates

class TimeCalculatorScriptFragment : Fragment() {

    lateinit var binding: FragmentTimeCalculatorScriptBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StorageViewModel

    var editTextMarginBottom by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimeCalculatorScriptBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        viewModel = ViewModelProvider(mainActivity)[StorageViewModel::class.java]
        viewModel.run {
            storageDetailForBottomSheet.observe(mainActivity) {
                if (it != null) {
                    binding.editTextScript.setText(it.script)
                }
            }
        }

        binding.run {
            editTextMarginBottom = editTextScript.marginBottom

            btnBottomNext.setOnClickListener {

                MyApplication.timeCalculatorScript = editTextScript.text.toString()

                findNavController().navigate(R.id.timeCalculatorTimeFragment)
            }

            btnLoadScript.setOnClickListener {
                viewModel.getStorageListForBottomSheet(this@TimeCalculatorScriptFragment.parentFragmentManager, mainActivity)
            }
            editTextScript.addTextChangedListener {
                btnBottomNext.isEnabled = editTextScript.text.isNotEmpty()
                btnKeyboardNext.isEnabled = editTextScript.text.isNotEmpty()
            }
        }

        initToolBar()
        setInsetsListener()

        return binding.root
    }

    private fun setInsetsListener(){
        //adjustResize 버전 별 대응
        //키보드 올라옴에 따라 인셋 변경 (안하면 시스템 UI에 의해 키보드 위 다음 버튼에 여백 생김)
        binding.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                root.setOnApplyWindowInsetsListener { _, windowInsets ->
                    //키보드
                    val imeInsets = windowInsets.getInsets(WindowInsets.Type.ime())
                    //네비게이션 바
                    val navBarInsets = windowInsets.getInsets(WindowInsets.Type.navigationBars())

                    val keyboardHeight = imeInsets.bottom
                    val navBarHeight = navBarInsets.bottom

                    if (keyboardHeight > navBarHeight) {
                        //키보드 올라옴
                        //스크립트 마진 : 다음 버튼 크기 + 기본 마진
                        editTextScript.updateMargins(bottom = editTextMarginBottom + binding.btnBottomNext.height)
                        // 키보드의
                        root.setPadding(0, 0, 0, keyboardHeight)
                        btnKeyboardNext.visibility = View.VISIBLE
                        btnLoadScript.visibility = View.GONE
                        btnBottomNext.visibility = View.GONE
                    } else {
                        //키보드 내려감
                        editTextScript.updateMargins(bottom = editTextMarginBottom)
                        root.setPadding(0, 0, 0, 0)
                        btnKeyboardNext.visibility = View.GONE
                        btnLoadScript.visibility = View.VISIBLE
                        btnBottomNext.visibility = View.VISIBLE
                    }
                    windowInsets
                }
            } else {
                requireActivity().window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                )
            }
        }
    }

    //동적으로 마진 설정하는 확장 함수
    fun View.updateMargins(
        left: Int = marginLeft,
        top: Int = marginTop,
        right: Int = marginRight,
        bottom: Int = marginBottom
    ) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(left, top, right, bottom)
        layoutParams = params
    }

    fun initToolBar() {
        binding.run {
            toolbar.buttonBack.visibility = View.VISIBLE
            toolbar.buttonClose.visibility = View.INVISIBLE
            toolbar.textViewTitle.visibility = View.VISIBLE
            toolbar.textViewTitle.text = "시간 계산"
            toolbar.textViewPage.run {
                visibility = View.VISIBLE
                text = "2/4"
            }
            toolbar.buttonBack.setOnClickListener {
                // 뒤로가기 버튼 클릭시 동작
                findNavController().popBackStack()
            }
        }
    }
}