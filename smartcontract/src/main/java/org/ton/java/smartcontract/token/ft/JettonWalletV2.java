package org.ton.java.smartcontract.token.ft;

import com.iwebpp.crypto.TweetNaclFast;
import lombok.Builder;
import lombok.Getter;
import org.ton.java.address.Address;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.smartcontract.token.nft.NftUtils;
import org.ton.java.smartcontract.types.JettonWalletData;
import org.ton.java.smartcontract.types.WalletCodes;
import org.ton.java.smartcontract.wallet.Contract;
import org.ton.java.tonlib.Tonlib;
import org.ton.java.tonlib.types.RunResult;
import org.ton.java.tonlib.types.TvmStackEntryCell;
import org.ton.java.tonlib.types.TvmStackEntryNumber;
import org.ton.java.tonlib.types.TvmStackEntrySlice;
import org.ton.java.utils.Utils;

import java.math.BigInteger;

import static java.util.Objects.isNull;

@Builder
@Getter
public class JettonWalletV2 implements Contract {

    TweetNaclFast.Signature.KeyPair keyPair;
    Address address;

    public static class JettonWalletV2Builder {
    }

    public static JettonWalletV2Builder builder() {
        return new CustomJettonWalletV2Builder();
    }

    private static class CustomJettonWalletV2Builder extends JettonWalletV2Builder {
        @Override
        public JettonWalletV2 build() {
            if (isNull(super.keyPair)) {
                super.keyPair = Utils.generateSignatureKeyPair();
            }
            return super.build();
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

    public String getName() {
        return "jettonWalletV2";
    }

    @Override
    public Cell createDataCell() {
        return CellBuilder.beginCell().endCell();
    }

    @Override
    public Cell createCodeCell() {
        return CellBuilder.beginCell().
                fromBoc(WalletCodes.jettonWalletV2.getValue()).
                endCell();
    }

    /**
     * @return Cell cell contains nft data
     */
    public static Cell createTransferBody(long queryId, BigInteger jettonAmount, Address toAddress, Address responseAddress, BigInteger forwardAmount, byte[] forwardPayload) {
        CellBuilder cell = CellBuilder.beginCell();
        cell.storeUint(0xf8a7ea5, 32);
        cell.storeUint(queryId, 64); // default
        cell.storeCoins(jettonAmount);
        cell.storeAddress(toAddress);
        cell.storeAddress(responseAddress);
        cell.storeBit(false); // null custom_payload
        cell.storeCoins(forwardAmount); // default 0
        cell.storeBit(false); // forward_payload in this slice, not ref cell
        if (forwardPayload.length != 0) {
            cell.storeBytes(forwardPayload);
        }
        return cell.endCell();
    }

    /**
     * @param queryId         long
     * @param jettonAmount    BigInteger
     * @param responseAddress Address
     */
    public static Cell createBurnBody(long queryId, BigInteger jettonAmount, Address responseAddress) {
        return CellBuilder.beginCell()
                .storeUint(0x595f07bc, 32) //burn up
                .storeUint(queryId, 64)
                .storeCoins(jettonAmount)
                .storeAddress(responseAddress)
                .endCell();
    }

    /**
     * Note, status==0 means unlocked - user can freely transfer and receive jettons (only admin can burn).
     * (status & 1) bit means user can not send jettons
     * (status & 2) bit means user can not receive jettons.
     * Master (minter) smart-contract able to make outgoing actions (transfer, burn jettons) with any status.
     *
     * @param queryId
     * @param status
     * @return
     */
    public static Cell createStatusBody(long queryId, int status) {
        return CellBuilder.beginCell()
                .storeUint(0xeed236d3, 32) //change status
                .storeUint(queryId, 64)
                .storeUint(status, 4) //new status
                .endCell();
    }

    public JettonWalletData getData() {
        RunResult result = tonlib.runMethod(address, "get_wallet_data");

        if (result.getExit_code() != 0) {
            throw new Error("method get_wallet_data, returned an exit code " + result.getExit_code());
        }

        TvmStackEntryNumber balanceNumber = (TvmStackEntryNumber) result.getStack().get(0);
        BigInteger balance = balanceNumber.getNumber();

        TvmStackEntrySlice ownerAddr = (TvmStackEntrySlice) result.getStack().get(1);
        Address ownerAddress = NftUtils.parseAddress(CellBuilder.beginCell().fromBoc(Utils.base64ToBytes(ownerAddr.getSlice().getBytes())).endCell());

        TvmStackEntrySlice jettonMinterAddr = (TvmStackEntrySlice) result.getStack().get(2);
        Address jettonMinterAddress = NftUtils.parseAddress(CellBuilder.beginCell().fromBoc(Utils.base64ToBytes(jettonMinterAddr.getSlice().getBytes())).endCell());

        TvmStackEntryCell jettonWallet = (TvmStackEntryCell) result.getStack().get(3);
        Cell jettonWalletCode = CellBuilder.beginCell().fromBoc(Utils.base64ToBytes(jettonWallet.getCell().getBytes())).endCell();
        return JettonWalletData.builder()
                .balance(balance)
                .ownerAddress(ownerAddress)
                .jettonMinterAddress(jettonMinterAddress)
                .jettonWalletCode(jettonWalletCode)
                .build();
    }

    public BigInteger getBalance() {
        RunResult result = tonlib.runMethod(address, "get_wallet_data");

        if (result.getExit_code() != 0) {
            throw new Error("method get_wallet_data, returned an exit code " + result.getExit_code());
        }

        TvmStackEntryNumber balanceNumber = (TvmStackEntryNumber) result.getStack().get(0);
        return balanceNumber.getNumber();
    }
}