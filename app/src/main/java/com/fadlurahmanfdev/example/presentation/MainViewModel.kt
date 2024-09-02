package com.fadlurahmanfdev.example.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fadlurahmanfdev.example.data.state.FetchNetworkState
import com.fadlurahmanfdev.example.domain.ExampleNetworkUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel(
    private val exampleNetworkUseCase: ExampleNetworkUseCase
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _fetchedPostState = MutableLiveData<FetchNetworkState>()
    val fetchedPostState:LiveData<FetchNetworkState> = _fetchedPostState

    fun fetchedPostOk() {
        _fetchedPostState.value = FetchNetworkState.LOADING
        compositeDisposable.add(exampleNetworkUseCase.getPostById(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _fetchedPostState.value = FetchNetworkState.SUCCESS
                },
                { exception ->
                    _fetchedPostState.value = FetchNetworkState.FAILED(
                        title = "TES",
                        message = "TES"
                    )
                },
                {}
            ))
    }
}