package com.example.domain.use_case.message_use_case


import com.example.domain.model.ChatModel
import com.example.domain.model.LastMessageModel
import com.example.domain.model.UsersModel
import com.example.domain.repository.MessageRepository
import com.example.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetLastMessagesUseCase @Inject constructor(private val messageRepository: MessageRepository) {



    operator fun invoke(myId:String,fireBaseResponse: (Resource<List<LastMessageModel>>) -> Unit) {

        messageRepository.getLastMessages(myId) {
            fireBaseResponse(it)
        }

    }

}