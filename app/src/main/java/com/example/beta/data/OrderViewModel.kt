package com.example.beta.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderViewModel: ViewModel() {
    private val col = Firebase.firestore.collection("Order")
    private val countCol = Firebase.firestore.collection("Count")
    private val orders = MutableLiveData<List<Order>>()
    private val counts = MutableLiveData<List<Count>>()

    init {
        col.addSnapshotListener { snap, _ -> orders.value = snap?.toObjects() }
        viewModelScope.launch {
            val orders = col.get().await().toObjects<Order>()
            col.get().await().toObjects<Order>()
        }
    }

    init {
        countCol.addSnapshotListener { snap, _ -> counts.value = snap?.toObjects() }
        viewModelScope.launch {
            val counts = countCol.get().await().toObjects<Count>()
            countCol.get().await().toObjects<Count>()
        }
    }

    fun set(f: Order) = col.document(f.docId).set(f)

    //getUserId
    fun get(userId : String): Order? {
        return orders.value?.find{ f -> f.userId == userId }
    }

    //Read COUNT
    suspend fun getCount(docId: String): Int? {
        val result = countCol.document(docId).get().await()
        return result.data?.get("count").toString().toIntOrNull()
    }

    //Update COUNT
    fun setCount(f: Count) {
        countCol.document(f.docId).set(f)
    }

    private fun idExists(docId: String): Boolean {
        return orders.value?.any{ f -> f.docId == docId} ?: false
    }


    fun validate(f: Order, insert: Boolean = true): String {

        var e = ""

        if (insert) {
            e += if (f.docId == "") "- Id is required.\n"
            else if (idExists(f.docId)) "- Id is duplicated.\n"
            //else if (f.id == "5001") "- Please submit again if only Id got problem.\n"
            else ""

        }

        e += if (f.payment == 0.0) "- Price is required.\n"
        else ""

        return e
    }

}