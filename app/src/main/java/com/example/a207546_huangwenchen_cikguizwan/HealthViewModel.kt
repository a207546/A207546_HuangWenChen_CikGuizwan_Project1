package com.example.a207546_huangwenchen_cikguizwan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HealthViewModel(application: Application) : AndroidViewModel(application) {
    private val _healthData = MutableLiveData(HealthData())
    val healthData: LiveData<HealthData> = _healthData

    private val db = HealthDatabase.getInstance(application)
    private val dao = db.recordDao()
    val allRecords: Flow<List<HealthRecordEntity>> = dao.getAllRecords()

    fun updateUserName(name: String) {
        val current = _healthData.value ?: HealthData()
        _healthData.value = current.copy(userName = name)
    }

    fun addWater(amount: Int) {
        val current = _healthData.value ?: HealthData()
        _healthData.value = current.copy(waterAmount = current.waterAmount + amount)
    }

    fun resetWater() {
        val current = _healthData.value ?: HealthData()
        _healthData.value = current.copy(waterAmount = 0)
    }

    fun addNewRecord(title: String, value: String) {
        viewModelScope.launch {
            dao.insert(HealthRecordEntity(title = title, value = value))
        }
    }
}