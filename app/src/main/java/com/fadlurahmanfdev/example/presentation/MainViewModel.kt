package com.fadlurahmanfdev.example.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fadlurahmanfdev.example.data.dto.exception.FeatureException
import com.fadlurahmanfdev.example.data.state.FetchNetworkState
import com.fadlurahmanfdev.example.domain.usecase.ExampleNetworkUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel(
    private val exampleNetworkUseCase: ExampleNetworkUseCase
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _fetchedPostState = MutableLiveData<FetchNetworkState>()
    val fetchedPostState: LiveData<FetchNetworkState> = _fetchedPostState

    fun fetchedPostUsingCorrectPinningPublicKey() {
        _fetchedPostState.value = FetchNetworkState.LOADING
        compositeDisposable.add(exampleNetworkUseCase.getPostByIdWithCorrectPinningPublicKey(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _fetchedPostState.value = FetchNetworkState.SUCCESS
                },
                { exception ->
                    if (exception is FeatureException) {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = exception.title,
                            message = exception.message ?: "-"
                        )
                    } else {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = "Failed / Gagal",
                            message = exception.message ?: "-"
                        )
                    }
                },
                {}
            ))
    }

    fun fetchedPostUsingIncorrectPinningPublicKey() {
        _fetchedPostState.value = FetchNetworkState.LOADING
        compositeDisposable.add(exampleNetworkUseCase.getPostByIdWithIncorrectPinningPublicKey(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _fetchedPostState.value = FetchNetworkState.SUCCESS
                },
                { exception ->
                    if (exception is FeatureException) {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = exception.title,
                            message = exception.message ?: "-"
                        )
                    } else {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = "Failed / Gagal",
                            message = exception.message ?: "-"
                        )
                    }
                },
                {}
            ))
    }

    fun fetchedPostUsingCorrectCertFromResource() {
        _fetchedPostState.value = FetchNetworkState.LOADING
        compositeDisposable.add(exampleNetworkUseCase.getPostByIdWithCorrectCertFromResource(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _fetchedPostState.value = FetchNetworkState.SUCCESS
                },
                { exception ->
                    if (exception is FeatureException) {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = exception.title,
                            message = exception.message ?: "-"
                        )
                    } else {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = "Failed / Gagal",
                            message = exception.message ?: "-"
                        )
                    }
                },
                {}
            ))
    }

    fun fetchedPostUsingIncorrectCertFromResource() {
        _fetchedPostState.value = FetchNetworkState.LOADING
        compositeDisposable.add(exampleNetworkUseCase.getPostByIdWithIncorrectCertFromResource(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _fetchedPostState.value = FetchNetworkState.SUCCESS
                },
                { exception ->
                    if (exception is FeatureException) {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = exception.title,
                            message = exception.message ?: "-"
                        )
                    } else {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = "Failed / Gagal",
                            message = exception.message ?: "-"
                        )
                    }
                },
                {}
            ))
    }

    fun fetchedPostUsingCorrectFingerprint() {
        _fetchedPostState.value = FetchNetworkState.LOADING
        compositeDisposable.add(exampleNetworkUseCase.getPostByIdWithCorrectFingerprint(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _fetchedPostState.value = FetchNetworkState.SUCCESS
                },
                { exception ->
                    if (exception is FeatureException) {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = exception.title,
                            message = exception.message ?: "-"
                        )
                    } else {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = "Failed / Gagal",
                            message = exception.message ?: "-"
                        )
                    }
                },
                {}
            ))
    }

    fun fetchedPostUsingIncorrectFingerprint() {
        _fetchedPostState.value = FetchNetworkState.LOADING
        compositeDisposable.add(exampleNetworkUseCase.getPostByIdWithIncorrectFingerprint(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _fetchedPostState.value = FetchNetworkState.SUCCESS
                },
                { exception ->
                    if (exception is FeatureException) {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = exception.title,
                            message = exception.message ?: "-"
                        )
                    } else {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = "Failed / Gagal",
                            message = exception.message ?: "-"
                        )
                    }
                },
                {}
            ))
    }

    fun fetchedPostWithRetrySSLMechanism() {
        _fetchedPostState.value = FetchNetworkState.LOADING
        compositeDisposable.add(exampleNetworkUseCase.getPostByIdWithRetrySSLMechanism(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    _fetchedPostState.value = FetchNetworkState.SUCCESS
                },
                { exception ->
                    if (exception is FeatureException) {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = exception.title,
                            message = exception.message ?: "-"
                        )
                    } else {
                        _fetchedPostState.value = FetchNetworkState.FAILED(
                            title = "Failed / Gagal",
                            message = exception.message ?: "-"
                        )
                    }
                },
                {}
            ))
    }
}