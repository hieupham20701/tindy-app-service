package com.tindy.app.service.impl;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.tindy.app.dto.respone.AttachmentResponse;
import com.tindy.app.mapper.MapData;
import com.tindy.app.model.entity.Attachments;
import com.tindy.app.model.entity.Message;
import com.tindy.app.model.enums.MessageType;
import com.tindy.app.repository.AttachmentRepository;
import com.tindy.app.repository.MessageRepository;
import com.tindy.app.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final MessageRepository messageRepository;
    private final UploadService uploadService;
    @Override
    public AttachmentResponse saveAttachment(Integer messageId, MultipartFile multipartFile) {
        try {
            Message message = messageRepository.findById(messageId).orElseThrow(()-> new UsernameNotFoundException("Not found!"));
            Attachments attachments = new Attachments();
            attachments.setMessage(message);
            String fileName = multipartFile.getOriginalFilename();
            attachments.setThumbnail(fileName);
            assert fileName != null;
            fileName = UUID.randomUUID().toString().concat(uploadService.getExtension(fileName));
            attachments.setFileName(fileName);
            File file = uploadService.convertToFile(multipartFile,fileName);

            String url = uploadService.uploadFile(file,fileName);
            attachments.setFileUrl(url);
            attachments.setCreatedAt(new Date(System.currentTimeMillis()));
            file.delete();
            Attachments attachmentSaved = attachmentRepository.save(attachments);
            attachmentSaved.setFileUrl(url);
            attachmentSaved.setThumbnail(fileName);
            return MapData.mapOne(attachmentSaved, AttachmentResponse.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AttachmentResponse downloadAttachment(Integer messageId, String fileName, String location) throws IOException {
        String destFileName = UUID.randomUUID().toString().concat(uploadService.getExtension(fileName));
        String destSavePath =location +"\\"+destFileName;

        Credentials credentials = GoogleCredentials
                .fromStream(Files.newInputStream(Paths.get("src/main/resources/credentials.json")));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        Blob blob = storage.get(BlobId.of("tindy-app-service.appspot.com", fileName));
        blob.downloadTo(Paths.get(destSavePath));
        return MapData.mapOne(attachmentRepository.findAttachmentsByFileName(fileName), AttachmentResponse.class);
    }

    @Override
    public List<AttachmentResponse> getAttachmentsOnConversation(Integer conversationId) {
        List<Message> messages = messageRepository.findMessagesByConversationId(conversationId);
        Map<Integer, List<Attachments>> mapAttachment = new HashMap<>();
        List<Attachments> attachments = new ArrayList<>();
        for(Message message : messages){
            if(!message.isDelete() && (message.getMessageType().equals(MessageType.FILE) || message.getMessageType().equals(MessageType.AUDIO) || message.getMessageType().equals(MessageType.IMAGE))){
                mapAttachment.put(message.getId(),attachmentRepository.findAttachmentsByMessageId(message.getId()));
            }
        }
        for ( Map.Entry<Integer, List<Attachments>> entry : mapAttachment.entrySet()) {
            Integer key = entry.getKey();
            List<Attachments>  attachmentsList= entry.getValue();
            attachments.addAll(attachmentsList);
        }
        return MapData.mapList(attachments, AttachmentResponse.class);
    }
}
