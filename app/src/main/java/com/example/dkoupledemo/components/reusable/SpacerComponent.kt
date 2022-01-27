package com.example.dkoupledemo.components.reusable

import android.content.res.Resources
import android.view.View
import com.example.dkoupledemo.R
import com.seannajera.dkouple.Component
import com.seannajera.dkouple.ComponentView
import com.seannajera.dkouple.DKoupleComponent

@DKoupleComponent(R.layout.component_spacer)
data class SpacerComponent(
    override val id: String,
    val dpHeight: Int
) : Component {

    override fun contentSameAs(otherComponent: Any): Boolean {
        val spacerComponent = otherComponent as? SpacerComponent
        return spacerComponent?.dpHeight == this.dpHeight
    }
}

class SpacerView(private val view: View) :
    ComponentView<SpacerComponent>(view) {
    override fun onViewUpdate(previous: SpacerComponent?, current: SpacerComponent) {
        view.layoutParams.height = (Resources.getSystem().displayMetrics.density * current.dpHeight).toInt()
    }
}