package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_routes")
data class SavedRouteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val busId: String,
    val busNumber: String,
    val serviceName: String,
    val origin: String,
    val destination: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_session")
data class UserSessionEntity(
    @PrimaryKey
    val id: Int = 1,
    val fullName: String,
    val phoneOrEmail: String,
    val passType: String,
    val passNumber: String,
    val isLoggedIn: Boolean,
    val walletBalance: Double = 350.0
)
