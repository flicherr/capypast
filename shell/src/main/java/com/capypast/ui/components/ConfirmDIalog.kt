package com.capypast.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
	show: Boolean,
	title: String = "capypast",
	message: String,
	onConfirm: () -> Unit,
	onDismiss: () -> Unit
) {
	if (show) {
		AlertDialog(
			onDismissRequest = onDismiss,
			title = { Text(title) },
			text = { Text(message) },
			confirmButton = {
				TextButton(onClick = {
					onConfirm()
					onDismiss()
				}) {
					Text("да")
				}
			},
			dismissButton = {
				TextButton(onClick = onDismiss) {
					Text("отмена")
				}
			}
		)
	}
}
