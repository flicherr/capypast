package com.capypast.ui.fragments

import android.annotation.SuppressLint
import android.content.ClipData
import android.graphics.BitmapFactory
import android.icu.text.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capypast.room.ClipType
import com.capypast.room.ClipboardEntity
import java.sql.Date


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ClipboardHistoryItem(
    entity: ClipboardEntity,
    onDelete: (ClipboardEntity) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 8.dp),
    ) {
        Column(Modifier.padding(14.dp)) {
            // ───────────── Заголовок: иконка, дата и кнопка удаления ─────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (entity.type) {
                            ClipType.TEXT -> Icons.Rounded.FavoriteBorder
                            ClipType.IMAGE -> Icons.Default.Favorite
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
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Закрепить",
                        )
                    }
                    IconButton(onClick = { onDelete(entity) }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Удалить",
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ───────────── Контент ─────────────
//            Surface(
//                onClick = {
//
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.Transparent)
//                    .hoverable(
//                        interactionSource = MutableInteractionSource(),
//                        enabled = false
//                    )
//            ) {
            when (entity.type) {
                ClipType.TEXT -> {
                    Text(
                        text = entity.content.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        lineHeight = 20.sp,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                ClipType.IMAGE -> {
                    entity.imagePath?.let { path ->
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
//            }

            // ───────────── Теги ─────────────
            if (entity.tags.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "#${entity.tags}",
                    style = MaterialTheme.typography.labelSmall
                        .copy(color = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}