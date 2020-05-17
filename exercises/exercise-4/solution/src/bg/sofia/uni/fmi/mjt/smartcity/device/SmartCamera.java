package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;

public class SmartCamera extends Device {
    public SmartCamera(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime, DeviceType.CAMERA);
    }
}
