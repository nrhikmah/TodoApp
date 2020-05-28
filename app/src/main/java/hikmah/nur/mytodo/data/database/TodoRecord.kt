package hikmah.nur.mytodo.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "todo")
@Parcelize()
data class TodoRecord (
    @PrimaryKey(autoGenerate = true) val id:Long?,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "note")
    val note:String,
    @ColumnInfo(name = "created")
    val created: Date? = null,
    @ColumnInfo(name = "updated")
    val updated: Date? = null,
    @ColumnInfo(name = "due")
    val due: Date? = null

): Parcelable