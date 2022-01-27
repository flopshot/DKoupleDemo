package com.example.dkoupledemo.components

import android.view.View
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.TextView
import com.example.dkoupledemo.Location
import com.example.dkoupledemo.R
import com.example.dkoupledemo.RegionSelectionState
import com.seannajera.dkouple.Component
import com.seannajera.dkouple.ComponentView
import com.seannajera.dkouple.DKoupleComponent
import kotlinx.coroutines.flow.MutableSharedFlow

@DKoupleComponent(R.layout.component_location_select)
data class LocationSelectComponent(val location: Location, val isSelected: Boolean) : Component {

    override val id: String = location.id

    override fun contentSameAs(otherComponent: Any): Boolean {
        val otherLocationComponent = otherComponent as? LocationSelectComponent
        return otherLocationComponent?.location?.name == this.location.name
                && otherLocationComponent.isSelected == this.isSelected
    }
}

class LocationSelectView(
    view: View,
    private val eventSender: MutableSharedFlow<RegionSelectionState>
) : ComponentView<LocationSelectComponent>(view) {

    private  val locationName: TextView by lazy { view.findViewById<TextView>(R.id.text_location) }
    private val locationView: FrameLayout by lazy { view.findViewById<FrameLayout>(R.id.location_select_clickable) }
    private val locationRadioButton: RadioButton by lazy { view.findViewById<RadioButton>(R.id.location_radio_button) }

    override fun onViewUpdate(previous: LocationSelectComponent?, current: LocationSelectComponent) {
        if (previous?.location?.name != current.location.name) {
            locationName.text = current.location.name
        }

        locationRadioButton.isChecked = current.isSelected

        locationView.setOnClickListener {
            eventSender.tryEmit(RegionSelectionState.LocationSelected(current.location))
        }
    }
}
