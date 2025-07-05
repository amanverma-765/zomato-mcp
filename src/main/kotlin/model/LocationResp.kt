package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationResp(
    @SerialName("location")
    val location: Location?,

    @SerialName("subtitle")
    val subtitle: String?,

    @SerialName("status")
    val status: String
) {
    @Serializable
    data class FooterData(
        @SerialName("title")
        val title: String
    )

    @Serializable
    data class Location(
        @SerialName("city")
        val city: City,
        @SerialName("city_id")
        val cityId: Int,
        @SerialName("current_poi_id")
        val currentPoiId: Int,
        @SerialName("display_title")
        val displayTitle: String,
        @SerialName("entity_id")
        val entityId: Int,
        @SerialName("entity_latitude")
        val entityLatitude: Double,
        @SerialName("entity_longitude")
        val entityLongitude: Double,
        @SerialName("entity_name")
        val entityName: String,
        @SerialName("entity_subtitle")
        val entitySubtitle: String,
        @SerialName("entity_title")
        val entityTitle: String,
        @SerialName("entity_type")
        val entityType: String,
        @SerialName("is_order_location")
        val isOrderLocation: Int,
        @SerialName("location_type")
        val locationType: String,
        @SerialName("place")
        val place: Place,
        @SerialName("user_defined_latitude")
        val userDefinedLatitude: Double,
        @SerialName("user_defined_longitude")
        val userDefinedLongitude: Double
    ) {
        @Serializable
        data class City(
            @SerialName("country_id")
            val countryId: Int,
            @SerialName("country_name")
            val countryName: String,
            @SerialName("has_table_finder")
            val hasTableFinder: Int,
            @SerialName("id")
            val id: Int,
            @SerialName("latitude")
            val latitude: String,
            @SerialName("longitude")
            val longitude: String,
            @SerialName("name")
            val name: String,
            @SerialName("online_ordering_support")
            val onlineOrderingSupport: Int,
            @SerialName("use_miles")
            val useMiles: Int
        )

        @Serializable
        data class Place(
            @SerialName("cell_id")
            val cellId: String,
            @SerialName("delivery_subzone_id")
            val deliverySubzoneId: Int,
            @SerialName("id")
            val id: Int,
            @SerialName("latitude")
            val latitude: Double,
            @SerialName("location_type")
            val locationType: String,
            @SerialName("longitude")
            val longitude: Double,
            @SerialName("o2_serviceablity")
            val o2Serviceablity: Boolean,
            @SerialName("place_id")
            val placeId: String,
            @SerialName("place_name")
            val placeName: String,
            @SerialName("place_type")
            val placeType: String
        )
    }
}