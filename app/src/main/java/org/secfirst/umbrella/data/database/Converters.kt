package org.secfirst.umbrella.data.database

import com.raizlabs.android.dbflow.converter.TypeConverter


class StringListConverter : TypeConverter<String, MutableList<String>>() {
    val separator = ","

    override fun getDBValue(model: MutableList<String>?): String =
            if (model == null || model.isEmpty())
                ""
            else
                model.joinToString(separator = separator) { it }

    override fun getModelValue(data: String?): MutableList<String> {
        return data?.split(separator)?.toMutableList() ?: mutableListOf()
    }
}