package cz.johrusk.showsmscode;

import android.util.Log;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    static SmsParser smsParser;


    @BeforeClass
    public static void setup() {

         smsParser = new SmsParser();

    }



    @Test
    public void Servis24() {
        Log.d("test","ServisTesStarts");
        Sms sms = smsParser.parse(999005,"CS-S24: Entered PLATBA 24 payment to account 0-6276462/0800; amount 6685,00 CZK; variable symbol 676856413. Authorization SMS code: 88336829");
        Assert.assertEquals(999005,sms.number);
        Assert.assertEquals("CS-S24",sms.unique);
        Assert.assertEquals("Česká Spořitelna",sms.sender);
        Assert.assertEquals("88336829",sms.code);


    }
}