package com.example.financeapp.data.repository

import com.example.financeapp.data.firebase.datasource.AuthDataSource
import com.example.financeapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val dataSource: AuthDataSource
) : AuthRepository {

    override val currentUser: Flow<FirebaseUser?> = dataSource.currentUser

    override val isLoggedIn: Boolean
        get() = dataSource.currentUserSnapshot != null

    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> =
        dataSource.signIn(email, password)

    override suspend fun signUp(email: String, password: String): Result<FirebaseUser> =
        dataSource.signUp(email, password)

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        dataSource.sendPasswordResetEmail(email)

    override fun signOut() = dataSource.signOut()
}
