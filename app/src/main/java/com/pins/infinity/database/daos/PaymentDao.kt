package com.pins.infinity.database.daos

import androidx.room.*
import com.pins.infinity.database.models.*
import io.reactivex.Single


/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlans(planItems: List<PlanItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUssd(ussdItem: UssdItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecovery(recoveryItem: RecoveryItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertInit(initItem: InitItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComplete(completeItem: CompleteItem)

    @Query("SELECT * FROM PlanItem")
    fun getPlanItems(): Single<List<PlanItem>>

    @Query("SELECT * FROM PlanItem WHERE `plan` LIKE :plan")
    fun getFilteredPlans(plan: String): Single<List<PlanItem>>

    @Query("SELECT * FROM UssdItem")
    fun getUssd(): Single<UssdItem>

    @Query("SELECT * FROM RecoveryItem")
    fun getRecovery(): Single<RecoveryItem>

    @Query("SELECT * FROM InitItem")
    fun getInitItem(): Single<InitItem>

    @Query("SELECT * FROM CompleteItem")
    fun getCompleteItem(): Single<CompleteItem>

    @Query("DELETE FROM PlanItem")
    fun clearPlanItem()

    @Query("DELETE FROM UssdItem")
    fun clearUssdItem()

    @Query("DELETE FROM RecoveryItem")
    fun clearRecoveryItem()

    @Query("DELETE FROM InitItem")
    fun clearInitItem()

    @Query("DELETE FROM CompleteItem")
    fun clearCompleteItem()

}