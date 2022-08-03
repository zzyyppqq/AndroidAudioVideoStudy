package com.zyp.androidaudiovideostudy.pref

import android.content.Context
import com.zyp.androidaudiovideostudy.app
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


inline fun <reified R, T> R.pref(default: T) = Preference(app(), "", default, R::class.java.simpleName)

inline fun <reified R, T> R.pref(name: String, default: T) = Preference(app(), name, default, R::class.java.simpleName)

inline fun <reified T> prefName(default: T, prefName: String) = Preference(app(), "", default, prefName)

inline fun <reified T> prefName(name: String, default: T, prefName: String) = Preference(app(), name, default, prefName)

class Preference<T>(val context: Context, val name: String, val default: T, val prefName: String = "default")
    : ReadWriteProperty<Any?, T>{

    private val prefs by lazy {
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(findProperName(property))
    }

    private fun findProperName(property: KProperty<*>) = if(name.isEmpty()) property.name else name

    private fun findPreference(key: String): T{
        return when(default){
            is Long -> prefs.getLong(key, default)
            is Int -> prefs.getInt(key, default)
            is Boolean -> prefs.getBoolean(key, default)
            is String -> prefs.getString(key, default)
            else -> throw IllegalArgumentException("Unsupported type.")
        } as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
         putPreference(findProperName(property), value)
    }

    private fun putPreference(key: String, value: T){
        with(prefs.edit()){
            when(value){
                is Long -> putLong(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                else -> throw IllegalArgumentException("Unsupported type.")
            }
        }.apply()
    }

}