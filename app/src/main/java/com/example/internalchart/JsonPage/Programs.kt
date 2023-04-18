package com.example.internalchart.JsonPage

class Programs : ArrayList<ProgramsItem>()

data class ProgramsItem(
    val createdtime: String,
    val download: Int,
    val fitnesstype: Double,
    val level: Int,
    val memberID: String,
    val mets: Double,
    val program: String,
    val status: Int,
    val subtitle: String,
    val title: String,
    val uniqueID: String,
    val updatedtime: String
)