package garl;
/** Copyright (c) 2019-2022 placeh.io,
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author xagau
 * @email seanbeecroft@gmail.com
 *
 */
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
