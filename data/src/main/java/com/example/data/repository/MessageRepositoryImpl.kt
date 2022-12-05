package com.example.data.repository

import android.app.Application
import android.net.Uri
import android.util.Log
import com.example.data.utils.FilePath.getFileName
import com.example.data.utils.MyNotificationManager
import com.example.domain.model.ChatModel
import com.example.domain.model.LastMessageModel
import com.example.domain.repository.MessageRepository
import com.example.domain.utils.Resource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val mStorageRef: StorageReference,
    private val application: Application,
    private val myNotificationManager : MyNotificationManager
) : MessageRepository {


    override fun sendMessage(
        chatId: String,
        messageSenderId: String?,
        messageReceiverId: String?,
        messageSenderName: String? ,
        messageReceiverName: String? ,
        messageText: String?
    ) {

        val chatRef = fireStore.collection("chat").document(chatId).collection(chatId)
        val chat = ChatModel(
            messageSenderId,
            messageReceiverId,
            messageSenderName,
            messageReceiverName,
            messageText,
            "",
            "text",
            FieldValue.serverTimestamp()
        )
        chatRef.add(chat)
        updateLastMessages(
            chatId,
            messageSenderName,
            messageReceiverName,
            messageSenderId,
            messageReceiverId,
            "text",
            messageText
        )
    }



    override fun getMessages(
        chatId: String,
        fireBaseResponse: (Resource<List<ChatModel>>) -> Unit
    ) {
        fireStore.collection("chat")
            .document(chatId).collection(chatId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.d("TAG", "Listen failed.", error)
                    fireBaseResponse(Resource.Error<List<ChatModel>>(error.localizedMessage!!.toString()))
                    return@addSnapshotListener
                }


                fireBaseResponse(
                    Resource.Success<List<ChatModel>>(
                        querySnapshot!!.toObjects(
                            ChatModel::class.java
                        )
                    )
                )

            }


    }

    override fun uploadFile(
        chatId: String,
        messageSenderId: String?,
        messageReceiverId: String?,
        messageSenderName: String? ,
        messageReceiverName: String? ,
        fileType: String,
        fileUri: Any,

        ) {
        val fileReference: StorageReference =
            mStorageRef.child(getFileName(application, fileUri as Uri?))
        val uploadTask: UploadTask = fileReference.putFile(fileUri)
        uploadTask.addOnSuccessListener {
            fileReference.downloadUrl.addOnSuccessListener { fileUrl ->
                val chatRef = fireStore.collection("chat").document(chatId).collection(chatId)
                val chat = ChatModel(
                    messageSenderId,
                    messageReceiverId,
                    messageSenderName,
                    messageReceiverName,
                    getFileName(application, fileUri as Uri?),
                    fileUrl.toString(),
                    fileType,
                    FieldValue.serverTimestamp()
                )
                chatRef.add(chat)
                updateLastMessages(
                    chatId,
                    messageSenderName,
                    messageReceiverName,
                    messageSenderId,
                    messageReceiverId,
                    msgType = fileType,
                    messageText = getFileName(application, fileUri as Uri?)
                )

            }
        }
            .addOnFailureListener {
                Log.d("addOnFailureListener", "addOnFailureListener: ${it.message}")
            }
            .addOnProgressListener {
                myNotificationManager.createUploadMediaNotification(it,false)

            }
            .addOnCompleteListener {
                myNotificationManager.createUploadMediaNotification(null,true)


            }

    }

    override fun updateLastMessageForCurrentUser(
        chatId: String?,
        messageSenderName: String?,
        messageReceiverName: String?,
        messageSenderId: String?,
        messageReceiverId: String?,
        msgType: String?,
        message: String?
    ) {


        val lastMessagesRef =
            fireStore.collection("lastMessages").document(messageSenderId.toString())
                .collection(messageSenderId.toString())
        val docRef: Query =
            fireStore.collection("lastMessages").document(messageSenderId.toString())
                .collection(messageSenderId.toString()).whereEqualTo("chatId", chatId)
        docRef.get().addOnSuccessListener { documents ->
            val list: MutableList<String> = ArrayList()
            for (document in documents) {

                list.add(document.id)
            }
            val lastMessageModel =
                LastMessageModel(
                    chatId = chatId,
                    messageSenderName = messageSenderName,
                    messageReceiverName = messageReceiverName,
                    messageSenderId = messageSenderId,
                    messageReceiverId = messageReceiverId,
                    msgTime = FieldValue.serverTimestamp(),
                    msgType = msgType,
                    message = message
                )
            if (documents.size() == 0) {

                lastMessagesRef.add(lastMessageModel)

            }else{
                for (id in list) {
                    fireStore.collection("lastMessages").document(messageSenderId.toString()).collection(messageSenderId.toString()).document(id)
                        .set(lastMessageModel)
                        .addOnSuccessListener {
                            Log.d("ChatActivity", " Updated!")

                        }

                }

            }
            }
        }




    override fun updateLastMessageForPeerUser(
        chatId: String?,
        messageSenderName: String?,
        messageReceiverName: String?,
        messageSenderId: String?,
        messageReceiverId: String?,
        msgType: String?,
        message: String?
    ) {


        val lastMessagesRef =
            fireStore.collection("lastMessages").document(messageReceiverId.toString())
                .collection(messageReceiverId.toString())
        val docRef: Query =
            fireStore.collection("lastMessages").document(messageReceiverId.toString())
                .collection(messageReceiverId.toString()).whereEqualTo("chatId", chatId)
        docRef.get().addOnSuccessListener { documents ->
            val list: MutableList<String> = ArrayList()
            for (document in documents) {

                list.add(document.id)
            }
            val lastMessageModel =
                LastMessageModel(
                    chatId = chatId,
                    messageSenderName = messageSenderName,
                    messageReceiverName = messageReceiverName,
                    messageSenderId = messageSenderId,
                    messageReceiverId = messageReceiverId,
                    msgTime = FieldValue.serverTimestamp(),
                    msgType = msgType,
                    message = message
                )
            if (documents.size() == 0) {

                lastMessagesRef.add(lastMessageModel)

            }else{
                for (id in list) {
                    fireStore.collection("lastMessages").document(messageReceiverId.toString()).collection(messageReceiverId.toString()).document(id)
                        .set(lastMessageModel)
                        .addOnSuccessListener {
                            Log.d("ChatActivity", " Updated!")

                        }

                }

            }
        }
    }

    private fun updateLastMessages(
        chatId: String,
        messageSenderName: String?,
        messageReceiverName: String?,
        messageSenderId: String?,
        messageReceiverId: String?,
        msgType: String?,
        messageText: String?
    ) {
        updateLastMessageForCurrentUser(
            chatId = chatId,
            messageSenderName = messageSenderName,
            messageReceiverName = messageReceiverName,
            messageSenderId = messageSenderId,
            messageReceiverId = messageReceiverId,
            msgType = msgType,
            message = messageText
        )
        updateLastMessageForPeerUser(
            chatId = chatId,
            messageSenderName = messageSenderName,
            messageReceiverName = messageReceiverName,
            messageSenderId = messageSenderId,
            messageReceiverId = messageReceiverId,
            msgType = msgType,
            message = messageText
        )
    }

    override fun getLastMessages(
        myId: String,
        fireBaseResponse: (Resource<List<LastMessageModel>>) -> Unit
    ) {
        fireStore
            .collection("lastMessages")
            .document(myId)
            .collection(myId)
            .orderBy("msgTime", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.d("TAG", "Listen failed.", error)
                    fireBaseResponse(Resource.Error<List<LastMessageModel>>(error.localizedMessage!!.toString()))
                    return@addSnapshotListener
                }


                fireBaseResponse(
                    Resource.Success<List<LastMessageModel>>(
                        querySnapshot!!.toObjects(
                            LastMessageModel::class.java
                        )
                    )
                )

            }
    }



}