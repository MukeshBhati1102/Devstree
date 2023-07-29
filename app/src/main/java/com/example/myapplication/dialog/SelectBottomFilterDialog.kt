package com.example.myapplication.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.FilterAdapter
import com.example.myapplication.data.FilterData
import com.example.myapplication.databinding.BottomSheetFilterBinding
import com.example.myapplication.utils.EnumID
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectBottomFilterDialog : BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var binding : BottomSheetFilterBinding
    var enumID : EnumID = EnumID.Filter_ASCENDING
    var dialogCallBack : DialogCallBack? = null

    private val filterAdapter by lazy {
        FilterAdapter(requireActivity())
    }

    override fun getTheme(): Int = R.style.SheetDialogNew

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_filter, container, false)
        binding = BottomSheetFilterBinding.bind(view)
        val wmlp = dialog!!.window!!.attributes
        wmlp.gravity = Gravity.CENTER

        dialog!!.window!!.requestFeature(STYLE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val bottomSheet =
                    (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
                BottomSheetBehavior.from<View>(bottomSheet).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    peekHeight = 0

                    var behavior = this
                    behavior.isHideable = false
                    behavior.setBottomSheetCallback(object :
                        BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        }
                    })
                }

                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        try {
            if (activity != null) {

                binding.recyclerview.apply {
                    layoutManager = LinearLayoutManager(requireActivity())
                    adapter = filterAdapter
                }

                val filterList = ArrayList<FilterData>()
                filterList.add(FilterData(getString(R.string.sort_distance_in_ascending_order), EnumID.Filter_ASCENDING, enumID == EnumID.Filter_ASCENDING))
                filterList.add(FilterData(getString(R.string.sort_distance_in_descending_order), EnumID.FILTER_DESCENDING,enumID == EnumID.FILTER_DESCENDING))
                filterAdapter.addAll(filterList)

                filterAdapter.setClickListener(object : FilterAdapter.ClickListener {
                    override fun onItemClick(position: Int) {
                        filterAdapter.changeSelection(position)
                    }
                })

                setClickListener()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun setClickListener() {
        binding.textViewClear.setOnClickListener(this)
        binding.imageCancel.setOnClickListener(this)
        binding.buttonSave.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.textViewClear -> {
                dialogCallBack?.let {
                    it.onClearClick()
                }
            }
            R.id.imageCancel -> {
                dismiss()
            }
            R.id.buttonSave -> {
                dialogCallBack?.let {
                    it.onApplyClick(filterAdapter.getSeletedItem()!!.enumID!!)
                }
            }
        }
    }

    interface DialogCallBack {
        fun onClearClick()

        fun onApplyClick(enumID: EnumID)
    }

}

