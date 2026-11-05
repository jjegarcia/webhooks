package com.application.hooksPlus.roomDetails

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import com.application.hooksPlus.R
import com.application.hooksPlus.databinding.ActivityRoomDetailsBinding
import com.application.hooksPlus.playerDetails.PlayerDetailsActivity
import com.application.hooksPlus.support.IntentArguments.*
import com.application.hooksPlus.support.ListenerInterface
import kotlinx.android.synthetic.main.activity_room_details.*
import kotlinx.android.synthetic.main.activity_rooms.*

class RoomDetailsActivity : AppCompatActivity(), ListenerInterface {
    lateinit var roomDetailsViewModel: RoomDetailsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_details)

        val roomDetailsArguments = intent.getParcelableExtra<RoomInfo>("roomID")
        val displayPlayerDetails = intent.getBooleanExtra("displayPlayerDetails", false)
        roomDetailsViewModel = roomDetailsArguments?.let { RoomDetailsViewModel(roomDetailsArguments = it, displayPlayerDetails = displayPlayerDetails) }!!
        ActivityRoomDetailsBinding.inflate(LayoutInflater.from(this)).apply {
            setContentView(root)
            viewModel = roomDetailsViewModel
            lifecycleOwner = this@RoomDetailsActivity
        }
        roomDetailsViewModel.initialiseRoomDetails(context = this, roomDetailsRecyclerView = roomsDetailsRecyclerView)
    }

    override fun back(view: View) {
        finish()
    }

    override fun callNextActivity(view: View, intentArguments: Parcelable) {
        val intent = Intent(this, PlayerDetailsActivity::class.java).putExtra(
            "playerDetails",
            intentArguments
        )
        this.startActivity(intent)
    }

    override fun callNextActivity(view: View, intentArguments: Parcelable, displayPlayerDetails: Boolean) {}
}