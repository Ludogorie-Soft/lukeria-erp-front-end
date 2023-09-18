package com.example.LukeriaFrontendApplication.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private Long id;
    @NotNull(message = "Моля въведете името на бизнеса!")
    private String businessName;
    private String englishBusinessName;
    @Pattern(regexp = "\\d{5,}", message = "Моля въведете поне 5 цифри за Ид.Номер - ЕИК!")
    private String idNumEIK;
    private boolean hasIdNumDDS;
    @NotNull(message = "Моля въведете адреса!")
    private String address;
    private String englishAddress;
    private String mol;

    // I did the fiend String because otherwise it always gets value of FALSE from the html form and only this way i could fix it
    private String isBulgarianClient;

    public boolean isBulgarianClient() {
        return Boolean.parseBoolean(isBulgarianClient);
    }

    public void setBulgarianClient(boolean bulgarianClient) {
        isBulgarianClient = String.valueOf(bulgarianClient);
    }
}

