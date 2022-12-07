package com.example.data.repository

import android.util.Log
import com.example.domain.model.UsersModel
import com.example.domain.repository.UsersRepository
import com.example.domain.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore): UsersRepository {


    override fun getUsers() : Flow<MutableList<UsersModel>>  {
        return fireStore.collection("users")
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.toObjects(UsersModel::class.java)
            }


    }



    override fun getUser(userId:String,fireBaseResponse:(Resource<List<UsersModel>>)->Unit)  {
         fireStore.collection("users")
            .whereEqualTo("userId",userId)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.d("TAG", "Listen failed.", error)
                    fireBaseResponse(Resource.Error<List<UsersModel>>(error.localizedMessage!!.toString()))
                    return@addSnapshotListener
                }


                fireBaseResponse(Resource.Success<List<UsersModel>>(querySnapshot!!.toObjects(UsersModel::class.java)))

            }



    }


}