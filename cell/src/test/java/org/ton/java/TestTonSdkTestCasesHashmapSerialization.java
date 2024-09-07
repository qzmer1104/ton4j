package org.ton.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.ton.java.address.Address;
import org.ton.java.cell.*;
import org.ton.java.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@Slf4j
@RunWith(JUnit4.class)
public class TestTonSdkTestCasesHashmapSerialization {
    public static final String numbersTestFileUrl = "https://raw.githubusercontent.com/neodix42/ton-sdk-test-cases/main/hashmap-serialization.json";
    Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
    String fileContentWithUseCases = IOUtils.toString(new URL(numbersTestFileUrl), Charset.defaultCharset());
    TonSdkTestCases tonSdkTestCases = gson.fromJson(fileContentWithUseCases, TonSdkTestCases.class);

    public TestTonSdkTestCasesHashmapSerialization() throws IOException {
    }

    @Test
    public void testHashmapSerialization1() {

        String testId = "hashmap-serialization-1";
        TonSdkTestCases.TestCase testCase = tonSdkTestCases.getTestCases().get(testId);

        String description = testCase.getDescription();

        log.info("testId: {}", testId);
        log.info("description: {}", description);

        int keySizeBits = Integer.parseInt(testCase.getInput().get("keySizeBits").toString());
        int valueSizeBits = Integer.parseInt(testCase.getInput().get("valueSizeBits").toString());
        Long key1 = Long.parseLong(testCase.getInput().get("key1").toString());
        Byte value1 = Byte.parseByte(testCase.getInput().get("value1").toString());
        Long key2 = Long.parseLong(testCase.getInput().get("key2").toString());
        Byte value2 = Byte.parseByte(testCase.getInput().get("value2").toString());
        Long key3 = Long.parseLong(testCase.getInput().get("key3").toString());
        Byte value3 = Byte.parseByte(testCase.getInput().get("value3").toString());
        Long key4 = Long.parseLong(testCase.getInput().get("key4").toString());
        Byte value4 = Byte.parseByte(testCase.getInput().get("value4").toString());

        TonHashMap x = new TonHashMap(keySizeBits);

        x.elements.put(key1, value1);
        x.elements.put(key2, value2);
        x.elements.put(key3, value3);
        x.elements.put(key4, value4);

        Cell cell = x.serialize(
                k -> CellBuilder.beginCell().storeUint((Long) k, keySizeBits).endCell().getBits(),
                v -> CellBuilder.beginCell().storeUint((byte) v, valueSizeBits).endCell()
        );

        String expectedCellPrint = testCase.getExpectedOutput().get("cellPrint").toString();
        String expectedCellHash = testCase.getExpectedOutput().get("cellHash").toString();
        String expectedCellBocWithCrc = testCase.getExpectedOutput().get("cellBocWithCrc").toString();

        assertThat(StringUtils.trim(cell.print())).isEqualTo(expectedCellPrint);
        assertThat(Utils.bytesToHex(cell.hash())).isEqualTo(expectedCellHash);
        assertThat(cell.toHex()).isEqualTo(expectedCellBocWithCrc);
    }

    @Test
    public void testHashmapSerialization2() {

        String testId = "hashmap-serialization-2";
        TonSdkTestCases.TestCase testCase = tonSdkTestCases.getTestCases().get(testId);

        String description = testCase.getDescription();

        log.info("testId: {}", testId);
        log.info("description: {}", description);

        int keySizeBits = Integer.parseInt(testCase.getInput().get("keySizeBits").toString());
        int valueSizeBits = Integer.parseInt(testCase.getInput().get("valueSizeBits").toString());
        Long key1 = Long.parseLong(testCase.getInput().get("key1").toString());
        Byte value1 = Byte.parseByte(testCase.getInput().get("value1").toString());

        TonHashMap x = new TonHashMap(keySizeBits);

        x.elements.put(key1, value1);

        Cell cell = x.serialize(
                k -> CellBuilder.beginCell().storeUint((Long) k, keySizeBits).endCell().getBits(),
                v -> CellBuilder.beginCell().storeUint((byte) v, valueSizeBits).endCell()
        );

        String expectedCellPrint = testCase.getExpectedOutput().get("cellPrint").toString();
        String expectedCellHash = testCase.getExpectedOutput().get("cellHash").toString();
        String expectedCellBocWithCrc = testCase.getExpectedOutput().get("cellBocWithCrc").toString();

        assertThat(StringUtils.trim(cell.print())).isEqualTo(expectedCellPrint);
        assertThat(Utils.bytesToHex(cell.hash())).isEqualTo(expectedCellHash);
        assertThat(cell.toHex()).isEqualTo(expectedCellBocWithCrc);
    }

