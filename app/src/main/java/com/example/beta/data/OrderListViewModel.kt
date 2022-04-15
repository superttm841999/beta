package com.example.beta.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class OrderListViewModel: ViewModel() {
    private val col = Firebase.firestore.collection("OrderFood")
    private val orderDetail = MutableLiveData<List<OrderFood>>()

    init {
        col.addSnapshotListener { snap, _ -> orderDetail.value = snap?.toObjects() }
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