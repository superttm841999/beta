package com.example.beta.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

//CART
@Entity
data class Cart (
    @PrimaryKey
    var id   : String,
    var name : String,
    var price: Double,
    var count: Int=0,
    var shop_name : String,
)

@Dao
interface CartDao {
    @Query("SELECT * FROM Cart")
    fun getAll(): LiveData<List<Cart>>

    @Query("SELECT * FROM Cart WHERE id = :id")
    fun get(id :String): LiveData<Cart>

    @Query("SELECT * FROM Cart WHERE name = :name")
    fun getName(name :String): LiveData<Cart>

    @Query("SELECT * FROM Cart WHERE shop_name = :shop_name")
    fun getShop(shop_name :String): LiveData<List<Cart>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(f:Cart) //Long -> row id

    @Update
    suspend fun update(f:Cart) //Int -> count

    @Delete
    suspend fun delete(f:Cart) //Int -> count

    @Query("DELETE FROM Cart")
    suspend fun deleteAll()
}

@Database(
    entities = [Cart::class],
    version = 1,
    exportSchema = false
)

abstract class DB : RoomDatabase() {
    abstract val cartDao: CartDao
    companion object{
        @Volatile
        private var instance:DB? =null

        @Synchronized
        fun getInstance(context: Context):DB{
            instance = instance ?: Room
                .databaseBuilder(context, DB::class.java, "database.db")
                // .createFromAsset("importDb.db")
                .fallbackToDestructiveMigration()
                .build()
            return instance!!
        }
    }
}
