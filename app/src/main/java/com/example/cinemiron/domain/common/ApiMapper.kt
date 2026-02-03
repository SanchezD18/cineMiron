package com.example.cinemiron.domain.common

interface ApiMapper<Domain, Entity> {
    fun mapToDomain(apiDto:Entity):Domain
}