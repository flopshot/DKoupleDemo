package com.example.dkoupledemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.dkoupledemo.components.LocationSelectView
import com.example.dkoupledemo.components.RegionSelectView
import com.example.dkoupledemo.components.reusable.MyTextView
import com.example.dkoupledemo.components.reusable.SpacerView
import com.seannajera.dkouple.Component
import com.seannajera.dkouple.ComponentAdapter
import com.seannajera.dkouple.ComponentFactory
import com.seannajera.dkouple.ComponentView
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.lang.IllegalStateException

class MainActivity : AppCompatActivity() {

    // ViewModel and comms
    private val eventSender: MutableSharedFlow<RegionSelectionState> = MutableSharedFlow(extraBufferCapacity = 30, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val viewModel: MainActivityViewModel = MainActivityViewModel(eventSender, lifecycleScope)

    // Recycler View Adapter et al
    private val componentFactory: ComponentFactory = MyComponentFactory(eventSender)
    private val componentAdapter: ComponentAdapter = ComponentAdapter(componentFactory)

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.region_recyclerview) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.adapter = componentAdapter

        lifecycleScope.launchWhenResumed {

            viewModel.componentState
                .collect { components ->
                    componentAdapter.applyComponents(components)
                }
        }
    }
}

class MyComponentFactory(
    private val eventSender: MutableSharedFlow<RegionSelectionState>
): ComponentFactory {

    override fun createView(layoutId: Int, view: View): ComponentView<out Component> {

        return when (layoutId) {
            R.layout.component_location_select -> LocationSelectView(view, eventSender)
            R.layout.component_region_select -> RegionSelectView(view, eventSender)
            R.layout.component_spacer -> SpacerView(view)
            R.layout.component_text -> MyTextView(view)
            else -> throw IllegalStateException()
        }
    }
}


