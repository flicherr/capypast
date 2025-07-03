package com.capypast.ui.overlay

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.capypast.room.entities.ClipEntity
import com.capypast.viewmodel.OverlayViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun OverlayWindow(
	onItemClick: (ClipEntity) -> Unit,
	onDismiss: (Unit) -> Unit,
	viewModel: OverlayViewModel
) {
	val items = viewModel
		.itemsFlow
		.collectAsLazyPagingItems()

	Surface(
		modifier = Modifier
			.padding(vertical = 4.dp)
			.clip(RoundedCornerShape(16.dp))
			.background(MaterialTheme.colorScheme.surface)
			.wrapContentHeight()
			.border(
				1.dp,
				MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
				RoundedCornerShape(16.dp)
			)
			.pointerInput(Unit) {
				detectTapGestures(onTap = { onDismiss })
			},
		tonalElevation = 6.dp,
		color = MaterialTheme.colorScheme.surface
	) {
		LazyColumn(
			modifier = Modifier
				.fillMaxWidth()
				.heightIn(max = 360.dp)
				.clip(RoundedCornerShape(12.dp))
				.background(MaterialTheme.colorScheme.surface)
		) {
			items(
				count = items.itemCount,
				key = { index ->
					items[index]?.id ?: index
				}
			) { index ->
				items[index]?.let { clip ->
					OverlayItem(
						entity = clip,
						onClick = { onItemClick(clip) },
						viewModel = viewModel
					)
				}
				HorizontalDivider(
					modifier = Modifier
						.fillMaxWidth()
						.height(4.dp)
				)
			}

			item {
				val loadState = items.loadState.append
				if (loadState is LoadState.Loading) {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.padding(12.dp),
						contentAlignment = Alignment.Center
					) {
						CircularProgressIndicator(
							strokeWidth = 2.dp,
							modifier = Modifier.size(20.dp)
						)
					}
				}
			}
		}

		if (items.loadState.refresh is LoadState.NotLoading && items.itemCount == 0) {
			Box(modifier = Modifier.fillMaxWidth()) {
				Text(
					"Нет элементов",
					modifier = Modifier.align(Alignment.Center),
					style = MaterialTheme.typography.titleSmall
				)
			}
		}
	}
}
