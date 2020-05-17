package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

public abstract class Device implements SmartDevice {
    private String ID;
    private String name;
    private double powerConsumption;
    private LocalDateTime installationDateTime;
    private DeviceType type;
    private static Map<DeviceType, Integer> deviceCounterByType = new EnumMap<>(DeviceType.class);

    public Device(String name, double powerConsumption, LocalDateTime installationDateTime, DeviceType type) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
        this.type = type;
        setDeviceTypeCounter(type);
        setID(type, name, deviceCounterByType.get(type));
    }

    private void setDeviceTypeCounter(DeviceType type) {
        if (type.equals(DeviceType.CAMERA)) {
            Integer currentCount = deviceCounterByType.putIfAbsent(DeviceType.CAMERA, 0);

            if (currentCount != null) {
                deviceCounterByType.put(DeviceType.CAMERA, ++currentCount);
            }
        }
        else if (type.equals(DeviceType.LAMP)) {
            Integer currentCount = deviceCounterByType.putIfAbsent(DeviceType.LAMP, 0);

            if (currentCount != null) {
                deviceCounterByType.put(DeviceType.LAMP, ++currentCount);
            }
        }
        else if (type.equals(DeviceType.TRAFFIC_LIGHT)) {
            Integer currentCount = deviceCounterByType.putIfAbsent(DeviceType.TRAFFIC_LIGHT, 0);

            if (currentCount != null) {
                deviceCounterByType.put(DeviceType.TRAFFIC_LIGHT, ++currentCount);
            }
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPowerConsumption() {
        return powerConsumption;
    }

    @Override
    public LocalDateTime getInstallationDateTime() {
        return installationDateTime;
    }

    @Override
    public DeviceType getType() {
        return type;
    }

    public void setID(DeviceType type, String name, Integer number) {
        this.ID = type.getShortName() + "-" + name + "-" + number.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Device device = (Device) obj;

        return this.getId().equals(device.getId());
    }

    public int compareTo(SmartDevice device) {
        return 1;
    }
}
