package com.example.beta

import android.app.Application
import com.example.beta.data.DB

class App : Application() {

    companion object{
        lateinit var db: DB
    }

    override fun onCreate(){
        super.onCreate()
        db = DB.getInstance(this)

//        GlobalScope.launch {
//            db.cartDao.insert(Cart("1001","Indian Food",1.00,1,"KFC"))
//            db.cartDao.insert(Cart("1002","Chinese Food",2.00,1,"KFC"))
//            db.cartDao.insert(Cart("1003","Malay Food",3.00,1,"KFC"))
//        }
    }

}