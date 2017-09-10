package ritwik.bluetooth.models;

public class DeviceInformation {
    private String deviceName;
    private String deviceMACAddress;

    public String getDeviceName () {return deviceName; }
    public void setDeviceName ( String deviceName ) { this.deviceName = deviceName; }

    public String getDeviceMACAddress () { return deviceMACAddress; }
    public void setDeviceMACAddress ( String deviceMACAddress ) { this.deviceMACAddress = deviceMACAddress; }
}