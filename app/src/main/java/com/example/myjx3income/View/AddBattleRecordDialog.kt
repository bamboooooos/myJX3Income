package com.example.myjx3income.View

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.opengl.ETC1
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.*
import com.example.myjx3income.R
import com.example.myjx3income.entity.InstanceZone
import com.example.myjx3income.entity.SpecialReward
import com.example.myjx3income.entity.Battle
import com.example.myjx3income.entity.SpecialBattleRel
import org.litepal.LitePal
import org.litepal.extension.deleteAll
import org.litepal.extension.find
import org.litepal.extension.findFirst
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class AddBattleRecordDialog(private val mContext:Context,
                            private val zoneList:List<InstanceZone>,
                            private val rewardList:List<SpecialReward>,
                            private val addMode:Boolean,
                            private val battleId:Long?):Dialog(mContext) {

    private val zoneMap:HashMap<String, Int> = HashMap()

    private val rewardMap:HashMap<String, Int> = HashMap()

    private val rewardIdList:ArrayList<Int> = ArrayList()

    var newBattle:Battle=Battle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_record)
        initView(mContext,zoneList,rewardList)
        initListener()
        if(!addMode){
            initDataChangeMode()
        }
        setTitle("添加一条记录")
        setCancelable(false)
    }

    private fun initDataChangeMode(){
        //不是新增模式
        newBattle=LitePal.find<Battle>(Battle::class.java,battleId!!)
        val specialList:List<SpecialBattleRel> = LitePal.
        where("battleId = ?",battleId.toString()).
        find<SpecialBattleRel>()
        findViewById<EditText>(R.id.et_add_record_name).setText(newBattle.userName.toString())
        findViewById<EditText>(R.id.et_add_record_income).setText(newBattle.income.toString())
        findViewById<EditText>(R.id.et_add_record_consume).setText(newBattle.consume.toString())
        findViewById<EditText>(R.id.et_add_record_other_income).setText(newBattle.otherIncome.toString())
        findViewById<EditText>(R.id.et_remark).setText(newBattle.remark.toString())
        val instancePosition:InstanceZone?=
            LitePal.where("zoneName = ?",newBattle.battleName).
            findFirst<InstanceZone>()
        val battleName=newBattle.battleName
        if(instancePosition==null){
            Toast.makeText(mContext,"请联系管理员，id为 $battleName 的副本不存在于数据库",Toast.LENGTH_SHORT).show()
        }
        findViewById<Spinner>(R.id.sp_add_record_instance).setSelection(instancePosition!!.zoneId-1)
        val datePicker:DatePicker=findViewById<DatePicker>(R.id.tp_add_record_battle_time)
        val dateString:String=newBattle.battleTime
        val c:Calendar= Calendar.getInstance()
        c.time=SimpleDateFormat("yyyy-MM-dd").parse(dateString)
        val battleDay=c.get(Calendar.DAY_OF_MONTH)
        val battleMonth=c.get(Calendar.MONTH)+1
        val battleYear=c.get(Calendar.YEAR)
        datePicker.init(battleYear,battleMonth,battleDay,null)
        rewardIdList.clear()
        val rewardsString:StringBuilder= StringBuilder()
        for(rel:SpecialBattleRel in specialList){
            rewardIdList.add(rel.specialRewardId)
            val sp:SpecialReward?=
                LitePal.where("rewardId = ?",rel.specialRewardId.toString()).
                findFirst<SpecialReward>()
            val spId=rel.specialRewardId
            if(sp==null){
                Toast.makeText(mContext,"请联系管理员，id为 $spId 的掉落不存在于数据库",Toast.LENGTH_SHORT).show()
            }
            rewardsString.append(sp!!.rewardName)
            rewardsString.append(",")
        }
        if(rewardsString.length>0) {
            rewardsString.deleteCharAt(rewardsString.length - 1)
        }
        findViewById<android.widget.TextView>(com.example.myjx3income.R.id.tv_rewards).text =
            rewardsString.toString()
    }

    private fun initView(mContext:Context,zoneList:List<InstanceZone>,rewardList:List<SpecialReward>){
        zoneMap.clear()
        val zoneStringList=ArrayList<String>()
        for(zone:InstanceZone in zoneList){
            zoneStringList.add(zone.zoneName)
            zoneMap[zone.zoneName] = zone.zoneId
        }
        //适配器
        val zoneSpinnerAdapter:ArrayAdapter<String> = ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, zoneStringList)
        //设置样式
        zoneSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //加载适配器
        findViewById<Spinner>(R.id.sp_add_record_instance).adapter=zoneSpinnerAdapter
        rewardMap.clear()
        val rewardStringList=ArrayList<String>()
        for(zone:SpecialReward in rewardList){
            rewardStringList.add(zone.rewardName)
            rewardMap[zone.rewardName] = zone.rewardId
        }
        //适配器
        val rewardSpinnerAdapter:ArrayAdapter<String> = ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, rewardStringList)
        //设置样式
        rewardSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //加载适配器
        findViewById<Spinner>(R.id.sp_add_record_reward).adapter=rewardSpinnerAdapter
    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    private fun initListener(){
        findViewById<TextView>(R.id.tv_rewards).movementMethod=ScrollingMovementMethod()
        findViewById<ImageView>(R.id.iv_add_reward).setOnClickListener {
            val rewardName=findViewById<Spinner>(R.id.sp_add_record_reward).selectedItem.toString()
            if(rewardIdList.size!=0){
                val ori=findViewById<TextView>(R.id.tv_rewards).text.toString()
                findViewById<TextView>(R.id.tv_rewards).text= "$ori,$rewardName"
            }else{
                findViewById<TextView>(R.id.tv_rewards).text= rewardName
            }
            rewardIdList.add(rewardMap.getOrDefault(rewardName,0))
        }
        findViewById<ImageView>(R.id.iv_inc_reward).setOnClickListener {
            val rewardName=findViewById<Spinner>(com.example.myjx3income.R.id.sp_add_record_reward).selectedItem.toString()
            rewardIdList.remove(rewardMap.getOrDefault(rewardName,0))
            if(rewardIdList.size!=0){
                val ori=findViewById<android.widget.TextView>(com.example.myjx3income.R.id.tv_rewards).text.toString()
                if(ori.indexOf(",$rewardName")!=-1){
                    findViewById<android.widget.TextView>(com.example.myjx3income.R.id.tv_rewards).text= ori.replaceFirst(",$rewardName","")
                }else{
                    val a=ori.replaceFirst(rewardName,"")
                    findViewById<TextView>(com.example.myjx3income.R.id.tv_rewards).text= ori.replaceFirst(rewardName,"")
                }
                var result=findViewById<android.widget.TextView>(com.example.myjx3income.R.id.tv_rewards).text.toString()
                while(result[0]==','){
                    result=result.replaceFirst(",","")
                }
                findViewById<TextView>(com.example.myjx3income.R.id.tv_rewards).text=result
            }else{
                findViewById<android.widget.TextView>(com.example.myjx3income.R.id.tv_rewards).text= ""
            }
        }
        findViewById<TextView>(R.id.tv_add_record_confirm).setOnClickListener {
            val username:String=findViewById<EditText>(R.id.et_add_record_name).text.toString()
            val incomeString=findViewById<EditText>(R.id.et_add_record_income).text.toString()
            val income:Int=if(incomeString!="") incomeString.toInt() else 0
            val consumeString=findViewById<EditText>(R.id.et_add_record_consume).text.toString()
            val consume:Int=if(consumeString!="") consumeString.toInt() else 0
            val otherConsumeString=findViewById<EditText>(R.id.et_add_record_other_income).text.toString()
            val otherIncome:Int=if(otherConsumeString!="") otherConsumeString.toInt() else 0
            val remark:String=findViewById<EditText>(R.id.et_remark).text.toString()
            val zoneName:String=findViewById<Spinner>(R.id.sp_add_record_instance).selectedItem.toString()
            val datePicker:DatePicker=findViewById<DatePicker>(R.id.tp_add_record_battle_time)
            val battleDay:Int=datePicker.dayOfMonth
            val battleMonth:Int=datePicker.month
            val battleYear:Int=datePicker.year
            //生成记录并记录
            newBattle.battleTime="$battleYear-$battleMonth-$battleDay"
            newBattle.income=income
            newBattle.userName=username
            newBattle.consume=consume
            newBattle.otherIncome=otherIncome
            newBattle.remark=remark
            newBattle.battleName=zoneName
            newBattle.save()
            LitePal.deleteAll<SpecialBattleRel>("battleId = ?",newBattle.id.toString())
            for(rewardId in rewardIdList){
                val newRewardRel=SpecialBattleRel()
                newRewardRel.battleId=newBattle.id
                newRewardRel.specialRewardId=rewardId
                newRewardRel.save()
            }
            this.cancel()
        }
        findViewById<TextView>(R.id.tv_add_record_cancel).setOnClickListener {
            this.dismiss()
        }
    }
}