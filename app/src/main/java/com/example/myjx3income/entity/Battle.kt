package com.example.myjx3income.entity

import org.litepal.crud.LitePalSupport
import java.util.*

/**
 * @author lin
 * 记录每一次战斗的基础信息
 */
class Battle :LitePalSupport(){

    //id
    var id:Int=0
    //战斗时间
    var battleTime:String=""
    //角色姓名
    var userName:String=""
    //副本名称
    var battleName:String=""
    //工资
    var income:Int=0
    //消费
    var consume:Int=0
    //额外收入
    var otherIncome:Int=0
    //备注
    var remark:String=""

}