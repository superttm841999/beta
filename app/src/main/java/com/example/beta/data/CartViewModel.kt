package com.example.beta.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beta.App.Companion.db
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    fun getAll(username : String) = db.cartDao.getAll(username)


    fun get(id: String) = db.cartDao.get(id)

    fun getName(name: String) = db.cartDao.get(name)

    fun getShopCart() = db.cartDao.getShopCart()

    fun getShop(shop_name :String, username : String) = db.cartDao.getShop(shop_name, username)

    fun insert(f: Cart) = viewModelScope.launch { db.cartDao.insert(f) }

    fun update(f: Cart) = viewModelScope.launch { db.cartDao.update(f) }

    fun delete(f: Cart) = viewModelScope.launch { db.cartDao.delete(f) }

    fun deleteShop(shop_name : String, username : String) = viewModelScope.launch { db.cartDao.deleteShop(shop_name,username)}

    fun deleteAll() = viewModelScope.launch { db.cartDao.deleteAll() }


    fun getShopAll() =  db.shopDao.getAll()

    fun insertShop(f: Shop) = viewModelScope.launch { db.shopDao.insert(f) }

    fun getShopDelete(shop_name: String) = viewModelScope.launch { db.shopDao.deleteShop(shop_name) }

    fun deleteShopAll() = viewModelScope.launch { db.shopDao.deleteAll() }

    fun validate(f: Cart): String {
        var err = ""

        if(f.count <= 0){
            err+= "- Quantity should be at least 1.\n"
        }else if(f.count >10){
            err+= "- Quantity cannot more than 10.\n"
        }

        return err

    }

}