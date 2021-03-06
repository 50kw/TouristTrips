package com.example.touristtrips.dependency_injections

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.touristtrips.domain.shared.util.Constants
import com.example.touristtrips.data.local.room.source.LocalDatabase
import com.example.touristtrips.data.local.room.repository.LocalLocationRepositoryImpl
import com.example.touristtrips.data.local.room.repository.LocalRouteRepositoryImpl
import com.example.touristtrips.data.remote.firebase.repository.LocationRepository
import com.example.touristtrips.data.remote.firebase.repository.RouteRepository
import com.example.touristtrips.data.remote.retrofit.source.DirectionsApi
import com.example.touristtrips.data.remote.retrofit.repository.DirectionsRepositoryImpl
import com.example.touristtrips.domain.my_locations.repository.LocalLocationRepository
import com.example.touristtrips.domain.shared.repository.DirectionsRepository
import com.example.touristtrips.domain.my_locations.use_case.*
import com.example.touristtrips.domain.my_routes.repository.LocalRouteRepository
import com.example.touristtrips.domain.my_routes.use_case.*
import com.example.touristtrips.domain.remote_routes.use_case.GetRouteLocations
import com.example.touristtrips.domain.remote_routes.use_case.GetRouteWithLocationsId
import com.example.touristtrips.domain.remote_routes.use_case.RouteUseCases
import com.example.touristtrips.domain.shared.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): LocalDatabase {
        return Room.databaseBuilder(
            app,
            LocalDatabase::class.java,
            LocalDatabase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideDirectionsServices(): DirectionsApi = Retrofit
        .Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .build()
        .create(DirectionsApi::class.java)

    @Provides
    @Singleton
    fun provideLocationRepository(db: LocalDatabase): LocalLocationRepository {
        return LocalLocationRepositoryImpl(db.locationDao)
    }

    @Provides
    @Singleton
    fun provideRouteRepository(db: LocalDatabase): LocalRouteRepository {
        return LocalRouteRepositoryImpl(db.routeDao)
    }

    @Provides
    @Singleton
    fun provideDirectionsRepository(directionsApi: DirectionsApi): DirectionsRepository = DirectionsRepositoryImpl(directionsApi)

    @Provides
    @Singleton
    fun provideRemoteRouteRepository(): RouteRepository = RouteRepository()

    @Provides
    @Singleton
    fun provideRemoteLocationRepository(): LocationRepository = LocationRepository()

    @Provides
    @Singleton
    fun provideMyLocationUseCases(repositoryLocal: LocalLocationRepository): MyLocationUseCases {
        return MyLocationUseCases(
            addLocation = AddLocation(repositoryLocal),
            getLocations = GetLocations(repositoryLocal),
            getLocation = GetLocation(repositoryLocal),
            updateLocation = UpdateLocation(repositoryLocal),
            deleteLocation = DeleteLocation(repositoryLocal),
            sortLocations = SortLocations(),
            findLocationsWithText = FindLocationsWithText(),
        )
    }

    @Provides
    @Singleton
    fun provideMyRouteUseCases(repositoryLocal: LocalRouteRepository): RoutesUseCases {
        return RoutesUseCases(
            addRoute = AddRoute(repositoryLocal),
            getRoutes = GetRoutes(repositoryLocal),
            getRoute = GetRoute(repositoryLocal),
            updateRoute = UpdateRoute(repositoryLocal),
            deleteRoute = DeleteRoute(repositoryLocal),
            addRouteLocation = AddRouteLocation(repositoryLocal),
            getRouteWithLocations = GetRouteWithLocations(repositoryLocal),
            getRoutesWithLocations = GetRoutesWithLocations(repositoryLocal),
            deleteRouteLocation = DeleteRouteLocation(repositoryLocal),
            sortRoutes = SortRoutes(),
            findRoutesWithText = FindRoutesWithText(),
            updateRouteLocation = UpdateRouteLocation(repositoryLocal),
        )
    }

    @Provides
    @Singleton
    fun provideRouteUseCases(repositoryRouteRemote: RouteRepository, repositoryLocationRemote: LocationRepository): RouteUseCases {
        return RouteUseCases (
            getRouteWithLocationsId = GetRouteWithLocationsId(repositoryRouteRemote),
            getRouteLocations = GetRouteLocations(repositoryLocationRemote),
            getRoutes = com.example.touristtrips.domain.remote_routes.use_case.GetRoutes(repositoryRouteRemote)
        )
    }

    @Provides
    @Singleton
    fun provideDirectionsPolylinesUseCase(repository: DirectionsRepository): GetRouteDirections {
        return GetRouteDirections(repository)
    }

    @Provides
    @Singleton
    fun provideRouteMapUseCases(repositoryDirections: DirectionsRepository, repositoryRouteLocal: LocalRouteRepository, repositoryRouteRemote: RouteRepository, repositoryLocationRemote: LocationRepository, @ApplicationContext appContext: Context): RouteMapUseCases {
        return RouteMapUseCases (
            getRouteDirections = GetRouteDirections(repositoryDirections),
            getRouteWithLocations = GetRouteWithLocations(repositoryRouteLocal),
            getRouteWithLocationsId = GetRouteWithLocationsId(repositoryRouteRemote),
            getRouteLocations = GetRouteLocations(repositoryLocationRemote),
            updateRouteLocation = UpdateRouteLocation(repositoryRouteLocal),
            context = appContext,
        )
    }

}