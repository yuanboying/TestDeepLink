package cn.by.test.deeplink.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deep_link")
data class DeeplinkModel constructor(
    @PrimaryKey var id: String,
    var host: String,
    var url: String,
    var description: String?
)