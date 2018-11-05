package io.github.thenilesh.httpirremote.dto;

import android.arch.persistence.room.Entity;

import java.util.Arrays;

public class IRCode {
    private String type;
    private String khz;
    private int[] data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKhz() {
        return khz;
    }

    public void setKhz(String khz) {
        this.khz = khz;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "IRCode{" +
                "type='" + type + '\'' +
                ", khz='" + khz + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
