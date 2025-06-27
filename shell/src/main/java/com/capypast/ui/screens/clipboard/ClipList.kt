package com.capypast.ui.screens.clipboard

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.capypast.helper.BiometricAuthHelper
import com.capypast.room.entities.ClipType
import com.capypast.service.overlay.OverlayActivity
import com.capypast.utils.authenticate
import com.capypast.utils.clipShare
import com.capypast.utils.setPrimaryClip
import com.capypast.viewmodel.ClipboardViewModel
import com.capypast.viewmodel.factories.ClipboardViewModelFactory
import compose.icons.TablerIcons
import compose.icons.tablericons.LetterCase
import compose.icons.tablericons.Photo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.nio.file.WatchEvent

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ClipList() {
	val context = LocalContext.current
	val viewModel: ClipboardViewModel = viewModel(
		factory = ClipboardViewModelFactory(context)
	)

	val listState = rememberLazyListState()
	val items = viewModel
		.itemsFlow
		.collectAsLazyPagingItems()

	var isPanelVisible by remember { mutableStateOf(true) }
	var previousIndex by remember { mutableStateOf(0) }
	var previousOffset by remember { mutableStateOf(0) }

	val activity = context as? FragmentActivity ?: return

	val biometricAuthenticator = remember {
		BiometricAuthHelper(activity, context)
	}

	LaunchedEffect(listState) {
		snapshotFlow {
			listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
		}
			.map { (index, offset) ->
				val scrolledUp = when {
					index < previousIndex -> true
					index > previousIndex -> false
					else -> offset < previousOffset
				}
				previousIndex = index
				previousOffset = offset
				scrolledUp
			}
			.distinctUntilChanged() // реагируем только на изменение направления
			.collectLatest { scrolledUp ->
				isPanelVisible = scrolledUp
			}
	}

	Column {
		AnimatedVisibility(
			visible = isPanelVisible,
			enter = fadeIn() + expandVertically(),
			exit = fadeOut() + shrinkVertically()
		) {
			Row(
				horizontalArrangement = Arrangement.Center,
				modifier = Modifier.fillMaxWidth()
			) {
				FilterChip(
					selected = ClipType.TEXT in viewModel.filter.value,
					onClick = { viewModel.toggleFilter(ClipType.TEXT) },
					label = {
						Icon(
							imageVector = TablerIcons.LetterCase,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary,
						)
						Spacer(Modifier.width(8.dp))
						Text("текст")
					},
					modifier = Modifier.width(150.dp),
					colors = FilterChipDefaults
						.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.surface),
				)

				Spacer(Modifier.width(8.dp))

				FilterChip(
					selected = ClipType.IMAGE in viewModel.filter.value,
					onClick = { viewModel.toggleFilter(ClipType.IMAGE) },
					label = {
						Icon(
							imageVector = TablerIcons.Photo,
							contentDescription = null,
							tint = MaterialTheme.colorScheme.primary,
						)
						Spacer(Modifier.width(8.dp))
						Text("изображения")
					},
					modifier = Modifier.width(150.dp),
					colors = FilterChipDefaults
						.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.surface),
				)
			}
		}

		LazyColumn(
			state = listState,
			contentPadding = PaddingValues(horizontal = 20.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
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
				entity?.let { clip ->
					ClipItem(
						entity = clip,
						onPinned = { toSetPinned ->
							viewModel.setPinned(toSetPinned)
						},
						onProtected = { toSetProtected ->
							viewModel.setProtected(toSetProtected)
						},
						onDelete = { toTrash ->
							viewModel.moveToTrash(toTrash)
						},
						onShare = { toShare ->
							clipShare(context, toShare)
						},
						isSelected = viewModel.selectedItems.contains(clip),
						onClick = {
							if (viewModel.selectionMode()) {
								viewModel.toggleSelection(clip)
							} else if (entity.isProtected && !viewModel.protectedClips.contains(entity)) {
								biometricAuthenticator.authenticate(
									onSuccess = {
										viewModel.addReadAccessClip(entity)
									},
									onError = { errorMessage ->
										Toast.makeText(
											context,
											errorMessage,
											Toast.LENGTH_SHORT
										).show()
										viewModel.clearReadAccessClips()
									}
								)
							} else if (entity.isProtected && viewModel.protectedClips.contains(entity)) {
								viewModel.removeReadAccessClip(clip)
							}
						},
						onLongClick = {
							viewModel.toggleSelection(clip)
						},
						viewModel = viewModel,
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

					else -> {}
				}
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
				"Нет элементов",
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