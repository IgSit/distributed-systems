package lab1.chat;

import java.util.UUID;

public record Message(UUID senderUuid, String message) {
}
