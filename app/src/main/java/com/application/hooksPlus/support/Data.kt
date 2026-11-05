package com.application.hooksPlus.support

import android.graphics.Color
import android.os.Parcelable
import com.application.hooksPlus.R
import kotlinx.android.parcel.Parcelize
@Parcelize
data class CurrentLists constructor(
    val rootList: List<String>,
    val hookList: MutableList<WordStructure>,
    val masterHookList: List<WordStructure>,
    var keyboardColorList: MutableMap<ColorStructure, HashMap<String, Int>>,
    var rootColorTableLeft: HashMap<String, Int>,
    var rootColorTableRight: HashMap<String, Int>,
    var rootParametersList: MutableList<RootParameters>,
    var wordParametersList: MutableList<WordParameters>,
    var boardList: MutableList<List<TileContent>>,
    var pointersList: DataPointers
):Parcelable

@Parcelize
data class DataPointers constructor(
    var wordsCounter: Int,
    var currentRoot: Int,
    var rootIndexCount: Int,
    var rootCount: Int
):Parcelable

@Parcelize
data class TileContent constructor(
    var nText: String? = null,
    var nEditable: Boolean,
    var nVisible: Boolean,
    var nColor: Int? = null
):Parcelable

@Parcelize
data class WordParameters constructor(
    var rootIndex: Int = 0,
    var showLeftLetter: Boolean,
    var rootSize: Int = 0,
    var matched: Boolean = false,
    var invisibleIfPlay: Boolean = true
):Parcelable

@Parcelize
data class RootParameters constructor(
    var nColor: Int? = null,
    var nEditable: Boolean,
    var nVisible: Int? = null
):Parcelable

@Parcelize
data class WordStructure(
    val word: String,
    val root: String
):Parcelable

@Parcelize
data class ColorStructure constructor(
    val root: String,
    val left: Boolean
):Parcelable


@Parcelize
data class ParcelableObject(
    val list: List<String>
) : Parcelable

data class LaunchCombo (
    val root: String,
    val closingTime: Int,
    val showLeftLetter: Boolean,
    val roomId:String
)

enum class Colors(val color: Int) {
    YELLOW(Color.YELLOW),
    GREEN(Color.GREEN),
    RED(Color.RED),
    MAGENTA(Color.MAGENTA),
    BLUE(Color.BLUE),
}

enum class Languages(val languageID: String) {
    ES(languageID = "es"),
    EN(languageID = "en")
}

enum class Roots(val value: Int) {
    ROOT1(value = 0),
    ROOT2(value = 1),
    ROOT3(value = 2),
    ROOT4(value = 3)
}

enum class Modes(val image: Int, val descrption: String) {
    PRACTISE(R.drawable.ic_book_g, "practise"),
    GAME(R.drawable.ic_controller, "game"),
    NINJA(R.drawable.ic_ninja_g, "ninja"),
    BATTLE(R.drawable.ic_shield_02_42,"battle")
}


