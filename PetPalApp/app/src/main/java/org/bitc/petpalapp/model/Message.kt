package org.bitc.petpalapp.model

data class Message(
    var message: String?,
    var sendId: String?,


){
    constructor():this("","")
}
