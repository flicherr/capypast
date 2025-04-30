package com.capypast.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.capypast.ui.appbar.ButtonBack
import com.capypast.ui.appbar.TitleAppBar
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    onClickToTrashcan: () -> Unit,
    onClickToBack: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            TopAppBar(
                title = { TitleAppBar(
                    "настройки",
                    modifier = Modifier.align(alignment = Alignment.End)
                ) },
                navigationIcon = { ButtonBack(onClickToBack) },
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )

            Column(
              modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                val context = LocalContext.current
//                ActionButton({}, "синхронизация", Icons.Rounded.Refresh)
                ActionButton({
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }, "включить мониторинг", Icons.Rounded.Build)
                ActionButton(onClickToTrashcan, "корзина", Icons.Rounded.Delete)
                ActionButton({}, "экспорт истории", Icons.Rounded.Share)
                ActionButton({}, "очистить историю", Icons.Rounded.Clear)
                ActionButton({}, "очистить корзину", Icons.Rounded.Close)
            }
        }
    }
}

@Composable
fun ActionButton(onClick: () -> Unit, title: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick() }
                .padding(horizontal = 4.dp, vertical = 14.dp)
                .fillMaxWidth()
        ) {

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(modifier = Modifier.width(14.dp))

            Text(title)
        }
    }
}