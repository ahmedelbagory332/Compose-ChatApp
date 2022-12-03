package com.example.domain.model




 class LastMessageModel  {

    var chatId: String? = null
    var messageSenderName: String? = null
    var messageReceiverName: String? = null
    var messageSenderId: String? = null
    var messageReceiverId: String? = null
    var msgTime: Any? = ""
     var msgType: String? = null
     var message: String? = null




      constructor(
         chatId: String?,
         messageSenderName: String?,
         messageReceiverName: String?,
         messageSenderId: String?,
         messageReceiverId: String?,
         msgTime: Any?,
         msgType: String?,
         message: String?
     ) {
         this.chatId = chatId
         this.messageSenderName = messageSenderName
         this.messageReceiverName = messageReceiverName
         this.messageSenderId = messageSenderId
         this.messageReceiverId = messageReceiverId
         this.msgTime = msgTime
         this.msgType = msgType
         this.message = message
     }

     constructor() {}


 }