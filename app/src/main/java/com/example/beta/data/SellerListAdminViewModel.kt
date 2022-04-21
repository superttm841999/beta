package com.example.beta.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class SellerListAdminViewModel : ViewModel() {
    private val col = Firebase.firestore.collection("Food")
    private val foodDetail = MutableLiveData<List<Food>>()
    private var sellerL = listOf<Seller>()
    var sellerList = MutableLiveData<List<Seller>>()

    init {
        col.addSnapshotListener { snap, _ -> foodDetail.value = snap?.toObjects() }
    }

    init {

        viewModelScope.launch {
            APPLICATION_FORM.addSnapshotListener { snap, _ -> sellerList.value = snap?.toObjects()
                sellerL = snap!!.toObjects()
                runBlocking {
                    updateResult()

                }

            }
        }
    }

    private suspend fun updateResult(){
        var list = sellerL.filter {
            it.status == 1}

        for (c in list) {
            c.count = FOODS.whereEqualTo("applicationId", c.docId).whereEqualTo("status","Published").get().await().size()
        }

        sellerList.value = list
    }

    suspend fun getAll(): List<Seller> {
        val applicationForms = APPLICATION_FORM.whereEqualTo("status",1).get().await().toObjects<Seller>()

        for (c in applicationForms) {
            c.count = FOODS.whereEqualTo("applicationId", c.docId).whereEqualTo("status","Published").get().await().size()
        }

        return applicationForms
    }

    suspend fun getAllAdmin(): List<Seller> {
        val applicationForms = APPLICATION_FORM.whereEqualTo("status",1).get().await().toObjects<Seller>()

        for (c in applicationForms) {
            c.count = FOODS.whereEqualTo("applicationId", c.docId).get().await().size()
        }

        return applicationForms
    }

    suspend fun get(id: String): Seller? {
        return APPLICATION_FORM.document(id).get().await().toObject<Seller>()
    }

    suspend fun getFoods(id: String): List<Food> {
        val foods = FOODS.whereEqualTo("applicationId",id).whereEqualTo("status","Published").get().await().toObjects<Food>()

        val applicationForm = get(id)

        for(f in foods){
            f.application = applicationForm!!
        }

        return foods
    }

    suspend fun getFoodsAdmin(id: String): List<Food> {
        val foods = FOODS.whereEqualTo("applicationId",id).get().await().toObjects<Food>()

        val applicationForm = get(id)

        for(f in foods){
            f.application = applicationForm!!
        }

        return foods
    }

    fun getFoodDetail(id: String): Food? {
        return foodDetail.value?.find{ f -> f.id == id }
    }

}