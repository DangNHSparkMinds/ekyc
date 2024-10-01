package net.sparkminds.ekyc.service.dto.request;

import com.google.gson.Gson;
import lombok.Data;
import net.sparkminds.ekyc.service.dto.enums.MessageType;
import java.util.Map;

@Data
public class MessageRequestDto {
    private static Gson gson = new Gson();
    private MessageType messageType;
    private Map<String, Object> params;
    public int getParamsLength() {
        return gson.toJson(params).length();
    }
}
