package com.example.dkoupledemo.components.reusable

import android.content.res.Resources.getSystem
import android.view.View
import android.widget.TextView
import com.example.dkoupledemo.R
import com.seannajera.dkouple.Component
import com.seannajera.dkouple.ComponentView
import com.seannajera.dkouple.DKoupleComponent

@DKoupleComponent(R.layout.component_text)
data class TextComponent(
    override val id: String,
    val content: String,
    val verticalPadding: Int = 0,
    val horizontalPadding: Int = 16,
) : Component {

    override fun contentSameAs(otherComponent: Any): Boolean {
        val otherTextComponent = otherComponent as? TextComponent
        return otherTextComponent == this
    }
}

class MyTextView(private val view: View) : ComponentView<TextComponent>(view) {
    private val textView: TextView by lazy { view.findViewById<TextView>(R.id.text_view_right_aligned) }

    override fun onViewUpdate(
        previous: TextComponent?,
        current: TextComponent
    ) {
        val paddingVertical = (getSystem().displayMetrics.density * current.verticalPadding).toInt()
        val paddingHorizontal = (getSystem().displayMetrics.density * current.horizontalPadding).toInt()
        textView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)

        textView.text = current.content
    }
}
