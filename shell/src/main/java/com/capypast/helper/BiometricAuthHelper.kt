package com.capypast.helper

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuthHelper(
	private val activity: FragmentActivity,
	private val context: Context
) {
	fun authenticate(
		title: String = "Подтверждение личности",
		description: String = "",
		onSuccess: () -> Unit,
		onError: (String) -> Unit
	) {
		val promptInfo = BiometricPrompt.PromptInfo.Builder()
			.setTitle(title)
			.setDescription(description)
			.setAllowedAuthenticators(
				BiometricManager.Authenticators.BIOMETRIC_STRONG or
						BiometricManager.Authenticators.DEVICE_CREDENTIAL
			)
			.build()

		BiometricPrompt(
			activity,
			ContextCompat.getMainExecutor(context),
			object : BiometricPrompt.AuthenticationCallback() {
				override fun onAuthenticationSucceeded(
					result: BiometricPrompt.AuthenticationResult
				) = onSuccess()

				override fun onAuthenticationError(
					errorCode: Int,
					errString: CharSequence
				) = onError(errString.toString())

				override fun onAuthenticationFailed() =
					onError("Аутентификация не пройдена")
			}
		).authenticate(promptInfo)
	}
}