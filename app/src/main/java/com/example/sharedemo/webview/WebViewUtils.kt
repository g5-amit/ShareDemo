package com.example.sharedemo.webview

import com.fasterxml.jackson.databind.ObjectMapper


class WebViewUtils {

    companion object{

        fun formatScript(function: String, vararg params: Any?): String {
            val builder = StringBuilder("javascript:").append(function).append('(')
            val length = params.size
            for (i in params.indices) {
                if (params[i] is String) {
                    builder.append("\'")
                }
                builder.append(params[i])
                if (params[i] is String) {
                    builder.append("\'")
                }
                if (i != length - 1) {
                    builder.append(",")
                }
            }
            builder.append(')')
            return builder.toString()
        }

        /**
         *  val person = Person("John Doe", 30, listOf("London", "Paris"))
         *  mapObjectToJsonString(person)
         */
        fun mapObjectToJsonString(obj: Any): String {
            val jsonMapper = ObjectMapper()
            return jsonMapper.writeValueAsString(obj)
        }

        /**
         * Ex: val webMainPojo = getObjectFromJsonString(jsonString, WebMainPojo::class.java)
         */
        fun <T : Any?> getObjectFromJsonString(jsonString: String, clazz: Class<T>): T {
            val objectMapper = ObjectMapper()
            return objectMapper.readValue(jsonString, clazz)
        }


    }

}