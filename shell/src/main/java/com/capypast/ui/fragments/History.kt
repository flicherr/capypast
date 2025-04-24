package com.capypast.ui.fragments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.capypast.viewmodel.ClipboardViewModel

@Composable
fun History(
    viewModel: ClipboardViewModel
) {
    val items = viewModel.itemsFlow
        .collectAsLazyPagingItems()

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 28.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(PaddingValues(
            bottom = 16.dp,
        )
        )
    ) {
        items(
            count = items.itemCount,
            key = { index ->
                items[index]?.id ?: index
            }
        ) { index ->
            val entity = items[index]
            entity?.let {
                ClipboardHistoryItem(
                    entity = it,
                    onDelete = { toDelete ->
                        viewModel.delete(toDelete)
                    }
                )
            }
        }

        items.apply {
            when (loadState.append) {
                is LoadState.Loading -> item {
                    CircularProgressIndicator(
                        Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }

                is LoadState.Error -> item {
                    Text(
                        "Ошибка загрузки",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { retry() }
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }

                else -> {} // нет состояния
            }
        }
    }
    // индикатор первого запуска
    if (items.loadState.refresh is LoadState.Loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))

        }
    }
    // состояние пустого списка
    if (items.loadState.refresh is LoadState.NotLoading && items.itemCount == 0) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                "История пуста",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
