package com.capypast.ui.screens.trashcan

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.capypast.room.entities.TrashEntity
import com.capypast.ui.components.ConfirmDialog
import com.capypast.utils.decodeImage
import compose.icons.TablerIcons
import compose.icons.tablericons.CircleX
import compose.icons.tablericons.LetterCase
import compose.icons.tablericons.Package
import compose.icons.tablericons.Photo
import compose.icons.tablericons.RotateClockwise2
import java.sql.Date

@Composable
fun TrashItem(
	entity: TrashEntity,
	onRestore: (TrashEntity) -> Unit,
	onDelete: (TrashEntity) -> Unit,
) {
	var confirmRestore by remember { mutableStateOf(false) }
	var confirmDelete by remember { mutableStateOf(false) }

	ConfirmDialog(
		show = confirmRestore,
		message =
			"Вы действительно хотите восстановить выбранный элемент?",
		onConfirm = { onRestore(entity) },
		onDismiss = { confirmRestore = false }
	)

	ConfirmDialog(
		show = confirmDelete,
		message =
			"Вы действительно хотите безвозвратно удалить выбранный элемент?",
		onConfirm = { onDelete(entity) },
		onDismiss = { confirmDelete = false }
	)

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
				.padding(top = 0.dp, start = 10.dp, end = 10.dp, bottom = 4.dp)
		) {
			/** ───────────── Заголовок: иконка, дата и кнопка удаления ───────────── */
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {
				Row(verticalAlignment = Alignment.CenterVertically) {
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
						style = MaterialTheme.typography.bodySmall
							.copy(fontWeight = FontWeight.Medium)
					)
				}
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.End
				) {
					IconButton(onClick = { confirmRestore = true }) {
						Icon(
							imageVector = TablerIcons.RotateClockwise2,
							contentDescription = "Восстановить",
						)
					}

					IconButton(onClick = { confirmDelete = true }) {
						Icon(
							imageVector = TablerIcons.CircleX,
							contentDescription = "Удалить",
						)
					}
				}
			}

			/** ────────────────────────────── Контент ────────────────────────────── */
			when (entity.type) {
				ClipType.TEXT -> {
					Text(
						text = entity.content,
						style = MaterialTheme.typography.titleMedium,
						lineHeight = 20.sp,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis
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
									.heightIn(min = 50.dp, max = 240.dp)
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

			/** ──────────────────────────────── Теги ──────────────────────────────── */
			Spacer(Modifier.height(8.dp))
			Row(
				verticalAlignment = Alignment.CenterVertically,
			) {
				Icon(
					TablerIcons.Package,
					contentDescription = "Скопировано из приложения...",
					modifier = Modifier.size(14.dp),
					tint = MaterialTheme.colorScheme.primary
				)
				Spacer(Modifier.width(2.dp))
				Text(
					text = entity.tags,
					style = MaterialTheme.typography.labelSmall
						.copy(color = MaterialTheme.colorScheme.primary)
				)
			}
		}
	}
}