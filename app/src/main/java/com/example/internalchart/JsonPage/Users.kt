package com.example.internalchart.JsonPage

class Users : ArrayList<UsersItem>()

data class UsersItem(
    val birthday: String,
    val email: String,
    val email_confirmed: Boolean,
    val exercise_type: Int,
    val gender: Int,
    val heart_rate_mac: Double,
    val heart_rate_rest: Int,
    val height: Double,
    val interval_strength: String,
    val memberid: String,
    val name: String,
    val picture: String,
    val target_mets: Int,
    val total_mets: Double,
    val vo2max: Double,
    val weight: Double
)