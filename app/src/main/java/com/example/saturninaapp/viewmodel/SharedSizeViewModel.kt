package com.example.saturninaapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedSizeViewModel: ViewModel() {
    val selectedSizes = MutableLiveData< HashMap<String, String> >()
}