package com.example.mvvmlib.net.interceptor

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object JsonUtils {
    fun formatJson(json: String?): String? {
        json?.let {
            try {
                var i = 0
                val len = it.length
                while (i < len) {
                    val c = it[i]
                    if (c == '{') {
                        return JSONObject(it).toString(2)
                    } else if (c == '[') {
                        return JSONArray(it).toString(2)
                    } else if (!Character.isWhitespace(c)) {
                        return it
                    }
                    i++
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return json
    }
}