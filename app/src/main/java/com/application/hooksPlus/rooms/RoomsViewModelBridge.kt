package com.application.hooksPlus.rooms

import com.application.hooksPlus.realtimedatabase.RankingDatabaseHandler
import com.application.hooksPlus.realtimedatabase.RoomsDatabaseHandler
import com.application.roomsm.ServerDataHandler

class RoomsViewModelBridge {
    var roomsAdapter: RoomsAdapter? = null
    var roomsDatabaseHandler: RoomsDatabaseHandler? = null
    var rankingDatabaseHandler: RankingDatabaseHandler? = null
    var serverDataHandler: ServerDataHandler? = null
}