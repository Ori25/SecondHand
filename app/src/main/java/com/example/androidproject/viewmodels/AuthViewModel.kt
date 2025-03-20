package com.example.androidproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidproject.database.common.AuthRepo
import com.example.androidproject.database.common.CurrentUserRepository
import com.example.androidproject.dto.UserLoginForm
import com.example.androidproject.dto.UserRegistrationForm
import com.example.androidproject.helpers.LoadingState
import com.example.androidproject.helpers.extensions.observeNotNull
import com.example.androidproject.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: CurrentUserRepository) :
    ViewModel() {

    private var _exceptions = MutableLiveData<Exception>()
    private var _loadingState = MutableLiveData<LoadingState>(LoadingState.Loaded)

    val currentUser = repository.currentUser
    val exceptions: LiveData<Exception> get() = _exceptions
    val loadingState: LiveData<LoadingState> get() = _loadingState


    init {
        repository.startListening()
    }

    fun signIn(form: UserLoginForm) {
        _loadingState.postValue(LoadingState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.signIn(form)
            } catch (e: Exception) {
                _exceptions.postValue(e)
            } finally {
                _loadingState.postValue(LoadingState.Loaded)
            }
        }
    }

    fun signUp(form: UserRegistrationForm) {
        _loadingState.postValue(LoadingState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.signUp(form)
            } catch (e: Exception) {
                _exceptions.postValue(e)
            } finally {
                _loadingState.postValue(LoadingState.Loaded)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopListening()
    }

}