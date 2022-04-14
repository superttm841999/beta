package com.example.beta.data

import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    var userId : String = "",
    var username : String = "",
    var address : String = "",
    //pending -0  approve -1  reject -2
    var status : Int = 0,
    var category : String = "",
    var approvalUser : String = "",
    var approvalName : String = "",
    var approvalEmail : String = ""
){
    @get:Exclude
    var count: Int = 0

    override fun toString() = name
}

data class Food(
    @DocumentId
    var id: String = "",
    var name: String = "",
    var price: Double = 0.00,
    var description: String = "",
    var image : Blob = Blob.fromBytes(ByteArray(0)),
    var status : String = "",
    var applicationId: String = "",
) {
    @get:Exclude
    var application: Seller = Seller()
}

data class Voucher(
    @DocumentId
    var docId : String = "",
    var name : String = "",
    var code : String = "",
    var value : Int = 0,
    //Invalid -0  Valid -1
    var status : Int = 0,
    var startDate : String = "",
    var endDate : String = "",
)

data class VoucherUsed(
    @DocumentId
    var docId : String = "",
    var username : String = "",
    var voucherId : String = "",
    var voucherName : String = "",
    var voucherCode : String = "",
)

data class OrderFood(
    @DocumentId
    var docId : String = "",
    var orderId : String = "",
    var foodId : String = "",
    var quantity : Int = 0,
){
    @get:Exclude
    var order: Order = Order()
    @get:Exclude
    var application: Seller = Seller()
}

data class Order(
    @DocumentId
    var docId : String = "",
    var payment : Double =  0.00,
    // //Pending -0  Accepted -1  Rejected -2  Done -3
    var status : Int = 0,
    var userId : String = "",
    var sellerId : String = "",
){
    @get:Exclude
    var count: Int = 0
}


val APPLICATION_FORM = Firebase.firestore.collection("Seller")
val FOODS = Firebase.firestore.collection("Food")
val ORDERS = Firebase.firestore.collection("Order")
val ORDER_DETAIL = Firebase.firestore.collection("OrderFood")
