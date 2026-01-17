package main.dto;

import lombok.Data;

@Data
public class UpdateEventAdminRequest {

    private String eventDate;
    private String stateAction;
}