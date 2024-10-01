package net.sparkminds.ekyc.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sparkminds.ekyc.entity.Kyc;
import net.sparkminds.ekyc.exception.BadRequestException;
import net.sparkminds.ekyc.exception.NotFoundException;
import net.sparkminds.ekyc.repository.KycRepository;
import net.sparkminds.ekyc.service.KycService;
import net.sparkminds.ekyc.service.dto.CitizenIdDto;
import net.sparkminds.ekyc.service.dto.KycDocumentDto;
import net.sparkminds.ekyc.service.dto.KycFaceDto;
import net.sparkminds.ekyc.service.dto.PassportDto;
import net.sparkminds.ekyc.service.dto.enums.DocumentType;
import net.sparkminds.ekyc.service.dto.enums.KycStatus;
import net.sparkminds.ekyc.service.dto.response.KycDocumentResponseDto;
import net.sparkminds.ekyc.service.dto.response.KycFaceResponseDto;
import net.sparkminds.ekyc.utils.SecurityUtil;
import net.sparkminds.ekyc.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Attribute;
import software.amazon.awssdk.services.rekognition.model.CompareFacesRequest;
import software.amazon.awssdk.services.rekognition.model.CompareFacesResponse;
import software.amazon.awssdk.services.rekognition.model.DetectFacesRequest;
import software.amazon.awssdk.services.rekognition.model.DetectFacesResponse;
import software.amazon.awssdk.services.rekognition.model.EyeDirection;
import software.amazon.awssdk.services.rekognition.model.FaceDetail;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Pose;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class KycServiceImpl implements KycService {

    private final String CITIZEN_IDENTITY_CARD_STRING = "CITIZEN IDENTITY CARD";
    private final String PASSPORT_STRING = "PASSPORT";

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;
    private final GenerativeModel generativeModel;
    private final RekognitionClient rekognitionClient;
    private final KycRepository kycRepository;

    /**
     * Verifies a user's document by extracting and validating its contents.
     *
     * @param frontImage  The front image of the document.
     * @param backImage   The back image of the document.
     * @param documentType The type of the document
     * @return A response object containing the verification result and document details.
     * @throws BadRequestException if the document is invalid or verification fails.
     */
    @Override
    public KycDocumentResponseDto verifyDocument(MultipartFile frontImage, MultipartFile backImage, DocumentType documentType) {
        // Validate the provided document files
        validateDocumentFiles(frontImage, backImage, documentType);

        // Get the current user ID
        Long userId = SecurityUtil.getCurrentUser();
        Kyc kycUpdate = kycRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("kyc.not.found"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        KycDocumentResponseDto responseDto = new KycDocumentResponseDto();
        KycDocumentDto kycDocumentDto = new KycDocumentDto();

        try {
            // Extract content from the document and clean the data
            String extractedContent = extractFromDocument(frontImage, backImage, documentType)
                    .replace("```json\n", "")
                    .replace("```", "").trim().toUpperCase(Locale.ROOT);

            // Check if the document is a Citizen ID Card
            if (extractedContent.contains(CITIZEN_IDENTITY_CARD_STRING)) {
                CitizenIdDto citizenIdDto = mapper.readValue(extractedContent, CitizenIdDto.class);

                // Upload images and set information
                String frontSideImageName = uploadDocumentImage(frontImage, userId, "citizen_id_front_side");
                String backSideImageName = uploadDocumentImage(backImage, userId, "citizen_id_back_side");
                citizenIdDto.setFrontSideImage(frontSideImageName);
                citizenIdDto.setBackSideImage(backSideImageName);

                // Set information for KYC document DTO and response DTO
                kycDocumentDto.setCountry(citizenIdDto.getNationality());
                kycDocumentDto.setType(CITIZEN_IDENTITY_CARD_STRING);
                kycDocumentDto.setMetaData(citizenIdDto);

                responseDto.setMetaData(citizenIdDto);
                responseDto.setDocumentType(DocumentType.CITIZEN_ID_CARD);

                // Check if the document is a Passport
            } else if (extractedContent.contains(PASSPORT_STRING)) {
                PassportDto passportDto = mapper.readValue(extractedContent, PassportDto.class);

                // Upload image and set information
                String frontSideImageName = uploadDocumentImage(frontImage, userId, "front_side");
                passportDto.setImage(frontSideImageName);

                // Set information for KYC document DTO and response DTO
                kycDocumentDto.setCountry(passportDto.getNationality());
                kycDocumentDto.setType(PASSPORT_STRING);
                kycDocumentDto.setMetaData(passportDto);

                responseDto.setMetaData(passportDto);
                responseDto.setDocumentType(DocumentType.PASSPORT);

            } else {
                throw new BadRequestException("document.invalid");
            }
        } catch (Exception e) {
            // Log the error and throw an exception
            log.error("Document verification failed for user {}", userId, e);
            throw new BadRequestException("document.verification.failed");
        }

        // Set the status and save the KYC information
        kycDocumentDto.setStastus(KycStatus.APPROVED);
        kycDocumentDto.setApprovedAt(LocalDateTime.now());
        kycUpdate.setKycDocumentDto(kycDocumentDto);
        kycRepository.save(kycUpdate);

        responseDto.setMessage("document.kyc.approved");
        responseDto.setStatus(KycStatus.APPROVED);
        return responseDto;
    }

    /**
     * Verifies the face in the provided image file against the stored KYC data.
     *
     * @param file The image file containing the face to be verified.
     * @return A response object containing the status and message of the KYC face verification process.
     */
    @Override
    public KycFaceResponseDto verifyFace(MultipartFile file) {
        KycFaceResponseDto responseDto = new KycFaceResponseDto();

        // Check if the input file is null or empty
        if (ValidateUtils.isNullOrEmpty(file)) {
            responseDto.setStatus(KycStatus.REJECTED);
            responseDto.setMessage("kyc.file.empty");
            return responseDto;
        }

        // Retrieve the current user ID and check for the existence of KYC
        Long userId = SecurityUtil.getCurrentUser();
        Kyc kycUpdate = kycRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("kyc.not.found"));

        // If the KYC status is REJECTED, set the response accordingly
        if (Objects.equals(KycStatus.REJECTED, kycUpdate.getStatus())) {
            responseDto.setStatus(KycStatus.REJECTED);
            responseDto.setMessage("kyc.verified");
            return responseDto;
        }

        try {
            // Convert MultipartFile to SdkBytes
            byte[] imageBytes = file.getBytes();
            SdkBytes sdkBytes = SdkBytes.fromByteArray(imageBytes);

            // Create a request to call the DetectFaces service of AWS Rekognition
            DetectFacesRequest request = DetectFacesRequest.builder()
                    .image(Image.builder().bytes(sdkBytes).build())
                    .attributes(Attribute.ALL)
                    .build();

            DetectFacesResponse result = rekognitionClient.detectFaces(request);
            List<FaceDetail> faceDetails = result.faceDetails();

            // Check the number of faces detected
            if (faceDetails.size() > 1) {
                responseDto.setMessage("kyc.multiple.faces.detected");
                responseDto.setStatus(KycStatus.REJECTED);
                return responseDto;
            }

            // Only take the first face detail and verify its validity
            FaceDetail faceDetail = faceDetails.get(0);
            verifyFrontFace(faceDetail, responseDto); // This method will update the responseDto status if needed

            if (KycStatus.REJECTED.equals(responseDto.getStatus())) {
                return responseDto; // Exit if the status has already been rejected
            }

            // Compare the face in the image with the face on the citizen ID document
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            CitizenIdDto citizenIdDto = mapper.convertValue(kycUpdate.getKycDocumentDto().getMetaData(), CitizenIdDto.class);

            if (!comparingFaces(file, citizenIdDto.getFrontSideImage())) {
                responseDto.setStatus(KycStatus.REJECTED);
                responseDto.setMessage("face.compare.fail");
                return responseDto;
            }

            // If all steps are successful, update KYC and response
            String fileName = uploadDocumentImage(file, userId, "front_face");
            KycFaceDto kycFaceDto = new KycFaceDto();
            kycFaceDto.setFrontFace(fileName);
            kycFaceDto.setStatus(KycStatus.APPROVED);
            kycFaceDto.setApprovedAt(LocalDateTime.now());

            // Update the KYC object with the face information and approval status
            kycUpdate.setKycFaceDto(kycFaceDto);
            kycUpdate.setStatus(KycStatus.APPROVED);
            kycUpdate.setApprovedAt(LocalDateTime.now());
            kycRepository.save(kycUpdate);

            // Update the response with KYC information
            responseDto.setMetaData(kycUpdate.getKycDocumentDto());
            responseDto.setStatus(KycStatus.APPROVED);
            responseDto.setMessage("kyc.face.approved");
        } catch (IOException e) {
            // Handle errors when reading the file
            responseDto.setStatus(KycStatus.REJECTED);
            responseDto.setMessage("kyc.file.read.error");
        } catch (Exception e) {
            // Handle errors for all other cases
            responseDto.setStatus(KycStatus.REJECTED);
            responseDto.setMessage("kyc.face.verification.failed");
        }

        return responseDto;
    }

    /**
     * Verifies the alignment and direction of the face in the given face detail.
     *
     * @param faceDetail  The details of the face to be verified.
     * @param responseDto The response object to update with the verification result.
     */
    private void verifyFrontFace(FaceDetail faceDetail, KycFaceResponseDto responseDto) {
        Pose pose = faceDetail.pose();
        EyeDirection eyeDirection = faceDetail.eyeDirection();
        if (Math.abs(pose.yaw()) > 10 || Math.abs(pose.pitch()) > 10 || Math.abs(pose.roll()) > 10) {
            responseDto.setMessage("face.not.align.center");
            responseDto.setStatus(KycStatus.REJECTED);
        } else if (Math.abs(eyeDirection.yaw()) > 10 || Math.abs(eyeDirection.pitch()) > 10) {
            responseDto.setMessage("eyes.not.look.straight");
            responseDto.setStatus(KycStatus.REJECTED);
        } else {
            responseDto.setMessage("kyc.face.success");
            responseDto.setStatus(KycStatus.APPROVED);
        }
    }

    /**
     * Extracts identity information from a document.
     *
     * @param frontSideImage The front side image file of the document.
     * @param backSideImage  The back side image file of the document (required for CITIZEN_ID_CARD).
     * @param documentType   The type of document being processed (e.g., CITIZEN_ID_CARD or passport).
     * @return A JSON string containing extracted document details.
     * @throws IOException         If an error occurs while reading the file.
     * @throws BadRequestException If the document is invalid or cannot be processed.
     */
    private String extractFromDocument(MultipartFile frontSideImage, MultipartFile backSideImage, DocumentType documentType) {
        GenerationConfig generationConfig = GenerationConfig.newBuilder()
                .setMaxOutputTokens(2048)
                .setTemperature(0.4F)
                .build();

        List<SafetySetting> safetySettings = Arrays.asList(
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                        .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_LOW_AND_ABOVE)
                        .build(),
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH)
                        .build()
        );

        GenerativeModel model = this.generativeModel
                .withGenerationConfig(generationConfig)
                .withSafetySettings(safetySettings);

        try {
            if (DocumentType.CITIZEN_ID_CARD.equals(documentType)) {
                String prompt = "Extract details from the document (Vietnamese): document type (English), number, full name, " +
                        "gender, date of expiry, date of birth, nationality, place of origin, place of residence. " +
                        "Return result as JSON with camel case property names.";

                Part frontSidePart = createPartFromMultipartFile(frontSideImage);
                Part backSidePart = createPartFromMultipartFile(backSideImage);
                GenerateContentResponse response = model.generateContent(ContentMaker.fromMultiModalData(frontSidePart, backSidePart, prompt));

                return ResponseHandler.getText(response);
            } else {
                String prompt = "Extract details from the document: document type (English), passport no, surname, given names, " +
                        "citizen id no, gender, date of issue, date of birth, nationality, place of birth. " +
                        "Return result as JSON with camel case property names.";

                Part frontSidePart = createPartFromMultipartFile(frontSideImage);
                GenerateContentResponse response = model.generateContent(ContentMaker.fromMultiModalData(frontSidePart, prompt));

                return ResponseHandler.getText(response);
            }
        } catch (Exception e) {
            throw new BadRequestException("document.invalid");
        }
    }

    /**
     * Validates the document files based on the provided document type.
     *
     * @param frontSideImage The front side image file of the document to be validated.
     * @param backSideImage  The back side image file of the document (required for CITIZEN_ID_CARD).
     * @param documentType   The type of document being processed, which determines the required files.
     * @throws BadRequestException if any of the required document files are missing or empty.
     */
    private void validateDocumentFiles(MultipartFile frontSideImage, MultipartFile backSideImage, DocumentType documentType) {
        if (DocumentType.CITIZEN_ID_CARD.equals(documentType)) {
            // For CITIZEN_ID_CARD, both front and back images are mandatory.
            if (ValidateUtils.isNullOrEmpty(frontSideImage) || ValidateUtils.isNullOrEmpty(backSideImage)) {
                throw new BadRequestException("file.empty");
            }
        } else if (ValidateUtils.isNullOrEmpty(frontSideImage)) {
            // For other document types, only the front image is mandatory.
            throw new BadRequestException("file.empty");
        }
    }

    /**
     * Creates a Part object from a given MultipartFile for further processing.
     *
     * @param file The MultipartFile to be converted into a Part object.
     * @return A Part object containing the MIME type and data of the input MultipartFile.
     * @throws IOException If an error occurs while reading the file content.
     */
    private Part createPartFromMultipartFile(MultipartFile file) throws IOException {
        // Ensure that the file content type is not null before creating the Part.
        return PartMaker.fromMimeTypeAndData(Objects.requireNonNull(file.getContentType()), file.getBytes());
    }

    /**
     * Compares a given image with an image stored in an S3 bucket to determine if the faces in both images match.
     *
     * @param file     The image file containing the face to be compared (uploaded by the user).
     * @param fileName The file name of the image stored in the S3 bucket to compare against.
     * @return {@code true} if a matching face is found, {@code false} otherwise.
     * @throws IllegalArgumentException if the input file is null or if face comparison fails due to an exception.
     */
    private boolean comparingFaces(MultipartFile file, String fileName) {
        GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(bucketName).key(fileName).build();
        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(objectRequest);

        Image sourceImage = null;
        Image targetImage = null;
        try {
            // Convert the source image (user uploaded) and target image (from S3) to ByteBuffer.
            ByteBuffer sourceImageBytes = ByteBuffer.wrap(file.getBytes());
            ByteBuffer targetImageBytes = ByteBuffer.wrap(s3Object.readAllBytes());

            // Create Image objects from ByteBuffer for comparison.
            sourceImage = Image.builder().bytes(SdkBytes.fromByteBuffer(sourceImageBytes)).build();
            targetImage = Image.builder().bytes(SdkBytes.fromByteBuffer(targetImageBytes)).build();
        } catch (IOException e) {
            log.error("Error while reading image bytes for face comparison: {}", e.getMessage(), e);
            return false;
        }

        // Create a CompareFacesRequest with a similarity threshold of 80%.
        CompareFacesRequest request = CompareFacesRequest.builder()
                .sourceImage(sourceImage)
                .targetImage(targetImage)
                .similarityThreshold(80F)
                .build();

        // Call AWS Rekognition to compare the faces and get the response.
        CompareFacesResponse compareFacesResult = rekognitionClient.compareFaces(request);

        // Return true if there are any face matches found in the result.
        return !compareFacesResult.faceMatches().isEmpty();
    }

    /**
     * Uploads a document image to the specified S3 bucket.
     *
     * @param file   The image file to be uploaded.
     * @param userId The ID of the user who owns the document.
     * @param suffix A suffix string used to differentiate between various images.
     * @return The generated file name used to store the image in the S3 bucket.
     * @throws IllegalArgumentException if the file is null or empty.
     */
    private String uploadDocumentImage(MultipartFile file, Long userId, String suffix) throws IOException {
        String fileName = String.format("user_%s_%s.png", userId, suffix);
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType("image/png")
                .contentDisposition("inline")  // Set content disposition to inline for viewing
                .acl(ObjectCannedACL.PUBLIC_READ)  // Set access control to public read
                .build();

        // Upload the file to S3 using an input stream from the provided file
        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return fileName;
    }
}
