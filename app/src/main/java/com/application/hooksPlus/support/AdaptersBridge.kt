package com.application.hooksPlus.support

import com.application.hooksPlus.board.BoardAdapter
import com.application.hooksPlus.keyboard.KeyboardAdapter
import com.application.hooksPlus.main.DotsAdapter
import com.application.hooksPlus.roots.RootsAdapter

class AdaptersBridge {
    var boardAdapter: BoardAdapter? = null
    var dotsAdapter: DotsAdapter? = null
    var rootsAdapter: RootsAdapter? = null
    var keyboardAdapter: KeyboardAdapter? = null
}