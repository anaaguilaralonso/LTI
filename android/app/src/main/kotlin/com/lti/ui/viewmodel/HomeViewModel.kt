package com.lti.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lti.data.api.ApiService
import com.lti.data.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val status: String) : HomeUiState
    data object Error : HomeUiState
}

class HomeViewModel(
    private val apiService: ApiService = RetrofitClient.apiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHealth()
    }

    private fun loadHealth() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val health = apiService.getHealth()
                _uiState.value = HomeUiState.Success(status = health.status)
            } catch (_: Exception) {
                _uiState.value = HomeUiState.Error
            }
        }
    }
}
