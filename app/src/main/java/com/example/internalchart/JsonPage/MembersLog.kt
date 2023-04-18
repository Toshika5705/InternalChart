package com.example.internalchart.JsonPage

class MembersLog : ArrayList<MembersLogItem>()

data class MembersLogItem(
    val calories: Int,
    val distance: Int,
    val hrAverage: Int,
    val hrMax: Int,
    val logTime: String,
    val machineName: String,
    val memberID: String,
    val mets: Double,
    val statusSeconds: Int,
    val targetSeconds: Int,
)