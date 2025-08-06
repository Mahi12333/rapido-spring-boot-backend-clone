package com.maven.Rapido.payload.request.feed;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CreateFeedDTO {
    private List<String> fileKeys;
    private String otherFormData; // placeholder
}
