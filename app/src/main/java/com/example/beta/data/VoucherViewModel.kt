package com.example.beta.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class VoucherViewModel : ViewModel() {
    private val col = Firebase.firestore.collection("Voucher")
    private val forms = MutableLiveData<List<Voucher>>()
    private var voucherL = listOf<Voucher>()
    var voucherList = MutableLiveData<List<Voucher>>()


    init {
        col.addSnapshotListener { snap, _ -> forms.value = snap?.toObjects() }
        viewModelScope.launch {
            val vouchers = col.get().await().toObjects<Voucher>()
            col
        }
    }

    init {

        viewModelScope.launch {
            col.addSnapshotListener { snap, _ -> voucherList.value = snap?.toObjects()
                voucherL = snap!!.toObjects()
                runBlocking {
                    //updateResult()
                }
            }
        }
    }


    suspend fun get(code: String): Voucher? {
        return forms.value?.find{ f -> f.code == code }

    }

    fun getAll() = forms

    private fun codeExists(code: String): Boolean {
        if(forms.value?.any{ f -> f.code == code} == true){
            return true
        }
        return false
    }

    fun validate(f: Voucher): Boolean {
        return codeExists(f.code)

    }
}