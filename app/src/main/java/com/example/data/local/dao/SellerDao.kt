package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SellerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SellerDao {
    @Query("SELECT * FROM sellers ORDER BY id DESC")
    fun getAllSellers(): Flow<List<SellerEntity>>

    @Query("SELECT * FROM sellers WHERE id = :id LIMIT 1")
    fun getSellerById(id: Long): Flow<SellerEntity?>

    @Query("SELECT * FROM sellers WHERE id = :id LIMIT 1")
    suspend fun getSellerByIdDirect(id: Long): SellerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeller(seller: SellerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSellers(sellers: List<SellerEntity>)

    @Update
    suspend fun updateSeller(seller: SellerEntity)

    @Query("UPDATE sellers SET isApproved = :approved WHERE id = :id")
    suspend fun updateApproval(id: Long, approved: Boolean)

    @Query("UPDATE sellers SET totalSalesAmount = totalSalesAmount + :amount, totalOrdersCount = totalOrdersCount + 1 WHERE id = :id")
    suspend fun updateSellerSales(id: Long, amount: Double)
}
