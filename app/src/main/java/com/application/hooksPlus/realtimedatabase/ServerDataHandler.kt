package com.application.roomsm

import android.util.Log
import com.application.hooksPlus.rooms.RoomsViewModel
import com.application.hooksPlus.rooms.RoomsViewModelBridge
import com.application.hooksPlus.support.IntentArguments.ServerData
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

private const val HOOKS_AP_DATABASE_ID = "Hooks_App"
private const val SERVER_DATA = "Server Data"

class ServerDataHandler constructor(
    val roomsViewModelBridge: RoomsViewModelBridge,
    val roomsViewModel: RoomsViewModel
) {
    var serverData: ServerData? = null

    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mChildEventListener: ChildEventListener? = null

    fun initReference() {
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseDatabase.let {
            if (it != null) {
                mDatabaseReference = it.getReference()
            }
        }
    }

    fun handleListeners() {
        mChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("Test", "reference created")
                processServerMessages(parseServerData(snapshot))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("Test", "reference changed")
                processServerMessages(parseServerData(snapshot))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.i("Test", "reference removed")
                processServerMessages(parseServerData(snapshot))
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("Test", "reference moved")
                processServerMessages(parseServerData(snapshot))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Test", "reference:onCancelled", error.toException())
            }
        }
        mDatabaseReference?.addChildEventListener(mChildEventListener as ChildEventListener)
    }

    internal fun processServerMessages(mServerData: ServerData?) {
        if (mServerData == null) {
            updateData(ServerData())
        }else
            if (mServerData.registryRequest) {
            roomsViewModelBridge.rankingDatabaseHandler?.signInUser()
        }
    }

    internal fun parseServerData(snapshot: DataSnapshot?): ServerData? {
        serverData = snapshot?.child(SERVER_DATA)?.getValue<ServerData>()
        roomsViewModel.updateServerData()
        return serverData
    }

    fun detachDatabaseListener() {
        if (mChildEventListener != null) {
            mDatabaseReference?.removeEventListener(mChildEventListener!!)
            mChildEventListener = null
        }
    }

    fun incrementRoomIdNumber() {
        var newValue = serverData?.roomIdNumber
        if (newValue != null) newValue++
        else newValue = 0
        updateServerParameter("roomIdNumber", newValue)
    }

    private fun updateServerParameter(parameter: String, value: Any) = mDatabaseReference?.child(HOOKS_AP_DATABASE_ID)?.child(SERVER_DATA)?.child(parameter)?.setValue(value)

    internal fun updateData(serverData: ServerData?) {
        mDatabaseReference?.child(HOOKS_AP_DATABASE_ID)?.child(SERVER_DATA)?.setValue(serverData)
    }
}