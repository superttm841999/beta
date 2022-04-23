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

class CardViewModel : ViewModel() {
    private var userId = ""
    private val cards = MutableLiveData<List<Card>>()
    private val counts = MutableLiveData<List<Count>>()
    private val countCol = Firebase.firestore.collection("Count")

    private var cardL = listOf<Card>()
    var cardList = MutableLiveData<List<Card>>()


    init {
        viewModelScope.launch {
            CARD.addSnapshotListener { snap, _ -> cardList.value = snap?.toObjects()
                cardL = snap!!.toObjects()
                runBlocking {
                    updateUserResult()
                }
            }
        }
    }

    init {
        countCol.addSnapshotListener { snap, _ -> counts.value = snap?.toObjects() }
        viewModelScope.launch {
            val counts = countCol.get().await().toObjects<Count>()
            countCol.get().await().toObjects<Count>()
        }
    }

    fun setUserId(userId: String){
        this.userId = userId
        runBlocking { updateUserResult() }
    }


    private suspend fun updateUserResult(){
        var list = cardL.filter {
            it.userId == this.userId
        }

        cardList.value = list
    }

    suspend fun getCardId(cardNo: String): List<Card> {

        return CARD.whereEqualTo("cardId", cardNo).get().await().toObjects()
    }

    suspend fun get(docId: String): Card? {
        return CARD.document(docId).get().await().toObject<Card>()
    }

    fun set(f: Card) = CARD.document(f.docId).set(f)

    //Read COUNT
    suspend fun getCount(docId: String): Int? {
        val result = countCol.document(docId).get().await()
        return result.data?.get("count").toString().toIntOrNull()
    }

    //Update COUNT
    fun setCount(f: Count) {
        countCol.document(f.docId).set(f)
    }

    fun delete(docId: String) {
        CARD.document(docId).delete()
    }

    fun deleteAll() {
        //col.get().addOnSuccessListener { snap -> snap.documents.forEach{ doc -> delete(doc.id) } }
        cardList.value?.forEach { f-> delete(f.docId) }
    }


    fun validate(f: Card): String {
        var e = ""
        var regexMastercard = Regex("^5[1-5^\\s*\$][0-9^\\s*\$]{1,17}\$")
        var regexVisa = Regex("^4[0-9^\\s*\$]{2,12}(?:[0-9^\\s*\$]{3})?\$")
        var regexDate = Regex("^[0-9]{2}/[0-9]{2}\$")


        if(f.type == "mastercard"){
            e += if (f.cardNo == "") "- Card No is required.\n"
            else if (!f.cardNo.matches(regexMastercard)) "- Card format is invalid.\n"
            else ""
        }

        if(f.type == "visa"){
            e += if (f.cardNo == "") "- Card No is required.\n"
            else if (!f.cardNo.matches(regexVisa)) "- Card format is invalid.\n"
            else ""
        }

        e += if (f.name == "") "- Name is required.\n"
        else if (f.name.length < 3) "- Name is too short.\n"
        else ""

        e += if (f.address == "") "- Address is required.\n"
        else if (f.address.length < 3) "- Name is too short.\n"
        else ""

        e += if (f.cvv == "") "- CVV is required.\n"
        else if (f.cvv.length != 3) "- CVV is too short.\n"
        else ""

        e += if (f.date == "") "- Date is required.\n"
        else if (!f.date.matches(regexDate)) "- Date format is invalid.\n"
        else ""



        return e
    }

}