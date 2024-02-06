package org.bitc.petpalapp

data class Message(
    var message: String?,
    var sendId: String?
){
    constructor():this("","")
}
