package com.example.beta.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SellerViewModel : ViewModel() {
    private val col = Firebase.firestore.collection("Seller")
    private val forms = MutableLiveData<List<Seller>>()
    private val countCol = Firebase.firestore.collection("Count")
    private val counts = MutableLiveData<List<Count>>()

    init {

        viewModelScope.launch {
            val cols = col.get().await().toObjects<Seller>()
            col.addSnapshotListener { snap, _ -> forms.value = snap?.toObjects() }
        }
    }

    init {
        countCol.addSnapshotListener { snap, _ -> counts.value = snap?.toObjects() }
        viewModelScope.launch {
            val counts = countCol.get().await().toObjects<Count>()
            countCol
        }
    }

    fun get(docId: String): Seller? {
        return forms.value?.find{ f -> f.docId == docId }
    }

    suspend fun getUsername(username:String): Seller?{
        var seller =col.get().await().toObjects<Seller>()

        for(s in seller){
            if(s.username == username){
                return forms.value?.find{ f -> f.docId == s.docId }
            }
        }
        return forms.value?.find{ f -> f.username == username }
    }



    fun getName(name: String): Seller? {
        return forms.value?.find{ f -> f.name == name }
    }

    fun getAll() = forms

    private fun delete(docId: String) {
        col.document(docId).delete()
    }

    fun deleteAll() {
        forms.value?.forEach { f-> delete(f.docId) }
    }

    fun set(f: Seller) {
        col.document(f.docId).set(f)
    }


    private fun idExists(id: String): Boolean {
        return forms.value?.any{ f -> f.docId == id} ?: false
    }

    private fun nameExists(name: String): Boolean {
        return forms.value?.any{ f -> f.name == name} ?: false
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

    fun validate(f: Seller, insert: Boolean = true): String {
        var e = ""
        var regexTime = Regex("^[0-9]{2}/[0-9]{2}$")

        if (insert) {
            e += if (f.docId == "") "- Id is required.\n"
            //else if (!f.id.matches(regexId)) "- Id format is invalid.\n"
            else if (idExists(f.docId)) "- Id is duplicated.\n"
            else if (f.docId == "1001") "- Please submit again if only Id got problem.\n"
            else ""

            e += if (nameExists(f.name)) "- Name is duplicated.\n"
            else ""
        }

        e += if (f.name == "") "- Name is required.\n"
        else if (f.name.length < 3) "- Name is too short.\n"
        else ""

        e += if (f.logo.toBytes().isEmpty()) "- Photo is required.\n"
        else ""

        e += if (f.category == "--SELECT--") "- Type is required. \n"
        else ""

        e += if (f.address == "") "- Address is required.\n"
        else if (f.address.length < 10) "- Address is too short.\n"
        else ""

        e += if (f.open == "") "- Open Time is required.\n"
        else if (f.open.matches(regexTime)) "- Open Time format is invalid.\n"
        else ""

        e += if (f.close == "") "- Close Time is required.\n"
        else if (f.close.matches(regexTime)) "- Close Time format is invalid.\n"
        else ""

        return e
    }
}