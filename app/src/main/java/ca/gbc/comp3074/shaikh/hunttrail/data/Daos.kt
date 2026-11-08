package ca.gbc.comp3074.shaikh.hunttrail.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Flow    = the UI updates automatically whenever the table changes
// suspend = runs inside a coroutine, never blocks the main thread

@Dao
interface PointDao {
    @Transaction
    @Query("SELECT * FROM points ORDER BY name COLLATE NOCASE")
    fun getAllWithTags(): Flow<List<PointWithTags>>

    @Transaction
    @Query("SELECT * FROM points WHERE id = :id")
    fun getWithTags(id: Int): Flow<PointWithTags?>

    @Insert
    suspend fun insert(point: PointOfInterest): Long

    @Update
    suspend fun update(point: PointOfInterest)

    @Delete
    suspend fun delete(point: PointOfInterest)

    @Query("UPDATE points SET rating = :rating WHERE id = :id")
    suspend fun updateRating(id: Int, rating: Int)

    @Query("SELECT COUNT(*) FROM points")
    suspend fun count(): Int
}

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY name")
    fun getAll(): Flow<List<Tag>>

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): Tag?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: Tag): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(ref: PointTagCrossRef)

    @Query("DELETE FROM point_tag_cross_ref WHERE pointId = :pointId")
    suspend fun clearTagsForPoint(pointId: Int)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM team_contacts ORDER BY name COLLATE NOCASE")
    fun getAll(): Flow<List<TeamContact>>

    @Insert
    suspend fun insert(contact: TeamContact)

    @Update
    suspend fun update(contact: TeamContact)

    @Delete
    suspend fun delete(contact: TeamContact)

    @Query("SELECT COUNT(*) FROM team_contacts")
    suspend fun count(): Int
}
