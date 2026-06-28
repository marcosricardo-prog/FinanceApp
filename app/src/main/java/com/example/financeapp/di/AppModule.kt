package com.example.financeapp.di

import androidx.room.Room
import com.example.financeapp.data.firebase.datasource.AuthDataSource
import com.example.financeapp.data.firebase.datasource.FirestoreDataSource
import com.example.financeapp.data.local.datastore.PreferencesManager
import com.example.financeapp.data.local.room.AppDatabase
import com.example.financeapp.data.remote.TransactionApi
import com.example.financeapp.data.repository.AuthRepositoryImpl
import com.example.financeapp.data.repository.DynamicTransactionRepository
import com.example.financeapp.data.repository.TransactionRepositoryFirebaseImpl
import com.example.financeapp.data.repository.TransactionRepositoryRemoteImpl
import com.example.financeapp.data.repository.TransactionRepositoryRoomImpl
import com.example.financeapp.domain.repository.AuthRepository
import com.example.financeapp.domain.repository.TransactionRepository
import com.example.financeapp.domain.usecase.DeleteTransactionUseCase
import com.example.financeapp.domain.usecase.GetTransactionsUseCase
import com.example.financeapp.domain.usecase.InsertTransactionsUseCase
import com.example.financeapp.domain.usecase.UpdateTransactionUseCase
import com.example.financeapp.ui.screens.auth.AuthViewModel
import com.example.financeapp.ui.screens.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // ── Infraestrutura ────────────────────────────────────────────────
    single { PreferencesManager(androidContext()) }
    single { FirestoreDataSource() }
    single { AuthDataSource() }              //fonte de autenticação Firebase

    // Room
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "finance_db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppDatabase>().transactionDao() }

    // Retrofit (MockAPI)
    single {
        Retrofit.Builder()
            .baseUrl("https://6a2dcda42edd4cb330d16bbe.mockapi.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TransactionApi::class.java)
    }

    // ── Repositórios de Transação ─────────────────────────────────────
    single { TransactionRepositoryRoomImpl(get()) }
    single { TransactionRepositoryRemoteImpl(get()) }
    single { TransactionRepositoryFirebaseImpl(get()) }

    // Strategy pattern: repositório dinâmico delega para a impl ativa
    single<TransactionRepository> {
        DynamicTransactionRepository(
            preferencesManager = get(),
            roomImpl = get(),
            remoteImpl = get(),
            firebaseImpl = get()
        )
    }

    // ── Repositório de Autenticação ───────────────────────────────────
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    // ── Use Cases ─────────────────────────────────────────────────────
    factory { GetTransactionsUseCase(get()) }
    factory { InsertTransactionsUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }

    // ── ViewModels ────────────────────────────────────────────────────
    viewModel {
        HomeViewModel(get(), get(), get(), get(), get())
    }
    viewModel {
        AuthViewModel(get())
    }
}
