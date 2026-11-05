package com.application.hooksPlus.realtimedatabase

import android.util.Log
import com.application.hooksPlus.rooms.RoomsViewModelBridge
import com.application.hooksPlus.support.IntentArguments.UserRanking
import com.application.hooksPlus.support.IntentArguments.UserState
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

private const val HOOKS_AP_DATABASE_ID = "Hooks_App"
private const val USER_RANKING_LIST = "User_Ranking_List"

class RankingDatabaseHandler constructor(
    private val roomsViewModelBridge: RoomsViewModelBridge
) {
    var userSignInRequest: Boolean = false
    val currentUserRanking = UserRanking("", 0, UserState.ACTIVE.value, false, listOf(0, 0, 0, 0, 0))
    private var userRankingList: MutableList<UserRanking>? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mMessageDatabaseReference: DatabaseReference? = null
    private var mChildEventListener: ChildEventListener? = null

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
                processRankingList(parseUserRankingList(snapshot))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("Test", "reference changed")
                processRankingList(parseUserRankingList(snapshot))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.i("Test", "reference removed")
                processRankingList(parseUserRankingList(snapshot))
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("Test", "reference moved")
                processRankingList(parseUserRankingList(snapshot))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Test", "reference:onCancelled", error.toException())
            }
        }
        mMessageDatabaseReference?.addChildEventListener(mChildEventListener as ChildEventListener)
    }

    private fun processRankingList(parseUserRankingList: MutableList<UserRanking>?) {
        if (parseUserRankingList == null) {
            setBlankUserRankingList()
        } else {
            if (!containsUser(parseUserRankingList)) {
                addUser(parseUserRankingList)
            }
            userRankingList = parseUserRankingList
            setRanking(parseUserRankingList)
            if (userSignInRequest || roomsViewModelBridge.serverDataHandler?.serverData?.registryRequest == true) {
                signInUser()
            }
        }
    }

    private fun setRanking(parseUserRankingList: MutableList<UserRanking>) {
        parseUserRankingList.forEach {
            if (it.playerId == currentUserRanking.playerId) {
                currentUserRanking.accRanking = it.accRanking
                currentUserRanking.scores = it.scores
                currentUserRanking.state = it.state
                currentUserRanking.registryReplied = it.registryReplied
                return
            }
        }
    }

    private fun containsUser(parseUserRankingList: MutableList<UserRanking>): Boolean {
        parseUserRankingList.forEach {
            if (it.playerId == currentUserRanking.playerId) return true
        }
        return false
    }

    private fun parseUserRankingList(snapshot: DataSnapshot): MutableList<UserRanking>? {
        return snapshot.child(USER_RANKING_LIST)
            .getValue<List<UserRanking>>()?.toMutableList()
    }

    private fun setBlankUserRankingList() {
        updateRankingList(mutableListOf(currentUserRanking))
    }

    private fun addUser(parseUserRankingList: MutableList<UserRanking>) {
        parseUserRankingList.add(currentUserRanking)
        updateRankingList(parseUserRankingList)
    }

    private fun updateRankingList(parseUserRankingList: MutableList<UserRanking>?) {
        mMessageDatabaseReference?.child(HOOKS_AP_DATABASE_ID)?.child(USER_RANKING_LIST)
            ?.setValue(parseUserRankingList)
    }

    fun signInUser() {
        if (userRankingList != null) {
            userSignInRequest = false
            userRankingList?.forEachIndexed { index, userRanking ->      //consider refactor to filter if empty?
                if (userRanking.playerId == currentUserRanking.playerId) {
                    updateUserRankingParameter(index, "state", UserState.ACTIVE.value)
                    updateUserRankingParameter(index, "registryReplied", true)
                }
            }
        }
    }

    private fun updateUserRankingParameter(index: Int, parameter: String, value: Any) {
        mMessageDatabaseReference?.child(HOOKS_AP_DATABASE_ID)?.child(USER_RANKING_LIST)?.child(index.toString())?.child(parameter)
            ?.setValue(value)
    }

    fun signOutUser() {
        userRankingList?.forEachIndexed { index, userRanking ->
            if (userRanking.playerId == currentUserRanking.playerId) {
                updateUserRankingParameter(index, "state", UserState.INACTIVE.value)
            }
        }
        detachDatabaseListener()
    }

    private fun detachDatabaseListener() {
        if (mChildEventListener != null) {
            mMessageDatabaseReference?.removeEventListener(mChildEventListener!!)
            mChildEventListener = null
        }
    }
}
