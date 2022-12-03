package com.example.domain.model


class ChatModel {

    var messageSenderId: String? = null
    var messageReceiverId: String? = null
    var messageSenderName: String? = null
    var messageReceiverName: String? = null
    var messageText: String? = null
    var messageFile: String? = null
    var messageType: String? = null
    var timestamp: Any? = ""




    constructor() {}

    constructor(
        messageSenderId: String?,
        messageReceiverId: String?,
        messageSenderName: String?,
        messageReceiverName: String?,
        messageText: String?,
        messageFile: String?,
        messageType: String?,
        timestamp: Any?
    ) {
        this.messageSenderId = messageSenderId
        this.messageReceiverId = messageReceiverId
        this.messageSenderName = messageSenderName
        this.messageReceiverName = messageReceiverName
        this.messageText = messageText
        this.messageFile = messageFile
        this.messageType = messageType
        this.timestamp = timestamp
    }


}