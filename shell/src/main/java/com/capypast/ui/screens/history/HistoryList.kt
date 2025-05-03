package com.capypast.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.capypast.viewmodel.ClipboardViewModel

@Composable
fun HistoryList(viewModel: ClipboardViewModel) {

	val listState = rememberLazyListState()
	val items = viewModel
		.itemsFlow
		.collectAsLazyPagingItems()
	var prevCount by remember { mutableIntStateOf(0) }

	LaunchedEffect(items.itemCount) {
		if (items.itemCount > prevCount) {
			listState.animateScrollToItem(0)
		}
		prevCount = items.itemCount
	}

	LazyColumn(
		state = listState,
		contentPadding = PaddingValues(horizontal = 20.dp),
		verticalArrangement = Arrangement.spacedBy(4.dp),
		modifier = Modifier
			.padding(PaddingValues(bottom = 16.dp))
	) {
		item {
			Text(
				text = "${items.itemCount} ${textItems(items.itemCount)}",
				modifier = Modifier
					.padding(top = 6.dp, start = 6.dp)
			)
		}

		items(
			count = items.itemCount,
			key = { index ->
				items[index]?.id ?: index
			}
		) { index ->
			val entity = items[index]
			entity?.let {
				HistoryItem(
					entity = it,
					onPinned = { toSetPinned ->
						viewModel.setPinned(toSetPinned)
					},
					onProtected = { toSetProtected ->
						viewModel.setProtected(toSetProtected)
					},
					onDelete = { toTrash ->
						viewModel.moveToTrash(toTrash)
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

fun textItems(count: Int): String {
	val last = count % 10
	return  if (last == 1) "элемент"
	else if (last > 1 && last < 5) "элемента"
	else "элементов"
}