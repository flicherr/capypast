package com.capypast.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.capypast.ui.appbar.ButtonBack
import com.capypast.ui.appbar.TitleAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Trashcan(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent,
    ) {
        Column {
            TopAppBar(
                title = { TitleAppBar(
                    "корзина",
                    modifier = Modifier.align(alignment = Alignment.End)
                ) },
                navigationIcon = { ButtonBack(onClick) },
                colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )

            LazyColumn(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
//
            }
        }
    }
}