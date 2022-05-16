package garl;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

class ComputerIdentifier
{
    static String generateLicenseKey()
    {
        try {
            SystemInfo systemInfo = new SystemInfo();
            OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
            HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
            CentralProcessor centralProcessor = hardwareAbstractionLayer.getProcessor();
            ComputerSystem computerSystem = hardwareAbstractionLayer.getComputerSystem();

            String vendor = operatingSystem.getManufacturer();
            String processorSerialNumber = computerSystem.getSerialNumber();
            String processorIdentifier = centralProcessor.getProcessorIdentifier().getIdentifier();
            int processors = centralProcessor.getLogicalProcessorCount();

            String delimiter = "#";

            String v = vendor +
                    delimiter +
                    processorSerialNumber +
                    delimiter +
                    processorIdentifier +
                    delimiter +
                    processors;

            v = v.replaceAll("\"", " ");
            v = v.replaceAll("'", " ");
            return v;
        } catch(Exception ex) {
            Log.info("Computer Identify failed:" + ex);
            ex.printStackTrace();
        } finally {
            return Utility.getMACAddress();
        }
    }

    public static void main(String[] arguments)
    {
        String identifier = generateLicenseKey();
        System.out.println(identifier);
    }
}
