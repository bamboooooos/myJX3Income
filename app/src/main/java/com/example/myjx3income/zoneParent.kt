package com.example.myjx3income

import java.util.*

class zoneParent {

    public fun getRewardParent(zoneId:Int):List<Int>{
        return when(zoneId){
            1->
                listOf(0, 1)
            2->
                listOf(0,1,2)
            4->
                listOf(-1,4)
            5->
                listOf(-2,5)
            6->
                listOf(-2,6)
            7->
                listOf(-1,-2,7)
            else->
                listOf(zoneId)
        }
    }

}