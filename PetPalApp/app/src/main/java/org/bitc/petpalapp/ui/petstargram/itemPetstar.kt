package org.bitc.petpalapp.ui.petstargram

data class itemPetstar(
    var docId:String?=null,
    var email:String?=null,
    var content:String?=null,
    var data:String?=null,
    var filename:String?=null,
    var faviruteCount:Int=0,
    var favorites:Map<String,Boolean> = HashMap()

//    var favorites: MutableMap<String, Boolean> = HashMap()) {
//
//    data class Comment(var uid: String? = null,
//                       var userId: String? = null,
//                       var comment: String? = null,
//                       var timestamp: Long? = null)
//}
)