    @Test
    public void testHashmapSerialization3() {

        String testId = "hashmap-serialization-3";
        TonSdkTestCases.TestCase testCase = tonSdkTestCases.getTestCases().get(testId);

        String description = testCase.getDescription();

        log.info("testId: {}", testId);
        log.info("description: {}", description);

        int keySizeBits = Integer.parseInt(testCase.getInput().get("keySizeBits").toString());
        int valueSizeBits = Integer.parseInt(testCase.getInput().get("valueSizeBits").toString());

        TonHashMap x = new TonHashMap(keySizeBits);

        assertThrows(Error.class, () -> {
            x.serialize(
                    k -> CellBuilder.beginCell().storeUint((Long) k, keySizeBits).endCell().getBits(),
                    v -> CellBuilder.beginCell().storeUint((byte) v, valueSizeBits).endCell()
            );
        });
    }

    @Test
    public void testHashmapSerialization4() {

        String testId = "hashmap-serialization-4";
        TonSdkTestCases.TestCase testCase = tonSdkTestCases.getTestCases().get(testId);

        String description = testCase.getDescription();

        log.info("testId: {}", testId);
        log.info("description: {}", description);

        int keySizeBits = Integer.parseInt(testCase.getInput().get("keySizeBits").toString());
        Long key1 = Long.parseLong(testCase.getInput().get("key1").toString());
        Address value1 = Address.of(testCase.getInput().get("value1").toString());
        Long key2 = Long.parseLong(testCase.getInput().get("key2").toString());
        Address value2 = Address.of(testCase.getInput().get("value2").toString());
        Long key3 = Long.parseLong(testCase.getInput().get("key3").toString());
        Address value3 = Address.of(testCase.getInput().get("value3").toString());
        Long key4 = Long.parseLong(testCase.getInput().get("key4").toString());
        Address value4 = Address.of(testCase.getInput().get("value4").toString());

        TonHashMapE x = new TonHashMapE(keySizeBits);

        x.elements.put(key1, value1);
        x.elements.put(key2, value2);
        x.elements.put(key3, value3);
        x.elements.put(key4, value4);

        Cell cell = x.serialize(
                k -> CellBuilder.beginCell().storeUint((Long) k, keySizeBits).endCell().getBits(),
                v -> CellBuilder.beginCell().storeAddress((Address) v).endCell()
        );

        String expectedCellPrint = testCase.getExpectedOutput().get("cellPrint").toString();
        String expectedCellHash = testCase.getExpectedOutput().get("cellHash").toString();
        String expectedCellBocWithCrc = testCase.getExpectedOutput().get("cellBocWithCrc").toString();

        assertThat(StringUtils.trim(cell.print())).isEqualTo(expectedCellPrint);
        assertThat(Utils.bytesToHex(cell.hash())).isEqualTo(expectedCellHash);
        assertThat(cell.toHex()).isEqualTo(expectedCellBocWithCrc);
    }

    @Test
    public void testHashmapSerialization5() {

        String testId = "hashmap-serialization-5";
        TonSdkTestCases.TestCase testCase = tonSdkTestCases.getTestCases().get(testId);

        String description = testCase.getDescription();

        log.info("testId: {}", testId);
        log.info("description: {}", description);

        int keySizeBits = Integer.parseInt(testCase.getInput().get("keySizeBits").toString());
        Long key1 = Long.parseLong(testCase.getInput().get("key1").toString());
        Address value1 = Address.of(testCase.getInput().get("value1").toString());

        TonHashMapE x = new TonHashMapE(keySizeBits);

        x.elements.put(key1, value1);

        Cell cell = x.serialize(
                k -> CellBuilder.beginCell().storeUint((Long) k, keySizeBits).endCell().getBits(),
                v -> CellBuilder.beginCell().storeAddress((Address) v).endCell()
        );

        String expectedCellPrint = testCase.getExpectedOutput().get("cellPrint").toString();
        String expectedCellHash = testCase.getExpectedOutput().get("cellHash").toString();
        String expectedCellBocWithCrc = testCase.getExpectedOutput().get("cellBocWithCrc").toString();

        assertThat(StringUtils.trim(cell.print())).isEqualTo(expectedCellPrint);
        assertThat(Utils.bytesToHex(cell.hash())).isEqualTo(expectedCellHash);
        assertThat(cell.toHex()).isEqualTo(expectedCellBocWithCrc);
    }

    @Test
    public void testHashmapSerialization6() {

        String testId = "hashmap-serialization-6";
        TonSdkTestCases.TestCase testCase = tonSdkTestCases.getTestCases().get(testId);

        String description = testCase.getDescription();

        log.info("testId: {}", testId);
        log.info("description: {}", description);

        int keySizeBits = Integer.parseInt(testCase.getInput().get("keySizeBits").toString());

        TonHashMapE x = new TonHashMapE(keySizeBits);

        Cell cell = x.serialize(
                k -> CellBuilder.beginCell().storeUint((Long) k, keySizeBits).endCell().getBits(),
                v -> CellBuilder.beginCell().storeAddress((Address) v).endCell()
        );

        String expectedCellPrint = testCase.getExpectedOutput().get("cellPrint").toString();
        String expectedCellHash = testCase.getExpectedOutput().get("cellHash").toString();
        String expectedCellBocWithCrc = testCase.getExpectedOutput().get("cellBocWithCrc").toString();

        assertThat(StringUtils.trim(cell.print())).isEqualTo(expectedCellPrint);
        assertThat(Utils.bytesToHex(cell.hash())).isEqualTo(expectedCellHash);
        assertThat(cell.toHex()).isEqualTo(expectedCellBocWithCrc);
    }

