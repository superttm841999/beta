package com.example.beta.address

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AddressViewModel(username: String): ViewModel(){
    private val addressDB = Firebase.firestore.collection("Address")
    private val userDB = Firebase.firestore.collection("Users")
    private val userRef = userDB.document(username)
    val addresses = MutableLiveData<List<Address>>()


    init {
        addressDB.whereEqualTo("user", userRef).addSnapshotListener { snap, _ ->
            addresses.value = snap?.toObjects()
        }
    }

    fun getAll() = addresses.value

    fun getAddressDetail(id: String): Address? {
        return addresses.value?.find{ f -> f.id == id }
    }

    suspend fun updateDefault(): QuerySnapshot? {
        return addressDB.whereEqualTo("user", userRef).get().await()
    }

    fun getSize(): Int{
        return addresses.value?.size ?: 0
    }





}