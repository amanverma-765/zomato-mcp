package model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationTokenResp(
    @SerialName("status")
    val status: String,
    @SerialName("location")
    val location: Location
) {
    @Serializable
    data class Location(
        @SerialName("horizontal_accuracy")
        val horizontalAccuracy: Int?,
        @SerialName("entity_id")
        val entityId: Int,
        @SerialName("entity_type")
        val entityType: String,
        @SerialName("city_id")
        val cityId: Int,
        @SerialName("is_order_location")
        val isOrderLocation: Int,
        @SerialName("entity_latitude")
        val entityLatitude: Double,
        @SerialName("entity_longitude")
        val entityLongitude: Double,
        @SerialName("entity_name")
        val entityName: String,
        @SerialName("entity_title")
        val entityTitle: String,
        @SerialName("entity_subtitle")
        val entitySubtitle: String,
        @SerialName("city")
        val city: City,
        @SerialName("user_defined_latitude")
        val userDefinedLatitude: Double,
        @SerialName("user_defined_longitude")
        val userDefinedLongitude: Double,
        @SerialName("display_title")
        val displayTitle: String,
        @SerialName("display_subtitle")
        val displaySubtitle: String,
        @SerialName("place")
        val place: Place,
        @SerialName("location_type")
        val locationType: String,
        @SerialName("current_poi_id")
        val currentPoiId: Int,
        @SerialName("token")
        val token: String
    ) {
        @Serializable
        data class City(
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String,
            @SerialName("country_id")
            val countryId: Int,
            @SerialName("country_name")
            val countryName: String,
            @SerialName("latitude")
            val latitude: String,
            @SerialName("longitude")
            val longitude: String,
            @SerialName("use_miles")
            val useMiles: Int,
            @SerialName("has_table_finder")
            val hasTableFinder: Int,
            @SerialName("online_ordering_support")
            val onlineOrderingSupport: Int
        )

        @Serializable
        data class Place(
            @SerialName("o2_serviceablity")
            val o2Serviceablity: Boolean,
            @SerialName("id")
            val id: Int,
            @SerialName("place_id")
            val placeId: String,
            @SerialName("place_type")
            val placeType: String,
            @SerialName("place_name")
            val placeName: String,
            @SerialName("cell_id")
            val cellId: String,
            @SerialName("delivery_subzone_id")
            val deliverySubzoneId: Int
        )
    }
}