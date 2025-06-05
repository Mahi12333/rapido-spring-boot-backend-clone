package com.maven.Rapido.payload.request.language;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LocalHolder {
    private Locale currentLocale;
}
