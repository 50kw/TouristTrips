package com.example.touristtrips.feature_location.domain.use_case

class MyLocationUseCases(
    val addLocation: AddLocation,
    val getLocations: GetLocations,
    val getLocation: GetLocation,
    val updateLocation: UpdateLocation,
    val deleteLocation: DeleteLocation
)