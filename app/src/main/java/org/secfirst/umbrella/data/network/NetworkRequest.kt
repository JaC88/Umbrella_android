package org.secfirst.umbrella.data.network

import org.json.JSONArray
import org.json.JSONObject
import org.secfirst.umbrella.misc.toMatrixUsername

fun registerUserRequest(username: String, password: String): String {
    val req = JSONObject()
    val auth = JSONObject()
    auth.put("type", "m.login.dummy")
    req.put("auth", auth)
    req.put("username", username)
    req.put("password", password)
    return req.toString()
}

fun loginUserRequest(username: String, password: String): String {
    val req = JSONObject()
    req.put("type", "m.login.password")
    req.put("user", username.toMatrixUsername())
    req.put("password", password)
    return req.toString()
}

fun sendMessageRequest(msgtype: String, body: String): String {
    val req = JSONObject()
    req.put("msgtype", msgtype)
    req.put("body", body)
    return req.toString()
}

fun createRoomRequest(name: String): String {
    val req = JSONObject()
    req.put("preset", "private_chat")
    req.put("room_alias_name", name)
    req.put("invite", JSONArray(listOf(name.toMatrixUsername())))
    return req.toString()
}

enum class Direction(val dir: String) {
    BACK("b"),
    FORWARD("f")
}