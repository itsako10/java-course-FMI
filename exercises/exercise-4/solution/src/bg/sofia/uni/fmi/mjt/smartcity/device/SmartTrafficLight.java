package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;

public class SmartTrafficLight extends Device {
    public SmartTrafficLight(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime, DeviceType.TRAFFIC_LIGHT);
    }
}
