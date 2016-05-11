# ShowSMSCode

 
##What is it?
ShowSMSCode is simply android app. Its main function is to check whether incoming SMS contain a code which is user supposed to use. If is SMS reckognized, code is showed over entire screen and user can easily transcribe it. There are also other handy fetature...

![Screenshot of the app](http://i.imgur.com/V5yGmxP.png?1)
![Screenshot of the app](http://i.imgur.com/nK6FZmG.png?1)

####Features:

- security codes which you received via SMS are showed over entire screen
- code is ready to paste imediatelly
- notification with code are sended to your phone and android wear 
- you can easily send an example of sms if it is not already in database


## How to add new SMS pattern to app.

#### 1) At first you have to add informations about sms to sms.json 

  a) You have to fill in all subjects ("id","ispublic","example","number","unique","sender","reg_ex","alt_numbers").

  b) Every paramater except alt_numbers have to be filled.

  c) What does parameters means:
  
 
   **id:** 
   if sender is typical number use unique number higher than 0 otherwise use number higher than 1000. <br>
   **ispublic:** 
     use true if it supposed to be shown in overview list (in app) otherwise use false (used just for debug)<br>
   **example:**
    it example of sms which is provided in overview list when you tap on sender.<br>
   **number:**
    it number of sender ( +420132456789; 724007007; InfoSMS; Verify ).    <br>
   **unique:**
    it text which have to be contained in SMS body. Otherwise it can't be reckognized.<br>
   **sender:**
    this text is used as title of notifications.<br>
   **reg_ex:**
    it's a regular expression to identify sms.<br>
   **alt_numbers:**
    if the sender use more number you can add them in array.

  d) example:
  ```javascript
     {
    "id": 1002,
    "ispublic":true,
    "example":"Confirmation code: 41366. Never give this code out. Wargaming.net",
    "number": "InfoSMS",
    "unique": "Wargaming",
    "sender": "WarGaming",
    "reg_ex": "code: (.....)"
  }
  ```


#### 2) When you are done, you just need to increase a version number in version.json. 

 **version:** 
   Version number <br>
 **news:** 
  You can add the description of SMS which you add. It will be showed as notification after update.<br>
```javascript
{
  "version": 32,
  "news": "Netflix Authorization"
}
```
