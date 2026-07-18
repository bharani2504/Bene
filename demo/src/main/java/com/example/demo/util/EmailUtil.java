package com.example.demo.util;

import com.example.demo.dto.Bene;

public class EmailUtil {

    public static String CreatedTemplate(Bene bene) {

        return "Hello " + bene.getBeneName() + ",\n\n"
                + "Your beneficiary profile has been created successfully.\n\n"
                + "Beneficiary Details:\n"
                + "Name : " + bene.getBeneName() + "\n"
                + "Nickname : " + bene.getBeneNicknName() + "\n"
                + "Mobile : " + bene.getMobile() + "\n"
                + "Email : " + bene.getEmail() + "\n\n"
                + "Thank you.";
    }
}
