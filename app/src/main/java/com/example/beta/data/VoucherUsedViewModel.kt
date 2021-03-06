package com.example.beta.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class VoucherUsedViewModel : ViewModel() {

    private var voucherId = ""
    private var username = ""

    private val col = Firebase.firestore.collection("VoucherUsed")
    private val countCol = Firebase.firestore.collection("Count")
    private val cat = listOf<VoucherUsed>()
    private val vouchers = MutableLiveData<List<VoucherUsed>>()
    private val counts = MutableLiveData<List<Count>>()

    private var voucherL = listOf<VoucherUsed>()
    var voucherList = MutableLiveData<List<VoucherUsed>>()

    private var voucherL1 = listOf<VoucherUsed>()
    var voucherList1 = MutableLiveData<List<VoucherUsed>>()

    init {

        viewModelScope.launch {
            col.addSnapshotListener { snap, _ -> voucherList1.value = snap?.toObjects()
                voucherL1 = snap!!.toObjects()
            }
        }
    }

    init {

        viewModelScope.launch {
            col.addSnapshotListener { snap, _ -> voucherList.value = snap?.toObjects()
                voucherL = snap!!.toObjects()
            }
        }
    }


    init {
        col.addSnapshotListener { snap, _ -> vouchers.value = snap?.toObjects() }
        viewModelScope.launch {
            val categories = col.get().await().toObjects<VoucherUsed>()
            col
        }
    }

    init {
        countCol.addSnapshotListener { snap, _ -> counts.value = snap?.toObjects() }
        viewModelScope.launch {
            val counts = countCol.get().await().toObjects<Count>()
            countCol.get().await().toObjects<Count>()
        }
    }

    fun checkVoucherUsed(voucherId:String,username:String){
        this.voucherId = voucherId
        this.username = username
        runBlocking { updateResult() }
    }

    private suspend fun updateResult(){
        var list = voucherL1.filter {
            it.voucherId == this.voucherId}.filter{ it.username == this.username }

        voucherList1.value = list
    }


     fun get(docId: String): VoucherUsed? {
        return vouchers.value?.find{ f -> f.voucherId == docId }
    }

    private fun getAll() = vouchers

    fun delete(id: String) {
        col.document(id).delete()
    }

    fun deleteAll() {
        //col.get().addOnSuccessListener { snap -> snap.documents.forEach{ doc -> delete(doc.id) } }
        vouchers.value?.forEach { f-> delete(f.docId) }
    }

    fun set(f: VoucherUsed) {
        col.document(f.docId).set(f)
    }

    fun getCategories() = getAll()

    //Read COUNT
    suspend fun getCount(docId: String): Int? {
        val result = countCol.document(docId).get().await()
        return result.data?.get("count").toString().toIntOrNull()
    }

    //Update COUNT
    fun setCount(f: Count) {
        countCol.document(f.docId).set(f)
    }

    suspend fun getVoucherId(id: String): Voucher? {
        return APPLICATION_FORM.document(id).get().await().toObject()
    }

//     private fun voucherExists(voucherId: String, username:String): Boolean {
//         var check = false
//
//         col.get().addOnSuccessListener {
//             snap ->
//             val list = snap.toObjects<VoucherUsed>()
//
//             list.forEach { f->
//                 if(f.voucherId == voucherId && f.username == username){
//                    check = true
//                     Log.d("validate1",f.voucherId)
//                     Log.d("validate2",f.username)
//                 }
//             }
//
//         }.addOnFailureListener{
//
//         }
//         Log.d("validate3",check.toString())
//         return check
//    }
//
//
//    fun validate(voucherId : String, username :String): Boolean {
//       return voucherExists(voucherId, username)
//
//    }
}