package com.example.presentation.ui.view_models

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.use_case.message_use_case.GetLastMessagesUseCase
import com.example.domain.use_case.message_use_case.GetMessagesUseCase
import com.example.domain.use_case.message_use_case.SendFileUseCase
import com.example.domain.use_case.message_use_case.SendMessageUseCase
import com.example.domain.utils.Resource
import com.example.domain.utils.states.ChatState
import com.example.domain.utils.states.LastMessageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatPageViewModel @Inject constructor(
    val sendMessageUseCase: SendMessageUseCase,
    val getMessagesUseCase: GetMessagesUseCase,
    val sendFileUseCase: SendFileUseCase,
    val getLastMessagesUseCase: GetLastMessagesUseCase,
    val application: Application
) : ViewModel() {

    private val _chatId = mutableStateOf("")
    val textMessage = mutableStateOf(TextFieldValue(""))
    var cameraImageName = mutableStateOf<String>("")

    var recordFileName = mutableStateOf("")
    var sliderPosition = mutableStateOf(0f)

    var isRecordClicked = mutableStateOf(true)
    var isPaused = mutableStateOf(false)
    var length = mutableStateOf(0)
    var currentVoiceMessagePosition = mutableStateOf(-1)
    var oldVoiceMessagePosition = mutableStateOf(-1)
    var selectedIndex = mutableStateOf(-1)


    var isNewRecord = mutableStateOf(true)
    var isRecording = mutableStateOf(true)
    var showTimer = mutableStateOf(false)
    var showIcons = mutableStateOf(textMessage.value.text.isEmpty())


    fun resetMediaRecorder() {
        isNewRecord = mutableStateOf(true)
        isRecording = mutableStateOf(true)
        showTimer = mutableStateOf(false)
        showIcons = mutableStateOf(textMessage.value.text.isEmpty())

    }

    private val _messagesState = mutableStateOf(ChatState())
    val messagesState: State<ChatState> = _messagesState

    private val _lastMessagesState = mutableStateOf(LastMessageState())
    val lastMessagesState: State<LastMessageState> = _lastMessagesState


    fun setChatId(chatId: String) {
        _chatId.value = chatId
    }

    fun sendMessage(
        messageSenderId: String?,
        messageReceiverId: String?,
        messageSenderName: String?,
        messageReceiverName: String?,
        messageText: String?
    ) {
        viewModelScope.launch {
            sendMessageUseCase(
                _chatId.value,
                messageSenderId,
                messageReceiverId,
                messageSenderName,
                messageReceiverName,
                messageText
            )
        }
    }

    fun sendFile(
        messageSenderId: String?,
        messageReceiverId: String?,
        messageSenderName: String?,
        messageReceiverName: String?,
        fileType: String,
        fileUri: Any,
    ) {
        viewModelScope.launch {
            sendFileUseCase(
                _chatId.value,
                messageSenderId,
                messageReceiverId,
                messageSenderName,
                messageReceiverName,
                fileType,
                fileUri,
            )
        }
    }


    fun getMessages() {
        viewModelScope.launch {
            getMessagesUseCase(_chatId.value) { result ->
                when (result) {
                    is Resource.Success -> {
                        _messagesState.value =
                            ChatState(messages = result.data ?: mutableListOf())
                    }
                    is Resource.Error -> {
                        _messagesState.value = ChatState(
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }
                    is Resource.Loading -> {

                    }
                }
            }
        }
    }

    fun getLastMessages(myId: String) {
        viewModelScope.launch {
            getLastMessagesUseCase(myId) { result ->
                when (result) {
                    is Resource.Success -> {
                        _lastMessagesState.value =
                            LastMessageState(messages = result.data ?: mutableListOf())
                    }
                    is Resource.Error -> {
                        _lastMessagesState.value = LastMessageState(
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }
                    is Resource.Loading -> {

                    }
                }
            }
        }
    }


}
