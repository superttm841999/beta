package com.example.beta.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderFoodViewModel : ViewModel() {
    private val col = Firebase.firestore.collection("OrderFood")
    private val countCol = Firebase.firestore.collection("Count")
    private val orderFoods = MutableLiveData<List<OrderFood>>()
    private val counts = MutableLiveData<List<Count>>()

    init {
        col.addSnapshotListener { snap, _ -> orderFoods.value = snap?.toObjects() }
        viewModelScope.launch {
            val orders = col.get().await().toObjects<OrderFood>()
            col.get().await().toObjects<OrderFood>()
        }
    }

    init {
        countCol.addSnapshotListener { snap, _ -> counts.value = snap?.toObjects() }
        viewModelScope.launch {
            val counts = countCol.get().await().toObjects<Count>()
            countCol.get().await().toObjects<Count>()
        }
    }

    fun set(f: OrderFood) = col.document(f.docId).set(f)

    //getUserId
    fun get(docId : String): OrderFood? {
        return orderFoods.value?.find{ f -> f.docId == docId }
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
        return orderFoods.value?.any{ f -> f.docId == docId} ?: false
    }


    fun validate(f: OrderFood, insert: Boolean = true): String {

        var e = ""

        if (insert) {
            e += if (f.docId == "") "- Id is required.\n"
            else if (idExists(f.docId)) "- Id is duplicated.\n"
            //else if (f.id == "5001") "- Please submit again if only Id got problem.\n"
            else ""

        }


        return e
    }

}