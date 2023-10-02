package cz.upce.sempra.domains;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class EmailMessage {
    private String from;

    private String to;

    private String subject;

    private String message;
}