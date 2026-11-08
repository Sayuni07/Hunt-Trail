package ca.gbc.comp3074.shaikh.hunttrail.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

// Table: points
@Entity(tableName = "points")
data class PointOfInterest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String,
    val latitude: Double? = null,   // filled by Geocoder when saving
    val longitude: Double? = null,
    val task: String,
    val rating: Int = 0             // 0 = not rated, 1-5 stars
)

// Table: tags (name is unique, so "easy" is stored only once)
@Entity(tableName = "tags", indices = [Index(value = ["name"], unique = true)])
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

// Many-to-many link between points and tags.
// CASCADE: deleting a point (or tag) also deletes its links.
@Entity(
    tableName = "point_tag_cross_ref",
    primaryKeys = ["pointId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = PointOfInterest::class,
            parentColumns = ["id"], childColumns = ["pointId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"], childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tagId")]
)
data class PointTagCrossRef(
    val pointId: Int,
    val tagId: Int
)

// Not a table: Room fills this with a point + all its tags (through the cross-ref)
data class PointWithTags(
    @Embedded val point: PointOfInterest,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PointTagCrossRef::class,
            parentColumn = "pointId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>
)

// Helper: just the tag names, sorted (extension, so Room ignores it)
val PointWithTags.tagNames: List<String>
    get() = tags.map { it.name }.sorted()

// Table: team_contacts
@Entity(tableName = "team_contacts")
data class TeamContact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val email: String
)
