package com.example.cinemiron.tmp_common.data

interface ApiMapper<Domain, Entity> {
    fun mapToDomain(apiDto:Entity):Domain
}