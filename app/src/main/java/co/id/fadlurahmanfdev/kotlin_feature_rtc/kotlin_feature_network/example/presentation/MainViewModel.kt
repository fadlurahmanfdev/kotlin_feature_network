package co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.presentation

import androidx.lifecycle.ViewModel
import co.id.fadlurahmanfdev.kotlin_feature_rtc.kotlin_feature_network.example.domain.ExampleNetworkUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel(
    private val exampleNetworkUseCase: ExampleNetworkUseCase
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    fun generateGuestSession() {
        compositeDisposable.add(exampleNetworkUseCase.generateGuestSession()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { guestSession ->
                    println("MASUK_")
                    println("$guestSession")
                },
                { exception ->
                    println("MASUK_ EXCEPTION")
                    println("$exception")
                },
                {}
            ))
    }
}