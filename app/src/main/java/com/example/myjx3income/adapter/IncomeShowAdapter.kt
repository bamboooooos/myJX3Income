package com.example.myjx3income.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myjx3income.MainActivity
import com.example.myjx3income.R
import com.example.myjx3income.entity.Battle

class IncomeShowAdapter(private val entityList: List<Battle>):
    RecyclerView.Adapter<IncomeShowAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var item_no = itemView.findViewById(R.id.item_tv_no) as TextView
        var item_username = itemView.findViewById(R.id.item_tv_username) as TextView
        var item_income = itemView.findViewById(R.id.item_tv_income) as TextView
        var item_time = itemView.findViewById(R.id.item_tv_time) as TextView

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IncomeShowAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_income, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncomeShowAdapter.ViewHolder, position: Int) {
        val entity=entityList[position]
        holder.item_no.text=(position+1).toString()
        holder.item_username.text=entity.userName
        val income:Int=entity.income-entity.consume+entity.otherIncome
        if(income>0){
            holder.item_income.text=income.toString()+"↑"
            holder.item_income.setTextColor(Color.argb(255,0,153,102))
        }else{
            holder.item_income.text=income.toString()+"↓"
            holder.item_income.setTextColor(Color.RED)
        }
        holder.item_time.text=entity.battleTime
        if(position%2==0)holder.itemView.setBackgroundColor(Color.argb(255,255,204,153))
        else holder.itemView.setBackgroundColor(Color.argb(255,204,255,255))
    }

    override fun getItemCount(): Int {
        return entityList.size
    }
}