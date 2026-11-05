package com.application.hooksPlus.support

import com.application.hooksPlus.R
import java.util.*

class HashTables constructor(val additionalHandlersBridge: AdditionalHandlersBridge) {

    var viewMap: HashMap<String, Int> =
        if (additionalHandlersBridge.languageHandler?.language == Languages.EN)
            englishTilesMap()
        else
            spanishTilesMap()

    private fun spanishTilesMap() = hashMapOf(
        "A" to R.drawable.ic_a,
        "B" to R.drawable.ic_b,
        "C" to R.drawable.ic_c,
        "1" to R.drawable.ic_ch,
        "D" to R.drawable.ic_d,
        "E" to R.drawable.ic_e,
        "F" to R.drawable.ic_f,
        "G" to R.drawable.ic_g,
        "H" to R.drawable.ic_h,
        "I" to R.drawable.ic_i,
        "J" to R.drawable.ic_j,
        "L" to R.drawable.ic_l,
        "2" to R.drawable.ic_ll,
        "M" to R.drawable.ic_m,
        "N" to R.drawable.ic_n,
        "3" to R.drawable.ic_nn,
        "O" to R.drawable.ic_o,
        "P" to R.drawable.ic_p,
        "Q" to R.drawable.ic_q,
        "R" to R.drawable.ic_r,
        "4" to R.drawable.ic_rr,
        "S" to R.drawable.ic_s,
        "T" to R.drawable.ic_t,
        "U" to R.drawable.ic_u,
        "V" to R.drawable.ic_v,
        "X" to R.drawable.ic_x,
        "Y" to R.drawable.ic_y,
        "Z" to R.drawable.ic_z
    )

    private fun englishTilesMap() = hashMapOf(
        "A" to R.drawable.ic_a,
        "B" to R.drawable.ic_b,
        "C" to R.drawable.ic_c,
        "D" to R.drawable.ic_d,
        "E" to R.drawable.ic_e,
        "F" to R.drawable.ic_f,
        "G" to R.drawable.ic_g,
        "H" to R.drawable.ic_h,
        "I" to R.drawable.ic_i,
        "J" to R.drawable.ic_j,
        "K" to R.drawable.ic_k,
        "L" to R.drawable.ic_l,
        "M" to R.drawable.ic_m,
        "N" to R.drawable.ic_n,
        "O" to R.drawable.ic_o,
        "P" to R.drawable.ic_p,
        "Q" to R.drawable.ic_q,
        "R" to R.drawable.ic_r,
        "S" to R.drawable.ic_s,
        "T" to R.drawable.ic_t,
        "U" to R.drawable.ic_u,
        "V" to R.drawable.ic_v,
        "W" to R.drawable.ic_w,
        "X" to R.drawable.ic_x,
        "Y" to R.drawable.ic_y,
        "Z" to R.drawable.ic_z
    )

    val letters =
        if (additionalHandlersBridge.languageHandler?.language == Languages.EN)
            englishValidWordList()
    else
            spanishValidWordList()

    private fun englishValidWordList() = listOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )

    private fun spanishValidWordList() = listOf(
        "A",
        "B",
        "C",
        "1",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "L",
        "2",
        "M",
        "N",
        "3",
        "O",
        "P",
        "Q",
        "R",
        "4",
        "S",
        "T",
        "U",
        "V",
        "X",
        "Y",
        "Z"
    )
}
//es special letters
//1 ch
//2 ll
//3 nn
//4 rr