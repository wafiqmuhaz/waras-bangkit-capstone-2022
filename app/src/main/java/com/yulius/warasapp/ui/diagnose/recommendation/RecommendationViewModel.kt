package com.yulius.warasapp.ui.diagnose.recommendation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yulius.warasapp.data.model.UserPreference

class RecommendationViewModel (private val pref: UserPreference) : ViewModel()  {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

}