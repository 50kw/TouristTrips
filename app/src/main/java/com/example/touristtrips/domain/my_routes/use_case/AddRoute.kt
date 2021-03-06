package com.example.touristtrips.domain.my_routes.use_case

import com.example.touristtrips.domain.shared.util.route.checkRouteFormatErrors
import com.example.touristtrips.domain.shared.model.route.InvalidRouteException
import com.example.touristtrips.domain.shared.model.route.Route
import com.example.touristtrips.domain.my_routes.repository.LocalRouteRepository

class AddRoute(
    private val repositoryLocal: LocalRouteRepository
) {

    @Throws(InvalidRouteException::class)
    suspend operator fun invoke(route: Route) {
        checkRouteFormatErrors(route)
        repositoryLocal.insertRoute(route)
    }
}