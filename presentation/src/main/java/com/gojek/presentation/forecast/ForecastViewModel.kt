package com.gojek.presentation.forecast

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.gojek.domain.interactor.forecast.GetForecast
import com.gojek.domain.model.ForecastData
import com.gojek.presentation.mapper.ForecastModelMapper
import com.gojek.presentation.model.ForecastModel
import io.reactivex.subscribers.DisposableSubscriber
import org.buffer.android.boilerplate.presentation.data.Resource
import org.buffer.android.boilerplate.presentation.data.ResourceState

import javax.inject.Inject

class ForecastViewModel @Inject internal constructor(
    private val Forecast: GetForecast,
    private val modelMapper:ForecastModelMapper): ViewModel(){

    private val forecastLiveData:MutableLiveData<Resource<ForecastModel>> = MutableLiveData()

    init {
        fetchForecast()
    }

    override fun onCleared() {
        Forecast.dispose()
        super.onCleared()
    }
    fun getForecast(): MutableLiveData<Resource<ForecastModel>> {
        return forecastLiveData
    }

    private fun fetchForecast() {
        forecastLiveData.postValue(Resource(ResourceState.LOADING,null,null))
        return Forecast.execute(ForecastSubscriber())
    }

    inner class ForecastSubscriber:DisposableSubscriber<ForecastData>() {
        override fun onComplete() {}

        override fun onNext(t: ForecastData?) {
            forecastLiveData.postValue(Resource(ResourceState.SUCCESS,modelMapper.mapFromData(t!!),null))
        }

        override fun onError(t: Throwable?) {
            forecastLiveData.postValue(Resource(ResourceState.ERROR, null, t?.message))
        }

    }
}