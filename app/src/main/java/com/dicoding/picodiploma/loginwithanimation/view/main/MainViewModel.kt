package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val userRepository: UserRepository, private val storyRepository: StoryRepository) : ViewModel() {

    private val _uploadResult = MutableLiveData<Boolean>()
    val uploadResult: LiveData<Boolean> get() = _uploadResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun setToken(token: String) {
        _token.value = token
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return storyRepository.getStories(token).cachedIn(viewModelScope)
    }

    fun uploadStory(token: String, multipartBody: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?) {
        _isLoading.value = true
        viewModelScope.launch {
            val success = storyRepository.uploadStory(token, multipartBody, description, lat, lon)
            _isLoading.value = false
            _uploadResult.value = success // Mengatur hasil pengunggahan
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}