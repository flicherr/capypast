package com.capypast.di

import androidx.room.Room
import com.capypast.room.ClipboardDatabase
import com.capypast.room.interactors.ExportBackupInteractor
import com.capypast.room.interactors.ImportBackupInteractor
import com.capypast.room.interactors.MoveToTrashInteractor
import com.capypast.room.interactors.RestoreFromTrashInteractor
import com.capypast.room.repositories.ClipboardRepository
import com.capypast.room.repositories.TrashRepository
import com.capypast.viewmodel.ClipboardViewModel
import com.capypast.viewmodel.OverlayViewModel
import com.capypast.viewmodel.TrashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val capypastModule = module {
	// --- Room ---
	single {
		Room.databaseBuilder(
			context = androidContext(),
			ClipboardDatabase::class.java,
			"cp.db"
		)
			.enableMultiInstanceInvalidation()
			.fallbackToDestructiveMigration(false)
			.build()
	}

	// --- DAO ---
	single { get<ClipboardDatabase>().clipDao() }
	single { get<ClipboardDatabase>().trashDao() }

	// --- Репозитории ---
	singleOf(::ClipboardRepository)
	singleOf(::TrashRepository)

	// --- Интеракторы ---
	singleOf(::ExportBackupInteractor)
	singleOf(::ImportBackupInteractor)
	singleOf(::MoveToTrashInteractor)
	singleOf(::RestoreFromTrashInteractor)

	// --- ViewModels ---
	viewModelOf(::ClipboardViewModel)
	viewModelOf(::TrashViewModel)
	viewModelOf(::OverlayViewModel)
}
