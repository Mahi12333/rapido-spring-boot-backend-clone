package com.maven.Rapido.utils.EmailTamplate;


import org.springframework.stereotype.Component;

@Component
public class EmailotpContent {

    public String getEmailContent(Integer otp, String phoneNumber, String name, String subject) {
        StringBuilder content = new StringBuilder();
        content.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; }")
                .append(".container { max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px; }")
                .append(".header { background-color: #f2f2f2; padding: 10px; text-align: center; }")
                .append(".content { margin-top: 20px; }")
                .append(".footer { margin-top: 20px; text-align: center; font-size: 12px; color: #777; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<div class='header'>")
                .append("<h2>").append(subject).append("</h2>")
                .append("</div>")
                .append("<div class='content'>")
                .append("<p>Dear ").append(name).append(",</p>")
                .append("<p>Your OTP is <strong>").append(otp).append("</strong>.</p>")
                .append("<p>Please use this OTP to verify your phone number <strong>").append(phoneNumber).append("</strong>.</p>")
                .append("</div>")
                .append("<div class='footer'>")
                .append("<p>&copy; ").append(java.time.LocalDate.now().getYear()).append(" Your Company. All rights reserved.</p>")
                .append("</div>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        return content.toString();
    }
}
