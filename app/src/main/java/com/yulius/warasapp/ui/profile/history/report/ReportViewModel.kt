package com.yulius.warasapp.ui.profile.history.report

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yulius.warasapp.data.model.*
import com.yulius.warasapp.data.remote.main.ApiConfig
import com.yulius.warasapp.util.ResponseCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportViewModel (private val pref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun sendReport(history: History, reports: String, status: String, callback: ResponseCallback){
        _isLoading.value = true
        ApiConfig.getApiService().sendReport(
            reports,
            status,
            history.id_user,
            history.id_history
        ).enqueue(object:
            Callback<ResponseReport> {
            override fun onResponse(
                call: Call<ResponseReport>,
                response: Response<ResponseReport>
            ) {
                val responseBody = response.body()
                Log.d("TAG", "onResponseReport: ${response.body()}")
                if(response.isSuccessful && responseBody != null){
                    if(responseBody.status == "Success") {
                        callback.getCallback("Add Report Success", true)
                        _isLoading.value = false
                    } else {
                        callback.getCallback("Add Report Failed", false)
                        _isLoading.value = false
                    }
                } else {
                    callback.getCallback(response.message(),false)
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<ResponseReport>, t: Throwable) {
                callback.getCallback(t.message.toString(),false)
                _isLoading.value = false
            }
        })
    }

    fun sendStatus(history: History, status: String, callback: ResponseCallback){
        _isLoading.value = true
        ApiConfig.getApiService().changeHistory(
            history.id_history,
            status,
        ).enqueue(object:
            Callback<Responses> {
            override fun onResponse(
                call: Call<Responses>,
                response: Response<Responses>
            ) {
                val responseBody = response.body()
                Log.d("TAG", "onResponseStatus: $response")
                Log.d("TAG", "onResponseStatus: ${response.body()}")
                if(response.isSuccessful && responseBody != null){
                    if(responseBody.status == "Succes") {
                        callback.getCallback("Add Report Success", true)
                        _isLoading.value = false
                    } else {
                        callback.getCallback("Add Report Failed", false)
                        _isLoading.value = false
                    }
                } else {
                    callback.getCallback(response.message(),false)
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<Responses>, t: Throwable) {
                callback.getCallback(t.message.toString(),false)
                _isLoading.value = false
            }
        })
    }
}