package com.example.dkoupledemo

import com.example.dkoupledemo.components.LocationSelectComponent
import com.example.dkoupledemo.components.RegionSelectComponent
import com.example.dkoupledemo.components.reusable.SpacerComponent
import com.example.dkoupledemo.components.reusable.TextComponent
import com.seannajera.dkouple.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivityViewModel(
    eventReceiver: SharedFlow<RegionSelectionState>,
    scope: CoroutineScope
) {

    private val mutableComponentState: MutableSharedFlow<List<Component>> = MutableSharedFlow(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val componentState: SharedFlow<List<Component>> = mutableComponentState

    private val initialComponents = regionData.flatMap { entry ->
        listOf(RegionSelectComponent(entry.key, false))
    }

    init {
        scope.launch {
            mutableComponentState.emit(initialComponents.appendFooter())
        }

        eventReceiver
            .onEach { state ->
                when (state) {
                    is RegionSelectionState.LocationSelected -> {
                        val updatedComponents = regionData.flatMap { entry ->

                            val subsequentLocations = if (entry.key.id == state.location.regionId) {
                                entry.value.map { LocationSelectComponent( it, it.id == state.location.id) }
                            } else {
                                emptyList()
                            }

                            listOf(RegionSelectComponent(entry.key, entry.key.id == state.location.regionId)) +
                                    subsequentLocations
                        }

                        mutableComponentState.emit(updatedComponents.appendFooter())
                    }
                    is RegionSelectionState.RegionDeSelected -> {
                        mutableComponentState.emit(initialComponents.appendFooter())
                    }
                    is RegionSelectionState.RegionSelected -> {
                        val updatedComponents = regionData.flatMap { entry ->

                            val subsequentLocations = if (entry.key.id == state.region.id) {
                                entry.value.map { LocationSelectComponent( it, false) }
                            } else {
                                emptyList()
                            }

                            listOf(RegionSelectComponent(entry.key, entry.key.id == state.region.id)) +
                                    subsequentLocations
                        }

                        mutableComponentState.emit(updatedComponents.appendFooter())
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(scope)
    }
}

sealed class RegionSelectionState {
    data class LocationSelected(val location: Location) : RegionSelectionState()
    data class RegionSelected(val region: Region) : RegionSelectionState()
    object RegionDeSelected : RegionSelectionState()
}


val regionData = mapOf(
    Region(id = "NV", name = "Nevada") to listOf(
        Location(id = "LV", name = "Las Vegas", regionId = "NV"),
        Location(id = "RN", name = "Reno", regionId = "NV")
    ),

    Region(id = "PA", name = "Pennsylvania") to listOf(
        Location(id = "PH", name = "Philadelphia", regionId = "PA"),
        Location(id = "PT", name = "Pittsburgh", regionId = "PA")
    ),

    Region(id = "CA", name = "California") to listOf(
        Location(id = "SLO", name = "San Luis Obispo", regionId = "CA"),
        Location(id = "PR", name = "Pico Rivera", regionId = "CA"),
        Location(id = "FA", name = "Fontana", regionId = "CA"),
        Location(id = "ON", name = "Ontario", regionId = "CA"),
        Location(id = "SF", name = "San Fransisco", regionId = "CA")
    ),

    Region(id = "OR", name = "Oregon") to listOf(
        Location(id = "CV", name = "Corvalis", regionId = "OR"),
        Location(id = "EU", name = "Eugene", regionId = "OR")
    ),

    Region(id = "CO", name = "Colorado") to listOf(
        Location(id = "FC", name = "Fort Collins", regionId = "CO"),
        Location(id = "AS", name = "Aspen", regionId = "CO")
    ),

    Region(id = "MN", name = "Minnesota") to listOf(
        Location(id = "SC", name = "St. Cloud", regionId = "MN"),
        Location(id = "SP", name = "St. Paul", regionId = "MN")
    ),

    Region(id = "VA", name = "Virginia") to listOf(
        Location(id = "RI", name = "Richmond", regionId = "VA"),
        Location(id = "CL", name = "Charlottesville", regionId = "VA")
    ),

    Region(id = "DC", name = "Washington DC") to listOf(
        Location(id = "CH", name = "Capital Hill", regionId = "DC"),
        Location(id = "DU", name = "Dupont Circle", regionId = "DC")
    ),
)

fun List<Component>.appendFooter(): List<Component> {
    return this + listOf(
        SpacerComponent(id = "MyFooterSpacer", dpHeight = 16),
        TextComponent(id = "MyFooter", "These are valid locations")
    )
}
