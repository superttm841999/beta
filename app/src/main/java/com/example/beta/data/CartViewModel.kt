package com.example.beta.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beta.App.Companion.db
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    fun getAll(username : String) = db.cartDao.getAll(username)

    fun get(id: String) = db.cartDao.get(id)

    fun getName(name: String) = db.cartDao.get(name)

    fun getShop(shop_name :String, username : String) = db.cartDao.getShop(shop_name, username)

    fun insert(f: Cart) = viewModelScope.launch { db.cartDao.insert(f) }

    fun update(f: Cart) = viewModelScope.launch { db.cartDao.update(f) }

    fun delete(f: Cart) = viewModelScope.launch { db.cartDao.delete(f) }

    fun deleteShop(shop_name : String, username : String) = viewModelScope.launch { db.cartDao.deleteShop(shop_name,username)}

    fun deleteAll() = viewModelScope.launch { db.cartDao.deleteAll() }

    fun validate(f: Cart): String {
        var err = ""

        if(f.count == 0){
            err+= "- Quantity should be at least 1.\n"
        }

        return err

    }

}