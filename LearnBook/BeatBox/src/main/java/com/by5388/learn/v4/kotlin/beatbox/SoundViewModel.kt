package com.by5388.learn.v4.kotlin.beatbox

import android.view.View
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable

class SoundViewModel : BaseObservable() {
    var sound: Sound? = null
        set(sound) {
            field = sound
            //通知更新：那些使用了 @get:Bindable 注解的方法
            notifyChange()
        }

    @get:Bindable
    val title: String?
        get() = sound?.name


    fun onButtonClick(v: View) {
        Toast.makeText(v.context, sound?.name, Toast.LENGTH_SHORT).show()
    }

    private fun temp() {
        //todo 所有的可绑定熟悉都变了，请全部更新
        notifyChange()
        //TODO 只更新title这个属性：title 由 @get:Bindable 生成
        notifyPropertyChanged(BR.title)
    }

    //一些语法糖
    private fun temp2() {
        //在xml中使用单引号代替双引号
        //android:text = "@{'FileName: '+ viewModel.title}"

        //null值合并操作符
        //android:text = "@{'FileName: '+ viewModel.title??'No File'}"
        //如果title的值是null,??操作符就会返回"No File"的值
        //当然如果没有使用??操作符，当出现null时，会自动设置为"null"
    }
}