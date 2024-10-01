package net.sparkminds.ekyc.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import net.sparkminds.ekyc.service.dto.KycDocumentDto;

@Slf4j
@Converter
public class KycDocumentConverter implements AttributeConverter<KycDocumentDto, String> {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    }

    @Override
    public String convertToDatabaseColumn(KycDocumentDto kycDocumentDto) {
        try {
            if (kycDocumentDto == null)
                return null;
            return mapper.writeValueAsString(kycDocumentDto);
        } catch (JsonProcessingException e) {
            log.error("Convert to database column kyc document error", e);
            return null;
        }
    }

    @Override
    public KycDocumentDto convertToEntityAttribute(String s) {
        try {
            if (StringUtils.isBlank(s))
                return null;
            return mapper.readValue(s, KycDocumentDto.class);
        } catch (JsonProcessingException e) {
            log.error("Convert to entity attribute kyc document error", e);
            return null;
        }
    }
}
