package org.ton.java.smartcontract.wallet.v1;

import com.iwebpp.crypto.TweetNaclFast;
import lombok.Builder;
import lombok.Getter;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.smartcontract.types.WalletCodes;
import org.ton.java.smartcontract.types.WalletV1R1Config;
import org.ton.java.smartcontract.utils.MsgUtils;
import org.ton.java.smartcontract.wallet.Contract;
import org.ton.java.tlb.types.CurrencyCollection;
import org.ton.java.tlb.types.InternalMessageInfo;
import org.ton.java.tlb.types.Message;
import org.ton.java.tlb.types.MsgAddressIntStd;
import org.ton.java.tonlib.Tonlib;
import org.ton.java.tonlib.types.ExtMessageInfo;
import org.ton.java.utils.Utils;

import java.math.BigInteger;

import static java.util.Objects.isNull;

@Builder
@Getter
public class WalletV1R1 implements Contract {

    TweetNaclFast.Signature.KeyPair keyPair;
    long initialSeqno;

    public static class WalletV1R1Builder {
        WalletV1R1Builder() {
            if (isNull(keyPair)) {
                keyPair = Utils.generateSignatureKeyPair();
            }
        }

    }

    private Tonlib tonlib;
    private long wc;

    @Override
    public Tonlib getTonlib() {
        return tonlib;
    }

    @Override
    public long getWorkchain() {
        return wc;
    }

    @Override
    public String getName() {
        return "V1R1";
    }

    @Override
    public Cell createDataCell() {
        return CellBuilder.beginCell()
                .storeUint(initialSeqno, 32) // seqno
                .storeBytes(keyPair.getPublicKey())
                .endCell();
    }

    @Override
    public Cell createCodeCell() {
        return CellBuilder.beginCell().
                fromBoc(WalletCodes.V1R1.getValue()).
                endCell();
    }

    Cell createDeployMessage() {
        return CellBuilder.beginCell().storeUint(initialSeqno, 32).endCell();
    }

    Cell createTransferBody(WalletV1R1Config config) {
        Cell order = Message.builder()
                .info(InternalMessageInfo.builder()
                        .bounce(config.getBounce())
                        .dstAddr(MsgAddressIntStd.builder()
                                .workchainId(config.getDestination().wc)
                                .address(config.getDestination().toBigInteger())
                                .build())
                        .value(CurrencyCollection.builder().coins(config.getAmount()).build())
                        .build())
                .init(config.getIntMsgStateInit())
                .body(CellBuilder.beginCell()
                        .storeUint(0, 32)
                        .storeString(config.getComment())
                        .endCell())
                .build().toCell();

        return CellBuilder.beginCell()
                .storeUint(BigInteger.valueOf(config.getSeqno()), 32)
                .storeUint((config.getMode() == 0) ? 3 : config.getMode() & 0xff, 8)
                .storeRef(order)
                .endCell();
    }


    /**
     * Sends amount of nano toncoins to destination address using specified seqno
     *
     * @param config WalletV1R1Config
     */
    public ExtMessageInfo send(WalletV1R1Config config) {
        Cell body = isNull(config.getBody()) ? createTransferBody(config) : config.getBody();
        Message externalMessage = MsgUtils.createExternalMessageWithSignedBody(keyPair, getAddress(), null, body);
        return tonlib.sendRawMessage(externalMessage.toCell().toBase64());
    }

    public Message prepareMsg(WalletV1R1Config config) {
        Cell body = isNull(config.getBody()) ? createTransferBody(config) : config.getBody();
        return MsgUtils.createExternalMessageWithSignedBody(keyPair, getAddress(), null, body);
    }


    public ExtMessageInfo deploy() {
        Cell body = createDeployMessage();
        Message externalMessage = MsgUtils.createExternalMessageWithSignedBody(keyPair, getAddress(), getStateInit(), body);
        return tonlib.sendRawMessage(externalMessage.toCell().toBase64());
    }
}