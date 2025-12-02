package com.example.cinemiron.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cinemiron.R


@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    HomeAppBar(modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}

@Composable
fun ScrollContent(innerPadding: PaddingValues) {
    val scrollState = rememberScrollState()
    val images = listOf(
        R.drawable.fatum,
        R.drawable.titanic,
        R.drawable.micrimen,
        R.drawable.superman,
        R.drawable.bellabestia,
        R.drawable.gladiator
    )

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Ultimas Vistas",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        ImageRow(images)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Recomendaciones para ti",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        ImageRow(images)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "En Cartelera",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        ImageRow(images)
    }
}

@Composable
fun ImageRow(images: List<Int>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        items(images) { image ->
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 110.dp, height = 150.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}