package com.example.myapplication.repository

import android.content.Context
import android.content.res.Resources
import androidx.core.net.toUri
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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class UserRepositoryImp private constructor(): UserRepository{
    private val auth = Firebase.auth
    private val storage = Firebase.storage
    private val dataBase = Firebase.firestore
    override suspend fun getCurrentUser(context: Context): InternetResult<User> {
        return when (val user = auth.currentUser) {
            null -> InternetResult.Failed(Exception())
            else -> {
                when (val mUser = getUser(context, user.uid)) {
                    is InternetResult.Success->{
                        InternetResult.Success(mUser.data)
                    }
                    else->{
                        InternetResult.Success(User(user))
                    }
                }
            }
        }
    }

    override suspend fun signIn(context: Context, email: String, password: String): InternetResult<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user == null) {
                InternetResult.Failed(Exception(context.getString(R.string.sign_in_false)))
            } else {
                when (val myUser = getUser(context, user.uid)) {
                    is InternetResult.Success -> {
                        InternetResult.Success(myUser.data)
                    }
                    else ->{
                        InternetResult.Success(User(user))
                    }
                }
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

    override suspend fun getUser(context: Context, uid: String): InternetResult<User> {
        return try {
            val result = dataBase.collection("users").document(uid).get().await()
            if (result.exists()) {
                val data = result.toObject(User::class.java)
                return InternetResult.Success(data)
            }
            else {
                return InternetResult.Failed(Exception(context.getString(R.string.empty_user_info)))
            }
        }
        catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun updateAvatar(user: User, avatar: String): InternetResult<User> {
        return try {
            val mainImage = avatar.toUri()
            val mainRef = storage.reference.child("images/${mainImage.lastPathSegment}")
            mainRef.putFile(mainImage).await()
            val url = mainRef.downloadUrl.await()
            user.avatar = url.toString()
            InternetResult.Success(user)
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

    override suspend fun addUser(context: Context, user: User): InternetResult<Void> {
        val uid = user.uid
        return try {
            val result = dataBase.collection("users").document(uid!!).set(user).await()
            InternetResult.Success(null)
        } catch (e: Exception) {
            InternetResult.Failed(e)
        }
    }

    override suspend fun removeUser(context: Context, uid: String): InternetResult<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(context: Context, user: User): InternetResult<Void> {
        val uid = user.uid
        return try {
            val result = dataBase.collection("users").document(uid!!).set(user, SetOptions.merge()).await()
            InternetResult.Success(null)
        } catch (e: Exception) {
            InternetResult.Failed(e)
        }
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