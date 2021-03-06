package com.example.touristtrips.domain.shared.util.location

import com.example.touristtrips.domain.my_locations.model.InvalidLocationException
import com.example.touristtrips.domain.my_locations.model.Location

fun checkLocationFormatErrors(location: Location) {
    if (location.title.isBlank()) {
        throw InvalidLocationException("Title of the location can't be empty.")
    }
    if (location.type.isBlank()) {
        throw InvalidLocationException("Type of the location can't be empty.")
    }
    if (location.description.isBlank()) {
        throw InvalidLocationException("Content of the location can't be empty.")
    }
    if (location.location_search.isBlank()) {
        if (location.latitude.isBlank()) {
            throw InvalidLocationException("Location search or latitude and longitude can't be empty.")
        }
        if (location.latitude.toDoubleOrNull() == null) {
            throw InvalidLocationException("Latitude of the location must be a number.")
        }
        if (location.latitude.toDouble() > 90 || location.latitude.toDouble() < -90) {
            throw InvalidLocationException("Latitude of the location must be between -90 and 90.")
        }
        if (location.longitude.isBlank()) {
            throw InvalidLocationException("Location search or latitude and longitude can't be empty.")
        }
        if (location.longitude.toDoubleOrNull() == null) {
            throw InvalidLocationException("Longitude of the location must be a number.")
        }
        if (location.longitude.toDouble() > 180 || location.longitude.toDouble() < -180) {
            throw InvalidLocationException("Longitude of the location must be between -180 and 180.")
        }
    }
    if (location.city.isBlank()) {
        throw InvalidLocationException("City of the location can't be empty.")
    }
    if (location.months_to_visit.isBlank()) {
        throw InvalidLocationException("Time to visit of the location can't be empty.")
    }
    /*if (location.price.isBlank()) {
        throw InvalidLocationException("Price of the location can't be empty.")
    }
    if (location.price.toDoubleOrNull() == null) {
        throw InvalidLocationException("Price of the location must be a number.")
    }*/
    /*if (location.imageUrl.isBlank()) {
        throw InvalidLocationException("Image url of the location can't be empty.")
    }
    if (!location.imageUrl.startsWith("https://")) {
        throw InvalidLocationException("Image url of the location must start with https://.")
    }*/

}