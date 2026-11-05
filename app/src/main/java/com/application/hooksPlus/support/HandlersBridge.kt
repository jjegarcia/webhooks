package com.application.hooksPlus.support

import com.application.hooksPlus.board.BoardEventHandler
import com.application.hooksPlus.board.HooksHandler
import com.application.hooksPlus.keyboard.KeyboardHandler
import com.application.hooksPlus.roots.RootsHandler

class HandlersBridge {
    var keyboardHandler: KeyboardHandler? = null
    var hooksHandler: HooksHandler? = null
    var boardEventHandler: BoardEventHandler? = null
    var rootsHandler: RootsHandler? = null
}