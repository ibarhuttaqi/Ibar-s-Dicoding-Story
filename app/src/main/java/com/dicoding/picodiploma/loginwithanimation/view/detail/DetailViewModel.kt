package com.dicoding.picodiploma.loginwithanimation.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.response.Story
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.launch

class DetailViewModel(private val userRepository: UserRepository, private val storyRepository: StoryRepository) : ViewModel() {

    private val _storyResponseItem = MutableLiveData<Story?>()
    val storyResponseItem: LiveData<Story?> = _storyResponseItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun getDetail(token: String, id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val response = storyRepository.getDetail(token, id)
            response.observeForever { storyItems ->
                _isLoading.value = false
                _storyResponseItem.value = storyItems
            }
        }
    }
}