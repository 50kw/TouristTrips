package com.example.touristtrips.feature_location.presentation.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touristtrips.feature_location.domain.model.InvalidLocationException
import com.example.touristtrips.feature_location.domain.model.Location
import com.example.touristtrips.feature_location.domain.use_case.LocationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditLocationViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases
) : ViewModel() {

    sealed class AddEditLocationEvent {
        data class SaveLocation(val location: Location): AddEditLocationEvent()
        data class EditLocation(val location: Location): AddEditLocationEvent()
        data class DeleteLocation(val location: Location): AddEditLocationEvent()
    }

    private val _eventFlow = MutableSharedFlow<LocationEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun onEvent(event: AddEditLocationEvent) {
        when (event) {
            is AddEditLocationEvent.SaveLocation -> {
                viewModelScope.launch {
                    try {
                        locationUseCases.addLocation(
                            event.location
                        )
                        _eventFlow.emit(LocationEvent.Success(Operation.SAVED, event.location))
                    } catch (e: InvalidLocationException) {
                        _eventFlow.emit(LocationEvent.Failure(e.message ?: "Failed to save location"))
                    }
                }
            }
            is AddEditLocationEvent.EditLocation -> {
                viewModelScope.launch {
                    try {
                        locationUseCases.updateLocation(
                            event.location
                        )
                        _eventFlow.emit(LocationEvent.Success(Operation.UPDATED, event.location))
                    } catch (e: InvalidLocationException) {
                        _eventFlow.emit(LocationEvent.Failure(e.message ?: "Failed to update location"))
                    }
                }
            }
            is AddEditLocationEvent.DeleteLocation -> {
                viewModelScope.launch {
                    try {
                        locationUseCases.deleteLocation(
                            event.location
                        )
                        _eventFlow.emit(LocationEvent.Success(Operation.DELETED, event.location))
                    } catch (e: InvalidLocationException) {
                        _eventFlow.emit(LocationEvent.Failure(e.message ?: "Failed to delete location"))
                    }
                }
            }

        }
    }

    fun getLocation(id: String) {
        if (id.isNotEmpty()) {
            viewModelScope.launch {
                val location: Location? = locationUseCases.getLocation(id)
                if (location != null && location.locationId == id) {
                    _eventFlow.emit(LocationEvent.Success(Operation.FOUND, location))
                } else {
                    _eventFlow.emit(LocationEvent.Failure("Location not found"))
                }
            }
        }

    }

    /*fun saveLocation(
        type: String = "",
        title: String = "",
        description: String = "",
        latitude: String = "",
        longitude: String = "",
        city: String = "",
        createdAt: Long = 0L,
        imageUrl: String = "",
        months_to_visit: String = "",
        price: Float = 0.0F
    ) {
        val location = Location(
            type = type,
            title = title,
            description = description,
            latitude = latitude,
            longitude = longitude,
            city = city,
            createdAt = createdAt,
            imageUrl = imageUrl,
            months_to_visit = months_to_visit,
            price = price
        )

    }*/

}