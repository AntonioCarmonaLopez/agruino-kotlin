const express =require('express');
const nodemailer = require('nodemailer');
const cors = require('cors');
const bodyParser = require('body-parser');
const app = express();
const port = process.env.PORT || '3000';
app.use(bodyParser.json());
app.use(cors())
app.post('/formulario', (req, res) => {
    let transporter = nodemailer.createTransport({
        host: 'smtp.gmail.com',
        port: 465,
        auth: {
        user: 'your email here',
        pass: 'tour pass here' 
        }
        });
        var mailOptions = {
            from: 'Admin',
            to: req.body.mail,
            subject: req.body.subject,
            text: req.body.msg
     };
     transporter.sendMail(mailOptions, function(error, info){
        if (error){
            console.log(error);
            res.send(500, error);
        } else {
            console.log("Email sent");
            res.status(200).send(req.body);
        }
    });
})

app.listen(port, () => {
    console.log(`Server is listening on port ${port}`);
  });