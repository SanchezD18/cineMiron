package com.example.cinemiron.data

import com.example.cinemiron.R

object PelisRepository {

    fun getContactInfo(): List<Pelis> = listOf(Pelis(
        id = 1,
        name = "La bella y la bestia",
        photo = R.drawable.bellabestia,
        contentDescription = null,
        sinopsis = "Una joven e inteligente mujer llamada Bella acepta vivir con una bestia aterradora en su castillo encantado para salvar a su padre. A medida que conviven, descubre que bajo su apariencia monstruosa se esconde un príncipe atrapado por un hechizo que solo el amor verdadero puede romper."
    ),
        Pelis(
            id = 2,
            name = "Fattum",
            photo = R.drawable.fatum,
            contentDescription = null,
            sinopsis = "Una película de suspenso y drama que explora los giros del destino y cómo las decisiones de un momento pueden alterar el curso de una vida para siempre, llevando a los personajes por caminos inesperados."
        ),
        Pelis(
            id = 3,
            name = "Gladiator",
            photo = R.drawable.gladiator,
            contentDescription = null,
            sinopsis = "El general romano Máximo Décimo Meridio es traicionado cuando el corrupto hijo del emperador asesina a su familia y lo destierra. Capturado y convertido en esclavo, se eleva como un gladiador para vengarse del hombre que destruyó su vida."
        ),
        Pelis(
            id = 4,
            name = "Mi crimen",
            photo = R.drawable.micrimen,
            contentDescription = null,
            sinopsis = "Una comedia negra donde una actriz en declive ve su carrera resucitar cuando es acusada de un crimen que no cometió, descubriendo que la notoriedad puede ser más valiosa que la inocencia en el mundo del espectáculo."
        ),
        Pelis(
            id = 5,
            name = "El Corredor del Laberinto",
            photo = R.drawable.gladiator,
            contentDescription = null,
            sinopsis = "Un drama intenso que sigue a un personaje que debe enfrentarse a una serie de desafíos extremos que pondrán a prueba sus límites físicos, mentales y emocionales, revelando su verdadera naturaleza."
        ),
        Pelis(
            id = 6,
            name = "Superman",
            photo = R.drawable.superman,
            contentDescription = null,
            sinopsis = "El icónico superhéroe de Krypton, criado como Clark Kent en la Tierra, utiliza sus extraordinarios poderes para proteger a la humanidad mientras equilibra su vida como periodista y su lucha contra el mal, encarnado por el genial Lex Luthor."
        ),
        Pelis(
            id = 7,
            name = "Titanic",
            photo = R.drawable.titanic,
            contentDescription = null,
            sinopsis = "En el transatlántico más lujoso del mundo, el RMS Titanic, la joven aristócrata Rose conoce al libre espíritu Jack durante el viaje inaugural. Su apasionado romance se ve interrumpido cuando el barco choca contra un iceberg en su travesía por el Atlántico Norte."
        ),
        Pelis(
            id = 8,
            name = "La guerra de los mundos",
            photo = R.drawable.waroftheworlds_poster,
            contentDescription = null,
            sinopsis = "Una invasión alienígena repentina y devastadora amenaza con exterminar a la humanidad. Un padre divorciado debe proteger a sus hijos mientras viajan a través de un paisaje caótico y aterrador, luchando por sobrevivir contra fuerzas tecnológicamente superiores."
        ))
}