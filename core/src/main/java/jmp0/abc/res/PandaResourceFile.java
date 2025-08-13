package jmp0.abc.res;

import com.google.common.primitives.UnsignedInteger;
import jmp0.abc.PandaParseException;
import jmp0.abc.file.PandaFileUtils;
import jmp0.abc.res.idtable.PandaResourceIdData;
import jmp0.abc.res.idtable.PandaResourceIdSet;
import jmp0.abc.res.limitkey.PandaResourceLimitKeyConfig;
import jmp0.abc.res.record.PandaResourceRecordItem;
import jmp0.abc.res.types.PandaResourceResType;
import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @Author: jmp0
 * @Email: jmp0@qq.com
 */
@Getter
public final class PandaResourceFile {
    private final byte[] data;
    private final HashMap<UnsignedInteger, PandaResourceOffset> offsets = new HashMap<>();
    private final PandaResourceIndexHeader header;
    private final HashMap<UnsignedInteger, PandaResourceLimitKeyConfig> limitKeys = new HashMap<>();
    private final LinkedList<PandaResourceIdSet> resourceIdSets = new LinkedList<>();
    private final HashMap<UnsignedInteger, PandaResourceIdData> resourceDatas = new HashMap<>();
    private final LinkedList<PandaResourceItem> resourceItems = new LinkedList<>();
    private UnsignedInteger INDEX = UnsignedInteger.ZERO;

    public PandaResourceFile(String path) throws IOException, PandaParseException {
        this(new FileInputStream(path));
    }

    public PandaResourceFile(InputStream inputStream) throws PandaParseException, IOException {
        this.data = inputStream.readAllBytes();
        this.header = new PandaResourceIndexHeader(this.data);
        INDEX = INDEX.plus(UnsignedInteger.valueOf((PandaResourceIndexHeader.SIZE)));
        parseLimitKeys();
        parseIdTables();
        parseDataRecord();
        System.out.println();
    }

    private void parseLimitKeys() throws PandaParseException {
        int limitKeyConfigSize = this.header.getLimitKeyConfigSize().intValue();
        for (int i = 0; i < limitKeyConfigSize; i++) {
            PandaResourceLimitKeyConfig config = PandaResourceLimitKeyConfig.create(this,INDEX);
            INDEX = INDEX.plus(config.getSIZE());
            limitKeys.put(config.getKeyOffset(),config);
        }
    }

    private void parseIdTables() throws PandaParseException {
        int limitKeyConfigSize = this.header.getLimitKeyConfigSize().intValue();
        for (int i = 0; i < limitKeyConfigSize; i++) {
            PandaResourceIdSet idSet = PandaResourceIdSet.create(this,INDEX);
            INDEX = INDEX.plus(idSet.getSIZE());
            resourceIdSets.add(idSet);
        }
        for (PandaResourceIdSet resourceIdSet : resourceIdSets) {
            for (PandaResourceIdData idDatum : resourceIdSet.getIdData()) {
                resourceDatas.put(idDatum.getDataOffset(), idDatum);
            }
        }
    }

    private void parseDataRecord() throws PandaParseException {
        while (INDEX.compareTo(this.header.getFileSize()) < 0){
            PandaResourceRecordItem item = parseDataRecordPrepare();
            INDEX = INDEX.plus(item.SIZE);
            parseDataRecordStart(item);
        }
    }

    private PandaResourceRecordItem parseDataRecordPrepare() throws PandaParseException {
        return PandaResourceRecordItem.create(this,INDEX);
    }

    private void parseDataRecordStart(PandaResourceRecordItem recordItem) throws PandaParseException {
        UnsignedInteger offset = INDEX.minus(UnsignedInteger.valueOf(12));
        //values
        short valueSize = PandaFileUtils.bytes2Uint16(this.getData(),INDEX.intValue());
        INDEX = INDEX.plus(UnsignedInteger.valueOf(2));
        byte[] values = PandaFileUtils.readSubBytes(getData(),INDEX.intValue(),valueSize);
        INDEX = INDEX.plus(UnsignedInteger.valueOf(valueSize));

        //file Name
        short fileNameSize = PandaFileUtils.bytes2Uint16(this.getData(),INDEX.intValue());
        INDEX = INDEX.plus(UnsignedInteger.valueOf(2));
        String fileName = PandaResourceUtils.bytes2String(PandaFileUtils.readSubBytes(getData(),INDEX.intValue(), fileNameSize - 1));
        INDEX = INDEX.plus(UnsignedInteger.valueOf(fileNameSize));

        PandaResourceIdData data = this.resourceDatas.get(offset);
        if (data == null) throw new PandaParseException("parseDataRecordStart can not find resourceIdSets!");
        if (data.getId().compareTo(recordItem.getId()) != 0) throw new PandaParseException("parseDataRecordStart id mismatch!");
        PandaResourceLimitKeyConfig limitKeyConfig = limitKeys.get(data.getParent().getOffset());
        if (limitKeyConfig == null) throw new PandaParseException("parseDataRecordStart limitKeyConfig not found!");

        PandaResourceItem resourceItem = new PandaResourceItem(fileName, limitKeyConfig.getKeyParams(), PandaResourceResType.resolve(recordItem.getResType().intValue()));
        String limitKey = PandaResourceUtils.parseKeyParam(limitKeyConfig.getKeyParams());
        resourceItem.setLimitKey(limitKey);
        resourceItem.setValues(values);
        resourceItem.parseValues();
        resourceItems.add(resourceItem);

    }

    public PandaResourceOffset resolveOffset(UnsignedInteger offset){
        return offsets.getOrDefault(offset, null);
    }

    public void addOffset(UnsignedInteger ptr,PandaResourceOffset offset){
        offsets.put(ptr, offset);
    }
}
