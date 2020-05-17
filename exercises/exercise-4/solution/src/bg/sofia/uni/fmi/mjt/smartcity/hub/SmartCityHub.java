package bg.sofia.uni.fmi.mjt.smartcity.hub;

import bg.sofia.uni.fmi.mjt.smartcity.device.PowerConsumptionEntry;
import bg.sofia.uni.fmi.mjt.smartcity.device.PowerConsumptionCompare;
import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class SmartCityHub {
    private Map<String, SmartDevice> registeredDevices;
    private Map<DeviceType, Integer> deviceQuantityPerType;

    public SmartCityHub() {
        registeredDevices = new LinkedHashMap<String, SmartDevice>();
        deviceQuantityPerType = new EnumMap<DeviceType, Integer>(DeviceType.class);
        deviceQuantityPerType.put(DeviceType.CAMERA, 0);
        deviceQuantityPerType.put(DeviceType.LAMP, 0);
        deviceQuantityPerType.put(DeviceType.TRAFFIC_LIGHT, 0);
    }

    /**
     * Adds a @device to the SmartCityHub.
     *
     * @throws IllegalArgumentException in case @device is null.
     * @throws DeviceAlreadyRegisteredException in case the @device is already registered.
     */
    public void register(SmartDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException("Given null device!");
        }

        if (registeredDevices.put(device.getId(), device) != null) {
            throw new DeviceAlreadyRegisteredException("Device already registered!");
        }

        incrementDeviceQuantityPerType(device.getType());
    }

    /**
     * Removes the @device from the SmartCityHub.
     *
     * @throws IllegalArgumentException in case null is passed.
     * @throws DeviceNotFoundException in case the @device is not found.
     */
    public void unregister(SmartDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException("Given null device!");
        }

        if (registeredDevices.remove(device) == null) {
            throw new DeviceNotFoundException("Device was not found!");
        }

        decrementDeviceQuantityPerType(device.getType());
    }

    /**
     * Returns a SmartDevice with an ID @id.
     *
     * @throws IllegalArgumentException in case @id is null.
     * @throws DeviceNotFoundException in case device with ID @id is not found.
     */
    public SmartDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("Given null ID!");
        }

        SmartDevice device = registeredDevices.get(id);

        if (device == null) {
            throw new DeviceNotFoundException("Device was not found!");
        }

        return device;
    }

    /**
     * Returns the total number of devices with type @type registered in SmartCityHub.
     *
     * @throws IllegalArgumentException in case @type is null.
     */
    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null) {
            throw new IllegalArgumentException("Given null type!");
        }

        return deviceQuantityPerType.get(type);
    }

    /**
     * Returns a collection of IDs of the top @n devices which consumed
     * the most power from the time of their installation until now.
     *
     * The total power consumption of a device is calculated by the hours elapsed
     * between the two LocalDateTime-s multiplied by the stated power consumption of the device.
     *
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Given n < 0!");
        }

        LocalDateTime timeNow = LocalDateTime.now();

        Set<Map.Entry<String, SmartDevice>> allDevicesSet = registeredDevices.entrySet();
        Set<Map.Entry<String, Double>> powerConsumptionPerID = new TreeSet<Map.Entry<String, Double>>(new PowerConsumptionCompare());

        for (Map.Entry<String, SmartDevice> i : allDevicesSet) {
            double powerConsumption = Duration.between(timeNow, i.getValue().getInstallationDateTime()).toHours() * i.getValue().getPowerConsumption();
            powerConsumptionPerID.add(new PowerConsumptionEntry(i.getKey(), powerConsumption));
        }

        Iterator<Map.Entry<String, Double>> iterator = powerConsumptionPerID.iterator();

        List<String> result = new ArrayList<>();

        for (int i = 0; iterator.hasNext() && i < n; ++i) {
            result.add(iterator.next().getKey());
        }

        return result;
    }

    /**
     * Returns a collection of the first @n registered devices, i.e the first @n that were added
     * in the SmartCityHub (registration != installation).
     *
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<SmartDevice> getFirstNDevicesByRegistration(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Given negative number n!");
        }

        Collection<SmartDevice> allDevices = registeredDevices.values();
        Iterator<SmartDevice> devicesIterator = allDevices.iterator();

        Collection<SmartDevice> result = new ArrayList<>();

        for (int i = 0; devicesIterator.hasNext() && i < n; ++i) {
            result.add(devicesIterator.next());
        }

        return result;
    }

    private void incrementDeviceQuantityPerType(DeviceType type) {
        if (type.equals(DeviceType.CAMERA)) {
            Integer currentCount = deviceQuantityPerType.putIfAbsent(DeviceType.CAMERA, 1);

            if (currentCount != null) {
                deviceQuantityPerType.put(DeviceType.CAMERA, ++currentCount);
            }
        }
        else if (type.equals(DeviceType.LAMP)) {
            Integer currentCount = deviceQuantityPerType.putIfAbsent(DeviceType.LAMP, 1);

            if (currentCount != null) {
                deviceQuantityPerType.put(DeviceType.LAMP, ++currentCount);
            }
        }
        else if (type.equals(DeviceType.TRAFFIC_LIGHT)) {
            Integer currentCount = deviceQuantityPerType.putIfAbsent(DeviceType.TRAFFIC_LIGHT, 1);

            if (currentCount != null) {
                deviceQuantityPerType.put(DeviceType.TRAFFIC_LIGHT, ++currentCount);
            }
        }
    }

    private void decrementDeviceQuantityPerType(DeviceType type) {
        if (type.equals(DeviceType.CAMERA)) {
            Integer currentCount = deviceQuantityPerType.putIfAbsent(DeviceType.CAMERA, 0);

            if (currentCount != null) {
                deviceQuantityPerType.put(DeviceType.CAMERA, --currentCount);
            }
        }
        else if (type.equals(DeviceType.LAMP)) {
            Integer currentCount = deviceQuantityPerType.putIfAbsent(DeviceType.LAMP, 0);

            if (currentCount != null) {
                deviceQuantityPerType.put(DeviceType.LAMP, --currentCount);
            }
        }
        else if (type.equals(DeviceType.TRAFFIC_LIGHT)) {
            Integer currentCount = deviceQuantityPerType.putIfAbsent(DeviceType.TRAFFIC_LIGHT, 0);

            if (currentCount != null) {
                deviceQuantityPerType.put(DeviceType.TRAFFIC_LIGHT, --currentCount);
            }
        }
    }
}