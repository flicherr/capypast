package com.capypast.ui.overlay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capypast.room.entities.ClipType
import com.capypast.room.entities.ClipEntity
import com.capypast.utils.decodeImage
import com.capypast.viewmodel.OverlayViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.LetterCase
import compose.icons.tablericons.Photo

@Composable
fun OverlayItem(
	entity: ClipEntity,
	onClick: () -> Unit,
	viewModel: OverlayViewModel
) {
	Row(
		modifier = Modifier
			.padding(vertical = 4.dp, horizontal = 8.dp)
			.fillMaxWidth()
			.background(Color.Transparent)
			.combinedClickable(onClick = onClick),
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			when (entity.type) {
				ClipType.TEXT -> TablerIcons.LetterCase
				ClipType.IMAGE -> TablerIcons.Photo
			},
			contentDescription = null,
			tint = MaterialTheme.colorScheme.primary,
			modifier = Modifier.size(20.dp)
		)

		Spacer(Modifier.width(8.dp))
		when (entity.isProtected) {
			(true && entity != viewModel.protectedClip) -> Text(
				text = "< содержимое скрыто >",
				style = MaterialTheme.typography.titleSmall,
				color = MaterialTheme.colorScheme.secondary,
				lineHeight = 20.sp,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis
			)

			else -> when (entity.type) {
				ClipType.TEXT -> {
					Text(
						text = entity.content,
						style = MaterialTheme.typography.titleMedium,
						lineHeight = 20.sp,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis,
						modifier = Modifier.weight(1f)
					)
				}

				ClipType.IMAGE -> {
					entity.content.let { path ->
						decodeImage(path)?.let { image ->
							Image(
								bitmap = image.asImageBitmap(),
								contentDescription = null,
								contentScale = ContentScale.Crop,
								modifier = Modifier
									.fillMaxWidth()
									.heightIn(min = 20.dp, max = 90.dp)
									.clip(RoundedCornerShape(8.dp))
									.border(
										1.dp,
										MaterialTheme
											.colorScheme
											.onSurface
											.copy(alpha = 0.1f),
										RoundedCornerShape(8.dp)
									)
							)
						}
					}
				}
			}
		}
	}
}
