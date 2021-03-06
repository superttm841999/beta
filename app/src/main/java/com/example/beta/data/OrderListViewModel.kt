package com.example.beta.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class OrderListViewModel: ViewModel() {
    private var sellerId = ""
    private var progress = 0
    private var status = 0
    private var userId = ""
    private val col = Firebase.firestore.collection("OrderFood")
    private val orderDetail = MutableLiveData<List<OrderFood>>()
    private var orderL = listOf<Order>()
    var orderList = MutableLiveData<List<Order>>()

    private var orderUserL = listOf<Order>()
    var orderUserList = MutableLiveData<List<Order>>()

    private var orderL1 = listOf<Order>()
    var orderList1 = MutableLiveData<List<Order>>()

    init {
        col.addSnapshotListener { snap, _ -> orderDetail.value = snap?.toObjects() }
    }

    init {
        viewModelScope.launch {
            ORDERS.addSnapshotListener { snap, _ -> orderUserList.value = snap?.toObjects()
                orderUserL = snap!!.toObjects()
                runBlocking {
                    updateUserResult()
                }
            }
        }
    }

    init {

        viewModelScope.launch {
            ORDERS.addSnapshotListener { snap, _ -> orderList.value = snap?.toObjects()
            orderL = snap!!.toObjects()
                runBlocking {
                    updateResult()
                }

            }
        }
    }

    init {

        viewModelScope.launch {
            ORDERS.addSnapshotListener { snap, _ -> orderList1.value = snap?.toObjects()
                orderL1 = snap!!.toObjects()
                runBlocking {
                    updateResultWithoutStatus()
                }

            }
        }
    }

    fun setSellerId(sellerId: String, progress: Int, status:Int){
        this.sellerId = sellerId
        this.progress = progress
        this.status = status
        runBlocking { updateResult() }
    }

    fun setSellerIdWithoutStatus(sellerId: String, progress: Int){
        this.sellerId = sellerId
        this.progress = progress
        runBlocking { updateResultWithoutStatus() }
    }

    fun setUserId(userId: String){
        this.userId = userId
        runBlocking { updateUserResult() }
    }

    suspend fun updateUserResult(){
        var list = orderUserL.filter {
           it.userId == this.userId
        }

        for(l in list){
            l.count = ORDER_DETAIL.whereEqualTo("orderId", l.docId).get().await().size()
        }

        val reverse = true
        list.sortedBy { f->f.docId }
        if(reverse)
            list = list.reversed()

        orderUserList.value = list
    }

    suspend fun updateResultWithoutStatus(){
        var list = orderL1.filter {
            it.sellerId == this.sellerId && (it.progress == this.progress || it.progress == 3)
        }

        for(l in list){
            l.count = ORDER_DETAIL.whereEqualTo("orderId", l.docId).get().await().size()
        }

        val reverse = true
        list.sortedBy { f->f.docId }
        if(reverse)
            list = list.reversed()

        orderList1.value = list
    }

    suspend fun updateResult(){
        var list = orderL.filter {
            it.sellerId == this.sellerId}.filter{ it.status == this.status }.filter{ it.progress == this.progress
        }
        orderList.value = list
        Log.d("iddddddd", orderList.value.toString())

        val reverse = true
        list.sortedBy { f->f.docId }
        if(reverse)
            list = list.reversed()

        orderList.value = list
    }

    suspend fun getAll(): List<Order> {
        val orderDetails = ORDERS.get().await().toObjects<Order>()

        for (c in orderDetails) {
            c.count = ORDER_DETAIL.whereEqualTo("orderId", c.docId).get().await().size()
        }

        return orderDetails
    }

    suspend fun getAllAdmin(userId : String): List<Order> {
        val orders = ORDERS.whereEqualTo("userId", userId).get().await().toObjects<Order>()

        for (c in orders) {
            c.count = ORDER_DETAIL.whereEqualTo("orderId", c.docId).get().await().size()
        }

        return orders
    }


    suspend fun getOrderId(orderId: String): List<OrderFood> {

        return ORDER_DETAIL.whereEqualTo("orderId", orderId).get().await().toObjects<OrderFood>()
    }

    suspend fun get(docId: String): Order? {
        return ORDERS.document(docId).get().await().toObject<Order>()
    }

    suspend fun getFoods(orderId: String): List<OrderFood> {
        val orderDetails = ORDER_DETAIL.whereEqualTo("orderId",orderId).get().await().toObjects<OrderFood>()

        val orders = get(orderId)

        for(f in orderDetails){
            f.order = orders!!
        }

        return orderDetails
    }

    suspend fun getFoodsAdmin(orderId: String): List<OrderFood> {
        val orderDetails = ORDER_DETAIL.whereEqualTo("orderId",orderId).get().await().toObjects<OrderFood>()

        val orders = get(orderId)

        for(f in orderDetails){
            f.order = orders!!
        }

        return orderDetails
    }


}