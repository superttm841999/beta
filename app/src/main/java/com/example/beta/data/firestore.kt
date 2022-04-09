package com.example.beta.data

import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import java.util.*

data class Count(
    @DocumentId
    var docId   : String = "",
    var count : Int = 0,
)

data class Category(
    @DocumentId
    var docId : String = "",
    var name : String = "",
//Different firebase field different with data class field
//@PropertyName("umur") var age : Int = 0
    var status : String = "",
)

data class Seller(
    @DocumentId
    var docId : String = "",
    var name : String = "",
    var date : Date = Date(),
    var logo : Blob = Blob.fromBytes(ByteArray(0)),
    var username : String = "",
    var address : String = "",
    //pending -0  approve -1  reject -2
    var status : Int = 0,
    var category : String = ""
){
    @get:Exclude
    var count: Int = 0

    override fun toString() = name
}