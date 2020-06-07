package cn.by.test.deeplink.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DeeplinkModel::class], version = 2)
abstract class DeepLinkDatabase : RoomDatabase() {

    companion object {
        private const val DEEP_LINK_DATABASE_NAME = "deep_link_db"

        private var INSTANCE: DeepLinkDatabase? = null

        private fun create(context: Context) =
            Room
                .databaseBuilder(
                    context.applicationContext,
                    DeepLinkDatabase::class.java,
                    DEEP_LINK_DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()


        @Synchronized
        fun get(context: Context): DeepLinkDatabase {
            if (INSTANCE == null) {
                INSTANCE = create(context.applicationContext)
            }
            return INSTANCE!!
        }
    }

    abstract fun deepLinkDao(): DeepLinkDao
}