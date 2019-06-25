package org.secfirst.umbrella.data.network

import com.google.gson.annotations.SerializedName


data class SyncResponse(@SerializedName("rooms")
                        var rooms: RoomSync,
                        @SerializedName("next_batch")
                        var next_batch: String = "")

data class RoomSync(@SerializedName("join")
                    var join: Map<String, Timeline>,
                    @SerializedName("invite")
                    var invite: Map<String, InviteState>)

data class Timeline(@SerializedName("timeline")
                    var timeline: Event)

data class InviteState(@SerializedName("invite_state")
                       var invite_state: Event)

data class Event(@SerializedName("events")
                 var events: List<Chunk>)
