package com.example.beta.address

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference

data class Address(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var phone: String = "",
    var state: String = "",
    var district: String = "",
    var postalCode: String = "",
    var detailAddress: String = "",
    var user: DocumentReference? = null,
    var default: Int = 0
)