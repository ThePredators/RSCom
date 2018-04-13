package fr.freekit.protocols.rscom;

/**
 * Created by TheDarkBook on 13/04/18.
 */

public class ArduinoExample {

    /**
     * A mettre coté Arduino
     */

    /**

     int main(void) {
         //initialization
         initIO();
         uart_init();
         sei();

         uint8_t i = 0;
         volatile uint8_t pause;

         for(;;){//this is the main loop
             pause = data;
             PORTB |= (1 << LED);
             for(i = 0; i < pause; i++)
                 _delay_us(10);
                 PORTB &= ~(1 << LED);
             for(i = 0; i < 255-pause; i++)
                _delay_us(10);
         }
     }


     */
}
