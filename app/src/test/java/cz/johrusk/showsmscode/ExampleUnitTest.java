package cz.johrusk.showsmscode;

import android.util.Log;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest{

    static SmsParser smsParser;


    @BeforeClass
    public static void setup() {

         smsParser = new SmsParser();

    }



//    @Test
//    public void Servis24() {
//        //android.util.Log.d("test","ServisTesStarts");
//        Sms sms = smsParser.parse(999005,"CS-S24: Entered PLATBA 24 payment to account 0-6276462/0800; amount 6685,00 CZK; variable symbol 676856413. Authorization SMS code: 88336829");
//        Assert.assertEquals(999005,sms.number);
//        Assert.assertEquals("CS-S24",sms.unique);
//        Assert.assertEquals("Česká Spořitelna",sms.sender);
//        Assert.assertEquals("88336829",sms.code);
//
//
//    }
    @Test
    public void Servis24(){
        String msg_content = ("CS-S24: Entered PLATBA 24 payment to account 0-6276462/0800; amount 6685,00 CZK; variable symbol 676856413. Authorization SMS code: 88336829");
        String unique = "CS-S24";

        String code = null;
    Pattern p = Pattern.compile("code: (\\d+)");
    Matcher matcher = p.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
    while (matcher.find() && mUnique.find()) {
        code = matcher.group(1);
        }
        Assert.assertEquals("88336829",code);
    }

    @Test
    public void Raiffeisenbank(){
        String msg_content = ("Vazeny kliente, 3D secure kod pro Vasi internetovou transakci na castku: 100.00 CZK u obchodnika: GOPAY s.r.o. je: 820205. Vase Raiffeisenbank");
        String unique = "Raiffeisenbank";

        String code = null;
        Pattern p = Pattern.compile("je: (\\d+)");
        Matcher matcher = p.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
        while (matcher.find() && mUnique.find()) {
            code = matcher.group(1);

        }
        Assert.assertEquals("820205",code);
    }

    @Test
    public void CSOB(){
        String msg_content = ("CSOB SMS klic: Prihlaseni do CSOB InternetBankingu 24 (trans. 364319819) potvrdte kodem f11-x29-zpk.");
        String unique = "CSOB";

        String code = null;
        Pattern p = Pattern.compile("kodem (...-...-...)");
        Matcher matcher = p.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
        while (matcher.find() && mUnique.find()) {
            code = matcher.group(1);
        }
        Assert.assertEquals("f11-x29-zpk",code);


    }
    //TODO Find out sender
    @Test
    public void MojeBanka(){
        String msg_content = ("MojeBanka: Prihlaseni do aplikace; datum a cas: 29.03.2016 21:52:19; Autorizacni SMS kod: 465 057");
        String unique = "MojeBanka";

        String code = null;
        Pattern p = Pattern.compile("SMS kod: (... ...)");
        Matcher matcher = p.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
        while (matcher.find() && mUnique.find()) {
            code = matcher.group(1);
        }
        Assert.assertEquals("465 057",code);
    }

    @Test
    public void WarGaming(){
        String msg_content = ("Confirmation code: 41366. Never give this code out. Wargaming.net");
        String unique = "Wargaming";

        String code = null;
        Pattern p = Pattern.compile("code: (.....)");
        Matcher matcher = p.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
        while (matcher.find() && mUnique.find()) {
            code = matcher.group(1);
        }
        Assert.assertEquals("41366",code);
    }

    @Test
    public void Blizzard(){
        String msg_content = ("Your verification code is 176015 - Blizzard Entertainment");
        String unique = "Blizzard";

        String code = null;
        Pattern p = Pattern.compile("code is (......)");
        Matcher matcher = p.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
        while (matcher.find() && mUnique.find()) {
            code = matcher.group(1);
        }
        Assert.assertEquals("176015",code);
    }


    @Test
    public void GoogleAuthenticatorCZ(){
        String msg_content = ("G-549121 je váš ověřovací kód Google.");
        String unique = "Google";

        String code = null;
        Pattern p = Pattern.compile("G-(......)");
        Matcher matcher = p.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
        while (matcher.find() && mUnique.find()) {
            code = matcher.group(1);
        }
        Assert.assertEquals("549121",code);
    }

    @Test
    public void GoogleAuthenticatorEN(){
        String msg_content = ("G-549121 is your Google verification code.");
        String unique = "Google";

        String code = null;
        Pattern p = Pattern.compile("G-(......)");
        Matcher matcher = p.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
        while (matcher.find() && mUnique.find()) {
            code = matcher.group(1);
        }
        Assert.assertEquals("549121",code);
    }

    @Test
    public void O2Active(){
        String msg_content = ("Vazeny zakazniku, zasilame jednorazovy PIN k autorizaci m-platby pro Audioteka s.a. na castku 1.0 Kc. PIN: 777DKE. Nesdelujte jine osobe. Vase O2.");
        String unique = "m-platby";

        String code = null;
        Pattern p = Pattern.compile("PIN: (......)");
        Matcher matcher = p.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
        while (matcher.find() && mUnique.find()) {
            code = matcher.group(1);
        }
        Assert.assertEquals("777DKE",code);
    }
    @Test
    public void Netflix(){
        String msg_content = ("Your Netflix verification code is 074922");
        String unique = "Netflix";

        String code = null;
        Pattern p = Pattern.compile("code is (......)");
        Matcher matcher = p.matcher(msg_content);
        Pattern pUnique = Pattern.compile(unique);
        Matcher mUnique = pUnique.matcher(msg_content);
        while (matcher.find() && mUnique.find()) {
            code = matcher.group(1);
        }
        Assert.assertEquals("074922",code);
    }

}
