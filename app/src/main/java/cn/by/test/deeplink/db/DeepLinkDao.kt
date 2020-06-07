package cn.by.test.deeplink.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.Single

@Dao
interface DeepLinkDao {

    @Query("select * from deep_link")
    fun getDeepLinkList(): Single<List<DeeplinkModel>>

    @Insert(onConflict = REPLACE)
    fun insertDeepLink(deeplinkModel: DeeplinkModel)

    @Delete
    fun deleteDeepLink(deeplinkModel: DeeplinkModel)
}