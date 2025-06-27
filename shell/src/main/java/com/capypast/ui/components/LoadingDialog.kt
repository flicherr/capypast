package com.capypast.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog(show: Boolean, message: String = "Пожалуйста, подождите...") {
	if (show) {
		Dialog(onDismissRequest = {}) {
			Surface(
				shape = RoundedCornerShape(16.dp),
				color = MaterialTheme.colorScheme.surface,
				tonalElevation = 8.dp
			) {
				Column(
					modifier = Modifier
						.padding(24.dp)
						.width(IntrinsicSize.Min),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					CircularProgressIndicator()
					Spacer(modifier = Modifier.height(16.dp))
					Text(message)
				}
			}
		}
	}
}
