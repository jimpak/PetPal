package org.bitc.petpalapp.model

data class ApplicationItem(
    var docId: String? = null,
    val petsitterId: String? = null,
    val petsitterNickname: String? = null,
    val applierId: String? = null,
    val applierNickname : String? = null,
    var status : String? =null,
    val date : String? =null,
    var petsitterType : String? =null
)
