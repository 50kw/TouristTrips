package com.example.touristtrips.domain.shared.model

sealed class SortOrder(val sortType: SortType) {
    class Type(sortType: SortType) : SortOrder(sortType)
    class City(sortType: SortType) : SortOrder(sortType)
    class TimeToVisit(sortType: SortType) : SortOrder(sortType)
    class Title(sortType: SortType) : SortOrder(sortType)
}