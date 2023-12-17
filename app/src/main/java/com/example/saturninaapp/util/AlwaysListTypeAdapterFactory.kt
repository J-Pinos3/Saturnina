package com.example.saturninaapp.util
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.ArrayList

class AlwaysListTypeAdapterFactory<E> private constructor() : TypeAdapterFactory {

    override fun <T> create(gson: Gson, typeToken: TypeToken<T>): TypeAdapter<T>? {
        if (!List::class.java.isAssignableFrom(typeToken.rawType)) {
            return null
        }

        val elementType = resolveTypeArgument(typeToken.type)
        @Suppress("UNCHECKED_CAST")
        val elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType)) as TypeAdapter<E>
        @Suppress("UNCHECKED_CAST")
        val alwaysListTypeAdapter = AlwaysListTypeAdapter(elementTypeAdapter).nullSafe() as TypeAdapter<T>

        return alwaysListTypeAdapter
    }

    private fun resolveTypeArgument(type: Type): Type {
        return if (type !is ParameterizedType) {
            Object::class.java
        } else {
            val parameterizedType = type as ParameterizedType
            parameterizedType.actualTypeArguments[0]
        }
    }

    private class AlwaysListTypeAdapter<E>(private val elementTypeAdapter: TypeAdapter<E>) :
        TypeAdapter<List<E>>() {

        @Throws(IOException::class)
        override fun write(out: JsonWriter, list: List<E>) {
            //throw UnsupportedOperationException()
            out.beginArray()
            for (item in list) {
                elementTypeAdapter.write(out, item)
            }
            out.endArray()
        }

        @Throws(IOException::class)
        override fun read(`in`: JsonReader): List<E> {
            val list = ArrayList<E>()
            val token = `in`.peek()
            when (token) {
                JsonToken.BEGIN_ARRAY -> {
                    `in`.beginArray()
                    while (`in`.hasNext()) {
                        list.add(elementTypeAdapter.read(`in`))
                    }
                    `in`.endArray()
                }
                JsonToken.BEGIN_OBJECT, JsonToken.STRING, JsonToken.NUMBER, JsonToken.BOOLEAN -> {
                    list.add(elementTypeAdapter.read(`in`))
                }
                JsonToken.NULL -> throw AssertionError("Must never happen: check if the type adapter configured with .nullSafe()")
                JsonToken.NAME, JsonToken.END_ARRAY, JsonToken.END_OBJECT, JsonToken.END_DOCUMENT -> {
                    throw IOException("Unexpected token: $token")
                }
                else -> throw AssertionError("Must never happen: $token")
            }
            return list
        }
    }

    companion object {
        fun <E> create(): AlwaysListTypeAdapterFactory<E> {
            return AlwaysListTypeAdapterFactory()
        }
    }
}
