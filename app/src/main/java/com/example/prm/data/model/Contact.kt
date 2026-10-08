package com.example.prm.data.model

data class Contact (
    val emails: List<String>,
    val phones: List<String>,
    val location: String,
    val preferredContact: String
)