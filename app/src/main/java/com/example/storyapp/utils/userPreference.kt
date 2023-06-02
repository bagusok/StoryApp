package com.example.storyapp.utils

import android.content.Context
import com.example.storyapp.model.UserModel


class UserPreference(context: Context) {
    private val preferences = context.getSharedPreferences("story_app", Context.MODE_PRIVATE)

    fun setUser(userId: String?, name: String?, token: String?) {
        val editor = preferences.edit()
        editor.putString("userId", userId)
        editor.putString("name", name)
        editor.putString("token", token)
        editor.apply()
    }
    fun getUser(): UserModel {
        val model = UserModel()
        model.userId = preferences.getString("userId", "")
        model.name = preferences.getString("name", "")
        model.token = preferences.getString("token", "")

        return model
    }

    fun clearUser() : Boolean{
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
        return true
    }


}


