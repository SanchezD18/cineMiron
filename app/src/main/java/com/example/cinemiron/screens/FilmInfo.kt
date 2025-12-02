package com.example.cinemiron.screens


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.cinemiron.R

import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.example.cinemiron.ui.theme.Primary

@Composable
fun FilmInfo(navController: NavController, modifier: Modifier){
    Column(modifier.fillMaxSize()

    ) {
        TopFilmColumn()
    }
}

var descripcion : String = "alñjsdklñfjsdklñjflñsjñkldfjklñsjdfklñjasklñfjklñasjdklñfjasklñdjf" +
        "ajsdklajsdklasjdkakjsdklaskjdklaskldjklajsjdkajsjkdkaksjdklajsjkd" +
        "kahdjkajksdajksdjajkdjkadasjkdajkshdasdadasdasdajfaskldjfasdfasdfasdf" +
        "asldhsjkfhjklashfjasjklhdfjklashdjklfjlashdfjklhasjklfhjklasdhfjklashdfklj"

@Composable
fun TopFilmColumn(){

    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxWidth()
    ) {
        item { TopFilmInfo() }
        item { DescriptionRow(descripcion) }
        item { HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp,horizontal = 22.dp),
            thickness = DividerDefaults.Thickness,
            color = Primary
        ) }
        item { RatingRow() }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp,horizontal = 22.dp),
                thickness = DividerDefaults.Thickness,
                color = Primary
            )
        }
    }
}
//Abstraer componentes y pasar parametros para datos pelicula
@Composable
fun TopFilmInfo(){
    Row(Modifier
        .fillMaxWidth()
        .height(275.dp)
        .padding(horizontal = 22.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.weight(1f)
        ) {
            Text(
                text = "Titulo",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "2000 · DIRECTED BY",
            )
            Text("Director Generico")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                TrailerButton()
                //Pasar parametros de duracion
                Text(
                    "420 mins"
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.waroftheworlds_poster),
            contentDescription = null,
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun TrailerButton() {
    Button(
        onClick = { print("Hello") },
        Modifier
            .height(24.dp)
            .width(104.dp)
            .padding(horizontal = 4.dp),
        contentPadding = PaddingValues(2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null
            )
            Text("TRAILER")
        }
    }
}

@Composable
fun DescriptionRow(description: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        Modifier.padding(horizontal = 22.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = description,
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable { expanded = !expanded }
        )
        if (!expanded) {
            Text(
                "Ver más...",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { expanded = !expanded }
            )
        } else{
            Text(
                "Mostrar menos.",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { expanded = !expanded }
            )
        }
    }
}

@Composable
fun DescriptionText(){
    Text(
        "alñjsdklñfjsdklñjflñsjñkldfjklñsjdfklñjasklñfjklñasjdklñfjasklñdjf" +
                "ajsdklajsdklasjdkakjsdklaskjdklaskldjklajsjdkajsjkdkaksjdklajsjkd" +
                "kahdjkajksdajksdjajkdjkadasjkdajkshdasdadasdasdajfaskldjfasdfasdfasdf" +
                "asldhsjkfhjklashfjasjklhdfjklashdjklfjlashdfjklhasjklfhjklasdhfjklashdfklj"

    )
}


@Composable
fun RatingRow(){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center


    ) {
        RatingColumn()
    }
}

@Composable
fun RatingColumn(){
    var ratingValue : Double = 4.7
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    )
    {
        Text(
            text = ratingValue.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        StarRatingBar(
            rating = ratingValue,
            maxStars = 5,
            starSize = 40.dp,
            activeColor = Color.White, // Dorado
            inactiveColor = Color.DarkGray
        )
    }
}

@Composable
fun StarRatingBar(
    rating: Double,
    maxStars: Int = 5,
    starSize: Dp = 32.dp,
    activeColor: Color = Color.Yellow,
    inactiveColor: Color = Color.Gray
) {
    Row(
        modifier = Modifier.wrapContentSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val starRating = when {
                rating >= i -> 1.0 // Estrella completa
                rating > i - 1 -> rating - (i - 1) // Estrella parcial
                else -> 0.0 // Estrella vacía
            }

            StarIcon(
                fillRatio = starRating,
                size = starSize,
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
        }
    }
}


@Composable
fun StarIcon(
    fillRatio: Double,
    size: Dp,
    activeColor: Color,
    inactiveColor: Color
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Estrella de fondo (inactiva)
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = inactiveColor,
            modifier = Modifier.size(size)
        )

        // Estrella activa con recorte
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = activeColor,
            modifier = Modifier
                .size(size)
                .drawWithContent {
                    drawIntoCanvas { canvas ->
                        canvas.save()
                        // Crear región de recorte
                        canvas.clipRect(
                            left = 0f,
                            top = 0f,
                            right = size.toPx() * fillRatio.toFloat(),
                            bottom = size.toPx()
                        )
                        drawContent()
                        canvas.restore()
                    }
                }
        )
    }
}



//@Preview
//@Composable
//fun PreviewFilmInfo(){
//    FilmInfo()
//}