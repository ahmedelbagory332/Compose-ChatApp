package com.example.data.repository

import android.annotation.SuppressLint
import com.example.domain.model.UsersModel
import com.example.domain.repository.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore : FirebaseFirestore,
    private val profileUpdates:UserProfileChangeRequest.Builder,
    private val userModel: UsersModel,
    ): AuthRepository {

     @SuppressLint("SuspiciousIndentation")
     override fun signUp(firstName: String, lastName: String, email: String, password: String):Flow<String> = flow{

       val createUser: Task<AuthResult> =  firebaseAuth.createUserWithEmailAndPassword(email, password)
            if (createUser.await().user!=null)
                userProfile(firstName, lastName, email).collect{
                    emit(it)

                }
            else
                emit(createUser.exception!!.localizedMessage!!.toString())
    }

    override fun signIn(email: String, password: String):Flow<String> = flow{

        val logInUser: Task<AuthResult> =  firebaseAuth.signInWithEmailAndPassword(email, password)
        if (logInUser.await().user!=null)
            emit("Sign In Successful")
        else
            emit(logInUser.exception!!.localizedMessage!!.toString())
    }




    private fun userProfile(firstName: String, lastName: String, email: String):Flow<String> = flow{

        try {
            if (firebaseAuth.currentUser != null) {

                val updateUserName = profileUpdates.setDisplayName("$firstName $lastName").build()
                coroutineScope {
                    val job = async{
                        firebaseAuth.currentUser!!.updateProfile(updateUserName)
                    }
                    job.await()
                        userModel.name = "$firstName $lastName"
                        userModel.email = email
                        userModel.userId = firebaseAuth.currentUser!!.uid
                        userModel.type = "offline"
                        userModel.lastSeen = FieldValue.serverTimestamp()
                        userModel.imageProfile = ""
                        fireStore.collection("users").add(userModel)
                       emit("Signed up Success, Welcome")

                }
            }
        }catch (e:Exception){
            emit(e.localizedMessage!!.toString())
        }



    }



}