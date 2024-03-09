package com.memad.fciattendance.ui.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memad.fciattendance.utils.Constants
import com.memad.fciattendance.utils.Resource
import com.memad.fciattendance.utils.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    private val sharedPreferencesHelper: SharedPreferencesHelper
) : ViewModel() {
    private val _resultFlow = MutableSharedFlow<Resource<List<String>>>()
    val resultFlow = _resultFlow.asSharedFlow()


    init {
        getSubjects()
    }

    private fun getSubjects() {
        viewModelScope.launch(Dispatchers.IO) {
            _resultFlow.emit(Resource.loading())
            Thread.sleep(1000)
            val subjects = sharedPreferencesHelper.readStringSet(Constants.SUBJECTS)
            getData(subjects)
        }
    }

    private suspend fun getData(subjects: Set<String>?) {
        if (subjects.isNullOrEmpty()) {
            _resultFlow.emit(Resource.error("No Subjects"))
        } else {
            _resultFlow.emit(Resource.success(subjects.toList()))
        }
    }

}