package com.application.hooksPlus.playerDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import com.application.hooksPlus.R
import com.application.hooksPlus.databinding.ActivityPlayerDetailsBinding
import com.application.hooksPlus.support.IntentArguments
import com.application.hooksPlus.support.ListenerInterface
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.activity_player_details.*
import kotlinx.android.synthetic.main.activity_room_details.*
import kotlinx.android.synthetic.main.player_row.*

class PlayerDetailsActivity : AppCompatActivity(), ListenerInterface {
    lateinit var playerDetailsViewModel: PlayerDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_details)
        val playerArguments = intent.getParcelableExtra<IntentArguments.PlayerInfo>("playerDetails")
        playerDetailsViewModel = playerArguments?.let { PlayerDetailsViewModel(it) }!!
        ActivityPlayerDetailsBinding.inflate(LayoutInflater.from(this)).apply {
            setContentView(root)
            viewModel = playerDetailsViewModel
            lifecycleOwner = this@PlayerDetailsActivity
        }

        playerDetailsViewModel.initialisePlayer(this, playerDetailsRecyclerView, rootResultsRecyclerView, playerArguments.name)
    }

    override fun back(view: View) {
        finish()
    }

    override fun callNextActivity(view: View, intentArguments: Parcelable) {}

    override fun callNextActivity(view: View, intentArguments: Parcelable, displayPlayerDetails: Boolean) {}
}
