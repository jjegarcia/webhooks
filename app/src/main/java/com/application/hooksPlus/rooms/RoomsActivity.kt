package com.application.hooksPlus.rooms

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.application.hooksPlus.databinding.ActivityRoomsBinding
import com.application.hooksPlus.roomDetails.RoomDetailsActivity
import com.application.hooksPlus.support.IntentArguments.KeyboardColorListWrapper
import com.application.hooksPlus.support.LaunchCombo
import com.application.hooksPlus.support.ListenerInterface

class RoomsActivity : AppCompatActivity(), ListenerInterface {
    lateinit var roomsViewModel: RoomsViewModel
    lateinit var spinner: Spinner
    private lateinit var binding: ActivityRoomsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = intent.getStringExtra("userID")
        roomsViewModel = user?.let { RoomsViewModel(it) }!!
        binding = ActivityRoomsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewModel = roomsViewModel
        binding.lifecycleOwner = this

        getIntentExtras()
        roomsViewModel.initialiseRooms(this, binding.roomsRecyclerView)
        roomsViewModel.launchRoot.observeNonNull(this) { launchPair ->
            backToPlay(launchPair)
        }
        roomsViewModel.liveTimerEnabled.observeNonNull(this) { visibility ->
            binding.closingTime.visibility = visibility
        }
        spinner = binding.rootSizeSelected
        val rootSizes = listOf(
            RootSizeSelection.RANDOM.value,
            RootSizeSelection.TWO.value,
            RootSizeSelection.THREE.value,
            RootSizeSelection.FOUR.value,
        )
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, rootSizes)
        spinner.adapter = adapter

        roomsViewModel.setSpinnerIndex(spinner,rootSizes)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (spinner.selectedItem.toString()) {
                    RootSizeSelection.TWO.value, RootSizeSelection.THREE.value, RootSizeSelection.FOUR.value -> {
                        roomsViewModel.roomsViewModelBridge.roomsDatabaseHandler?.createRootFromSelectedSize(
                            spinner.selectedItem as String
                        )
                        roomsViewModel.writeSharedPrefs(
                            "selected_root_size",
                            this@RoomsActivity.spinner.selectedItem as String
                        )

                    }

                    else -> {
                        roomsViewModel.writeSharedPrefs(
                            "selected_root_size",
                            this@RoomsActivity.spinner.selectedItem as String
                        )
                    }
                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun getIntentExtras() {
        roomsViewModel.roomId = intent.getStringExtra("roomId")
        roomsViewModel.rootList = intent.getParcelableArrayListExtra("rootList")!!
        roomsViewModel.playedTime = intent.getIntExtra("playedTime", 0)
        roomsViewModel.score = intent.getIntExtra("score", 0)
        roomsViewModel.language = intent.getStringExtra("language")
        roomsViewModel.played = intent.getBooleanExtra("played", false)
        if (roomsViewModel.played) {
            val keyboardColorListWrapper: KeyboardColorListWrapper? =
                intent.getParcelableExtra("keyboardColorListWrapper")
            if (keyboardColorListWrapper != null) {
                roomsViewModel.keyboardColorListLeft =
                    keyboardColorListWrapper.keyboardColorListLeft
            }
            if (keyboardColorListWrapper != null) {
                roomsViewModel.keyboardColorListRight =
                    keyboardColorListWrapper.keyboardColorListRight
            }
        }
    }

    private fun backToPlay(launchCombo: LaunchCombo) {
        val returnIntent = Intent()
        returnIntent.putExtra("roomId", launchCombo.roomId)
        returnIntent.putExtra("root", launchCombo.root)
        returnIntent.putExtra("showLeftLetter", launchCombo.showLeftLetter)
        setResult(RESULT_OK, returnIntent)
        invokeBack()
    }

    private fun invokeBack() {
        roomsViewModel.roomsViewModelBridge.rankingDatabaseHandler?.signOutUser()
        roomsViewModel.roomsViewModelBridge.roomsDatabaseHandler?.detachDatabaseListener()
        roomsViewModel.roomsViewModelBridge.serverDataHandler?.detachDatabaseListener()
        finish()
    }

    override fun back(view: View) = invokeBack()

    override fun callNextActivity(view: View, intentArguments: Parcelable) {}

    override fun callNextActivity(
        view: View,
        intentArguments: Parcelable,
        displayPlayerDetails: Boolean
    ) {
        val intent = Intent(this, RoomDetailsActivity::class.java)
            .putExtra("roomID", intentArguments)
            .putExtra("displayPlayerDetails", displayPlayerDetails)
        this.startActivity(intent)
    }

    private fun <T> LiveData<T>.observeNonNull(
        roomsActivity: RoomsActivity,
        observer: (T) -> Unit
    ) {
        observe(roomsActivity, { t: T ->
            if (t != null) observer(t)
        })
    }

}

enum class RootSizeSelection(val value: String) {
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    RANDOM("")
}
