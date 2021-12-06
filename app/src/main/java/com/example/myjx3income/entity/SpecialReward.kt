package com.example.myjx3income.entity

import org.litepal.crud.LitePalSupport


/**
 * @author lin
 * 特殊掉落表->可以不断往里录入
 */
class SpecialReward:LitePalSupport() {
    //特殊掉落名称
    var rewardName:String=""
    //特殊掉落id
    var rewardId:Int=0
    //对应副本
    var zoneId:Int=0
}