    @Test
    public void testHashmapSerialization7() {

        String testId = "hashmap-serialization-7";
        TonSdkTestCases.TestCase testCase = tonSdkTestCases.getTestCases().get(testId);

        String description = testCase.getDescription();

        log.info("testId: {}", testId);
        log.info("description: {}", description);

        int keySizeBits = Integer.parseInt(testCase.getInput().get("keySizeBits").toString());
        int valueSizeBits = Integer.parseInt(testCase.getInput().get("valueSizeBits").toString());

        Long key1 = Long.parseLong(testCase.getInput().get("key1").toString());
        Byte value1 = Byte.parseByte(testCase.getInput().get("value1").toString());
        Long key2 = Long.parseLong(testCase.getInput().get("key2").toString());
        Byte value2 = Byte.parseByte(testCase.getInput().get("value2").toString());
        Long key3 = Long.parseLong(testCase.getInput().get("key3").toString());
        Byte value3 = Byte.parseByte(testCase.getInput().get("value3").toString());
        Long key4 = Long.parseLong(testCase.getInput().get("key4").toString());
        Byte value4 = Byte.parseByte(testCase.getInput().get("value4").toString());

        TonPfxHashMap x = new TonPfxHashMap(keySizeBits);

        x.elements.put(key1, value1);
        x.elements.put(key2, value2);
        x.elements.put(key3, value3);
        x.elements.put(key4, value4);

        Cell cell = x.serialize(
                k -> CellBuilder.beginCell().storeUint((Long) k, keySizeBits).endCell().getBits(),
                v -> CellBuilder.beginCell().storeUint((byte) v, valueSizeBits).endCell()
        );

        String expectedCellPrint = testCase.getExpectedOutput().get("cellPrint").toString();
        String expectedCellHash = testCase.getExpectedOutput().get("cellHash").toString();
        String expectedCellBocWithCrc = testCase.getExpectedOutput().get("cellBocWithCrc").toString();

        assertThat(StringUtils.trim(cell.print())).isEqualTo(expectedCellPrint);
        assertThat(Utils.bytesToHex(cell.hash())).isEqualTo(expectedCellHash);
        assertThat(cell.toHex()).isEqualTo(expectedCellBocWithCrc);
    }

    @Test
    public void testHashmapSerialization8() {

        String testId = "hashmap-serialization-8";
        TonSdkTestCases.TestCase testCase = tonSdkTestCases.getTestCases().get(testId);

        String description = testCase.getDescription();

        log.info("testId: {}", testId);
        log.info("description: {}", description);

        int keySizeBits = Integer.parseInt(testCase.getInput().get("keySizeBits").toString());
        int valueSizeBits = Integer.parseInt(testCase.getInput().get("valueSizeBits").toString());

        Long key1 = Long.parseLong(testCase.getInput().get("key1").toString());
        Byte value1 = Byte.parseByte(testCase.getInput().get("value1").toString());
        Long key2 = Long.parseLong(testCase.getInput().get("key2").toString());
        Byte value2 = Byte.parseByte(testCase.getInput().get("value2").toString());
        Long key3 = Long.parseLong(testCase.getInput().get("key3").toString());
        Byte value3 = Byte.parseByte(testCase.getInput().get("value3").toString());
        Long key4 = Long.parseLong(testCase.getInput().get("key4").toString());
        Byte value4 = Byte.parseByte(testCase.getInput().get("value4").toString());

        TonPfxHashMapE x = new TonPfxHashMapE(keySizeBits);

        x.elements.put(key1, value1);
        x.elements.put(key2, value2);
        x.elements.put(key3, value3);
        x.elements.put(key4, value4);

        Cell cell = x.serialize(
                k -> CellBuilder.beginCell().storeUint((Long) k, keySizeBits).endCell().getBits(),
                v -> CellBuilder.beginCell().storeUint((byte) v, valueSizeBits).endCell()
        );

        String expectedCellPrint = testCase.getExpectedOutput().get("cellPrint").toString();
        String expectedCellHash = testCase.getExpectedOutput().get("cellHash").toString();
        String expectedCellBocWithCrc = testCase.getExpectedOutput().get("cellBocWithCrc").toString();

        assertThat(StringUtils.trim(cell.print())).isEqualTo(expectedCellPrint);
        assertThat(Utils.bytesToHex(cell.hash())).isEqualTo(expectedCellHash);
        assertThat(cell.toHex()).isEqualTo(expectedCellBocWithCrc);
    }
}