package org.secfirst.umbrella.data.database.matrix_room

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.secfirst.umbrella.data.database.AppDatabase
import org.secfirst.umbrella.data.database.StringListConverter

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