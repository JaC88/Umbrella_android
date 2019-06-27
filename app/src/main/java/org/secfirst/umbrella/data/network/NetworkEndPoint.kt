package org.secfirst.umbrella.data.network

object NetworkEndPoint {
    const val BASE_URL = "https://api.secfirst.org/"
    const val FEED_LIST = "v3/feed?"

    //MATRIX
    const val MATRIX_BASE_URL = "https://comms.secfirst.org/"
    const val MATRIX_REGISTER_USER = "_matrix/client/r0/register"
    const val MATRIX_USER_LOGIN = "_matrix/client/r0/login"
    const val MATRIX_USER_LOGOUT = "_matrix/client/r0/logout"
    const val MATRIX_CREATE_ROOM = "_matrix/client/r0/createRoom"
    const val MATRIX_JOINED_ROOMS = "_matrix/client/r0/joined_rooms"
    const val MATRIX_SEND_MESSAGE = "_matrix/client/r0/rooms/{room_id}/send/m.room.message"
    const val MATRIX_ROOM_MESSAGES = "_matrix/client/r0/rooms/{room_id}/messages"
    const val MATRIX_ROOM_JOINED_MEMBERS = "_matrix/client/r0/rooms/{room_id}/joined_members"
    const val MATRIX_JOIN_ROOM = "_matrix/client/r0/rooms/{room_id}/join"
    const val MATRIX_SYNC = "_matrix/client/r0/sync"
    const val MATRIX_UPLOAD_FILE = "_matrix/media/r0/upload"
    const val MATRIX_DOWNLOAD_FILE = "_matrix/media/r0/download/{serverName}/{mediaId}"
}
