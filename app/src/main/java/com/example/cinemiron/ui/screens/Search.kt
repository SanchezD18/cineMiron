package com.example.cinemiron.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cinemiron.data.PelisRepository
import com.example.cinemiron.ui.components.CardPelis

@Composable
fun SearchScreen(navController: NavController, modifier: Modifier = Modifier) {
    SearchApp(modifier, navController)
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchApp(modifier: Modifier, navController: NavController) {
    val pelis = remember { PelisRepository.getContactInfo() }
    val textSearch = rememberTextFieldState()
    var textOnSearch by rememberSaveable { mutableStateOf("") }
    val gridState = rememberLazyStaggeredGridState()


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DockedSearchBarConFiltro(
            textFieldState = textSearch,
            onSearch = { textOnSearch = it }
        )

        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            columns = StaggeredGridCells.Fixed(1),
            state = gridState,
        ) {
            items(pelis) { peli ->
                val comentario = peli.name
                if (textOnSearch.isEmpty() || comentario.contains(
                        textOnSearch,
                        ignoreCase = true
                    )
                ) {
                    CardPelis(peli, onClick = {navController.navigate("filminfo")})
                }
            }
        }
    }
}


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DockedSearchBarConFiltro(
        textFieldState: TextFieldState,
        onSearch: (String) -> Unit
    ) {
        var expanded by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(textFieldState.text.toString()) {
            onSearch(textFieldState.text.toString())
        }

        DockedSearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0,
                        length, it) } },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        expanded = false
                    },

                    expanded = false,
                    onExpandedChange = { expanded = false },
                    placeholder = { Text("Search",
                        color = Color.Gray) },
                    colors = SearchBarDefaults.inputFieldColors(
                        focusedTextColor = Color.Red
                    )
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        )
        {

        }
    }