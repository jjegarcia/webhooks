package com.application.hooksPlus.realtimedatabase

import com.application.hooksPlus.support.ParcelableObject
import java.util.*


class RootsFactory {
    fun randomRoot(rootList: ArrayList<ParcelableObject>): String {
        val randomRootSize = getRandomNumber(rootList.size)
        val rootIndex = getRandomNumber(rootList.get(randomRootSize).list.size)
        val root = rootList.get(randomRootSize).list.get(rootIndex)
        return root
    }

    fun root(rootList: ArrayList<ParcelableObject>, selectedRootSize: String): String {
        val randomRootSize = selectedRootSize.toInt()-1
        val rootIndex = getRandomNumber(rootList.get(randomRootSize).list.size)
        val root = rootList.get(randomRootSize).list.get(rootIndex)
        return root
    }

    private fun getRandomNumber(size: Int) = Random().nextInt(size)

    fun getShowLeftLetter(): Boolean = getRandomNumber(3211) % 2 == 0
}