package com.example.touristtrips.domain.my_locations.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_entity")
data class Location(
    @PrimaryKey val locationId: String = "",
    val type: String = "",
    val title: String = "",
    val description: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val city: String = "",
    val createdAt: Long = 0L,
    val imageUrl: String = "",
    val months_to_visit: String = "",
    val price: Float = 0.0F,
    val location_search: String = ""
)

class InvalidLocationException(message: String): Exception(message)
