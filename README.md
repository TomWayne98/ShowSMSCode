# ShowSMSCode

 ![Screenshot of the app](meta/screenshots/screenshot-small.png)
 ![Screenshot of the app](meta/screenshots/screenshot-wear.png)

Install it:

<a href="https://play.google.com/store/apps/details?id=eu.inmite.apps.smsjizdenka&hl=en"><img src="http://www.android.com/images/brand/get_it_on_play_logo_small.png" alt="Get it on Google Play" /></a>

Features:

- security codes which you received via SMS are showed over entire screen
- code is ready to paste imediatelly
- notification with code are sended to your phone and android wear 
- you can easily send an example of sms if it is not already in database


## How to add new SMS pattern to app.

 ** 1) At first you have to add informations about sms to sms.json **

  a) You have to fill in all subjects ("id","ispublic","example","number","unique","sender","reg_ex","alt_numbers").

  b) Every paramater except alt_numbers have to be filled.

  c) What does parameters means:

   ***id:** 
   it has to be higher than last sms patern in DB. If a number of sender isn't in number format (+420132456789; 724007007)    it'snecessary to use number higher than 1000.
   **ispublic:**
    use true if it supposed to be shown in overview list (in app) otherwise use false (used just for debug)
   **example:**
    it example of sms which is provided in overview list when you tap on sender.
   **number:**
    it number of sender ( +420132456789; 724007007; InfoSMS; Verify ).    
   **unique:**
    it text which have to be contained in SMS body. Otherwise it can't be reckognized.
   **sender:**
    this text is used as title of notifications.
   reg_ex:
    it's a regular expression to identify sms.
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


** 2) Finally you just need to increase a version number in version.json. ** 
  Application updates DB only when the version in repo is higher than local
