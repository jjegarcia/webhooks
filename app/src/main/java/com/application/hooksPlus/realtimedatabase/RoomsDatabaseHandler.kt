package com.application.hooksPlus.realtimedatabase

import android.util.Log
import com.application.hooksPlus.rooms.RoomsViewModel
import com.application.hooksPlus.rooms.RoomsViewModelBridge
import com.application.hooksPlus.rooms.RootSizeSelection
import com.application.hooksPlus.support.AdditionalHandlersBridge
import com.application.hooksPlus.support.HashTables
import com.application.hooksPlus.support.IntentArguments.*
import com.application.hooksPlus.support.LaunchCombo
import com.application.hooksPlus.support.ParcelableObject
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue


private const val HOOKS_AP_DATABASE_ID = "Hooks_App"
private const val HOOKS_AP_DATABASE_ROOM_LIST = "Room_List"
private const val CLOSING_TIME = 86400

class RoomsDatabaseHandler(
    additionalHandlersBridge: AdditionalHandlersBridge,
    private val roomsViewModelBridge: RoomsViewModelBridge,
    private val user: String,
    private val rootList: ArrayList<ParcelableObject>, //try use VM instead
    private val RootsFactory: RootsFactory,
    val roomsViewModel: RoomsViewModel
) {
    private var initSignIn: Boolean = true
    private val hashTables: HashTables = HashTables(additionalHandlersBridge)
    private val letterHashMap: HashMap<String, Int> = hashTables.viewMap
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mMessageDatabaseReference: DatabaseReference? = null
    private var mChildEventListener: ChildEventListener? = null
    private var synchRequest: Boolean? = false
    private var roomList: MutableList<RoomInfo>? = null
    private var addedRoom: String = ""

    fun initReference() {
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseDatabase.let {
            if (it != null) {
                mMessageDatabaseReference = it.reference
            }
        }
    }

    fun handleListeners() {
        mChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("Test", "reference created")
                updateAdapter(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("Test", "reference changed")
                updateAdapter(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                updateAdapter(snapshot)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                updateAdapter(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Test", "reference:onCancelled", error.toException())
            }
        }
        mMessageDatabaseReference?.addChildEventListener(mChildEventListener as ChildEventListener)
    }

    private fun updateAdapter(snapshot: DataSnapshot) {
        if (roomsViewModelBridge.serverDataHandler?.serverData?.ignoreAdding == false) {

            synchRequest = getSyncRequest(snapshot)

            val response = snapshot.value
            Log.i("Test", "recieved from database $response")

            val parsedRoomlist = parseSnapshot(snapshot)
            roomList = parsedRoomlist
            if (parsedRoomlist.isNotEmpty() && roomsViewModelBridge.serverDataHandler?.serverData?.ignoreAdding == false) {
                if (isUserInAnyUnPlayedRoom(parsedRoomlist))
                    updateRoomsAdapter(parsedRoomlist)
                else {
                    evaluateAddNewRoom(parsedRoomlist, getRoot(roomsViewModel.savedRootSize))
                }
            } else
                evaluateAddNewRoom(null)
            if (roomsViewModel.played == true && roomsViewModelBridge.serverDataHandler?.serverData?.ignoreAdding == false) { // && requestMatchUpdate == true) {
                updateMatch()
            }
        }
    }

    private fun evaluateAddNewRoom(parsedRoomlist: MutableList<RoomInfo>?, root: String? = null) {
        if (roomsViewModelBridge.serverDataHandler?.serverData?.registryRequest == true || initSignIn) {
            initSignIn = false

            if (parsedRoomlist != null) {
                addNewRoom(parsedRoomlist, root)
            } else {
                addNewRoom()
            }
            roomsViewModelBridge.serverDataHandler?.incrementRoomIdNumber()
        }
    }

    private fun addNewRoom(parsedRoomlist: MutableList<RoomInfo>, root: String? = null) {
        addedRoom = parsedRoomlist.size.toString()
        mMessageDatabaseReference?.child(HOOKS_AP_DATABASE_ID)?.child(HOOKS_AP_DATABASE_ROOM_LIST)
            ?.child(addedRoom)
            ?.setValue(getRoomInfo(root))
    }

    private fun addNewRoom() {
        addedRoom = "0"
        replaceRoomListContent(generateRoomList())
    }

    private fun isUserInAnyUnPlayedRoom(parsedRoomlist: List<RoomInfo>): Boolean {
        var playable: Boolean
        var userInRoom: Boolean
        var userUnPlayed: Boolean
        var index = 0
        parsedRoomlist.map { roomInfo ->
            playable = roomInfo.playable
            roomInfo.playerList.map { playerInfo ->
                userInRoom = playerInfo.playerId.contains(user)
                userUnPlayed = !playerInfo.matchResultInfo.played
                if (userInRoom && userUnPlayed && playable) {
                    return true
                }
            }
            index++
        }
        return false
    }

    private fun updateRoomsAdapter(parsedRoomlist: MutableList<RoomInfo>) {
        if (roomsViewModelBridge.roomsAdapter == null) return
        roomsViewModelBridge.roomsAdapter?.roomsList = parsedRoomlist
        roomsViewModelBridge.roomsAdapter?.notifyDataSetChanged()
    }

    private fun parseSnapshot(snapshot: DataSnapshot): MutableList<RoomInfo> {
        return (snapshot.child(HOOKS_AP_DATABASE_ROOM_LIST)
            .getValue<List<RoomInfo>>()?.toMutableList() ?: mutableListOf())
    }

    fun subscribeToRoom(roomPosition: Int) {
        if (isUserInPlayerList(roomPosition) == false) {
            roomsViewModelBridge.roomsAdapter?.roomsList?.get(roomPosition)?.playerList.apply {
                roomsViewModelBridge.roomsAdapter?.roomsList?.get(roomPosition)?.root?.let { root ->
                    roomsViewModelBridge.roomsAdapter?.roomsList?.get(roomPosition)?.showLeftLetter?.let { showLeftLetter ->
                        setPlayerInfo(
                            root = root,
                            showLeftLetter = showLeftLetter
                        )
                    }
                }?.let {
                    this?.add(
                        it
                    )
                }
            }
            roomsViewModelBridge.roomsAdapter?.roomsList?.get(roomPosition)?.apply {
                this.people = this.playerList.size
                updateRoomInfoContent(roomPosition)
            }
        } else {
            setPlayed(roomPosition)
            roomsViewModelBridge.roomsAdapter?.roomsList?.get(roomPosition)?.let {
                roomsViewModel.backToPlay(
                    LaunchCombo(
                        it.root,
                        it.closingTime,
                        it.showLeftLetter,
                        it.roomId
                    )
                )
            }
        }
    }

    private fun setPlayed(roomPosition: Int) {
        val userIndex =
            roomsViewModelBridge.roomsAdapter?.roomsList?.get(roomPosition)?.playerList?.let {
                getUserIndex(it)
            }
        setMatchResultsInfoValue(roomPosition, userIndex, "played", true)
    }

    private fun getUserIndex(playerList: MutableList<PlayerInfo>): Int =
        playerList.indexOfFirst { playerInfo ->
            playerInfo.playerId == user
        }

    private fun RoomInfo.updateRoomInfoContent(position: Int) {
        mMessageDatabaseReference?.child(HOOKS_AP_DATABASE_ID)
            ?.child(HOOKS_AP_DATABASE_ROOM_LIST)?.child(
                position.toString()
            )?.setValue(this)
    }

    fun isUserInPlayerList(position: Int): Boolean? {
        val response =
            roomsViewModelBridge.roomsAdapter?.roomsList?.get(position)?.playerList?.map { playerInfo ->
                playerInfo.playerId
            }?.contains(user)
        return response
    }

    private fun replaceRoomListContent(list: MutableList<RoomInfo>?) {
        mMessageDatabaseReference?.child(HOOKS_AP_DATABASE_ID)?.child(HOOKS_AP_DATABASE_ROOM_LIST)
            ?.setValue(list)
    }

    private fun getBlankColorTable(hashTables: HashTables): HashMap<String, Int> {
        val itemTable = mutableMapOf<String, Int>()
        repeat(hashTables.letters.size) {
            itemTable[hashTables.letters[it]] = 0xdce4ed
        }
        return itemTable as HashMap<String, Int>
    }

    private fun generateRoomList(): MutableList<RoomInfo> = mutableListOf(getRoomInfo())

    private fun getRoomInfo(root: String? = null): RoomInfo {
        val showLeftLetter = RootsFactory.getShowLeftLetter()
        val workedRoot = root ?: RootsFactory.randomRoot(rootList)
        return RoomInfo(
            root = workedRoot,
            showLeftLetter = showLeftLetter,
            language = retrieveLanguage(),
            roomId = getRoomIdValue(),
            people = 1,
            closingTime = roomsViewModelBridge.serverDataHandler?.serverData?.playingTime
                ?: CLOSING_TIME,
            results = 0,
            playable = true,
            playerList = mutableListOf(setPlayerInfo(workedRoot, showLeftLetter))
        )
    }

    private fun setPlayerInfo(root: String, showLeftLetter: Boolean) =
        PlayerInfo(
            root = root,
            showLeftLetter = showLeftLetter,
            roomId = getRoomIdValue(),
            playerId = user,
            name = user,
            email = user,
            ranking = 0,
            score = 0,
            place = 0,
            matchResultInfo = generateMatchResultsInfo(),
        )

    private fun getRoomIdValue(): String {
        val value = roomsViewModelBridge.serverDataHandler?.serverData?.roomIdNumber ?: 0
        return "room$value"
    }

    private fun generateMatchResultsInfo() = MatchResultsInfo(
        user, user, 0, 0, 0,
        letterHashMap, getBlankColorTable(hashTables), getBlankColorTable(hashTables)
    )

    private fun retrieveLanguage() = roomsViewModel.language.apply { return this ?: "" }

    fun detachDatabaseListener() {
        if (mChildEventListener != null) {
            mMessageDatabaseReference?.removeEventListener(mChildEventListener!!)
            mChildEventListener = null
        }
    }

    fun updateMatch() {
        if (roomsViewModel.played && roomsViewModelBridge.roomsAdapter?.roomsList?.isNotEmpty() == true) {
            val roomIndex = getRoomIdIndex(roomsViewModel.roomId)
            if (roomIndex != null && roomIndex > -1) {
                val playerIndex = getPlayerIdIndex(user, roomIndex)
                if (playerIndex != null && playerIndex > -1) {
                    setPlayerInfoValue(roomIndex, playerIndex, "score", roomsViewModel.score)
                    setMatchResultsInfoValue(roomIndex, playerIndex, "score", roomsViewModel.score)
                    setMatchResultsInfoValue(
                        roomIndex,
                        playerIndex,
                        "playedTime",
                        roomsViewModel.playedTime
                    )
                    setMatchResultsInfoValue(
                        roomIndex,
                        playerIndex,
                        "keyboardColorListLeft",
                        roomsViewModel.keyboardColorListLeft
                    )
                    setMatchResultsInfoValue(
                        roomIndex,
                        playerIndex,
                        "keyboardColorListRight",
                        roomsViewModel.keyboardColorListRight
                    )
                    setMatchResultsInfoValue(
                        roomIndex,
                        playerIndex,
                        "played",
                        roomsViewModel.played
                    )
                }
            }
            roomsViewModel.played = false
        }
    }

    private fun setMatchResultsInfoValue(
        roomIndex: Int?,
        playerIndex: Int?,
        key: String?,
        value: Any?
    ) {
        if (key != null) {
            mMessageDatabaseReference?.child(HOOKS_AP_DATABASE_ID)
                ?.child(HOOKS_AP_DATABASE_ROOM_LIST)?.child(roomIndex.toString())
                ?.child("playerList")
                ?.child(playerIndex.toString())?.child("matchResultInfo")?.child(key)
                ?.setValue(value)
        }
    }

    private fun setPlayerInfoValue(roomIndex: Int?, playerIndex: Int?, key: String?, value: Any?) =
        key?.let {
            mMessageDatabaseReference?.child(HOOKS_AP_DATABASE_ID)
                ?.child(HOOKS_AP_DATABASE_ROOM_LIST)?.child(roomIndex.toString())
                ?.child("playerList")
                ?.child(playerIndex.toString())?.child(it)?.setValue(value)
        }

    private fun getPlayerIdIndex(playerId: String?, roomIndex: Int?): Int? =
        roomIndex?.let {
            roomsViewModelBridge.roomsAdapter?.roomsList?.get(it)?.playerList?.indexOfFirst { playerInfo ->
                playerInfo.playerId == playerId
            }
        }

    private fun getRoomIdIndex(roomId: String?): Int? =
        roomsViewModelBridge.roomsAdapter?.roomsList?.indexOfFirst { roomInfo -> roomInfo.roomId == roomId }

    fun setSyncRequest() =
        mMessageDatabaseReference?.child(HOOKS_AP_DATABASE_ID)?.child("synchRequest")
            ?.setValue(true)

    private fun getSyncRequest(snapshot: DataSnapshot): Boolean? =
        snapshot.child("synchRequest").getValue<Boolean>()

    fun createRootFromSelectedSize(selectedRootSize: String) {
        val root = getRoot(selectedRootSize)
        evaluateAddNewRoom(roomsViewModelBridge.roomsAdapter?.roomsList, root)
    }


    private fun getRoot(selectedRootSize: String): String {
        val root = if (selectedRootSize == RootSizeSelection.RANDOM.value) {
            RootsFactory.randomRoot(rootList)
        } else {
            RootsFactory.root(rootList, selectedRootSize)
        }
        return root
    }
}