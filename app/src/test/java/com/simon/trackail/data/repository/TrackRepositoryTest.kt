package com.simon.trackail.data.repository

import com.simon.trackail.data.local.dao.ShipmentDao
import com.simon.trackail.data.local.entity.Shipment
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

    private lateinit var repository: TrackRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = TrackRepository(shipmentDao)
    }

    @Test
    fun `addOrUpdateShipment 应该调用 DAO 的 insert 方法`() = runTest {
        val shipment = Shipment(
            trackingNumber = "123456",
            carrierCode = null,
            alias = "测试包裹",
            status = 0,
            isInPool = true,
            lastUpdate = 0
        )
        
        `when`(shipmentDao.insertShipment(shipment)).thenReturn(1L)
        
        repository.addOrUpdateShipment(shipment)
        
        verify(shipmentDao).insertShipment(shipment)
    }

    @Test
    fun `getActivePoolShipments 应该调用 DAO 的 getShipmentsToRefresh 方法`() = runTest {
        repository.getActivePoolShipments()
        verify(shipmentDao).getShipmentsToRefresh()
    }
}
