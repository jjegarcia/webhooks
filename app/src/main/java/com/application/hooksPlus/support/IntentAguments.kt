package com.application.hooksPlus.support

import android.os.Parcelable
import android.view.View
import kotlinx.android.parcel.Parcelize

const val REMOVE_ROOM_TIME_THRESHOLD = -86400
private const val PLAYING_TIME = 86400

class IntentArguments {
    @Parcelize
    data class RoomInfo(
        var root: String = "",
        var showLeftLetter: Boolean = true,
        val language: String? = "",
        var roomId: String = "",
        var people: Int = 0,
        var closingTime: Int = 0,
        var results: Int = 0,
        var playable: Boolean = true,
        var playerList: MutableList<PlayerInfo> = mutableListOf<PlayerInfo>(),
        var rootSelected: String = ""
    ) : Parcelable

    @Parcelize
    data class PlayerInfo(
        var root: String = "",
        var showLeftLetter: Boolean = true,
        var roomId: String = "",
        var playerId: String = "",
        var name: String? = "",
        var email: String? = "",
        var ranking: Int = 0,
        var score: Int = 0,
        var place: Int = 0,
        var matchResultInfo: MatchResultsInfo = MatchResultsInfo()
    ) : Parcelable

    @Parcelize
    data class MatchResultsInfo(
        var playerId: String = "",
        var name: String? = "",
        var score: Int = 0,
        var playedTime: Int = 0,
        var place: Int = 0,
        var letterHashMap: HashMap<String, Int> = hashMapOf(),
        var keyboardColorListLeft: HashMap<String, Int> = hashMapOf(),
        var keyboardColorListRight: HashMap<String, Int> = hashMapOf(),
        var played: Boolean = false
    ) : Parcelable

    @Parcelize
    data class MatchSummaryInfo(
        var scoreText: String = "",
        var playedTimeText: String = "",
        var placeText: String = ""
    ) : Parcelable

    @Parcelize
    data class LetterParameters(
        var viewSource: Int = 0,
        var visibilityValue: Int = View.VISIBLE
    ) : Parcelable

    @Parcelize
    data class WordParameters(
        var letter1: LetterParameters = LetterParameters(),
        var letter2: LetterParameters = LetterParameters(),
        var letter3: LetterParameters = LetterParameters(),
        var letter4: LetterParameters = LetterParameters(),
        var letter5: LetterParameters = LetterParameters(),
        var letter6: LetterParameters = LetterParameters()
    ) : Parcelable

    @Parcelize
    data class KeyboardColorListWrapper(
        var keyboardColorListLeft: HashMap<String, Int> = hashMapOf(),
        var keyboardColorListRight: HashMap<String, Int> = hashMapOf()
    ) : Parcelable

    @Parcelize
    data class UserRanking(
        var playerId: String = "",
        var accRanking: Int = 0,
        var state: Int = UserState.ACTIVE.value,
        var registryReplied: Boolean = true,
        var scores: List<Int> = listOf()
    ) : Parcelable

    @Parcelize
    data class ServerData(
        var registryRequest: Boolean = false,
        val removeRoomTimeThreshold: Int = REMOVE_ROOM_TIME_THRESHOLD,
        val playingTime: Int = PLAYING_TIME,
        var roomIdNumber: Int = 0,
        var syncRequest: Boolean = false,
        var forcePlayerDetailsEnabled: Boolean = true,
        var displayTimeEnabled: Boolean= false,
        var ignoreAdding: Boolean = false,
    ) : Parcelable

    enum class UserState(val value: Int) {
        ACTIVE(value = 1),
        INACTIVE(value = 0),
        OTHER(value = 2)
    }

}