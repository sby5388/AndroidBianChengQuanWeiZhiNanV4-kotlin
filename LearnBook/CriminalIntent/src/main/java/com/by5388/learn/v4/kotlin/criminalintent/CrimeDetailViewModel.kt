package com.by5388.learn.v4.kotlin.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

/**
 * @author  admin  on 2021/6/6.
 */
class CrimeDetailViewModel : ViewModel() {

    private val mCrimeRepository = CrimeRepository.get()
    private val mCrimeIdLiveData = MutableLiveData<UUID>()

    var mCrimeLiveData: LiveData<Crime?> =
        /**
         * TODO: 2021/6/6 wtf Transformations.switchMap
         * 你可能觉得奇怪，既然crime ID归CrimeDetailViewModel私有，
         * 这里为什么把crime ID封装在LiveData里。
         * CrimeDetailViewModel里有谁要侦听私有ID的变化呢？
         * 答案就在LiveData的Transformation语句中。
         * LiveData数据转换（live data transformation）
         * 是设置两个LiveData对象之间触发和反馈关系的一个解决办法。
         * 一个数据转换函数需要两个参数：一个用作触发器（trigger）的LiveData对象，
         * 一个返回LiveData对象的映射函数（mapping function）。
         * 数据转换函数会返回一个数据转换结果（transformation result）——
         * 其实就是一个新LiveData对象。
         * 每次只要触发器LiveData有新值设置，
         * 数据转换函数返回的新LiveData对象的值就会得到更新。
         *
         * 数据转换结果的值要靠执行映射函数算出。
         * 从映射函数返回的LiveData的value属性会被用来设置数据转换结果（新LiveData对象）的value属性。
         * 这样使用数据转换意味着CrimeFragment只需观察CrimeDetailViewModel.crimeLiveData一次。
         * 当CrimeFragment更改了要显示crime记录的ID，
         * CrimeDetailViewModel就会把新的crime数据发布给LiveData数据流。
         */
        Transformations.switchMap(mCrimeIdLiveData) { crimeId ->
            mCrimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: UUID) {
        mCrimeIdLiveData.value = crimeId
    }

    fun updateCrime(id: UUID, date: Date) {
        mCrimeRepository.updateCrime(id, date)
    }

    fun saveCrime(crime: Crime) {
        mCrimeRepository.updateCrime(crime)
    }

    fun getPhotoFile(crime: Crime): File {
        return mCrimeRepository.getPhotoFile(crime)
    }

}