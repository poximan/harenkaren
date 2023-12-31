package com.example.demo.model
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "report_table")
data class Report(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "pto_obs_censo")
    var fishingType: String,

    @ColumnInfo(name = "ctx_social")
    var specie: String,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "photo_path")
    var photoPath: String,

    @ColumnInfo(name="latitude")
    var latitude: Double?,

    @ColumnInfo(name="longitude")
    var longitude: Double?
):Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }
}

