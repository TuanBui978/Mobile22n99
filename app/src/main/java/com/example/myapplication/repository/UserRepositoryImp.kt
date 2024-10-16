package com.example.myapplication.repository

import android.content.Context
import android.content.res.Resources
import com.example.myapplication.R
import com.example.myapplication.model.InternetResult
import com.example.myapplication.model.User
import com.google.firebase.Firebase
import com.google.firebase.FirebaseError.ERROR_EMAIL_ALREADY_IN_USE
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class UserRepositoryImp: UserRepository {
    private val auth = Firebase.auth
    override suspend fun getCurrentUser(): InternetResult<User> {
        return when (val result = auth.currentUser) {
            null -> InternetResult.Failed(Exception())
            else -> InternetResult.Success(User(result))
        }
    }

    override suspend fun signIn(context: Context, email: String, password: String): InternetResult<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user == null) {
                InternetResult.Failed(Exception(context.getString(R.string.sign_in_false)))
            } else {
                InternetResult.Success(User(user))
            }
        }
        catch (e: FirebaseAuthWeakPasswordException) {
            InternetResult.Failed(Exception(context.getString(R.string.password_is_too_short)))
        }
        catch (e: FirebaseAuthUserCollisionException) {
            InternetResult.Failed(Exception(context.getString(R.string.email_not_found)))
        }
        catch (e: FirebaseAuthInvalidCredentialsException) {
            InternetResult.Failed(Exception(context.getString(R.string.email_or_password_is_wrong)))
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun signUp(context: Context, email: String, password: String): InternetResult<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user == null) {
                InternetResult.Failed(Exception(context.getString(R.string.register_false)))
            } else {
                InternetResult.Success(User(user))
            }
        }
        catch (e: FirebaseAuthWeakPasswordException) {
            InternetResult.Failed(Exception(context.getString(R.string.password_is_too_short)))
        }
        catch (e: FirebaseAuthInvalidCredentialsException) {
            InternetResult.Failed(Exception(context.getString(R.string.invalid_email)))
        }
        catch (e: FirebaseAuthUserCollisionException) {
            InternetResult.Failed(Exception(context.getString(R.string.email_already_in_used)))
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }

    }

    override fun signOut() {
        auth.signOut()
    }

    companion object {
        private var userRepository: UserRepositoryImp? = null
        fun getInstance(): UserRepositoryImp {
            if (userRepository == null) {
                userRepository = UserRepositoryImp()
            }
            return userRepository!!
        }
    }

}