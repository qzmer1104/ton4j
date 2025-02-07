package org.ton.java.smartcontract.types;

import lombok.Builder;
import lombok.Data;
import org.ton.java.address.Address;
import org.ton.java.cell.Cell;

import java.math.BigInteger;

@Builder
@Data
public class ItemData {
    boolean isInitialized;
    BigInteger index;
    Address collectionAddress;
    Address ownerAddress;
    Cell contentCell;
    String contentUri;
}
