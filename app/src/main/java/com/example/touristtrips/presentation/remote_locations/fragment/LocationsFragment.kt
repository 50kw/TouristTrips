package com.example.touristtrips.presentation.remote_locations.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.touristtrips.R
import com.example.touristtrips.databinding.FragmentLocationsBinding
import com.example.touristtrips.presentation.remote_locations.viewmodel.LocationsViewModel
import com.example.touristtrips.presentation.shared.epoxy.location_epoxy_model.LocationsEpoxyController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationsFragment : Fragment() {
    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = _binding!!

    private val locationsViewModel: LocationsViewModel by navGraphViewModels(R.id.locations_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val controller = LocationsEpoxyController(::itemSelected, textWatcher)
        binding.epoxyRecyclerView.setController(controller)

        locationsViewModel.getLocations()

        locationsViewModel.locationsState.observe(viewLifecycleOwner) { locationState ->
            locationsViewModel.setAllLocations()
            controller.locationsState = locationState
        }
    }

    private fun itemSelected(id: String) {
        findNavController().navigate(LocationsFragmentDirections.actionLocationsFragmentToLocationFragment2(id))
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // nothing
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0 != null) {
                locationsViewModel.showLocationsWithText(p0.toString())
            }
        }

        override fun afterTextChanged(p0: Editable?) {
            // nothing
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_locations_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menuSort) {
        findNavController().navigate(LocationsFragmentDirections.actionLocationsFragmentToSortOrderBottomSheetDialogFragment())
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}