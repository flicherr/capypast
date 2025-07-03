package com.capypast.ui.screens.clipboard

import android.annotation.SuppressLint
import android.icu.text.DateFormat
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.capypast.helper.BiometricAuthHelper
import com.capypast.room.entities.ClipType
import com.capypast.room.entities.ClipEntity
import com.capypast.ui.components.ConfirmDialog
import com.capypast.utils.setPrimaryClip
import com.capypast.utils.decodeImage
import com.capypast.viewmodel.ClipboardViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import compose.icons.tablericons.DotsVertical
import compose.icons.tablericons.LetterCase
import compose.icons.tablericons.Package
import compose.icons.tablericons.Photo
import compose.icons.tablericons.Pin
import compose.icons.tablericons.Pinned
import compose.icons.tablericons.PinnedOff
import compose.icons.tablericons.Share
import compose.icons.tablericons.Shield
import compose.icons.tablericons.ShieldX
import compose.icons.tablericons.Trash
import java.sql.Date

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ClipItem(
	entity: ClipEntity,
	viewModel: ClipboardViewModel,
	onPinned: (ClipEntity) -> Unit,
	onProtected: (ClipEntity) -> Unit,
	onDelete: (ClipEntity) -> Unit,
	onShare: (ClipEntity) -> Unit,
	isSelected: Boolean,
	onClick: () -> Unit,
	onLongClick: () -> Unit,
) {
	var menuExpanded by remember { mutableStateOf(false) }
	var confirmProtected by remember { mutableStateOf(false) }
	var confirmRemove by remember { mutableStateOf(false) }
	val backgroundColor =
		MaterialTheme.colorScheme.surface.copy(alpha = 0.2f).takeIf { isSelected }
			?: MaterialTheme.colorScheme.surface

	val context = LocalContext.current
	val activity = context as? FragmentActivity ?: return
	val biometricAuthenticator = remember {
		BiometricAuthHelper(activity, context)
	}

	ConfirmDialog(
		show = confirmProtected,
		message =
			"Вы действительно хотите " +
					"${"ВКЛЮЧИТЬ".takeIf { !entity.isProtected } ?: "ОТКЛЮЧИТЬ"} " +
					"защищённый режим для выбранного элемента?",
		onConfirm = {
			biometricAuthenticator.authenticate(
				title = "Подтвердите доступ",
				onSuccess = {
					onProtected(entity)
					menuExpanded = false
				},
				onError = {
					Toast
						.makeText(context, it, Toast.LENGTH_SHORT)
						.show()
				}
			)
		},
		onDismiss = {
			confirmProtected = false
		}
	)
	ConfirmDialog(
		show = confirmRemove,
		message =
			"Вы действительно хотите переместить выбранный элемент в корзину?",
		onConfirm = {
			onDelete(entity)
			menuExpanded = false
		},
		onDismiss = {
			confirmRemove = false
		}
	)

	Card(
		shape = RoundedCornerShape(12.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
		border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
			.takeIf { entity.isProtected }
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(backgroundColor)
				.combinedClickable(
					onClick = onClick,
					onLongClick = onLongClick
				)
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
							style = MaterialTheme
								.typography
								.bodySmall
								.copy(fontWeight = FontWeight.Medium)
						)
					}
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.End
					) {
						IconButton(
							onClick = {
								if (!viewModel.protectedClips.contains(entity)
									&& entity.isProtected
								) {
									biometricAuthenticator.authenticate(
										title = "Подтвердите доступ",
										onSuccess = {
											setPrimaryClip(
												context = context,
												content = entity.content,
												type = entity.type
											)
											Toast.makeText(
												context,
												"Capy!",
												Toast.LENGTH_SHORT
											).show()
										},
										onError = {
											Toast.makeText(
												context,
												"Не удалось скопировать защищённый элемент",
												Toast.LENGTH_SHORT
											).show()
										}
									)
								} else {
									setPrimaryClip(
										context = context,
										content = entity.content,
										type = entity.type
									)
									Toast.makeText(
										context,
										"Capy!",
										Toast.LENGTH_SHORT
									).show()
								}
							}
						) {
							Icon(
								imageVector = TablerIcons.Copy,
								contentDescription = "Копировать",
							)
						}

						IconButton(onClick = { menuExpanded = true }) {
							Icon(
								imageVector = TablerIcons.DotsVertical,
								contentDescription = "Отхер...",
							)
						}

						DropdownMenu(
							expanded = menuExpanded,
							onDismissRequest = { menuExpanded = false },
							containerColor = MaterialTheme.colorScheme.surface
						) {
							Row {
								IconButton(
									onClick = {
										menuExpanded = false
										onPinned(entity)
									}
								) {
									Icon(
										imageVector =
											TablerIcons.Pinned
												.takeIf { !entity.pinned }
												?: TablerIcons.PinnedOff,
										contentDescription = "Закреп",
									)
								}

								IconButton(
									onClick = { confirmProtected = true }
								) {
									Icon(
										imageVector =
											TablerIcons.Shield
												.takeIf { !entity.isProtected }
												?: TablerIcons.ShieldX,
										contentDescription = "Защищённый доступ",
									)
								}

								IconButton(
									onClick = { confirmRemove = true }
								) {
									Icon(
										imageVector = TablerIcons.Trash,
										contentDescription = "В корзину",
									)
								}

								IconButton(
									onClick = {
										menuExpanded = false
										onShare(entity)
									}
								) {
									Icon(
										imageVector = TablerIcons.Share,
										contentDescription = "Поделиться",
									)
								}
							}
						}
					}
				}

				/** ────────────────────────────── Контент ────────────────────────────── */
				when(entity.isProtected) {
					(true && !viewModel.protectedClips.contains(entity)) -> Text(
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
				}

				/** ──────────────────────────────── Теги ──────────────────────────────── */
				Spacer(Modifier.height(8.dp))
				Row(
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier.height(18.dp)
				) {
					Icon(
						TablerIcons.Package,
						contentDescription = "Скопировано из приложения...",
						modifier = Modifier.size(14.dp),
						tint = MaterialTheme.colorScheme.primary
					)
					Spacer(Modifier.width(2.dp))
					Text(
						text = entity.tags.substringAfterLast("."),
						style = MaterialTheme.typography.labelSmall
							.copy(color = MaterialTheme.colorScheme.primary),
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