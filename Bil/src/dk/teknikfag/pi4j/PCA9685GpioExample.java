package dk.teknikfag.pi4j;

import java.math.BigDecimal;
import java.util.Scanner;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

/**
 * <p>
 * This example code demonstrates how to setup a custom GpioProvider
 * for GPIO PWM pin control using the PCA9685 16-channel, 12-bit PWM I2C-bus LED/Servo controller.
 * </p>
 * <p>
 * More information about the PCA9685 can be found here:<br>
 * <a href="http://www.nxp.com/documents/data_sheet/PCA9685.pdf">PCA9685.pdf</a><br><br>
 * ...and especially about the board here:<br>
 * <a href="http://www.adafruit.com/products/815">Adafruit 16-Channel 12-bit PWM/Servo Driver</a>
 * </p>
 *
 * @author Christian Wehrli
 * @see PCA9685GpioProvider
 */

public class PCA9685GpioExample implements Runnable  {

	Thread t;
    @SuppressWarnings("unused")
	private static final int SPEED_MIN = 1200;
    @SuppressWarnings("unused")
	private static final int SPEED_MAX = 1400;
    
    public PCA9685GpioProvider provider;

    @SuppressWarnings("resource")
    public void PCA9685Gpio_start() throws Exception {
        System.out.println("<--Pi4J--> LOAD");
        // This would theoretically lead into a resolution of 5 microseconds per step:
        // 4096 Steps (12 Bit)
        // T = 4096 * 0.000005s = 0.02048s
        // f = 1 / T = 48.828125


        BigDecimal frequency = new BigDecimal("48.828");
        System.out.println("<--Pi4J--> frequens");
        
        BigDecimal frequencyCorrectionFactor = new BigDecimal("1.0235");
        System.out.println("<--Pi4J--> frequens fix");

        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        System.out.println("<--Pi4J--> bus");


       	provider = new PCA9685GpioProvider(bus, 0x40, frequency, frequencyCorrectionFactor);
        System.out.println("<--Pi4J--> provider GPIO");


        // Define outputs in use for this example
        @SuppressWarnings("unused")
		GpioPinPwmOutput[] myOutputs = provisionPwmOutputs(provider);

        // Reset outputs
        provider.reset();
        System.out.println("PROVIDER RESET!");

        //
        // Set various PWM patterns for outputs 0..9
        final int offset = 400;
        final int pulseDuration = 600;

        for (int ti = 0; ti < 10; ti++) {
            Pin pin = PCA9685Pin.ALL[ti];
            int onPosition = checkForOverflow(offset * ti);
            int offPosition = checkForOverflow(pulseDuration * (ti + 1));
            provider.setPwm(pin, onPosition, offPosition);
        }
        
        /* DEMO
        for(int speed = SPEED_MIN; speed<SPEED_MAX;speed += 5) {

            provider.setPwm(PCA9685Pin.PWM_00, speed);
            provider.setPwm(PCA9685Pin.PWM_01, speed);
            provider.setPwm(PCA9685Pin.PWM_02, speed);
            provider.setPwm(PCA9685Pin.PWM_03, speed);
            provider.setPwm(PCA9685Pin.PWM_04, speed);
            provider.setPwm(PCA9685Pin.PWM_05, speed);
            Thread.sleep(500);
            System.out.println("SPEED "+speed);
        	
        }
        /*
        for(int speed = SPEED_MIN; speed<SPEED_MAX;speed += 5) {

            provider.setPwm(PCA9685Pin.PWM_00, speed);
            Thread.sleep(400);
            System.out.println("SPEED "+speed);
        	
        }
        */

    }

    private static  int checkForOverflow(int position) {
        int result = position;
        if (position > PCA9685GpioProvider.PWM_STEPS - 1) {
            result = position - PCA9685GpioProvider.PWM_STEPS - 1;
        }
        return result;
    }

	public void start () {
		System.out.println("Starting PCA9685");
		if (t == null) {
			t = new Thread (this);
		    t.start ();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
    private static  GpioPinPwmOutput[] provisionPwmOutputs(final PCA9685GpioProvider gpioProvider) {
        GpioController gpio = GpioFactory.getInstance();
        GpioPinPwmOutput myOutputs[] = {
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_00, "Pulse 00"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_01, "Pulse 01"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_02, "Pulse 02"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_03, "Pulse 03"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_04, "Pulse 04"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_05, "Pulse 05"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_06, "Pulse 06"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_07, "Pulse 07"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_08, "Pulse 08"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_09, "Pulse 09"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_10, "Always ON"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_11, "Always OFF"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_12, "Servo pulse MIN"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_13, "Servo pulse NEUTRAL"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_14, "Servo pulse MAX"),
                gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_15, "not used")};
        return myOutputs;
}
}