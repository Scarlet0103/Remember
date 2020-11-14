package scarlet.believe.remember.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import scarlet.believe.remember.R
import scarlet.believe.remember.utils.BottomSheetThemeInterface

class AddBottomSheet(val listner : BottomSheetThemeInterface) : BottomSheetDialogFragment() {

    private lateinit var lightTheme : TextView
    private lateinit var darkTheme : TextView
    private lateinit var autoTheme : TextView

    fun newInstance(): AddBottomSheet {
        return AddBottomSheet(listner)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottomsheet_settings, container, false)
        initView(view)
        return view
    }

    private fun initView(view : View){

        lightTheme = view.findViewById<TextView>(R.id.lighttheme_setting)
        darkTheme = view.findViewById<TextView>(R.id.darktheme_setting)
        autoTheme = view.findViewById<TextView>(R.id.autotheme_setting)

        lightTheme.setOnClickListener {
            listner.themeSelected(1)
        }
        darkTheme.setOnClickListener {
            listner.themeSelected(2)
        }
        autoTheme.setOnClickListener {
            listner.themeSelected(3)
        }

    }

}