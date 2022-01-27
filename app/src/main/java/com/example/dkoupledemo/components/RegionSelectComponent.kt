package com.example.dkoupledemo.components

import com.example.dkoupledemo.R
import android.animation.ObjectAnimator
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.dkoupledemo.Region
import com.example.dkoupledemo.RegionSelectionState
import com.seannajera.dkouple.Component
import com.seannajera.dkouple.ComponentView
import com.seannajera.dkouple.DKoupleComponent
import kotlinx.coroutines.flow.MutableSharedFlow

@DKoupleComponent(R.layout.component_region_select)
data class RegionSelectComponent(val region: Region, val isSelected: Boolean) : Component {

    override val id: String = region.id

    override fun contentSameAs(otherComponent: Any): Boolean {
        val otherRegionComponent = otherComponent as? RegionSelectComponent
        return otherRegionComponent?.region?.name == this.region.name &&
                otherRegionComponent.isSelected == this.isSelected
    }
}

class RegionSelectView(
    view: View,
    private val eventSender: MutableSharedFlow<RegionSelectionState>
) : ComponentView<RegionSelectComponent>(view) {

    private val regionName: TextView by lazy { view.findViewById<TextView>(R.id.text_region) }
    private val regionView: ConstraintLayout by lazy { view.findViewById<ConstraintLayout>(R.id.region_layout) }
    private val regionIcon: ImageView by lazy { view.findViewById<ImageView>(R.id.arrow_icon) }

    override fun onViewUpdate(
        previous: RegionSelectComponent?,
        current: RegionSelectComponent
    ) {
        if (previous?.region?.name != current.region.name) {
            regionName.text = current.region.name
        }

        if (previous != null && previous.isSelected != current.isSelected) {
            val (startPosition, endPosition) = if (current.isSelected) {
                Pair(0f, 180f)
            } else {
                Pair(180f, 0f)
            }

            ObjectAnimator.ofFloat(regionIcon, View.ROTATION, startPosition, endPosition)
                .setDuration(300)
                .start()
        }

        regionView.setOnClickListener {
            if (!current.isSelected) {
                eventSender.tryEmit(RegionSelectionState.RegionSelected(current.region))
            } else {
                eventSender.tryEmit(RegionSelectionState.RegionDeSelected)
            }
        }
    }
}
