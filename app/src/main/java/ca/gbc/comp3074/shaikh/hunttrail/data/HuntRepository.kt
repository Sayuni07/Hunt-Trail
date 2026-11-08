package ca.gbc.comp3074.shaikh.hunttrail.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

// The only class the screens talk to. It hides the DAOs from the UI.
class HuntRepository(db: HuntTrailDatabase) {

    private val pointDao = db.pointDao()
    private val tagDao = db.tagDao()
    private val contactDao = db.contactDao()

    // ---------- Points ----------
    val points: Flow<List<PointWithTags>> = pointDao.getAllWithTags()
    val tags: Flow<List<Tag>> = tagDao.getAll()

    fun point(id: Int): Flow<PointWithTags?> = pointDao.getWithTags(id)

    // Insert (id = 0) or update a point, then link its tags
    suspend fun savePoint(point: PointOfInterest, tagNames: List<String>) {
        val pointId = if (point.id == 0) {
            pointDao.insert(point).toInt()
        } else {
            pointDao.update(point)
            point.id
        }
        tagDao.clearTagsForPoint(pointId)
        tagNames.map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }
            .distinct()
            .forEach { name ->
                val tagId = tagDao.findByName(name)?.id ?: tagDao.insert(Tag(name = name)).toInt()
                tagDao.insertCrossRef(PointTagCrossRef(pointId, tagId))
            }
    }

    suspend fun deletePoint(point: PointOfInterest) = pointDao.delete(point)

    suspend fun setRating(pointId: Int, rating: Int) = pointDao.updateRating(pointId, rating)

    // ---------- Contacts ----------
    val contacts: Flow<List<TeamContact>> = contactDao.getAll()

    suspend fun saveContact(contact: TeamContact) {
        if (contact.id == 0) contactDao.insert(contact) else contactDao.update(contact)
    }

    suspend fun deleteContact(contact: TeamContact) = contactDao.delete(contact)

    // ---------- First launch: add sample data ----------
    suspend fun seedIfEmpty() {
        if (pointDao.count() == 0) {
            savePoint(
                PointOfInterest(
                    name = "CN Tower", address = "290 Bremner Blvd, Toronto, ON",
                    latitude = 43.6426, longitude = -79.3871,
                    task = "Find the year the tower opened on the plaque.", rating = 4
                ), listOf("easy", "info")
            )
            savePoint(
                PointOfInterest(
                    name = "Casa Loma", address = "1 Austin Terrace, Toronto, ON",
                    latitude = 43.6780, longitude = -79.4094,
                    task = "Snap a photo of the castle towers.", rating = 5
                ), listOf("photo")
            )
            savePoint(
                PointOfInterest(
                    name = "Graffiti Alley", address = "Rush Lane, Toronto, ON",
                    latitude = 43.6479, longitude = -79.4003,
                    task = "Take a team selfie in front of a mural.", rating = 3
                ), listOf("photo", "hard")
            )
            savePoint(
                PointOfInterest(
                    name = "St. Lawrence Market", address = "93 Front St E, Toronto, ON",
                    latitude = 43.6487, longitude = -79.3716,
                    task = "Find out what year the market was founded."
                ), listOf("info")
            )
        }
        if (contactDao.count() == 0) {
            contactDao.insert(TeamContact(name = "Daviti Sidana", phone = "416-555-0101", email = "daviti@example.com"))
            contactDao.insert(TeamContact(name = "Tanzeem Shaikh", phone = "416-555-0102", email = "tanzeem@example.com"))
            contactDao.insert(TeamContact(name = "Sayuni Wimaladharma", phone = "416-555-0103", email = "sayuni@example.com"))
            contactDao.insert(TeamContact(name = "Noela Bilonda Kabundi", phone = "416-555-0104", email = "noela@example.com"))
        }
    }

    companion object {
        @Volatile
        private var instance: HuntRepository? = null

        fun get(context: Context): HuntRepository =
            instance ?: synchronized(this) {
                instance ?: HuntRepository(HuntTrailDatabase.getInstance(context)).also { instance = it }
            }
    }
}
