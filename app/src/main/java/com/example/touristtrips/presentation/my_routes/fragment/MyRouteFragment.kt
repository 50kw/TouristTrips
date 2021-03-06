package com.example.touristtrips.presentation.my_routes.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.touristtrips.R
import com.example.touristtrips.databinding.DialogLocationSequenceBinding
import com.example.touristtrips.domain.shared.util.Operation
import com.example.touristtrips.databinding.FragmentRouteBinding
import com.example.touristtrips.domain.shared.model.route.Route
import com.example.touristtrips.presentation.my_routes.viewmodel.AddEditRouteViewModel
import com.example.touristtrips.presentation.my_routes.route_locations_epoxy.RouteLocationsEpoxyController
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MyRouteFragment : Fragment() {
    private var _binding: FragmentRouteBinding? = null
    private val binding get() = _binding!!

    private val addEditRoutesViewModel: AddEditRouteViewModel by viewModels()

    private val safeArgs: MyRouteFragmentArgs by navArgs()

    private val routeId: String by lazy {
        safeArgs.routeId
    }

    private lateinit var currentRoute: Route

    override fun onResume() {
        super.onResume()
        addEditRoutesViewModel.getRouteWithLocations(routeId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.locationHeaderImageView.setOnClickListener {
            findNavController().navigate(
                MyRouteFragmentDirections.actionRouteFragmentToRouteLocationSelectionFragment(
                    routeId
                )
            )
        }

        lifecycleScope.launchWhenStarted {
            addEditRoutesViewModel.eventFlow.collectLatest { event ->
                when (event) {
                    is AddEditRouteViewModel.RouteEvent.Success -> {
                        if (event.operation == Operation.FOUND) {
                            currentRoute = event.route
                            displayRoute(currentRoute)
                        } else {
                            Toast.makeText(
                                context,
                                "Route ${event.operation.displayName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (event.operation == Operation.DELETED) {
                            findNavController().navigateUp()
                        }

                    }
                    is AddEditRouteViewModel.RouteEvent.Failure -> {
                        Toast.makeText(context, event.errorText, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val controller = RouteLocationsEpoxyController(::itemSelected, ::deleteItemSelected, ::sequenceItemSelected)
        binding.routeLocationsEpoxyRecyclerView.setController(controller)

        addEditRoutesViewModel.locationsListLiveData.observe(viewLifecycleOwner) { locations ->
            controller.locationsList = locations
            updatePrice()
        }
    }

    private fun sequenceItemSelected(id: String) {
        locationSequenceDialog(id)
    }

    private fun locationSequenceDialog(id: String) {
        val dialogBinding: DialogLocationSequenceBinding = DialogLocationSequenceBinding.inflate(layoutInflater)

        val currentLocation = addEditRoutesViewModel.getLocation(id)
        val currentLocationIndex = addEditRoutesViewModel.getLocationIndex(currentLocation)

        dialogBinding.titleTextView.text = currentLocation.title
        Picasso.get().load(Uri.parse(currentLocation.imageUrl)).placeholder(R.drawable.bruno_soares_284974)
            .into(dialogBinding.headerImageView)

        val customDialog = AlertDialog.Builder(requireContext(), 0).create()

        customDialog.apply {
            setView(dialogBinding.root)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }.show()

        dialogBinding.saveButton.setOnClickListener {
            val selectedSequence = dialogBinding.sequenceAutoCompleteTextView.text.toString().toIntOrNull()
            if (selectedSequence != null && selectedSequence - 1 != currentLocationIndex )
            addEditRoutesViewModel.onEvent(
                AddEditRouteViewModel.AddEditRouteEvent.UpdateLocation(routeId, id, selectedSequence - 1)
            )
            customDialog.dismiss()
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, addEditRoutesViewModel.getLocationsSizeArray())

        dialogBinding.sequenceAutoCompleteTextView.setAdapter(adapter)
        dialogBinding.sequenceAutoCompleteTextView.setText((currentLocationIndex + 1).toString(), false)
    }


    private fun deleteItemSelected(id: String) {
        addEditRoutesViewModel.onEvent(
            AddEditRouteViewModel.AddEditRouteEvent.DeleteLocation(
                routeId,
                id
            )
        )
    }

    private fun itemSelected(id: String) {
        findNavController().navigate(
            MyRouteFragmentDirections.actionRouteFragmentToLocationFragment(
                id
            )
        )
    }

    private fun displayRoute(route: Route) {
        binding.titleTextVIew.text = route.title
        binding.typeTextView.text = route.type
        binding.cityTextView.text = route.city
        binding.timeToVisitTextView.text = route.months_to_visit
        binding.descriptionTextView.text = route.description
        binding.priceTextView.text = route.price.toString()
        Picasso.get().load(Uri.parse(route.imageUrl)).placeholder(R.drawable.bruno_soares_284974)
            .into(binding.headerImageView)
    }

    private fun updatePrice() {
        binding.priceTextView.text = getAllLocationsPrice().toString()
    }

    private fun getAllLocationsPrice(): Float {
        var price = 0F
        addEditRoutesViewModel.locationsListLiveData.value?.forEach { location ->
            price += location.price
        }
        return price
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_map_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menuEdit) {
            findNavController().navigate(
                MyRouteFragmentDirections.actionRouteFragmentToAddEditRouteFragment(
                    routeId
                )
            )
            true
        } else if (item.itemId == R.id.menuDelete) {
            addEditRoutesViewModel.onEvent(
                AddEditRouteViewModel.AddEditRouteEvent.DeleteRoute(
                    currentRoute
                )
            )
            true
        } else if (item.itemId == R.id.menuMap) {
            findNavController().navigate(
                MyRouteFragmentDirections.actionRouteFragmentToRouteMapsFragment(
                    myRouteId = routeId
                )
            )
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