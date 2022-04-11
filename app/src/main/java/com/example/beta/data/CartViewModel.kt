package com.example.beta.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beta.App.Companion.db
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    fun getAll() = db.cartDao.getAll()

    fun get(id: String) = db.cartDao.get(id)

    fun getName(name: String) = db.cartDao.get(name)

    fun getShop(shop_name :String) = db.cartDao.getShop(shop_name)

    fun insert(f: Cart) = viewModelScope.launch { db.cartDao.insert(f) }

    fun update(f: Cart) = viewModelScope.launch { db.cartDao.update(f) }

    fun delete(f: Cart) = viewModelScope.launch { db.cartDao.delete(f) }

    fun deleteAll() = viewModelScope.launch { db.cartDao.deleteAll() }

    fun validate(f: Cart): String {
        var err = ""

        if(f.name == ""){
            err+= "- Name is required.\n"
        }

        if(f.price == 0.0){
            err+= "- Price is required.\n"
        }
        else if(f.price < 0.01 || f.price > 999.99){
            err+= "- Price must between 0.01 - 99.99.\n"
        }
        return err

    }

}