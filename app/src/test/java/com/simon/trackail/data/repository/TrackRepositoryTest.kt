package com.simon.trackail.data.repository

import com.simon.trackail.data.local.dao.ShipmentDao
import com.simon.trackail.data.local.entity.Shipment
import com.simon.trackail.data.remote.TrackApiService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * TrackRepository 的单元测试
 */
class TrackRepositoryTest {

    @Mock
    private lateinit var shipmentDao: ShipmentDao

    @Mock
    private lateinit var apiService: TrackApiService

    private lateinit var repository: TrackRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = TrackRepository(shipmentDao, apiService)
    }

    @Test
    fun `addAndRegisterShipment 应该调用 DAO 的 insert 方法`() = runTest {
        val shipment = Shipment(
            trackingNumber = "123456",
            carrierCode = null,
            alias = "测试包裹",
            status = 0,
            isInPool = true,
            lastUpdate = 0
        )
        
        `when`(shipmentDao.insertShipment(shipment)).thenReturn(1L)
        
        repository.addAndRegisterShipment("fake_token", shipment)
        
        verify(shipmentDao).insertShipment(shipment)
    }

    @Test
    fun `getActivePoolShipments 应该调用 DAO 的 getShipmentsToRefresh 方法`() = runTest {
        repository.getActivePoolShipments()
        verify(shipmentDao).getShipmentsToRefresh()
    }
}
