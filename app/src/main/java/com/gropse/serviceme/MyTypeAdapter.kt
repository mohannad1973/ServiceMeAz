package com.gropse.serviceme

import android.provider.Settings

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.gropse.serviceme.network.NetworkConstants

import java.lang.reflect.Type
import java.util.ArrayList

class MyTypeAdapter : JsonDeserializer<List<Any>> {

    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): List<Any> {
        val vals = ArrayList<Any>()
        if (json.isJsonArray) {
            for (e in json.asJsonArray) {
                vals.add(ctx.deserialize<Any>(e, Any::class.java) as Any)
            }
        } else if (json.isJsonObject) {
            vals.add(ctx.deserialize<Any>(json, Any::class.java) as Any)
        } else {
            throw RuntimeException("Unexpected JSON type: " + json.javaClass)
        }
        return vals


    }
}