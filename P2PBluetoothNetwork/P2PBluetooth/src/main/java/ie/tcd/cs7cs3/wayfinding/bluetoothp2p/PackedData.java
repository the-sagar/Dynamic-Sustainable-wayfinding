package ie.tcd.cs7cs3.wayfinding.bluetoothp2p;

import java.io.Serializable;
import java.util.Arrays;

public class PackedData implements Serializable {
    private Integer readVersion;
    private Boolean checkVersion;
    private byte[] content;
    public PackedData(Integer readVersion, Boolean checkVersion, byte[] content) {
        super();
        this.readVersion = readVersion;
        this.checkVersion = checkVersion;
        this.content = content;
    }
    public Integer getReadVersion() {
        return readVersion;
    }
    public void setReadVersion(Integer readVersion) {
        this.readVersion = readVersion;
    }
    public Boolean getCheckVersion() {
        return checkVersion;
    }
    public void setCheckVersion(Boolean checkVersion) {
        this.checkVersion = checkVersion;
    }
    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PackedData{" +
                "readVersion=" + readVersion +
                ", checkVersion=" + checkVersion +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
