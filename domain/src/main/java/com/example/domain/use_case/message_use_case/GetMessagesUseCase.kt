package com.example.domain.use_case.message_use_case


import com.example.domain.model.ChatModel
import com.example.domain.model.UsersModel
import com.example.domain.repository.MessageRepository
import com.example.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(private val messageRepository: MessageRepository) {



    operator fun invoke(chatId:String,fireBaseResponse: (Resource<List<ChatModel>>) -> Unit) {

        messageRepository.getMessages(chatId) {
            fireBaseResponse(it)
        }

    }

}