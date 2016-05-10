# ShowSMSCode

How to add new sms to DB

  1) At first you have to add informations about sms to sms.json<br>
    a) You have to fill in all subjects ("id","ispublic","example","number","unique","sender","reg_ex","alt_numbers").
    b) Every paramater except alt_numbers have to be filled.
    c) What does parameters means:
      id: it has to be higher than last sms patern in DB. If a number of sender isn't in number format (+420132456789; 724007007) it's     necessary to use number higher than 1000.
      ispublic: use true if it supposed to be shown in overview list (in app) otherwise use false (used just for debug)
      example: it example of sms which is provided in overview list when you tap on sender.
      number: it number of sender ( +420132456789; 724007007; InfoSMS; Verify ).
      unique: it text which have to be contained in SMS body. Otherwise it can't be reckognized.
      sender: this text is used as title of notifications.
      reg_ex: it's a regular expression to identify sms.
      alt_numbers: if the sender use more number you can add them in array.
      
    d) example:
        {
      "id": 1002,
      "ispublic":true,
      "example":"Confirmation code: 41366. Never give this code out. Wargaming.net",
      "number": "InfoSMS",
      "unique": "Wargaming",
      "sender": "WarGaming",
      "reg_ex": "code: (.....)"
     }
      
        
  2) Finally you just need to increase a version number in version.json. <br>
     Application updates DB only when the version in repo is higher than local
