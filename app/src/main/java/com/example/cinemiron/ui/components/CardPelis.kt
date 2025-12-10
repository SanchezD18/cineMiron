package com.example.cinemiron.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cinemiron.data.Pelis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPelis(
    peli: Pelis,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC2CFBE))
    ) {
        Row(modifier = Modifier.fillMaxWidth()
            .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
         ) {
            Image(
                painter = painterResource(id = peli.photo),
                contentDescription = peli.contentDescription,
                modifier = Modifier
                    .height(120.dp)
                    .width(80.dp)
                    .clip(RoundedCornerShape(25.dp)),
                contentScale = ContentScale.Fit
            )
            Column{
            Text(
                text = peli.name,
                modifier = Modifier.padding(start = 10.dp),
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF000000)
            )
            Text(text = peli.sinopsis,
                modifier = Modifier.padding(start = 10.dp),
                fontSize = 12.sp)
        }}
    }
}