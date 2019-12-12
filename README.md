# AppPromote 
Aplu is an android app for app promotion, I opend souce this project so everyone can learn stuff about android development. 

Including firebase authentication, firestore integration, and some node.js on cloud function.

```diff
+ Every user gets install, we send him an email. 
+ This is the function code, 
+ Sorry, i did not include the function file
+ you should follow the firebasae document and set up cloud function by yourself.
  
```

```node.js
  let transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'email credential',
        pass: 'password'
    }
});

exports.sendMail = functions.https.onCall((data, context) => {

        const dest = data.email;

        const mailOptions = {
            from: 'Aplu Team <your email>', // Something like: Jane Doe <janedoe@gmail.com>
            to:dest,
            subject: 'New Installs !', // email subject
            html: `<p style="font-size: 16px;">Congratulations ! You got a new installs lately , your hard work is worthy ! go check it out !</p>
                <br />
                <img src="https://firebasestorage.googleapis.com/v0/b/apppromote-5f2cd.appspot.com/o/image.jpg?alt=media&token=84f3861e-7284-4a27-ad2e-b854cd26eca7" />
            ` // email content in HTML
        };
  
        // returning result
       transporter.sendMail(mailOptions, (erro, info) => {
       
         // return data;
        });    
});
```

```diff
+ Don't forget to sponsor or donate 👏🏻, college bill is not kidding 😂

```
In order to run this project , you have to go to firebase and set up your own database.
This app was released on google play, go check it out !

https://play.google.com/store/apps/details?id=com.bestfree.apppromote


```diff
+ Donate with wechat
```
<img src="https://github.com/lau1944/Promotion-App/blob/master/wechat.png" alt="Donate with wechat" width="300"/>
