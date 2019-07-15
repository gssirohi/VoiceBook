package mygojekapp

import com.gojek.domain.datamanager.WeatherDataManager
import com.gojek.domain.executor.PostExecutionThread
import com.gojek.domain.executor.ThreadExecutor
import com.gojek.domain.interactor.forecast.GetForecast
import com.gojek.domain.model.ForecastData
import com.gojek.domain.model.ForecastRequest
import com.gojek.mygojekapp.test.factory.ForecastFactory
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.util.*

@RunWith(JUnit4::class)
class GetForecastUseCaseTest {

    private lateinit var usecase: GetForecast
    private lateinit var mockThreadExecutor: ThreadExecutor
    private lateinit var mockPostExecutionThread: PostExecutionThread

    private lateinit var dataManager: WeatherDataManager

    @Before
    fun setUp(){
        mockThreadExecutor = mock()
        mockPostExecutionThread = mock()
        dataManager = mock()
        usecase = GetForecast(dataManager,mockThreadExecutor,mockPostExecutionThread)

    }

    @Test
    fun buildUseCaseObservableCallsDataManager(){
        usecase.buildUseCaseObservable(ForecastRequest("bangalore",5))
        verify(dataManager).getForecast("bangalore",5)
    }

    @Test
    fun buildUseCaseObservableCompletes() {
        stubDataManagerGetForecast(Flowable.just(ForecastFactory.makeForecastDomainData()))
        val testObserver = usecase.buildUseCaseObservable(ForecastRequest("bangalore",5)).test()
        testObserver.assertComplete()
    }

    @Test
    fun buildUseCaseObservableReturnsData(){
        var data = ForecastFactory.makeForecastDomainData()
        val flowableEntity = Flowable.just(data)
        stubDataManagerGetForecast(flowableEntity)
        val testObservable = usecase.buildUseCaseObservable(ForecastRequest("Test",5)).test()
        testObservable.assertValue(data)
    }

    private fun stubDataManagerGetForecast(just: Flowable<ForecastData>?) {
        whenever(dataManager.getForecast(Mockito.anyString(),Mockito.anyInt()))
            .thenReturn(just)
    }
}