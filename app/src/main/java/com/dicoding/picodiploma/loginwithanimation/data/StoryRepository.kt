package com.dicoding.picodiploma.loginwithanimation.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.api.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.data.api.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.api.response.Story
import com.dicoding.picodiploma.loginwithanimation.data.api.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.retrofit.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.database.StoryDatabase
import com.dicoding.picodiploma.loginwithanimation.data.database.StoryRemoteMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        val service = ApiConfig.getApiService(token)
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
                config = PagingConfig(
                    pageSize = 20,
                ),
                remoteMediator = StoryRemoteMediator(storyDatabase, service),
                pagingSourceFactory = {
                    storyDatabase.storyDao().getAllStory()
                }
            ).liveData
    }

    fun getDetail(token: String, id: String): MutableLiveData<Story?> {
        val storyData = MutableLiveData<Story?>()

        val client = ApiConfig.getApiService(token).getDetail(id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                if (response.isSuccessful) {
                    Log.d("StoryRepository", "Response bodyy: ${response.body()}")
                    storyData.value = response.body()?.story
                } else {
                    Log.e("StoryRepository", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                Log.e("StoryRepository", "onFailure: ${t.message}")
            }
        })

        return storyData
    }

    suspend fun uploadStory(token: String, multipartBody: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?): Boolean {

        return withContext(Dispatchers.IO) {
            try {
                val response = ApiConfig.getApiService(token).uploadStory(description, multipartBody, lat, lon)
                if (response.error != true) {
                    Log.d("StoryRepository", "Upload response body: $response")
                    true
                } else {
                    Log.e("StoryRepository", "Upload failed: response is null")
                    false
                }
            } catch (e: Exception) {
                Log.e("StoryRepository", "onFailure: ${e.message}")
                false
            }
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(storyDatabase: StoryDatabase, apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService).also { instance = it }
            }
    }
}