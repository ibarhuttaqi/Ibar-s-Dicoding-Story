package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.data.api.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.api.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MapsRepository(apiService: ApiService) {

    fun getMarkers(token: String): Flow<List<ListStoryItem>> = flow {
        val response = ApiConfig.getApiService(token).getStoriesWithLocation(location = 1)
        Log.d("response maps marker: ", "this is $response")
        emit(response.listStory)
    }

    companion object {
        @Volatile
        private var instance: MapsRepository? = null

        fun getInstance(apiService: ApiService): MapsRepository =
            instance ?: synchronized(this) {
                instance ?: MapsRepository(apiService).also { instance = it }
            }
    }
}