package com.capypast.ui.screens.history

import android.graphics.BitmapFactory
import android.icu.text.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capypast.room.entities.ClipType
import com.capypast.room.entities.ClipboardEntity
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import compose.icons.tablericons.LetterCase
import compose.icons.tablericons.Photo
import compose.icons.tablericons.Pin
import compose.icons.tablericons.Pinned
import compose.icons.tablericons.Trash
import java.sql.Date

@Composable
fun HistoryItem(
	entity: ClipboardEntity,
	onPinned: (ClipboardEntity) -> Unit,
	onDelete: (ClipboardEntity) -> Unit,
) {
	Card(
		shape = RoundedCornerShape(12.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface
		),
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
	) {
		Column(
			modifier = Modifier
				.padding(vertical = 4.dp, horizontal = 10.dp)
		) {
			/** ───────────── Заголовок: иконка, дата и кнопка удаления ───────────── */
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						imageVector = when (entity.type) {
							ClipType.TEXT -> TablerIcons.LetterCase
							ClipType.IMAGE -> TablerIcons.Photo
						},
						contentDescription = null,
						tint = MaterialTheme.colorScheme.primary,
						modifier = Modifier.size(20.dp)
					)
					Spacer(Modifier.width(8.dp))
					Text(
						text = DateFormat.getDateTimeInstance()
							.format(Date(entity.timestamp)),
						style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
					)
				}
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.End
				) {
					IconButton(onClick = {  }) {
						Icon(
							imageVector = TablerIcons.Copy,
							contentDescription = "Копировать",
						)
					}
//                    IconButton(onClick = { onProtected(entity) }) {
//                        Icon(
//                            imageVector = TablerIcons.DotsVertical ,
//                            contentDescription = "Управлять",
//                        )
//                    }
					IconButton(onClick = { onPinned(entity) }) {
						Icon(
							imageVector = TablerIcons.Pinned ,
							contentDescription = "Закреп",
						)
					}
//                    IconButton(onClick = {  }) {
//                        Icon(
//                            imageVector = TablerIcons.Shield ,
//                            contentDescription = "Защищённый доступ",
//                        )
//                    }
					IconButton(onClick = { onDelete(entity) }) {
						Icon(
							imageVector = TablerIcons.Trash,
							contentDescription = "В корзину",
						)
					}
				}
			}
			Spacer(Modifier.height(12.dp))

			/** ────────────────────────────── Контент ────────────────────────────── */
			when (entity.type) {
				ClipType.TEXT -> {
					Text(
						text = entity.content,
						style = MaterialTheme.typography.titleMedium,
						lineHeight = 20.sp,
						maxLines = 5,
						overflow = TextOverflow.Ellipsis
					)
				}

				ClipType.IMAGE -> {
					entity.content.let { path ->
						val bitmap = BitmapFactory.decodeFile(path)
						Image(
							bitmap = bitmap.asImageBitmap(),
							contentDescription = null,
							contentScale = ContentScale.Crop,
							modifier = Modifier
								.fillMaxWidth()
								.heightIn(min = 100.dp, max = 200.dp)
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
			/** ──────────────────────────────── Теги ──────────────────────────────── */
			if (entity.tags.isNotBlank()) {
				Spacer(Modifier.height(12.dp))
				Row(
					verticalAlignment = Alignment.CenterVertically,
				) {
					Text(
						text = "#${entity.tags}",
						style = MaterialTheme.typography.labelSmall
							.copy(color = MaterialTheme.colorScheme.primary)
					)
					if (entity.pinned) {
						Column(
							horizontalAlignment = Alignment.End,
							modifier = Modifier.fillMaxWidth()
						) {
							Icon(
								TablerIcons.Pin,
								contentDescription = "Закреплён",
								modifier = Modifier.size(18.dp),
								tint = MaterialTheme.colorScheme.primary
							)
						}
					}
				}
			}
		}
	}
}