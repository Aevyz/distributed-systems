package dev.lochert.ds.blockchain.http.server.strategy.maintenance

import dev.lochert.ds.blockchain.address.AddressList

class BackupAddressMaintenance : AddressMaintenance {
    override fun maintain(addressList: AddressList) {
        addressList.exportToFile()
    }
}