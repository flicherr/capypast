package com.capypast.utils

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

fun authenticate(
	activity: AppCompatActivity,
	onSuccess: () -> Unit,
	onError: () -> Unit
) {
	val biometricManager = BiometricManager.from(activity)

	if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
		!= BiometricManager.BIOMETRIC_SUCCESS) {
		onError()
		return
	}

	val executor: Executor = ContextCompat.getMainExecutor(activity)

	val callback = object : BiometricPrompt.AuthenticationCallback() {
		override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
			super.onAuthenticationSucceeded(result)
			onSuccess()
		}

		override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
			super.onAuthenticationError(errorCode, errString)
			onError()
		}

		override fun onAuthenticationFailed() {
			super.onAuthenticationFailed()
			// Не вызываем onError — пользователь может попробовать ещё раз
		}
	}

	val promptInfo = BiometricPrompt.PromptInfo.Builder()
		.setTitle("Подтвердите личность")
		.setSubtitle("Используйте биометрию или пароль")
		.setAllowedAuthenticators(
			BiometricManager.Authenticators.BIOMETRIC_WEAK or
					BiometricManager.Authenticators.DEVICE_CREDENTIAL
		)
		.build()

	BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
}