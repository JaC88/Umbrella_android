package org.secfirst.umbrella.data.database.matrix_account

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.data.database.AppDatabase
import org.secfirst.umbrella.data.database.StringListConverter

@Parcelize
@Table(database = AppDatabase::class, useBooleanGetterSetters = true, cachingEnabled = true)
data class Account(
        @PrimaryKey
        var username: String = "",
        @Column
        var password: String = "",
        @Column
        var access_token: String = "",
        @Column
        var home_server: String = "",
        @Column
        var device_id: String = "",
        @Column
        var email: String = "",
        @Column
        var isLoggedIn: Boolean = false,
        @Column(typeConverter = StringListConverter::class)
        @SerializedName("joined_rooms")
        var joined_rooms: MutableList<String> = mutableListOf(),
        @Column
        var next_batch: String = "") : Parcelable

@Table(database = AppDatabase::class, useBooleanGetterSetters = true, cachingEnabled = true)
data class Room(@PrimaryKey
                var room_id: String = "",
                @Column
                var room_alias_name: String = "",
                @Column
                var preset: String = "",
                @Column
                var name: String = "",
                @Column
                var topic: String = "",
                @Column(typeConverter = StringListConverter::class)
                var invite: MutableList<String> = mutableListOf(),
                @Column(typeConverter = StringListConverter::class)
                var joinedMembers: MutableList<String> = mutableListOf())
