package com.memad.fciattendance.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.memad.fciattendance.data.HistoryDao
import com.memad.fciattendance.data.HistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyDao: HistoryDao
) : ViewModel() {

    private val historyList = MutableLiveData<List<HistoryEntity>>()
    val history: LiveData<List<HistoryEntity>> = liveData {
        delay(225)
        emitSource(historyList)
    }

    init {
        getHistory()
    }

    private fun getHistory() {
        historyDao.getAllHistory().observeForever {
            if (it != null) {
                historyList.postValue(it.reversed())
            }
            if (it.isEmpty()) {
                historyList.postValue(emptyList())
            }
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.deleteAllHistory()
        }
        getHistory()
    }

    fun deleteHistory(historyEntity: HistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.deleteHistory(historyEntity.historyId!!)
        }
        getHistory()
    }

}