package com.example.cinemiron.data

data class Pelis(
    val id: Int,
    val name: String,
    val photo: Int,
    val contentDescription: String? = null,
    val sinopsis: String
)
