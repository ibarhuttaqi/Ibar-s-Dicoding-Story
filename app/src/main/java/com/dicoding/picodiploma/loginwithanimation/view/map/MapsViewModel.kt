package com.dicoding.picodiploma.loginwithanimation.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.picodiploma.loginwithanimation.data.MapsRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.response.ListStoryItem

class MapsViewModel(private val mapsRepository: MapsRepository) : ViewModel() {

    fun getMarkers(token: String): LiveData<List<ListStoryItem>> {
        return mapsRepository.getMarkers(token).asLiveData()
    }
}