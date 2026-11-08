package ca.gbc.comp3074.shaikh.hunttrail.ui

// Temporary model for the UI prototype.
// Later this gets replaced by the Room entity PointOfInterest.
data class PointUi(
    val id: Int,
    val name: String,
    val address: String,
    val task: String,
    val tags: List<String>,
    val rating: Int
)

object SampleData {
    val points = listOf(
        PointUi(1, "CN Tower", "290 Bremner Blvd, Toronto",
            "Find the year the tower opened on the plaque.", listOf("easy", "info"), 4),
        PointUi(2, "Casa Loma", "1 Austin Terrace, Toronto",
            "Snap a photo of the castle towers.", listOf("photo"), 5),
        PointUi(3, "Graffiti Alley", "Rush Lane, Toronto",
            "Take a team selfie in front of a mural.", listOf("photo", "hard"), 3),
        PointUi(4, "St. Lawrence Market", "93 Front St E, Toronto",
            "Find out what year the market was founded.", listOf("info"), 0),
        PointUi(5, "Nathan Phillips Square", "100 Queen St W, Toronto",
            "Photo with the TORONTO sign.", listOf("easy", "photo"), 4),
        PointUi(6, "GBC Casa Loma Campus", "160 Kendal Ave, Toronto",
            "Find the building letter on the main entrance.", listOf("hard", "info"), 2)
    )
}