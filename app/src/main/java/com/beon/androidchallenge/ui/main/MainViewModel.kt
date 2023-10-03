package com.beon.androidchallenge.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beon.androidchallenge.data.repository.FactRepository
import com.beon.androidchallenge.domain.model.Fact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val currentFact = MutableLiveData<Fact?>(null)
    val error = MutableLiveData<String>(null)

    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private var lastCallTime = 0L
    private val callInterval = 2000L

    fun searchNumberFact(number: String) {

        if (number.isEmpty()) {
            currentFact.postValue(null)
            return
        }

        val currentTime = System.currentTimeMillis()

        if (currentTime - lastCallTime >= callInterval) {
            lastCallTime = currentTime

            viewModelScope.launch {

                FactRepository.getInstance()
                    .getFactForNumber(number, object : FactRepository.FactRepositoryCallback<Fact> {
                        override fun onResponse(response: Fact) {
                            currentFact.postValue(response)
                        }

                        override fun onError() {
                            error.postValue("API ERROR. Try again")
                        }

                    })
            }
        }
    }
}