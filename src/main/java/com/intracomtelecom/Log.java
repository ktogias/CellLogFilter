package com.intracomtelecom;

import lombok.Data;

import java.util.Date;

@Data
public class Log {
    private Date recordDate;
    private String OSS;
    private String eNodeB;
    private String cell;
    private String carrier;
    private String cellAvailability;
    private String CQI;
    private String downlinkCellThroughput;
    private String downlinkPacketLossRate;
    private String downlinkUserThroughput;
}
