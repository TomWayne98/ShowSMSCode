package cz.johrusk.showsmscode;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;



/**
 * Class for testing new SMS patterns
 */

@RunWith(MockitoJUnitRunner.class)
public class TestNewSMSHere {

    static SmsParser smsParser;

    @BeforeClass
    public static void setup() {smsParser = new SmsParser();}

    /**
     * Write method for testing SMS patterns here.
     * You just need to replace these four vars with correct Strings for new SMS.
     */

    @Test
    public void ExampleForTesting(){

        String msg_content = ("Hello world. Your code is: 123456 "); //SMS Body
        String unique = "Hello world";//Unique code which has to be included in SMS
        String pattern = "code is: (\\d+)";//Regular expression to find a code
        String expectedResult = "123456";// Code which should be find

        // It return code which you expect.
        String code = SmsParser.TestSMSPattern(msg_content,unique,pattern);
        Assert.assertEquals(expectedResult, code);
    }

    @Test
    public void Servis24(){
        String msg_content = ("CS-S24: Entered PLATBA 24 payment to account 0-6276462/0800; amount 6685,00 CZK; variable symbol 676856413. Authorization SMS code: 88336829");
        String unique = "CS-S24";
        String patern = "code: (\\d+)";
        String expectedResult = "88336829";

        String code = SmsParser.TestSMSPattern(msg_content,unique,patern);
        Assert.assertEquals(expectedResult, code);
    }

    @Test
    public void Raiffeisenbank(){
        String msg_content = ("Vazeny kliente, 3D secure kod pro Vasi internetovou transakci na castku: 100.00 CZK u obchodnika: GOPAY s.r.o. je: 820205. Vase Raiffeisenbank");
        String unique = "Raiffeisenbank";
        String patern ="je: (\\d+)";
        String expectedResult = "820205";

        String code = SmsParser.TestSMSPattern(msg_content,unique,patern);
        Assert.assertEquals(expectedResult, code);
    }

    @Test
    public void CSOB(){
        String msg_content = ("CSOB SMS klic: Prihlaseni do CSOB InternetBankingu 24 (trans. 364319819) potvrdte kodem f11-x29-zpk.");
        String unique = "CSOB";
        String patern = "kodem (...-...-...)";
        String expectedResult ="f11-x29-zpk";

        String code = SmsParser.TestSMSPattern(msg_content,unique,patern);
        Assert.assertEquals(expectedResult, code);
    }
    //TODO Find out sender
    @Test
    public void MojeBanka(){
        String msg_content = ("MojeBanka: Prihlaseni do aplikace; datum a cas: 29.03.2016 21:52:19; Autorizacni SMS kod: 465 057");
        String unique = "MojeBanka";
        String patern = "SMS kod: (... ...)";
        String expectedResult = "465 057";

        String code = SmsParser.TestSMSPattern(msg_content,unique,patern);
        Assert.assertEquals(expectedResult, code);
    }

    @Test
    public void WarGaming(){
        String msg_content = ("Confirmation code: 41366. Never give this code out. Wargaming.net");
        String unique = "Wargaming";
        String patern = "code: (.....)";
        String expectedResult = "41366";

        String code = SmsParser.TestSMSPattern(msg_content,unique,patern);
        Assert.assertEquals(expectedResult, code);
    }

    @Test
    public void Blizzard(){
        String msg_content = ("Your verification code is 176015 - Blizzard Entertainment");
        String unique = "Blizzard";
        String patern = "code is (......)";
        String expectedResult = "176015";

        String code = SmsParser.TestSMSPattern(msg_content,unique,patern);
        Assert.assertEquals(expectedResult, code);
    }

    @Test
    public void GoogleAuthenticatorCZ(){
        String msg_content = ("G-549121 je váš ověřovací kód Google.");
        String unique = "Google";
        String patern = "G-(......)";
        String expectedResult = "549121";

        String code = SmsParser.TestSMSPattern(msg_content,unique,patern);
        Assert.assertEquals(expectedResult, code);
    }

    @Test
    public void GoogleAuthenticatorEN(){
        String msg_content = ("G-549121 is your Google verification code.");
        String unique = "Google";
        String patern = "G-(......)";
        String expectedResult = "549121";

        String code = SmsParser.TestSMSPattern(msg_content,unique,patern);
        Assert.assertEquals(expectedResult, code);
    }

    @Test
    public void O2Active(){
        String msg_content = ("Vazeny zakazniku, zasilame jednorazovy PIN k autorizaci m-platby pro Audioteka s.a. na castku 1.0 Kc. PIN: 777DKE. Nesdelujte jine osobe. Vase O2.");
        String unique = "m-platby";
        String patern = "PIN: (......)";
        String expectedResult = "777DKE";

        String code = SmsParser.TestSMSPattern(msg_content,unique,patern);
        Assert.assertEquals(expectedResult, code);
    }
    @Test
    public void Netflix(){
        String msg_content = ("Your Netflix verification code is 074922");
        String unique = "Netflix";
        String patern = "code is (......)";
        String expectedResult = "074922";

        String code = SmsParser.TestSMSPattern(msg_content,unique,patern);
        Assert.assertEquals(expectedResult, code);
    }
    @Test
    public void Liftago() {
        String msg_content = ("Overovaci kod Liftago pro base telefonni cislo je: 9738. Platnost kodu vyprsi za 30 minut.");
        String unique = "Liftago";
        String patern = "cislo je: (....)";
        String expectedResult = "9738";

        String code = SmsParser.TestSMSPattern(msg_content, unique, patern);
        Assert.assertEquals(expectedResult, code);
    }
}
