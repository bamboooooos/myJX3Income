package com.example.myjx3income

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.litepal.LitePal

import android.database.sqlite.SQLiteDatabase
import android.opengl.Visibility
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myjx3income.View.AddBattleRecordDialog
import com.example.myjx3income.adapter.IncomeShowAdapter
import com.example.myjx3income.adapter.RecyclerViewClickListener2
import com.example.myjx3income.entity.Battle
import com.example.myjx3income.entity.InstanceZone
import com.example.myjx3income.entity.SpecialReward
import org.litepal.LitePal.getDatabase
import org.litepal.crud.LitePalSupport
import org.litepal.extension.delete
import org.litepal.extension.find


class MainActivity : AppCompatActivity() {

    val showList:ArrayList<Battle> = ArrayList()

    val incomeAdapter:IncomeShowAdapter= IncomeShowAdapter(showList)

    var addMode:Boolean=true
    var battleId:Int?=null

    var selectMode:Int=6

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListener()
        val db = getDatabase()
//        val songs = LitePal.where("name like ? and duration < ?", "song%", "200").order("duration").find<Song>()
        var mSpf:SharedPreferences=getPreferences(MODE_PRIVATE)
        val isInit:Boolean=mSpf.getBoolean("isInit",true)
        if(isInit){ //如果是初始化
            initZone()
            initReward()
            //修改标志位
            val editor:SharedPreferences.Editor=mSpf.edit()
            editor.putBoolean("isInit",false)
            editor.apply()
        }
        //初始化RecyclerView
        val incomeRecyclerView:RecyclerView = findViewById(R.id.rv_main)
        incomeRecyclerView.layoutManager=LinearLayoutManager(this)
        incomeRecyclerView.adapter=incomeAdapter
        //查询数据
        getIncomeDataMode()
        incomeAdapter.notifyDataSetChanged()
    }

    fun initListener(){
        val btnAdd:ImageView=findViewById<ImageView>(R.id.iv_add_record)
        btnAdd.setOnClickListener {
            val addNewBattleDialog:AddBattleRecordDialog=AddBattleRecordDialog(this,
                LitePal.order("zoneId").find<InstanceZone>(),
                LitePal.order("rewardId").find<SpecialReward>(),
                addMode,if(battleId==null) null else battleId!!.toLong())
            addNewBattleDialog.setOnCancelListener {
                btnAdd.visibility=View.VISIBLE
                getIncomeDataMode()
                incomeAdapter.notifyDataSetChanged()
                battleId=null
                addMode=true
            }
            addNewBattleDialog.setOnDismissListener {
                btnAdd.visibility=View.VISIBLE
                battleId=null
                addMode=true
            }
            addNewBattleDialog.show()
            btnAdd.visibility= View.GONE
        }
        val rvBattle:RecyclerView=findViewById<RecyclerView>(R.id.rv_main)
        rvBattle.addOnItemTouchListener(RecyclerViewClickListener2(this,
            rvBattle,
            object:RecyclerViewClickListener2.OnItemClickListener{
                override fun onItemClick(view: View?, position: Int) {
                    battleId=showList[position].id
                    addMode=false
                    btnAdd.callOnClick()
                }

                override fun onItemLongClick(view: View?, position: Int) {
                    battleId=showList[position].id
                    val b:AlertDialog.Builder=AlertDialog.Builder(this@MainActivity)
                    b.setTitle("")
                    b.setMessage("是否删除该记录？")
                    b.setCancelable(false)
                    b.setNegativeButton("删除", DialogInterface.OnClickListener { dialog,
                                                                                which ->
                        LitePal.delete<Battle>(battleId!!.toLong())
                        getIncomeDataMode()
                        incomeAdapter.notifyDataSetChanged()
                        dialog.cancel()
                    })
                    b.setPositiveButton("取消", DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                    })
                    b.show()
                }
            }
        ))
        //表头排序
        val tvUserName=findViewById<TextView>(R.id.tv_title_username)
        val tvIncome=findViewById<TextView>(R.id.tv_title_income)
        val tvTime=findViewById<TextView>(R.id.tv_title_time)
        tvUserName.setOnClickListener {
            var oriName:String=tvUserName.text.toString()
            if(oriName.contains("↑")){
                selectMode=2
                getIncomeDataMode()
                tvUserName.text = oriName.replace("↑","↓")
            }else{
                selectMode=1
                getIncomeDataMode()
                tvUserName.text = oriName.replace("↓","")+"↑"
            }
            tvIncome.text="实际收入"
            tvTime.text="时间"
            incomeAdapter.notifyDataSetChanged()
        }
        tvIncome.setOnClickListener {
            var oriName:String=tvIncome.text.toString()
            if(oriName.contains("↑")){
                selectMode=4
                getIncomeDataMode()
                tvIncome.text = oriName.replace("↑","↓")
            }else{
                selectMode=3
                getIncomeDataMode()
                tvIncome.text = oriName.replace("↓","")+"↑"
            }
            tvUserName.text="角色名"
            tvTime.text="时间"
            incomeAdapter.notifyDataSetChanged()
        }
        tvTime.setOnClickListener {
            var oriName:String=tvTime.text.toString()
            if(oriName.contains("↓")){
                selectMode=5
                getIncomeDataMode()
                tvTime.text = oriName.replace("↓","↑")
            }else{
                selectMode=6
                getIncomeDataMode()
                tvTime.text = oriName.replace("↑","")+"↓"
            }
            tvUserName.text="角色名"
            tvIncome.text="实际收入"
            incomeAdapter.notifyDataSetChanged()
        }
    }

    private fun getIncomeDataMode(){
        showList.clear()
        when(selectMode){
            1->//角色名排列
                showList.addAll(LitePal.order("username asc").find<Battle>())
            2->//角色名倒排列
                showList.addAll(LitePal.order("username desc").find<Battle>())
            3->//实际收入排序
                showList.addAll(LitePal.order("income-consume+otherIncome asc").find<Battle>())
            4->//实际收入倒排序
                showList.addAll(LitePal.order("income-consume+otherIncome desc").find<Battle>())
            5->//时间排序
                showList.addAll(LitePal.order("battleTime asc").find<Battle>())
            6->//时间倒排序
                showList.addAll(LitePal.order("battleTime desc").find<Battle>())
            else->
                return
        }
    }

    private fun getTestData():List<Battle>{
        val result:ArrayList<Battle> = ArrayList()
        lateinit var battle:Battle
        for(i in 1..30){
            battle= Battle()
            battle.userName="用户"+i.toString()
            battle.income=20+(Math.random()*10).toInt()
            battle.consume=20+(Math.random()*10).toInt()
            battle.otherIncome=5+(Math.random()*5).toInt()
            battle.battleTime="2021-12-3"
            result.add(battle)
        }
        return result;
    }

    private fun initZone():Unit{
        var zone=InstanceZone()
        zone.zoneId=1
        zone.zoneName="25人普通雷域大泽"
        zone.save()
        zone=InstanceZone()
        zone.zoneId=2
        zone.zoneName="25人英雄雷域大泽"
        zone.save()
        zone=InstanceZone()
        zone.zoneId=3
        zone.zoneName="10人普通雷域大泽"
        zone.save()
        zone=InstanceZone()
        zone.zoneId=4
        zone.zoneName="敖龙岛-范阳夜变"
        zone.save()
        zone=InstanceZone()
        zone.zoneId=5
        zone.zoneName="狼神殿"
        zone.save()
        zone=InstanceZone()
        zone.zoneId=6
        zone.zoneName="千雷殿-燕然峰"
        zone.save()
        zone=InstanceZone()
        zone.zoneId=7
        zone.zoneName="周常/其他副本"
        zone.save()
    }

    private fun initReward():Unit{
        var reward:SpecialReward=SpecialReward()
        reward.rewardId=1
        reward.rewardName="天乙玄晶"
        reward.zoneId=0
        reward.save()

        reward=SpecialReward()
        reward.rewardId=2
        reward.rewardName="归墟玄晶"
        reward.zoneId=-1
        reward.save()

        reward=SpecialReward()
        reward.rewardId=3
        reward.rewardName="老版本玄晶"
        reward.zoneId=-2
        reward.save()

        reward=SpecialReward()
        reward.rewardId=4
        reward.rewardName="书函"
        reward.zoneId=5
        reward.save()

        reward=SpecialReward()
        reward.rewardId=5
        reward.rewardName="赤纹野正宗"
        reward.zoneId=4
        reward.save()

        reward=SpecialReward()
        reward.rewardId=6
        reward.rewardName="隐狐匿踪"
        reward.zoneId=4
        reward.save()

        reward=SpecialReward()
        reward.rewardId=7
        reward.rewardName="木木"
        reward.zoneId=4
        reward.save()

        reward=SpecialReward()
        reward.rewardId=8
        reward.rewardName="星云踏月骓"
        reward.zoneId=4
        reward.save()

        reward=SpecialReward()
        reward.rewardId=9
        reward.rewardName="簪花空竹"
        reward.zoneId=4
        reward.save()

        reward=SpecialReward()
        reward.rewardId=10
        reward.rewardName="弃身·肆"
        reward.zoneId=4
        reward.save()

        reward=SpecialReward()
        reward.rewardId=11
        reward.rewardName="幽明录"
        reward.zoneId=4
        reward.save()

        reward=SpecialReward()
        reward.rewardId=12
        reward.rewardName="聆音"
        reward.zoneId=4
        reward.save()

        reward=SpecialReward()
        reward.rewardId=13
        reward.rewardName="夜泊蝶影"
        reward.zoneId=4
        reward.save()

        reward=SpecialReward()
        reward.rewardId=14
        reward.rewardName="武器盒子"
        reward.zoneId=1
        reward.save()

        reward=SpecialReward()
        reward.rewardId=15
        reward.rewardName="特效武器盒子"
        reward.zoneId=1
        reward.save()

        reward=SpecialReward()
        reward.rewardId=16
        reward.rewardName="大眼仔"
        reward.zoneId=1
        reward.save()

        reward=SpecialReward()
        reward.rewardId=17
        reward.rewardName="荒原切"
        reward.zoneId=1
        reward.save()

        reward=SpecialReward()
        reward.rewardId=18
        reward.rewardName="掠影无迹"
        reward.zoneId=1
        reward.save()

        reward=SpecialReward()
        reward.rewardId=19
        reward.rewardName="游空竹翼"
        reward.zoneId=1
        reward.save()

        reward=SpecialReward()
        reward.rewardId=20
        reward.rewardName="带体精简"
        reward.zoneId=1
        reward.save()

        reward=SpecialReward()
        reward.rewardId=21
        reward.rewardName="无体精简"
        reward.zoneId=1
        reward.save()

        reward=SpecialReward()
        reward.rewardId=22
        reward.rewardName="周常挂件"
        reward.zoneId=7
        reward.save()
    }

}