package com.application.hooksPlus.rooms

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.View
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.realtimedatabase.RankingDatabaseHandler
import com.application.hooksPlus.realtimedatabase.RoomsDatabaseHandler
import com.application.hooksPlus.realtimedatabase.RootsFactory
import com.application.hooksPlus.support.*
import com.application.roomsm.ServerDataHandler
import kotlin.properties.Delegates
private const val PREFS_NAME = "rooms_preferences"

@SuppressLint("StaticFieldLeak")
private lateinit var roomsRecycler: RecyclerView

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RoomsViewModel constructor(val user: String) : ViewModel() {
    var playerNameText: String
    var roomId: String? = null
    val roomsViewModelBridge = RoomsViewModelBridge()
    var language: String? = null
    var playedTime by Delegates.notNull<Int>()
    var score by Delegates.notNull<Int>()
    var played: Boolean = false
    lateinit var rootList: ArrayList<ParcelableObject>
    lateinit var keyboardColorListLeft: HashMap<String, Int>
    lateinit var keyboardColorListRight: HashMap<String, Int>
    private val _launchRoot = MutableLiveData<LaunchCombo>()
    val launchRoot: LiveData<LaunchCombo>
        get() = _launchRoot
    var currentRoomInfo: IntentArguments.RoomInfo? = null
    private val _liveTimerEnabled = MutableLiveData<Int>()
    val liveTimerEnabled: LiveData<Int>
        get() = _liveTimerEnabled
    lateinit var sharedPrefs: android.content.SharedPreferences
    lateinit var savedRootSize: String
    fun backPrevious(view: View) = (view.context as ListenerInterface).back(view)

    fun backToPlay(launchCombo: LaunchCombo) = _launchRoot.postValue(launchCombo)

    fun callNextActivity(
        view: View,
        roomInfo: IntentArguments.RoomInfo,
        displayPlayerDetails: Boolean
    ) =
        (view.context as ListenerInterface).callNextActivity(
            view = view,
            intentArguments = roomInfo,
            displayPlayerDetails = displayPlayerDetails
        )

    fun initialiseRooms(
        context: Context,
        roomsRecyclerView: RecyclerView
    ) {
        setupAdapter(roomsRecyclerView, context)
        val additionalHandlersBridge = AdditionalHandlersBridge()
        setupDatabaseHandler(additionalHandlersBridge, user)
        if (played) {
            roomsViewModelBridge.roomsDatabaseHandler?.setSyncRequest()
        }
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    private fun setupAdapter(
        roomsRecyclerView: RecyclerView,
        context: Context
    ) {
        roomsViewModelBridge.roomsAdapter =
            RoomsAdapter(roomsViewModel = this, roomsViewModelBridge = roomsViewModelBridge)
        roomsRecycler = roomsRecyclerView
        roomsRecycler.apply {
            layoutManager = LinearLayoutManager(
                context, RecyclerView.VERTICAL, true
            ).apply {
                stackFromEnd = true
            }
            adapter = roomsViewModelBridge.roomsAdapter
        }
    }

    private fun setupDatabaseHandler(
        additionalHandlersBridge: AdditionalHandlersBridge,
        user: String
    ) {
        roomsViewModelBridge.serverDataHandler = ServerDataHandler(roomsViewModelBridge,this)
        roomsViewModelBridge.serverDataHandler?.initReference()
        roomsViewModelBridge.serverDataHandler?.handleListeners()
        roomsViewModelBridge.rankingDatabaseHandler = RankingDatabaseHandler(roomsViewModelBridge)
        roomsViewModelBridge.rankingDatabaseHandler?.currentUserRanking?.playerId = user
        roomsViewModelBridge.rankingDatabaseHandler?.initReference()
        roomsViewModelBridge.rankingDatabaseHandler?.userSignInRequest = true
        roomsViewModelBridge.roomsDatabaseHandler =
            RoomsDatabaseHandler(
                additionalHandlersBridge,
                roomsViewModelBridge,
                user,
                rootList,
                RootsFactory(),
                this
            )
        roomsViewModelBridge.roomsDatabaseHandler?.initReference()
        roomsViewModelBridge.rankingDatabaseHandler?.handleListeners()
        roomsViewModelBridge.roomsDatabaseHandler?.handleListeners()
    }

    fun updateServerData() {
        //    fun getTimeVisibility() =
        _liveTimerEnabled.postValue(
            if (roomsViewModelBridge.serverDataHandler?.serverData?.displayTimeEnabled == true) View.VISIBLE
            else View.INVISIBLE
        )
//

    }
    fun writeSharedPrefs(tag: String, value: Any) {
        sharedPrefs.edit()
            .putString(tag, value as String)
            .apply()
    }

    fun setSpinnerIndex(spinner: Spinner, rootSizes: List<String>) {
        // Restore saved selection so spinner points to the last chosen root size.
        savedRootSize = sharedPrefs.getString("selected_root_size", RootSizeSelection.RANDOM.value).toString()
        val savedIndex = rootSizes.indexOf(savedRootSize)
        if (savedIndex >= 0) {
            spinner.setSelection(savedIndex, false)
        }
    }
    init {
        playerNameText = user
    }
}