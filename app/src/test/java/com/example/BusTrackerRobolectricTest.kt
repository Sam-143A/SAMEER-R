package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.BusRepository
import com.example.data.local.AppDatabase
import com.example.data.local.SavedRouteEntity
import com.example.data.local.UserSessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BusTrackerRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: BusRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = BusRepository(db)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInitialBusesLoaded() {
        val buses = repository.buses.value
        assertTrue("Should load initial KSRTC fleet buses", buses.isNotEmpty())
        assertTrue("Should include SWIFT service", buses.any { it.busNumber.contains("KL-15-A-1284") })
    }

    @Test
    fun testSaveFavoriteRouteToRoom() = runBlocking {
        val firstBus = repository.buses.value.first()
        repository.toggleFavorite(firstBus)

        val saved = repository.savedRoutes.first { it.isNotEmpty() }
        assertEquals(1, saved.size)
        assertEquals(firstBus.busNumber, saved.first().busNumber)
    }

    @Test
    fun testUserSessionPersistence() = runBlocking {
        repository.saveLoginSession(
            fullName = "Anjali S. Menon",
            phoneOrEmail = "anjali@commuter.in",
            passType = "Student Concession",
            passNumber = "STU-8821"
        )

        val session = repository.userSession.first { it != null }
        assertNotNull(session)
        assertEquals("Anjali S. Menon", session?.fullName)
        assertTrue(session?.isLoggedIn == true)
    }
}
