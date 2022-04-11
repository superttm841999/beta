package com.example.beta.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FoodViewModel : ViewModel() {
    private val col = Firebase.firestore.collection("Food")
    private val countCol = Firebase.firestore.collection("Count")
    private val foods = MutableLiveData<List<Food>>()
    private val counts = MutableLiveData<List<Count>>()

    init {
        col.addSnapshotListener { snap, _ -> foods.value = snap?.toObjects() }
        viewModelScope.launch {
            val foods = col.get().await().toObjects<Food>()
            col
        }
    }

    init {
        countCol.addSnapshotListener { snap, _ -> counts.value = snap?.toObjects() }
        viewModelScope.launch {
            val counts = countCol.get().await().toObjects<Count>()
            countCol
        }
    }

    fun set(f: Food) = col.document(f.id).set(f)

    fun get(id: String): Food? {
        return foods.value?.find{ f -> f.id == id }
    }

    //Read COUNT
    fun getCount(docId: String): Count? {
        return counts.value?.find{ f -> f.docId == docId }
    }

    //Update COUNT
    fun setCount(f: Count) {
        countCol.document(f.docId).set(f)
    }

    private fun idExists(id: String): Boolean {
        return foods.value?.any{ f -> f.id == id} ?: false
    }

    private fun nameExists(name: String): Boolean {
        return foods.value?.any{ f -> f.name == name} ?: false
    }

    fun validate(f: Food, insert: Boolean = true): String {

        var e = ""

        if (insert) {
            e += if (f.id == "") "- Id is required.\n"
            else if (idExists(f.id)) "- Id is duplicated.\n"
            else ""

            e += if (f.name == "") "- Name is required.\n"
            else if (f.name.length < 3) "- Name is too short.\n"
            else if (nameExists(f.name)) "- Name is duplicated.\n"
            else ""
        }

        e += if (f.image.toBytes().isEmpty()) "- Photo is required.\n"
        else ""

        e += if (f.price == 0.0) "- Price is required.\n"
        else ""

        return e
    }

}